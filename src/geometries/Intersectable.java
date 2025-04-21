package geometries;

import primitives.*;
import java.util.List;

/**
 * Interface for geometric objects that can be intersected by a ray. Any class
 * implementing this interface must provide a method to compute intersection
 * points with a given ray.
 */
public interface Intersectable {

	/**
	 * Computes the intersection points between this geometric object and the given
	 * ray.
	 *
	 * @param ray the {@link Ray} used to find intersections with the object
	 * @return a list of {@link Point} objects representing the intersection points,
	 *         or {@code null} if there are no intersections
	 */
	List<Point> findIntersections(Ray ray);
}
