package renderer;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

import java.util.LinkedList;
import java.util.MissingResourceException;
import java.util.stream.IntStream;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import renderer.PixelManager.Pixel;
import renderer.grid.GridRayTracer;
import scene.Scene;

/**
 * The {@code Camera} class represents a virtual camera in a 3D rendering
 * engine. It defines the camera's position, orientation, and view plane
 * parameters.
 * <p>
 * This camera uses a builder design pattern to construct valid configurations.
 * Rays can be constructed through pixels on the view plane using
 * {@link #constructRay(int, int)}.
 * </p>
 */
public class Camera implements Cloneable {

	/**
	 * The camera's position in space.
	 */
	private Point cameraPoint;

	/**
	 * The forward direction vector (toward the view plane).
	 */
	private Vector vTo;

	/**
	 * The upward direction vector.
	 */
	private Vector vUp;

	/**
	 * The rightward direction vector, perpendicular to both vTo and vUp.
	 */
	private Vector vRight;

	/**
	 * The height of the view plane.
	 */
	private double height = 0d;

	/**
	 * The width of the view plane.
	 */
	private double width = 0d;

	/**
	 * The distance from the camera to the view plane.
	 */
	private double distance = 0d;

	/**
	 * The precomputed center point of the view plane, used to save computation.
	 */
	private Point viewPlanePC;

	/**
	 * Optional rotation angle in degrees to rotate the up vector around the vTo
	 * axis.
	 */
	private double rotationAngleDegrees = 0d;

	/**
	 * Optional translation vector to move the camera from its original position.
	 */
	private Vector transition;

	/**
	 * The image writer used to write pixel colors to an image file.
	 */
	private ImageWriter imageWriter;

	/**
	 * The ray tracer used to determine the color seen through each ray.
	 */
	private RayTracerBase rayTracer;

	/**
	 * The number of horizontal pixels in the image.
	 */
	private int nX = 1;

	/**
	 * The number of vertical pixels in the image.
	 */
	private int nY = 1;

	/**
	 * The width of a single pixel
	 */
	private double rX;

	/**
	 * The height of a single pixel
	 */
	private double rY;

	/** Amount of threads to use fore rendering image by the camera */
	private int threadsCount = 0;
	/**
	 * Amount of threads to spare for Java VM threads:<br>
	 * Spare threads if trying to use all the cores
	 */
	private static final int SPARE_THREADS = 2;
	/**
	 * Debug print interval in seconds (for progress percentage)<br>
	 * if it is zero - there is no progress output
	 */
	private double printInterval = 0;
	/**
	 * Pixel manager for supporting:
	 * <ul>
	 * <li>multi-threading</li>
	 * <li>debug print of progress percentage in Console window/tab</li>
	 * </ul>
	 */
	private PixelManager pixelManager;

	/**
	 * Private constructor to enforce use of the builder.
	 */
	private Camera() {
	}

	/**
	 * Returns a builder instance for constructing a {@code Camera}.
	 *
	 * @return a new {@link Camera.Builder} instance
	 */
	public static Builder getBuilder() {
		return new Builder();
	}

	/**
	 * Creates a new {@link Builder} instance based on the given existing
	 * {@link Camera}.
	 *
	 * @param oldCamera the camera to copy from
	 * @return a new {@code Builder} initialized with the parameters of
	 *         {@code oldCamera}
	 */
	public static Builder getBuilder(Camera oldCamera) {
		return new Builder(oldCamera);
	}

	/**
	 * Constructs a {@link Ray} from the camera through a specific pixel on the view
	 * plane.
	 *
	 * @param j column index (0-based)
	 * @param i row index (0-based)
	 * @return the constructed {@link Ray} through the specified pixel
	 */
	public Ray constructRay(int j, int i) {
		double yI = -(i - (nY - 1d) / 2d) * rY;
		double xJ = (j - (nX - 1d) / 2d) * rX;
		Point pIJ = viewPlanePC;
		if (!isZero(xJ))
			pIJ = pIJ.add(vRight.scale(xJ));
		if (!isZero(yI))
			pIJ = pIJ.add(vUp.scale(yI));
		Vector vIJ = pIJ.subtract(cameraPoint);
		return new Ray(cameraPoint, vIJ);
	}

