package geometries;

import java.util.List;
import static primitives.Util.isZero;
import static primitives.Util.alignZero;

import primitives.*;

/**
 * The {@code Plane} class represents a two-dimensional plane in
 * three-dimensional space. A plane can be defined either by a point and a
 * normal vector, or by three non-collinear points.
 */
public class Plane extends Geometry {

	/** A point on the plane. */
	private final Point point;

	/** The normal vector to the plane. */
	private final Vector normal;

	/**
	 * Constructs a plane given a point on the plane and a normal vector.
	 * 
	 * @param point  A point on the plane.
	 * @param normal The normal vector to the plane. It is automatically normalized.
	 */
	public Plane(Point point, Vector normal) {
		this.point = point;
		this.normal = normal.normalize();
	}

	/**
	 * Constructs a plane given three non-collinear points in space.
	 * 
	 * @param x The first point defining the plane.
	 * @param y The second point defining the plane.
	 * @param z The third point defining the plane.
	 * @throws IllegalArgumentException If there are collinear points or if the
	 *                                  points are on the same line
	 */
	public Plane(Point x, Point y, Point z) {
		this.point = x;
		Vector vector1 = x.subtract(y);
		Vector vector2 = y.subtract(z);
		this.normal = vector1.crossProduct(vector2).normalize();
	}

	@Override
	public Vector getNormal(Point point) {
		return normal;
	}

	@Override
	public String toString() {
		return "Plane [point=" + point + ", normal=" + normal + "]";
	}

	@Override
	public List<Point> findIntersections(Ray ray) {
		Point head = ray.getHead();
		if (head.equals(this.point))
			return null;
		double numerator = this.normal.dotProduct(this.point.subtract(head));
		double denominator = this.normal.dotProduct(ray.getDirection());
		if (isZero(numerator) || isZero(denominator))
			return null;
		double t = alignZero(numerator / denominator);
		return t <= 0 ? null : List.of(ray.getPoint(t));
	}

}
