package geometries;

import primitives.*;

/**
 * The {@code Cylinder} class represents a three-dimensional cylinder
 * that extends a {@link Tube} with a finite height.
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
        super(radius, axis);
        this.height = height;
    }

    /**
     * Returns the normal vector at a given point on the cylinder's surface.
     * 
     * @param point The point on the cylinder's surface.
     * @return The normal vector to the surface at the given point.
     */
    @Override
    public Vector getNormal(Point point) {
        return null;
    }
}
