package primitives;

/**
 * This class represents a point in 3D space using the {@link Double3} class.
 */
public class Point {
    /** The coordinates of the point */
    protected final Double3 xyz;
    
    /** A constant representing the origin point (0,0,0) */
    public static final Point ZERO = new Point(0, 0, 0);
    
    /**
     * Constructs a point with the specified coordinates.
     * 
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     */
    public Point(double x, double y, double z) {
        this.xyz = new Double3(x, y, z);
    }
    
    /**
     * Constructs a point using a {@link Double3} instance.
     * 
     * @param xyz the {@link Double3} instance representing the coordinates
     */
    public Point(Double3 xyz) {
        this.xyz = xyz;
    }
    
    /**
     * Checks if this point is equal to another object.
     * 
     * @param obj the object to compare to
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Point other) && this.xyz.equals(other.xyz);
    }
    
    /**
     * Returns a string representation of the point.
     * 
     * @return a string representing the point
     */
    @Override
    public String toString() {
        return "Point [xyz=" + xyz.toString() + "]";
    }
    
    /**
     * Subtracts another point from this point and returns the resulting vector.
     * 
     * @param other the point to subtract
     * @return a {@link Vector} representing the difference
     */
    public Vector subtract(Point other) {
        return new Vector(xyz.subtract(other.xyz));
    }
    
    /**
     * Adds another point to this point and returns the resulting point.
     * 
     * @param other the point to add
     * @return a new {@link Point} representing the sum
     */
    public Point add(Point other) {
        return new Point(xyz.add(other.xyz));
    }
    
    /**
     * Calculates the squared distance between this point and another point.
     * 
     * @param other the other point
     * @return the squared distance between the two points
     */
    public double distanceSquared(Point other) {
        double dx = xyz.d1() - other.xyz.d1();
        double dy = xyz.d2() - other.xyz.d2();
        double dz = xyz.d3() - other.xyz.d3();
        return (dx * dx) + (dy * dy) + (dz * dz);
    }
    
    /**
     * Calculates the distance between this point and another point.
     * 
     * @param other the other point
     * @return the Euclidean distance between the two points
     */
    public double distance(Point other) {
        return Math.sqrt(distanceSquared(other));
    }
}
