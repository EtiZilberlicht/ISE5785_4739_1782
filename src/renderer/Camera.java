package renderer;

import primitives.*;
import static primitives.Util.isZero;
import static primitives.Util.alignZero;

import java.util.MissingResourceException;

public class Camera implements Cloneable {
	private Point cameraPoint;
	private Vector vTo, vUp, vRight;
	private double height = 0d, width = 0d, distance = 0d;
	// View plane center point to save CPU time – it’s always the same
	private Point viewPlanePC;
	private double rotationAngleDegrees = 0d;
	private Vector translation;

	private Camera() {
	};

	public static Builder getBuilder() {
		return new Builder();
	}

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

	public static class Builder {

		private final Camera camera = new Camera();

		public Builder setLocation(Point p0) {
			camera.cameraPoint = p0;
			return this;
		}

		public Builder setDirection(Vector vTo, Vector vUp) {
			if (!isZero(vTo.dotProduct(vUp)))
				throw new IllegalArgumentException("vTo and vUp must be orthogonal");

			camera.vTo = vTo.normalize();
			camera.vUp = vUp.normalize();
			camera.vRight = vTo.crossProduct(vUp).normalize();
			return this;
		}

		public Builder setDirection(Point purpose, Vector vUp) {
			Vector to = purpose.subtract(camera.cameraPoint).normalize();
			Vector right = to.crossProduct(vUp).normalize();
			Vector up = right.crossProduct(to).normalize();
			camera.vTo = to;
			camera.vUp = up;
			camera.vRight = right;
			return this;

		}

		public Builder setDirection(Point purpose) {
			setDirection(purpose, new Vector(0, 1, 0));
			return this;
		}

		public Builder setVpSize(double width, double height) {
			if (width <= 0 || height <= 0) {
				throw new IllegalArgumentException("width and height must be positive");
			}
			camera.width = width;
			camera.height = height;
			return this;
		}

		public Builder setVpDistance(double distance) {
			if (distance <= 0) {
				throw new IllegalArgumentException("distance from camera to view must be positive");
			}
			camera.distance = distance;
			return this;
		}

		public Builder setResolution(int nX, int nY) {
			return this;
		}

		public Builder setRotation(double angleDegrees) {
			camera.rotationAngleDegrees = angleDegrees;
			return this;
		}

		public Builder setTranslation(Vector move) {
			camera.translation = move;
			return this;
		}

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

			// Ensure normalized vectors
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
