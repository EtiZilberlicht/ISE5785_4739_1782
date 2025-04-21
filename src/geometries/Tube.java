package geometries;

import primitives.*;
import static primitives.Util.*;
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
	public List<Point> findIntersections(Ray ray) {
		// Given ray (A + ta) and this Tube ray (B + tb)
		Point pointA = ray.getHead();
		Point pointB = axis.getHead();
		Vector vectorA = ray.getDirection();
		Vector vectorB = axis.getDirection();

		double ab = vectorA.dotProduct(vectorB);
		Point d;
		double dis;
		// if is parallel to tube
		try {
			vectorA.crossProduct(vectorB);
		} catch (IllegalArgumentException ex) {
			return null;
		}

		double bb = 1; // it is a unit vector therefore it's squared size is 1
		double aa = 1;
		try {
			// Vector AB
			Vector c = pointB.subtract(pointA);
			// dot-product calc
			double bc = vectorB.dotProduct(c);
			double ac = vectorA.dotProduct(c);

			// The closest point on (A + t1a)
			double t1 = (-ab * bc + ac * bb) / (aa * bb - ab * ab);
			try {
				d = pointA.add(vectorA.scale(t1));
			} catch (IllegalArgumentException ex) {
				d = pointA;
			}

			// The closest point on (B + t2b)
			double t2 = (ab * ac - bc * aa) / (/* aa * bb */ 1 - ab * ab);
			Point e;
			try {
				e = pointB.add(vectorB.scale(t2));
			} catch (IllegalArgumentException ex) {
				e = pointB;
			}

			// distance between two rays
			dis = d.distance(e);

		} catch (IllegalArgumentException ex) {
			// If A and B are the same
			d = ray.getHead();
			dis = 0;
		}

		double diff = alignZero(dis - radius);
		// The ray doesn't touch the Tube or it is tangent to the Tube
		if (diff >= 0.0)
			return null;

		/*
		 * We know that the ray goes through the tube. Lets cut the tube parallel to the
		 * ray. We will get a ellipse where the height is radius. We need to calculate
		 * the width
		 */
		double width;
		// Whether the ray is orthogonal to the tube?
		try {
			// sin's between (B + tb) and (A + ta) is |VxU|
			double sinA = vectorA.crossProduct(vectorB).length();
			// ellipse width
			width = radius / sinA;
		} catch (IllegalArgumentException ex) { // it is orthogonal
			width = radius;
		}
		// ellipse equation x^2/k^2 + y^2 = radius^2
		// if the width is w then k is w/r
		double k = width / radius;
		// y is d for our ray x^2/k^2 + k^2 = radius^2 => x^2/k^2 = radius^2 -d^2 =>
		// x^2 = (radius^2 -d^2)*k^2 => x = sqrt(radius^2 -d^2)*k
		double th = Math.sqrt(radius * radius - dis * dis) * k;

		// the two points
		Point p1 = d.subtract(vectorA.scale(th));
		Point p2 = d.add(vectorA.scale(th));

		// Check if the points are in range and return them

		try {
			// the ray starts before point 1
			if (!(alignZero(p1.subtract(pointA).dotProduct(vectorA)) < 0.0))
				return List.of(p1, p2);
		} catch (IllegalArgumentException ignore) {
			// the ray starts at point1
		}

		try {
			// the ray starts before point 1
			if (!(p1.subtract(pointA).dotProduct(vectorA) < 0.0))
				return List.of(p1);
		} catch (IllegalArgumentException ignore) {
			// the ray starts at point1
		}

		try {
			// the ray starts before point 2
			if (!(p2.subtract(pointA).dotProduct(vectorA) < 0.0))
				return List.of(p2);
		} catch (IllegalArgumentException ignore) {
			// the ray starts at point2
		}

		return null;
	}

}
