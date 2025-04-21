package primitives;
import static primitives.Util.isZero;

/**
 * This class represents a ray in 3D space, defined by a starting point (head)
 * and a direction vector.
 */
public class Ray {
	/** The starting point of the ray */
	private final Point head;

	/** The normalized direction vector of the ray */
	private final Vector direction;

	/**
	 * Constructs a ray with a given head point and direction vector. The direction
	 * vector is normalized upon creation.
	 *
	 * @param head      the starting point of the ray
	 * @param direction the direction vector of the ray
	 */
	public Ray(Point head, Vector direction) {
		this.head = head;
		this.direction = direction.normalize();
	}

	/**
	 * Returns the head (starting point) of the vector.
	 *
	 * @return the head point of the vector.
	 */
	public Point getHead() {
		return head;
	}

	/**
	 * Returns the direction of the vector as a unit vector.
	 *
	 * @return the direction of the vector.
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
	 * Returns a point on the ray at a given distance from the head.
	 *
	 * @param t the distance from the head along the direction vector.
	 *          If {@code t} is zero, the head point is returned.
	 * @return the point located at distance {@code t} from the head in the direction of the ray.
	 */
	public Point getPoint(double t) {
		return isZero(t) ? head : head.add(direction.scale(t));
	}

}