	/**
	 * Renders the image by casting rays through all pixels on the view plane.
	 *
	 * @return this camera instance for method chaining
	 */
//	public Camera renderImage() {
//		for (int i = 0; i < nX; i++)
//			for (int j = 0; j < nY; j++)
//				castRay(i, j);
//		return this;
//	}

	/**
	 * This function renders image's pixel color map from the scene included in the
	 * ray tracer object
	 * 
	 * @return the camera object itself
	 */
	public Camera renderImage() {
		pixelManager = new PixelManager(nY, nX, printInterval);
		return switch (threadsCount) {
		case 0 -> renderImageNoThreads();
		case -1 -> renderImageStream();
		default -> renderImageRawThreads();
		};
	}

	/**
	 * Draws a grid on the image with the specified interval and color.
	 *
	 * @param interval spacing between grid lines (in pixels)
	 * @param color    the color of the grid lines
	 * @return this camera instance for method chaining
	 */
	public Camera printGrid(int interval, Color color) {
		for (int i = 0; i < nX; i++)
			for (int j = 0; j < nY; j++)
				if (i % interval == 0 || j % interval == 0)
					imageWriter.writePixel(i, j, color);
		return this;
	}

	/**
	 * Writes the image to disk with the specified name.
	 *
	 * @param imageName name of the output image file
	 * @return this camera instance for method chaining
	 */
	public Camera writeToImage(String imageName) {
		imageWriter.writeToImage(imageName);
		return this;
	}

	/**
	 * Casts a single ray through the specified pixel and writes its color.
	 *
	 * @param column the pixel's column index
	 * @param row    the pixel's row index
	 */
	private void castRay(int column, int row) {
		Ray ray = constructRay(column, row);
		Color color = rayTracer.traceRay(ray);
		imageWriter.writePixel(column, row, color);
		pixelManager.pixelDone();
	}

	/**
	 * Render image using multi-threading by parallel streaming
	 * 
	 * @return the camera object itself
	 */
	private Camera renderImageStream() {
		IntStream.range(0, nY).parallel().forEach(i -> IntStream.range(0, nX).parallel().forEach(j -> castRay(j, i)));
		return this;
	}

	/**
	 * Render image without multi-threading
	 * 
	 * @return the camera object itself
	 */
	private Camera renderImageNoThreads() {
		for (int i = 0; i < nY; ++i)
			for (int j = 0; j < nX; ++j)
				castRay(j, i);
		return this;
	}

	/**
	 * Render image using multi-threading by creating and running raw threads
	 * 
	 * @return the camera object itself
	 */
	private Camera renderImageRawThreads() {
		var threads = new LinkedList<Thread>();
		while (threadsCount-- > 0)
			threads.add(new Thread(() -> {
				Pixel pixel;
				while ((pixel = pixelManager.nextPixel()) != null)
					castRay(pixel.col(), pixel.row());
			}));
		for (var thread : threads)
			thread.start();
		try {
			for (var thread : threads)
				thread.join();
		} catch (InterruptedException ignored) {
		}
		return this;
	}

	/**
	 * Builder class for {@link Camera}, following the Builder design pattern.
	 * Ensures immutability and validation before creating a camera instance.
	 */
	public static class Builder {

		/**
		 * default constructor
		 */
		public Builder() {
			this.camera = new Camera();
		}

		/**
		 * Constructs a Builder instance initialized with an existing Camera object.
		 *
		 * @param oldCamera the Camera object to initialize the builder with
		 */
		public Builder(Camera oldCamera) {
			this.camera = oldCamera;
		}

		/**
		 * The camera being built.
		 */
		private final Camera camera;

		/**
		 * Sets the camera's location.
		 *
		 * @param p0 the camera position
		 * @return this builder instance
		 */
		public Builder setLocation(Point p0) {
			camera.cameraPoint = p0;
			return this;
		}

