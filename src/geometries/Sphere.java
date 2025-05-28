package geometries;

import static primitives.Util.alignZero;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

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
	protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
		Point point = ray.getHead();

		if (center.equals(point))
			return alignZero(radius) > maxDistance ? null : List.of(new Intersection(this, ray.getPoint(radius)));

		Vector u = center.subtract(point);
		double tm = ray.getDirection().dotProduct(u);
		double dSquared = u.lengthSquared() - tm * tm;

		double thSquared = squaredRadius - dSquared;
		if (alignZero(thSquared) <= 0)
			return null;
		double th = Math.sqrt(thSquared); // always positive

		double t1 = alignZero(tm - th);
		double t2 = alignZero(tm + th); // always greater than t1

		if (t2 <= 0 || alignZero(t1 - maxDistance) > 0)
			return null;

		if (alignZero(t2 - maxDistance) > 0)
			return t1 <= 0 ? null : List.of(new Intersection(this, ray.getPoint(t1)));

		return t1 <= 0 ? List.of(new Intersection(this, ray.getPoint(t2)))
				: List.of(new Intersection(this, ray.getPoint(t1)), new Intersection(this, ray.getPoint(t2)));
	}

}
