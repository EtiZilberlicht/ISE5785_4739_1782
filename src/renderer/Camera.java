package renderer;

import primitives.*;
import static primitives.Util.isZero;
import static primitives.Util.alignZero;

import java.util.MissingResourceException;

/**
 * The {@code Camera} class represents a virtual camera in a 3D rendering
 * engine. It defines the camera's position, orientation, and view plane
 * parameters.
 * <p>
 * This camera uses a builder design pattern to construct valid configurations.
 * Rays can be constructed through pixels on the view plane using
 * {@link #constructRay(int, int, int, int)}.
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
	private Vector translation;

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
	 * Constructs a {@link Ray} from the camera through a specific pixel on the view
	 * plane.
	 *
	 * @param nX number of horizontal pixels (columns)
	 * @param nY number of vertical pixels (rows)
	 * @param j  column index (0-based)
	 * @param i  row index (0-based)
	 * @return the constructed {@link Ray} through the specified pixel
	 */
	public Ray constructRay(int nX, int nY, int j, int i) {
		double rY = height / nY;
		double rX = width / nX;
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
	 * Builder class for {@link Camera}, following the Builder design pattern.
	 * Ensures immutability and validation before creating a camera instance.
	 */
	public static class Builder {

		/**
		 * default constructor
		 */
		public Builder() {
		}

		/**
		 * The camera being built.
		 */
		private final Camera camera = new Camera();

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
			if (width <= 0 || height <= 0) {
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
			if (distance <= 0) {
				throw new IllegalArgumentException("distance from camera to view must be positive");
			}
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
		public Builder setTranslation(Vector move) {
			camera.translation = move;
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
			if (camera.width == 0d)
				throw new MissingResourceException(description, className, "width");
			if (camera.height == 0d)
				throw new MissingResourceException(description, className, "height");
			if (camera.distance == 0d)
				throw new MissingResourceException(description, className, "distance");

			if (!isZero(camera.vTo.dotProduct(camera.vUp)))
				throw new IllegalArgumentException("vTo and vUp must be orthogonal");

			if (alignZero(camera.width) <= 0)
				throw new IllegalArgumentException("width " + positiveMessage);
			if (alignZero(camera.height) <= 0)
				throw new IllegalArgumentException("height " + positiveMessage);
			if (alignZero(camera.distance) <= 0)
				throw new IllegalArgumentException("distance " + positiveMessage);

			camera.vTo = camera.vTo.normalize();
			camera.vUp = camera.vUp.normalize();

			if (!isZero(camera.rotationAngleDegrees)) {
				double angleRad = Math.toRadians(camera.rotationAngleDegrees);
				Vector vUpRotated = camera.vUp.scale(Math.cos(angleRad)).add(camera.vRight.scale(Math.sin(angleRad)));
				camera.vUp = vUpRotated.normalize();
			}

			if (camera.translation != null)
				camera.cameraPoint = camera.cameraPoint.add(camera.translation);

			camera.vRight = camera.vTo.crossProduct(camera.vUp).normalize();
			camera.viewPlanePC = camera.cameraPoint.add(camera.vTo.scale(camera.distance));

			try {
				return (Camera) camera.clone();
			} catch (CloneNotSupportedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
