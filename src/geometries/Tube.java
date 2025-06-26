package geometries;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

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
		Point o = isZero(t) ? head : axis.getPoint(t);
		return point.subtract(o).normalize();
	}

	@Override
	public String toString() {
		return "Tube [axis=" + axis + "]";
	}

	@Override
	protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {
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
			d = ray.getPoint(t1);

			// The closest point on (B + t2b)
			double t2 = (ab * ac - bc * aa) / (/* aa * bb */ 1 - ab * ab);
			Point e;
			e = axis.getPoint(t2);

			// distance between two rays
			dis = d.distance(e);

		} catch (IllegalArgumentException ex) {
			// If A and B are the same
			d = pointA;
			dis = 0;
		}

		// The ray doesn't touch the Tube or it is tangent to the Tube
		if (alignZero(dis - radius) >= 0d)
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
		double th = Math.sqrt(squaredRadius - dis * dis) * k;

		// the two points
		Point p1 = d.subtract(vectorA.scale(th));
		Point p2 = d.add(vectorA.scale(th));

		double t1 = alignZero(p1.distance(pointA));
		double t2 = alignZero(p2.distance(pointA));

		try {
			// the ray starts before point 1
			if (!(alignZero(p1.subtract(pointA).dotProduct(vectorA)) < 0d)
					&& !(p2.subtract(pointA).dotProduct(vectorA) < 0d) && alignZero(t1 - maxDistance) <= 0
					&& alignZero(t2 - maxDistance) <= 0)
				return List.of(new Intersection(this, p1), new Intersection(this, p2));
		} catch (IllegalArgumentException ignore) {
			// the ray starts at point1
		}

		try {
			// the ray starts before point 1
			if (!(p1.subtract(pointA).dotProduct(vectorA) < 0d) && alignZero(t1 - maxDistance) <= 0)
				return List.of(new Intersection(this, p1));
		} catch (IllegalArgumentException ignore) {
			// the ray starts at point1
		}

		try {
			// the ray starts before point 2
			if (!(p2.subtract(pointA).dotProduct(vectorA) < 0d) && alignZero(t2 - maxDistance) <= 0)
				return List.of(new Intersection(this, p2));
		} catch (IllegalArgumentException ignore) {
			// the ray starts at point2
		}

		return null;
	}

	@Override
	protected AABB computeBoundingBox() {
		return null; // Infinite shape – has no bounding box
	}

}
