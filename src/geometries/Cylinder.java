package geometries;

import primitives.*;
import static primitives.Util.isZero;

import java.util.LinkedList;
import java.util.List;

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
		super(axis, radius);
		this.height = height;
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
		Point o = head.add(direction.scale(t));
		return point.subtract(o).normalize();
	}

	@Override
	public String toString() {
		return "Cylinder [" + height + ",  " + axis + ", " + radius + "]";
	}

	@Override
	public List<Point> findIntersections(Ray ray) {
		// Initialize intersections list
		List<Point> intersections = new LinkedList<>();

		// Find intersections with the infinite cylinder
		Tube tube = new Tube(axis, radius);
		List<Point> infiniteCylinderIntersections = tube.findIntersections(ray);
		if (infiniteCylinderIntersections != null) {
			intersections.addAll(infiniteCylinderIntersections);
		}

		intersections.removeIf(intersection -> {
			double t = axis.getDirection().dotProduct(intersection.subtract(axis.getPoint(0d)));
			return t <= 0d || t >= height;
		});

		// Define planes for the bottom and top bases
		Plane bottomBase = new Plane(axis.getPoint(0d), axis.getDirection());
		Plane topBase = new Plane(axis.getPoint(height), axis.getDirection());

		// Return intersections if there are exactly 2 (so they are on the sides of the
		// cylinder)
		if (intersections.size() == 2) {
			return List.of(intersections.get(0), intersections.get(1));
		}

		// Find intersections with the bottom base
		List<Point> bottomBaseIntersections = bottomBase.findIntersections(ray);
		if (bottomBaseIntersections != null) {
			Point intersection = bottomBaseIntersections.getFirst();
			if (axis.getPoint(0d).distanceSquared(intersection) <= radius * radius) {
				intersections.add(intersection);
			}
		}

		// Find intersections with the top base
		List<Point> topBaseIntersections = topBase.findIntersections(ray);
		if (topBaseIntersections != null) {
			Point intersection = topBaseIntersections.getFirst();
			if (axis.getPoint(height).distanceSquared(intersection) <= radius * radius) {
				intersections.add(intersection);
			}
		}

		// if the ray is tangent to the cylinder
		if (intersections.size() == 2 && axis.getHead().distanceSquared(intersections.get(0)) == radius * radius
				&& axis.getPoint(height).distanceSquared(intersections.get(1)) == radius * radius) {
			Vector v = intersections.get(1).subtract(intersections.get(0));
			if (v.normalize().equals(axis.getDirection()) || v.normalize().equals(axis.getDirection().scale(-1d)))
				return null;
		}

		// Return null if no valid intersections found
		List<Point> geoPoints = new LinkedList<>();
		for (Point p : intersections) {
			geoPoints.add(p);
		}

		return geoPoints.isEmpty() ? null : geoPoints;
	}

}
