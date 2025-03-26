package primitives;

/**
 * This class represents a vector in 3D space. A vector has direction and
 * magnitude and is defined by its endpoint.
 */
public class Vector extends Point {

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
}
