package geometries;

import java.util.List;

import lighting.LightSource;
import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Abstract base class for geometric objects that can be intersected by a
 * {@link Ray}. Provides a unified interface for calculating intersection points
 * between rays and geometries.
 */
public abstract class Intersectable {

	/**
	 * Default constructor for Intersectable.
	 */
	public Intersectable() {
	}

	/**
	 * Finds all intersection points (excluding geometry information) between a
	 * given ray and this geometry.
	 *
	 * @param ray the {@link Ray} used to find intersections
	 * @return a list of {@link Point} objects representing the intersection points,
	 *         or {@code null} if no intersections are found
	 */
	public final List<Point> findIntersections(Ray ray) {
		var list = calculateIntersections(ray);
		return list == null ? null : list.stream().map(intersection -> intersection.point).toList();
	}

	/**
	 * Delegates intersection calculation to the subclass implementation.
	 *
	 * @param ray the {@link Ray} to check for intersections
	 * @return a list of {@link Intersection} objects, or {@code null} if none found
	 */
	public final List<Intersection> calculateIntersections(Ray ray) {
		return calculateIntersections(ray, Double.POSITIVE_INFINITY);
	}

	public final List<Intersection> calculateIntersections(Ray ray, double maxDistance) {
		return calculateIntersectionsHelper(ray, maxDistance);
	}

	/**
	 * Template method to be implemented by each specific geometry. Calculates full
	 * intersection data including geometry, point, and material.
	 *
	 * @param ray the {@link Ray} to test against the geometry
	 * @return a list of {@link Intersection} records, or {@code null} if none
	 */
	protected abstract List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance);

	/**
	 * Class representing a full intersection record between a ray and a geometry.
	 * Includes the intersected geometry, intersection point, and additional data
	 * used for shading (vectors and lighting information).
	 */
	public static class Intersection {

		/**
		 * The geometry object that was intersected.
		 */
		public final Geometry geometry;

		/**
		 * The point at which the intersection occurred.
		 */
		public final Point point;

		/**
		 * The material of the intersected geometry.
		 */
		public final Material material;

		/**
		 * Direction vector from the camera/viewer to the intersection point. Set during
		 * shading.
		 */
		public Vector v;

		/**
		 * Surface normal at the point of intersection. Set during shading.
		 */
		public Vector normal;

		/**
		 * Dot product of view vector and surface normal. Used for light angle
		 * calculations.
		 */
		public double vNormal;

		/**
		 * The light source currently being processed in shading.
		 */
		public LightSource light;

		/**
		 * Direction vector from the intersection point to the light source.
		 */
		public Vector l;

		/**
		 * Dot product of light vector and surface normal. Used in diffuse and specular
		 * calculations.
		 */
		public double lNormal;

		/**
		 * Constructs an {@code Intersection} object with the given geometry and point.
		 *
		 * @param geometry the intersected geometry
		 * @param point    the point of intersection
		 */
		public Intersection(Geometry geometry, Point point) {
			this.geometry = geometry;
			this.point = point;
			this.material = geometry != null ? geometry.getMaterial() : null;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			return (obj instanceof Intersection other) && this.geometry == other.geometry
					&& this.point.equals(other.point);
		}

		@Override
		public String toString() {
			return "Intersection [geometry=" + geometry + ", point=" + point + "]";
		}
	}
}
