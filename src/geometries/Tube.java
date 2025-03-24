package geometries;

import primitives.*;

/**
 * The {@code Tube} class represents an infinite cylindrical tube in 3D space.
 * It extends {@code RadialGeometry} and is defined by a central axis and a radius.
 */
public class Tube extends RadialGeometry {

    /** The central axis of the tube represented as a {@code Ray}. */
    protected final Ray axis;

    /**
     * Constructs a tube with the specified radius and central axis.
     * 
     * @param radius the radius of the tube
     * @param axis the central axis of the tube
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        this.axis = axis;
    }


    @Override
    public Vector getNormal(Point point) {
        return null;
    }


	@Override
	public String toString() {
		return "Tube [axis=" + axis + "]";
	}
    
    
}

