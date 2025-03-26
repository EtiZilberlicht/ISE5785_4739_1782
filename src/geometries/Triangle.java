package geometries;

import primitives.Point;

/**
 * The {@code Triangle} class represents a triangle in 3D space.
 * It extends the {@code Polygon} class, inheriting its properties and behaviors.
 * A triangle is defined by three vertices in 3D space.
 *
 * <p>Since a triangle is a specific case of a polygon with exactly three vertices,
 * this class serves as a convenient abstraction over the general {@code Polygon} class.
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
}

