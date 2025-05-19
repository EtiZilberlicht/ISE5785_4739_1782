package geometries;

import static primitives.Util.isZero;

import java.util.Collections;
//import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * The {@code Cylinder} class represents a three-dimensional cylinder that
 * extends a {@link Tube} with a finite height.
 */
public class Cylinder extends Tube {

	/** The height of the cylinder. */
	private final double height;
	/** The plain of the bottom. */
	private final Plane bottom;
	/** The plain of the top. */
	private final Plane top;

	/**
	 * Constructs a cylinder with the given radius, axis, and height.
	 * 
	 * @param radius The radius of the cylinder.
	 * @param axis   The central axis of the cylinder represented by a {@link Ray}.
	 * @param height The height of the cylinder.
	 */
	public Cylinder(double radius, Ray axis, double height) {
		super(axis, radius);
		this.height = height;
		bottom = new Plane(axis.getPoint(0d), axis.getDirection());
		top = new Plane(axis.getPoint(height), axis.getDirection());

	}

	@Override
	public Vector getNormal(Point point) {
		Point head = axis.getHead();
		Vector direction = axis.getDirection();
		double t = 0;
		try {
			t = direction.dotProduct(point.subtract(head));// If the point is exactly at the base of the cylinder,
															// normal is opposite to
															// the axis direction
		} catch (IllegalArgumentException exception) {
			return direction.scale(-1);
		}
		if (isZero(t)) // If the projection results in zero, the point is on the bottom base
			return direction.scale(-1);
		if (isZero(t - height)) // If the projection results in the cylinder's height, the point is on the top
								// base
			return direction;
		Point o = axis.getPoint(t);
		return point.subtract(o).normalize();
	}

	@Override
	public String toString() {
		return "Cylinder [" + height + ",  " + axis + ", " + radius + "]";
	}

	@Override
	protected List<Intersection> calculateIntersectionsHelper(Ray ray) {
		// Initialize intersections list
		List<Point> intersections = new LinkedList<>();

		// Find intersections with the infinite cylinder
		Tube tube = new Tube(axis, radius);
		List<Point> infiniteCylinderIntersections = tube.findIntersections(ray);
		if (infiniteCylinderIntersections != null) {
			intersections.addAll(infiniteCylinderIntersections);
		}

		intersections.removeIf(intersection -> {
			double t = axis.getDirection().dotProduct(intersection.subtract(axis.getHead()));
			return t <= 0d || t >= height;
		});

		// Return intersections if there are exactly 2 (so they are on the sides of the
		// cylinder)
		if (intersections.size() == 2) {
			return List.of(new Intersection(this, intersections.get(0)), new Intersection(this, intersections.get(1)));
		}

		// Find intersections with the bottom base
		List<Point> bottomIntersections = bottom.findIntersections(ray);
		if (bottomIntersections != null) {
			Point intersection = bottomIntersections.get(0);
			if (axis.getHead().distanceSquared(intersection) <= squaredRadius) {
				intersections.add(intersection);
			}
		}

		// Find intersections with the top base
		List<Point> topIntersections = top.findIntersections(ray);
		if (topIntersections != null) {
			Point intersection = topIntersections.getFirst();
			if (axis.getPoint(height).distanceSquared(intersection) <= squaredRadius) {
				intersections.add(intersection);
			}
		}

		// if the ray is tangent to the cylinder
		if (intersections.size() == 2 && axis.getHead().distanceSquared(intersections.get(0)) == squaredRadius
				&& axis.getPoint(height).distanceSquared(intersections.get(1)) == squaredRadius) {
			Vector v = intersections.get(1).subtract(intersections.get(0));
			if (v.normalize().equals(axis.getDirection()) || v.normalize().equals(axis.getDirection().scale(-1d)))
				return null;
		}
		// two options for swap
		// intersections.sort(Comparator.comparingDouble(p ->
		// p.distance(ray.getHead())));

		if (intersections.size() == 2
				&& intersections.get(0).distance(ray.getHead()) > intersections.get(1).distance(ray.getHead()))
			Collections.swap(intersections, 0, 1);

		// Return null if no valid intersections found
		List<Intersection> geoPoints = new LinkedList<>();
		for (Point p : intersections) {
			geoPoints.add(new Intersection(this, p));
		}

		return geoPoints.isEmpty() ? null : geoPoints;
	}

}
