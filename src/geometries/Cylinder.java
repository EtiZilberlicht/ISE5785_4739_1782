package geometries;

import primitives.*;
import static primitives.Util.isZero;

/**
 * The {@code Cylinder} class represents a three-dimensional cylinder that
 * extends a {@link Tube} with a finite height.
 */
public class Cylinder extends Tube {

	/** The height of the cylinder. */
	private final double height;

	/**
	 * Constructs a cylinder with the given radius, axis, and height.
	 * 
	 * @param radius The radius of the cylinder.
	 * @param axis   The central axis of the cylinder represented by a {@link Ray}.
	 * @param height The height of the cylinder.
	 */
	public Cylinder(double radius, Ray axis, double height) {
		super(radius, axis);
		this.height = height;
	}

	@Override
	public Vector getNormal(Point point) {
		Point head = axis.getHead();
		Vector direction = axis.getDirection();
		if (point.equals(head)) // If the point is exactly at the base of the cylinder, normal is opposite to
								// the axis direction
			return direction.scale(-1);
		double t = direction.dotProduct(point.subtract(head));
		if (isZero(t)) // If the projection results in zero, the point is on the bottom base
			return direction.scale(-1);
		if (isZero(t - height)) // If the projection results in the cylinder's height, the point is on the top
								// base
			return direction;
		Point o = head.add(direction.scale(t));
		return point.subtract(o).normalize();
	}

	@Override
	public String toString() {
		return "Cylinder [" + height + ",  " + axis + ", " + radius + "]";
	}

}
