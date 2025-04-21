package geometries;

import java.util.List;

import primitives.*;
import static primitives.Util.isZero;


/**
 * The {@code Triangle} class represents a triangle in 3D space. It extends the
 * {@code Polygon} class, inheriting its properties and behaviors. A triangle is
 * defined by three vertices in 3D space.
 *
 * <p>
 * Since a triangle is a specific case of a polygon with exactly three vertices,
 * this class serves as a convenient abstraction over the general
 * {@code Polygon} class.
 *
 * @author [Your Name]
 */
public class Triangle extends Polygon {

	/**
	 * Constructs a triangle with three given vertices.
	 *
	 * @param vertex1 the first vertex of the triangle
	 * @param vertex2 the second vertex of the triangle
	 * @param vertex3 the third vertex of the triangle
	 */
	public Triangle(Point vertex1, Point vertex2, Point vertex3) {
		super(vertex1, vertex2, vertex3);
	}

	@Override
	public List<Point> findIntersections(Ray ray) {
        Point p1 = vertices.get(0);
        Point p2 = vertices.get(1);
        Point p3 = vertices.get(2);
        
        final double EPSILON = 1e-10;

        Vector edge1 = p2.subtract(p1);
        Vector edge2 = p3.subtract(p1);
        Vector h = ray.getDirection().crossProduct(edge2);
        double a = edge1.dotProduct(h);

        if (isZero(a) || Math.abs(a) < EPSILON) {
            return null; // Ray is parallel to the triangle
        }

        double f = 1.0 / a;
        Vector s = ray.getHead().subtract(p1);
        double u = f * s.dotProduct(h);

        if (u <= EPSILON || u >= 1.0 - EPSILON) {
            return null; // Point is outside or on edge
        }

        Vector q = s.crossProduct(edge1);
        double v = f * ray.getDirection().dotProduct(q);

        if (v <= EPSILON || v >= 1.0 - EPSILON) {
            return null; // Point is outside or on edge
        }

        if (u + v >= 1.0 - EPSILON) {
            return null; // Point is on or outside the third edge
        }

        double t = f * edge2.dotProduct(q);
        if (t > EPSILON) {
            Point intersection = ray.getPoint(t);
            return List.of(intersection);
        }

        return null; // No intersection
	}
}