		/**
		 * Sets the camera's direction and up vector.
		 *
		 * @param vTo the viewing direction
		 * @param vUp the up vector (must be orthogonal to vTo)
		 * @return this builder instance
		 * @throws IllegalArgumentException if {@code vTo} and {@code vUp} are not
		 *                                  orthogonal
		 */
		public Builder setDirection(Vector vTo, Vector vUp) {
			if (!isZero(vTo.dotProduct(vUp)))
				throw new IllegalArgumentException("vTo and vUp must be orthogonal");

			camera.vTo = vTo.normalize();
			camera.vUp = vUp.normalize();
			camera.vRight = vTo.crossProduct(vUp).normalize();
			return this;
		}

		/**
		 * Sets the direction of the camera to look at a target point with a given up
		 * vector.
		 *
		 * @param purpose the point the camera should look at
		 * @param vUp     the up direction
		 * @return this builder instance
		 */
		public Builder setDirection(Point purpose, Vector vUp) {
			Vector to = purpose.subtract(camera.cameraPoint).normalize();
			Vector right = to.crossProduct(vUp).normalize();
			Vector up = right.crossProduct(to).normalize();
			camera.vTo = to;
			camera.vUp = up;
			camera.vRight = right;
			camera.viewPlanePC = purpose;
			return this;
		}

		/**
		 * Sets the direction of the camera to look at a target point. Assumes up vector
		 * is (0,1,0).
		 *
		 * @param purpose the point the camera should look at
		 * @return this builder instance
		 */
		public Builder setDirection(Point purpose) {
			setDirection(purpose, new Vector(0, 1, 0));
			return this;
		}

		/**
		 * Sets the size of the view plane.
		 *
		 * @param width  the view plane width
		 * @param height the view plane height
		 * @return this builder instance
		 * @throws IllegalArgumentException if width or height are not positive
		 */
		public Builder setVpSize(double width, double height) {
			if (alignZero(width) <= 0 || alignZero(height) <= 0) {
				throw new IllegalArgumentException("width and height must be positive");
			}
			camera.width = width;
			camera.height = height;
			return this;
		}

		/**
		 * Sets the distance from the camera to the view plane.
		 *
		 * @param distance distance value (must be positive)
		 * @return this builder instance
		 * @throws IllegalArgumentException if distance is not positive
		 */
		public Builder setVpDistance(double distance) {
			if (alignZero(distance) <= 0)
				throw new IllegalArgumentException("distance from camera to view must be positive");

			camera.distance = distance;
			return this;
		}

		/**
		 * (Placeholder) Sets the resolution of the view plane. Currently does nothing.
		 *
		 * @param nX number of columns
		 * @param nY number of rows
		 * @return this builder instance
		 */
		public Builder setResolution(int nX, int nY) {
			camera.nX = nX;
			camera.nY = nY;
			return this;
		}

		/**
		 * Sets a rotation angle (in degrees) around the vTo axis.
		 *
		 * @param angleDegrees the angle to rotate the up vector
		 * @return this builder instance
		 */
		public Builder setRotation(double angleDegrees) {
			camera.rotationAngleDegrees = angleDegrees;
			return this;
		}

		/**
		 * Translates the camera location by a given vector.
		 *
		 * @param move the translation vector
		 * @return this builder instance
		 */
		public Builder setTransition(Vector move) {
			camera.transition = move;
			return this;
		}

		/**
		 * Sets the ray tracer used by the camera.
		 *
		 * @param scene the scene to trace rays through
		 * @param type  the type of ray tracer (e.g., SIMPLE or GRID)
		 * @return this builder instance
		 */
		public Builder setRayTracer(Scene scene, RayTracerType type) {
			switch (type) {
			case RayTracerType.SIMPLE:
				camera.rayTracer = new SimpleRayTracer(scene);
				break;
			case RayTracerType.GRID:
				camera.rayTracer = new GridRayTracer(scene);
				break;
			default:
				break;
			}
			return this;
		}

		/**
		 * Set multi-threading <br>
		 * Parameter value meaning:
		 * <ul>
		 * <li>-2 - number of threads is number of logical processors less 2</li>
		 * <li>-1 - stream processing parallelization (implicit multi-threading) is
		 * used</li>
		 * <li>0 - multi-threading is not activated</li>
		 * <li>1 and more - literally number of threads</li>
		 * </ul>
		 * 
		 * @param threads number of threads
		 * @return builder object itself
		 */
		public Builder setMultithreading(int threads) {
			if (threads < -3)
				throw new IllegalArgumentException("Multithreading parameter must be -2 or higher");
			if (threads == -2) {
				int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
				camera.threadsCount = cores <= 2 ? 1 : cores;
			} else
				camera.threadsCount = threads;
			return this;
		}

