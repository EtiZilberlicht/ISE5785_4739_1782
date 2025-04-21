package geometries;

import primitives.*;
import static primitives.Util.isZero;

import java.util.List;

/**
 * The {@code Tube} class represents an infinite cylindrical tube in 3D space.
 * It extends {@code RadialGeometry} and is defined by a central axis and a
 * radius.
 */
public class Tube extends RadialGeometry {

	/** The central axis of the tube represented as a {@code Ray}. */
	protected final Ray axis;

	/**
	 * Constructs a tube with the specified radius and central axis.
	 * 
	 * @param radius the radius of the tube
	 * @param axis   the central axis of the tube
	 */
	public Tube(Ray axis, double radius) {
		super(radius);
		this.axis = axis;
	}

	@Override
	public Vector getNormal(Point point) {
		Point head = axis.getHead();
		Vector direction = axis.getDirection();
		double t = direction.dotProduct(point.subtract(head));
		Point o = isZero(t) ? head : head.add(direction.scale(t));
		return point.subtract(o).normalize();
	}

	@Override
	public String toString() {
		return "Tube [axis=" + axis + "]";
	}
	
	@Override
	public List<Point> findIntersections(Ray ray)
	{
		return null;
	}

}
