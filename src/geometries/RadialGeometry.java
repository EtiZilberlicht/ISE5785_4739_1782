package geometries;

/**
 * The {@code RadialGeometry} class represents geometric objects that have a radius,
 * such as spheres, cylinders, and tubes.
 * This is an abstract class that serves as a base for all radial geometries.
 */
public abstract class RadialGeometry extends Geometry {
    
    /** The radius of the geometric shape. */
    protected final double radius;

    /**
     * Constructs a radial geometry with the specified radius.
     *
     * @param radius The radius of the geometric shape.
     */
    public RadialGeometry(double radius) {
        this.radius = radius;
    }

	@Override
	public String toString() {
		return "RadialGeometry [radius=" + radius + "]";
	}
    
}

