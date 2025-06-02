package primitives;

import static primitives.Util.alignZero;

import java.util.List;

import geometries.Intersectable.Intersection;

/**
 * Represents a ray in 3D space, defined by a starting point (head) and a
 * normalized direction vector.
 */
public class Ray {
	/**
	 * The starting point of the ray.
	 */
	private final Point head;

	/**
	 * The normalized direction vector of the ray.
	 */
	private final Vector direction;

	/**
	 * A small constant value used to slightly offset the ray's starting point in
	 * the direction of the surface normal to prevent self-intersections caused by
	 * floating-point precision errors.
	 *
	 * <p>
	 * Commonly used in reflection and refraction rays to avoid "shadow acne" and
	 * other rendering artifacts.
	 * </p>
	 */
	private static final double DELTA = 0.1;

	/**
	 * Constructs a ray with the specified head point and direction vector. The
	 * direction vector is normalized upon construction.
	 *
	 * @param head      the starting point of the ray
	 * @param direction the direction vector of the ray (will be normalized)
	 */
	public Ray(Point head, Vector direction) {
		this.head = head;
		this.direction = direction.normalize();
	}

	/**
	 * Constructs a ray with a slight offset from the original head point in the
	 * direction of the normal vector {@code n}, to avoid self-intersection issues
	 * (commonly used in reflection/refraction).
	 *
	 * <p>
	 * If the dot product between {@code n} and {@code direction} is positive, the
	 * ray is shifted slightly along {@code n}, otherwise it is shifted in the
	 * opposite direction.
	 * </p>
	 *
	 * @param head      the original head point of the ray
	 * @param direction the direction vector of the ray
	 * @param n         the normal vector used to offset the ray head
	 */
	public Ray(Point head, Vector direction, Vector n) {
		double dotP = alignZero(n.dotProduct(direction));
		this.head = dotP == 0 ? head : head.add(n.scale((dotP) > 0 ? DELTA : -DELTA));
		this.direction = direction;
	}

	/**
	 * Returns the starting point (head) of the ray.
	 *
	 * @return the head point of the ray
	 */
	public Point getHead() {
		return head;
	}

	/**
	 * Returns the normalized direction vector of the ray.
	 *
	 * @return the direction vector of the ray
	 */
	public Vector getDirection() {
		return direction;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		return (obj instanceof Ray other) && this.head.equals(other.head) && this.direction.equals(other.direction);
	}

	@Override
	public String toString() {
		return "Ray [" + head + ", " + direction + "]";
	}

	/**
	 * Returns a point located at distance {@code t} from the ray's head in the
	 * direction of the ray.
	 * <p>
	 * If {@code t} is zero, returns the head point itself.
	 *
	 * @param t the distance from the head along the direction vector
	 * @return the point at distance {@code t} from the head along the ray direction
	 */
	public Point getPoint(double t) {
		try {
			return head.add(direction.scale(t));
		} catch (IllegalArgumentException e) {
			// In case of zero scaling or invalid operation, return head
			return head;
		}
	}

	/**
	 * Finds the point in the given list that is closest to the ray's head.
	 *
	 * @param points a list of points to search; may be {@code null}
	 * @return the closest point to the ray's head, or {@code null} if the list is
	 *         {@code null} or empty
	 */
	public Point findClosestPoint(List<Point> points) {
		return points == null ? null
				: findClosestIntersection(points.stream().map(p -> new Intersection(null, p)).toList()).point;
	}

	/**
	 * Finds the closest intersection from a list of intersections to the ray's
	 * head.
	 *
	 * @param intersections a list of {@link Intersection} objects; may be
	 *                      {@code null}
	 * @return the closest {@link Intersection} to the ray's head, or {@code null}
	 *         if the list is {@code null} or empty
	 */
	public Intersection findClosestIntersection(List<Intersection> intersections) {
		if (intersections == null)
			return null;

		Intersection closestIntersection = null;
		double minDistance = Double.POSITIVE_INFINITY;
		for (Intersection intersection : intersections) {
			double distance = head.distanceSquared(intersection.point);
			if (distance < minDistance) {
				minDistance = distance;
				closestIntersection = intersection;
			}
		}
		return closestIntersection;
	}

}
