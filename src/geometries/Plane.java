package geometries;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

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

	private final Vector v1;

	private final Vector v2;

	/**
	 * Constructs a plane given a point on the plane and a normal vector.
	 * 
	 * @param point  A point on the plane.
	 * @param normal The normal vector to the plane. It is automatically normalized.
	 */
	public Plane(Point point, Vector normal) {
		this.point = point;
		this.normal = normal.normalize();
		var vectors = getPlaneVectors();
		this.v1 = vectors.getFirst();
		this.v2 = vectors.get(1);
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
		this.v1 = vector1;
		this.v2 = vector2;
	}

	@Override
	public Vector getNormal(Point point) {
		return normal;
	}

	/**
	 * @return the v1
	 */
	public Vector getV1() {
		return v1;
	}

	/**
	 * @return the v2
	 */
	public Vector getV2() {
		return v2;
	}

	@Override
	public String toString() {
		return "Plane [point=" + point + ", normal=" + normal + "]";
	}

	@Override
	protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
		Point head = ray.getHead();
		if (head.equals(this.point))
			return null;
		double numerator = this.normal.dotProduct(this.point.subtract(head));
		double denominator = this.normal.dotProduct(ray.getDirection());
		if (isZero(denominator))
			return null;
		double t = alignZero(numerator / denominator);
		return t <= 0 || alignZero(t - maxDistance) > 0 ? null : List.of(new Intersection(this, ray.getPoint(t)));
	}

	public List<Vector> getPlaneVectors() {
		// Normalize the normal vector to ensure unit length
		Vector n = normal.normalize();

		// Choose a helper vector that is not parallel to the normal
		// We select the axis where the normal has the smallest component
		Vector helper = (Math.abs(n.getX()) < Math.abs(n.getY()) && Math.abs(n.getX()) < Math.abs(n.getZ()))
				? new Vector(1, 0, 0) // x-axis
				: (Math.abs(n.getY()) < Math.abs(n.getZ()) ? new Vector(0, 1, 0) : new Vector(0, 0, 1)); // y or z-axis

		// First vector on the plane: perpendicular to both normal and helper
		Vector v1 = n.crossProduct(helper).normalize();

		// Second vector on the plane: perpendicular to both normal and v1
		Vector v2 = n.crossProduct(v1).normalize();

		// Return two orthonormal vectors that lie on the plane
		return List.of(v1, v2);
	}

}
