package geometries;

import primitives.*;

/**
 * The {@code Sphere} class represents a sphere in 3D space. A sphere is defined
 * by a center point and a radius.
 */
public class Sphere extends RadialGeometry {

	/** The center point of the sphere. */
	private final Point center;

	/**
	 * Constructs a sphere with the specified radius and center point.
	 *
	 * @param radius The radius of the sphere.
	 * @param center The center point of the sphere.
	 */
	public Sphere(double radius, Point center) {
		super(radius);
		this.center = center;
	}

	@Override
	public Vector getNormal(Point point) {
		return point.subtract(center).normalize();
	}

	@Override
	public String toString() {
		return "Sphere [center=" + center + "]";
	}

}
