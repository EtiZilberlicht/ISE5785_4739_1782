package geometries;

import java.util.List;
import static primitives.Util.*;
import primitives.*;

/**
 * Polygon class represents two-dimensional polygon in 3D Cartesian coordinate
 * system
 * 
 * @author Dan
 */
public class Polygon extends Geometry {
	/** List of polygon's vertices */
	protected final List<Point> vertices;
	/** Associated plane in which the polygon lays */
	protected final Plane plane;
	/** The size of the polygon - the amount of the vertices in the polygon */
	private final int size;

	/**
	 * Polygon constructor based on vertices list. The list must be ordered by edge
	 * path. The polygon must be convex.
	 * 
	 * @param vertices list of vertices according to their order by edge path
	 * @throws IllegalArgumentException in any case of illegal combination of
	 *                                  vertices:
	 *                                  <ul>
	 *                                  <li>Less than 3 vertices</li>
	 *                                  <li>Consequent vertices are in the same
	 *                                  point
	 *                                  <li>The vertices are not in the same
	 *                                  plane</li>
	 *                                  <li>The order of vertices is not according
	 *                                  to edge path</li>
	 *                                  <li>Three consequent vertices lay in the
	 *                                  same line (180&#176; angle between two
	 *                                  consequent edges)
	 *                                  <li>The polygon is concave (not convex)</li>
	 *                                  </ul>
	 */
	public Polygon(Point... vertices) {
		if (vertices.length < 3)
			throw new IllegalArgumentException("A polygon can't have less than 3 vertices");
		this.vertices = List.of(vertices);
		size = vertices.length;

		// Generate the plane according to the first three vertices and associate the
		// polygon with this plane.
		// The plane holds the invariant normal (orthogonal unit) vector to the polygon
		plane = new Plane(vertices[0], vertices[1], vertices[2]);
		if (size == 3)
			return; // no need for more tests for a Triangle

		Vector n = plane.getNormal(vertices[0]);
		// Subtracting any subsequent points will throw an IllegalArgumentException
		// because of Zero Vector if they are in the same point
		Vector edge1 = vertices[size - 1].subtract(vertices[size - 2]);
		Vector edge2 = vertices[0].subtract(vertices[size - 1]);

		// Cross Product of any subsequent edges will throw an IllegalArgumentException
		// because of Zero Vector if they connect three vertices that lay in the same
		// line.
		// Generate the direction of the polygon according to the angle between last and
		// first edge being less than 180deg. It is hold by the sign of its dot product
		// with the normal. If all the rest consequent edges will generate the same sign
		// - the polygon is convex ("kamur" in Hebrew).
		boolean positive = edge1.crossProduct(edge2).dotProduct(n) > 0;
		for (var i = 1; i < size; ++i) {
			// Test that the point is in the same plane as calculated originally
			if (!isZero(vertices[i].subtract(vertices[0]).dotProduct(n)))
				throw new IllegalArgumentException("All vertices of a polygon must lay in the same plane");
			// Test the consequent edges have
			edge1 = edge2;
			edge2 = vertices[i].subtract(vertices[i - 1]);
			if (positive != (edge1.crossProduct(edge2).dotProduct(n) > 0))
				throw new IllegalArgumentException("All vertices must be ordered and the polygon must be convex");
		}
	}

	@Override
	public Vector getNormal(Point point) {
		return plane.getNormal(point);
	}

	@Override
	public List<Point> findIntersections(Ray ray) {
		// Step 1: Find intersection with the polygon's plane
		Plane plane = new Plane(vertices.get(0), vertices.get(1), vertices.get(2));
		List<Point> planeIntersections = plane.findIntersections(ray);
		if (planeIntersections == null) {
			return null;
		}

		Point p = planeIntersections.get(0); // Only one intersection point with a plane
		Vector n = plane.getNormal(Point.ZERO); // Normal to the polygon's plane

		// Step 2: Check if point is strictly inside the polygon (assuming convex
		// polygon)
		int size = vertices.size();
		for (int i = 0; i < size; i++) {
			Point v1 = vertices.get(i);
			Point v2 = vertices.get((i + 1) % size);

			Vector edge;
			Vector vp;

			try {
				edge = v2.subtract(v1);
				vp = p.subtract(v1);
			} catch (IllegalArgumentException e) {
				// p == v1 or v2 ⇒ point is on vertex ⇒ exclude
				return null;
			}

			Vector cross;
			try {
				cross = edge.crossProduct(vp);
			} catch (IllegalArgumentException e) {
				// vp parallel or identical to edge ⇒ point is on edge ⇒ exclude
				return null;
			}

			double sign = alignZero(n.dotProduct(cross));
			if (sign <= 0) {
				return null; // Outside or on edge
			}
		}

		return List.of(p); // Point is strictly inside the polygon
	}
}
