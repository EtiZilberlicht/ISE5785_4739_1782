package primitives;

/**
 * This class represents a ray in 3D space, defined by a starting point (head) and a direction vector.
 */
public class Ray {
    /** The starting point of the ray */
    private final Point head;
    
    /** The normalized direction vector of the ray */
    private final Vector direction;
    
    /**
     * Constructs a ray with a given head point and direction vector.
     * The direction vector is normalized upon creation.
     *
     * @param head the starting point of the ray
     * @param direction the direction vector of the ray
     */
    public Ray(Point head, Vector direction) {
        this.head = head;
        this.direction = direction.normalize();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof Ray other)
                && this.head.equals(other.head)
                && this.direction.equals(other.direction);
    }
    
    @Override
    public String toString() {
        return "Ray [head=" + head + ", direction=" + direction + "]";
    }
}

