package geometries;

/**
 * The {@code RadialGeometry} class represents geometric objects that have a
 * radius, such as spheres, cylinders, and tubes. This is an abstract class that
 * serves as a base for all radial geometries.
 * <p>
 * It extends the {@link Geometry} class and introduces a radius property that
 * defines the size of the radial geometric shape.
 * </p>
 */
public abstract class RadialGeometry extends Geometry {

	/**
	 * The radius of the geometric shape. This value is immutable and represents the
	 * distance from the center to the boundary of the shape.
	 */
	protected final double radius;

	/**
	 * The squared radius of the geometric shape. This is a precomputed value
	 * (radius * radius) for optimization purposes in geometric calculations.
	 */
	protected final double squaredRadius;

	/**
	 * Constructs a {@code RadialGeometry} object with the specified radius.
	 *
	 * @param radius The radius of the geometric shape. It must be a non-negative
	 *               value.
	 * @throws IllegalArgumentException if the radius is negative.
	 */
	public RadialGeometry(double radius) {
		if (radius < 0) {
			throw new IllegalArgumentException("Radius cannot be negative");
		}
		this.radius = radius;
		this.squaredRadius = radius * radius;
	}

	/**
	 * Returns a string representation of the {@code RadialGeometry} object.
	 *
	 * @return A string containing the radius value.
	 */
	@Override
	public String toString() {
		return "RadialGeometry [radius=" + radius + "]";
	}
}
