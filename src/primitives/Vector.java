package primitives;

import java.util.List;

/**
 * This class represents a vector in 3D space. A vector has direction and
 * magnitude and is defined by its endpoint.
 */
public class Vector extends Point {
	/** A constant representing the axis x */
	public static final Vector AXIS_X = new Vector(1, 0, 0);
	/** A constant representing the axis y */
	public static final Vector AXIS_Y = new Vector(0, 1, 0);
	/** A constant representing the axis z */
	public static final Vector AXIS_Z = new Vector(0, 0, 1);

	/**
	 * Constructor that initializes a vector with given x, y, and z coordinates.
	 * 
	 * @param x The x coordinate of the vector.
	 * @param y The y coordinate of the vector.
	 * @param z The z coordinate of the vector.
	 * @throws IllegalArgumentException if the vector is a zero vector.
	 */
	public Vector(double x, double y, double z) {
		super(x, y, z);
		if (xyz.equals(Double3.ZERO))
			throw new IllegalArgumentException("Unable to create zero vector");
	}

	/**
	 * Constructor that initializes a vector with a given {@link Double3} object.
	 * 
	 * @param xyz The Double3 object representing the vector's coordinates.
	 * @throws IllegalArgumentException if the vector is a zero vector.
	 */
	public Vector(Double3 xyz) {
		super(xyz);
		if (xyz.equals(Double3.ZERO))
			throw new IllegalArgumentException("Unable to create zero vector");
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		return (obj instanceof Vector other) && this.xyz.equals(other.xyz);
	}

	@Override
	public String toString() {
		return "Vector [" + xyz + "]";
	}

	/**
	 * Adds another vector to this vector.
	 * 
	 * @param other The vector to be added.
	 * @return A new vector representing the sum.
	 */
	public Vector add(Vector other) {
		return new Vector(xyz.add(other.xyz));
	}

	/**
	 * Scales this vector by a scalar value.
	 * 
	 * @param scalar The scaling factor.
	 * @return A new vector that is scaled by the given factor.
	 */
	public Vector scale(double scalar) {
		return new Vector(xyz.scale(scalar));
	}

	/**
	 * Computes the dot product of this vector and another vector.
	 * 
	 * @param other The other vector.
	 * @return The dot product value.
	 */
	public double dotProduct(Vector other) {
		return (xyz.d1() * other.xyz.d1()) + (xyz.d2() * other.xyz.d2()) + (xyz.d3() * other.xyz.d3());
	}

	/**
	 * Computes the cross product of this vector and another vector.
	 * 
	 * @param other The other vector.
	 * @return A new vector representing the cross product.
	 */
	public Vector crossProduct(Vector other) {
		double xA = xyz.d1();
		double yA = xyz.d2();
		double zA = xyz.d3();
		double xB = other.xyz.d1();
		double yB = other.xyz.d2();
		double zB = other.xyz.d3();
		return new Vector(yA * zB - zA * yB, zA * xB - xA * zB, xA * yB - yA * xB);
	}

	/**
	 * Computes the squared length (magnitude) of the vector.
	 * 
	 * @return The squared length of the vector.
	 */
	public double lengthSquared() {
		return dotProduct(this);
	}

	/**
	 * Computes the length (magnitude) of the vector.
	 * 
	 * @return The length of the vector.
	 */
	public double length() {
		return Math.sqrt(lengthSquared());
	}

	/**
	 * Normalizes this vector to a unit vector (length = 1).
	 * 
	 * @return A new normalized vector.
	 */
	public Vector normalize() {
		double vectorLength = length();
		if (vectorLength == 1)
			return this;
		return new Vector(xyz.reduce(vectorLength));
	}

	/**
	 * Computes two orthonormal vectors that lie on the plane and are perpendicular
	 * to the normal. These vectors can be used to span the plane or generate
	 * coordinate systems.
	 *
	 * @return a {@link List} of two {@link Vector} instances (v1, v2) lying on the
	 *         plane
	 */
	public List<Vector> getVectors() {
		// Normalize the normal vector to ensure unit length
		Vector n = this.normalize();

		// Choose a helper vector that is not parallel to the normal
		// We select the axis where the normal has the smallest component
		Vector helper = (Math.abs(n.getX()) < Math.abs(n.getY()) && Math.abs(n.getX()) < Math.abs(n.getZ()))
				? new Vector(1, 0, 0) // x-axis
				: (Math.abs(n.getY()) < Math.abs(n.getZ()) ? new Vector(0, 1, 0) : new Vector(0, 0, 1)); // y or z-axis

		// First vector on the plane: perpendicular to both normal and helper
		Vector v1 = n.crossProduct(helper).normalize();

		// Second vector on the plane: perpendicular to both normal and v1
		Vector v2 = n.crossProduct(v1).normalize();

		// Return two orthonormal vectors that lie on the plane
		return List.of(v1, v2);
	}
}
