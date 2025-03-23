package geometries;

import primitives.*;

/**
 * The {@code Plane} class represents a two-dimensional plane in three-dimensional space.
 * A plane can be defined either by a point and a normal vector, or by three non-collinear points.
 */
public class Plane extends Geometry {

    /** A point on the plane. */
    private final Point point;

    /** The normal vector to the plane. */
    private final Vector normal;

    /**
     * Constructs a plane given a point on the plane and a normal vector.
     * 
     * @param point  A point on the plane.
     * @param normal The normal vector to the plane. It is automatically normalized.
     */
    public Plane(Point point, Vector normal) {
        this.point = point;
        this.normal = normal.normalize();
    }

    /**
     * Constructs a plane given three non-collinear points in space.
     * 
     * @param x The first point defining the plane.
     * @param y The second point defining the plane.
     * @param z The third point defining the plane.
     */
    public Plane(Point x, Point y, Point z) {
        this.point = x;
        this.normal = null; // TODO: Compute the normal using cross product of two vectors from the points
    }

    /**
     * Returns the normal vector to the plane at a given point.
     * 
     * @param point A point on the plane (not used in this implementation).
     * @return The normal vector to the plane.
     */
    @Override
    public Vector getNormal(Point point) {
        return null; // TODO: Implement the correct normal calculation
    }
}

