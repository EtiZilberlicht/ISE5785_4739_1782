package geometries;

import java.util.List;
import primitives.*;
import static primitives.Util.alignZero;

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

	@Override
	public List<Point> findIntersections(Ray ray) {
		Point point = ray.getHead();
		if (center.equals(point))
			return List.of(ray.getPoint(radius));
		Vector u = center.subtract(point);
		double tm = ray.getDirection().dotProduct(u);
		double dSquared = u.lengthSquared() - tm * tm;
		double thSquared = squaredRadius - dSquared;
		if (alignZero(thSquared) <= 0)
			return null;
		double th = Math.sqrt(thSquared); // always positive
		double t1 = alignZero(tm - th);
		double t2 = alignZero(tm + th); // always greater than t1
		if (t1 > 0 && t2 > 0) {
			return List.of(ray.getPoint(t1), ray.getPoint(t2));
		} else if (t1 > 0) {
			return List.of(ray.getPoint(t1));
		} else if (t2 > 0) {
			return List.of(ray.getPoint(t2));
		}
		return null;

	}

}
