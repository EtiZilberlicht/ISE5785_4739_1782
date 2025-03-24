package geometries;

import primitives.*;

/**
 * The {@code Geometry} class is an abstract class that represents a geometric shape in 3D space.
 * Any geometric shape that extends this class must implement a method to calculate the normal vector at a given point.
 */
public abstract class Geometry {

    /**
     * Returns the normal vector to the geometric body at a given point on its surface.
     *
     * @param point The point on the geometric body.
     * @return The normal vector at the given point.
     */
    public abstract Vector getNormal(Point point); // also can return null for now
}