		/**
		 * Set debug printing interval. If it's zero - there won't be printing at all
		 * 
		 * @param interval printing interval in %
		 * @return builder object itself
		 */
		public Builder setDebugPrint(double interval) {
			if (interval < 0)
				throw new IllegalArgumentException("interval parameter must be non-negative");
			camera.printInterval = interval;
			return this;
		}

		/**
		 * Validates and builds the {@link Camera} instance.
		 *
		 * @return a fully initialized {@link Camera}
		 * @throws MissingResourceException if a required field is missing
		 * @throws IllegalArgumentException if any parameter is invalid
		 * @throws RuntimeException         if cloning fails unexpectedly
		 */
		public Camera build() {
			final String className = "Camera";
			final String description = "Missing data";
			final String positiveMessage = "must be positive";

			if (camera.cameraPoint == null)
				throw new MissingResourceException(description, className, "cameraPoint");
			if (camera.vUp == null)
				throw new MissingResourceException(description, className, "vUp");
			if (camera.vTo == null)
				throw new MissingResourceException(description, className, "vTo");
			if (!isZero(camera.vTo.dotProduct(camera.vUp)))
				throw new IllegalArgumentException("vTo and vUp must be orthogonal");
			camera.vTo = camera.vTo.normalize();
			camera.vUp = camera.vUp.normalize();
			camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();

			if (alignZero(camera.distance) <= 0)
				throw new IllegalArgumentException("distance " + positiveMessage);
			camera.viewPlanePC = camera.cameraPoint.add(camera.vTo.scale(camera.distance));

			if (alignZero(camera.nX) <= 0)
				throw new IllegalArgumentException("nX " + positiveMessage);
			if (alignZero(camera.nY) <= 0)
				throw new IllegalArgumentException("nY " + positiveMessage);
			camera.imageWriter = new ImageWriter(camera.nX, camera.nY);

			if (alignZero(camera.width) <= 0)
				throw new IllegalArgumentException("width " + positiveMessage);
			if (alignZero(camera.height) <= 0)
				throw new IllegalArgumentException("height " + positiveMessage);

			camera.rY = camera.height / camera.nY;
			camera.rX = camera.width / camera.nX;

			if (camera.rayTracer == null)
				camera.rayTracer = new SimpleRayTracer(null);

			if (!isZero(camera.rotationAngleDegrees)) {
				double angleDeg = camera.rotationAngleDegrees % 360;
				if (angleDeg < 0)
					angleDeg += 360;

				Vector newUp, newRight;

				if (isZero(angleDeg - 90)) {
					newUp = camera.vRight;
					newRight = camera.vUp.scale(-1);
				} else if (isZero(angleDeg - 180)) {
					newUp = camera.vUp.scale(-1);
					newRight = camera.vRight.scale(-1);
				} else if (isZero(angleDeg - 270)) {
					newUp = camera.vRight.scale(-1);
					newRight = camera.vUp;
				} else {
					double angleRad = Math.toRadians(angleDeg);
					double cos = Math.cos(angleRad);
					double sin = Math.sin(angleRad);

					newUp = camera.vUp.scale(cos).add(camera.vRight.scale(sin));
					newRight = camera.vRight.scale(cos).subtract(camera.vUp.scale(sin));
				}

				camera.vUp = newUp.normalize();
				camera.vRight = newRight.normalize();
			}

			if (camera.transition != null) {

				camera.cameraPoint = camera.cameraPoint.add(camera.transition);
				Vector to = camera.viewPlanePC.subtract(camera.cameraPoint).normalize();
				Vector right = to.crossProduct(camera.vUp).normalize();
				Vector up = right.crossProduct(to).normalize();
				camera.vTo = to;
				camera.vUp = up;
				camera.vRight = right;
				camera.distance = camera.cameraPoint.distance(camera.viewPlanePC);
			}

			if (camera.rayTracer instanceof GridRayTracer gTracer) {
				gTracer.setupGrid();
			}

			try {
				return (Camera) camera.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
