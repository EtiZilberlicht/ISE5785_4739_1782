package geometries;

import static java.lang.Math.max;
import static java.lang.Math.min;

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

	/**
	 * Calculates the intersection points between the given {@link Ray} and the
	 * geometry, up to a specified maximum distance from the ray's origin.
	 * <p>
	 * This method delegates the actual computation to
	 * {@code calculateIntersectionsHelper}.
	 *
	 * @param ray         the ray to intersect with the geometry
	 * @param maxDistance the maximum distance from the ray's origin to consider
	 *                    intersections
	 * @return a list of {@link Intersection} objects representing the intersection
	 *         points, or {@code null} if there are no intersections within the
	 *         given distance
	 */
	public final List<Intersection> calculateIntersections(Ray ray, double maxDistance) {
		return calculateIntersectionsHelper(ray, maxDistance);
	}

	/**
	 * Calculates all intersection points between the given {@link Ray} and the
	 * geometry, up to a specified maximum distance.
	 *
	 * @param ray         the ray to intersect with the geometry
	 * @param maxDistance the maximum allowed distance for intersection points from
	 *                    the ray origin
	 * @return a list of {@link Intersection} objects representing the intersection
	 *         points, or {@code null} if there are no intersections within the
	 *         given distance
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

	/** Indicates whether to use the bounding box for this geometry */
	protected boolean useBoundingBox = false;

	/** The bounding box for this geometry */
	protected AABB box = null;

	/**
	 * Enables or disables the use of a bounding box for this geometry.
	 * 
	 * When enabled, if the bounding box has not yet been computed, it will be
	 * computed automatically.
	 * 
	 * @param enabled true to enable bounding box usage, false to disable it
	 * @return this geometry instance (for method chaining)
	 */
	public Intersectable setBoundingBoxEnabled(boolean enabled) {
		useBoundingBox = enabled;
		if (enabled && box == null) {
			box = computeBoundingBox();
		}
		return this;
	}

	/**
	 * Returns the bounding box of the geometry (if enabled).
	 *
	 * @return the bounding box, or null if not used
	 */
	public AABB getBoundingBox() {
		return useBoundingBox ? box : null;
	}

	/**
	 * Computes the bounding box for this geometry. Override in concrete classes.
	 *
	 * @return the computed bounding box
	 */
	protected abstract AABB computeBoundingBox();

	/**
	 * Represents an axis-aligned bounding box (AABB).
	 */
	public static class AABB {
		/** The minimum corner point of the bounding box */
		private final Point min;

		/** The maximum corner point of the bounding box */
		private final Point max;

		/**
		 * Constructs an AABB with the specified minimum and maximum points.
		 *
		 * @param min the minimum corner point (smallest x, y, z)
		 * @param max the maximum corner point (largest x, y, z)
		 */
		public AABB(Point min, Point max) {
			this.min = min;
			this.max = max;
		}

		/**
		 * Returns the minimum corner point of the bounding box.
		 *
		 * @return the min point
		 */
		public Point getMin() {
			return min;
		}

		/**
		 * Returns the maximum corner point of the bounding box.
		 *
		 * @return the max point
		 */
		public Point getMax() {
			return max;
		}

		/**
		 * Checks whether the given ray intersects this bounding box.
		 *
		 * @param ray the ray to test
		 * @return true if the ray intersects the box, false otherwise
		 */
		public boolean intersects(Ray ray) {
			Point origin = ray.getHead();
			Vector dir = ray.getDirection();

			double tMin = Double.NEGATIVE_INFINITY;
			double tMax = Double.POSITIVE_INFINITY;

			double[] originArr = { origin.getX(), origin.getY(), origin.getZ() };
			double[] dirArr = { dir.getX(), dir.getY(), dir.getZ() };
			double[] minArr = { min.getX(), min.getY(), min.getZ() };
			double[] maxArr = { max.getX(), max.getY(), max.getZ() };

			for (int i = 0; i < 3; i++) {
				if (dirArr[i] == 0) {
					if (originArr[i] < minArr[i] || originArr[i] > maxArr[i])
						return false;
				} else {
					double t1 = (minArr[i] - originArr[i]) / dirArr[i];
					double t2 = (maxArr[i] - originArr[i]) / dirArr[i];
					double tNear = min(t1, t2);
					double tFar = max(t1, t2);

					tMin = max(tMin, tNear);
					tMax = min(tMax, tFar);

					if (tMin > tMax)
						return false;
				}
			}
			return true;
		}

		/**
		 * Computes the union of this bounding box with another, returning a new AABB
		 * that fully contains both.
		 *
		 * @param other another bounding box to union with
		 * @return a new AABB that encloses both this and the other bounding box
		 */
		public AABB union(AABB other) {
			Point newMin = new Point(min(this.min.getX(), other.min.getX()), min(this.min.getY(), other.min.getY()),
					min(this.min.getZ(), other.min.getZ()));
			Point newMax = new Point(max(this.max.getX(), other.max.getX()), max(this.max.getY(), other.max.getY()),
					max(this.max.getZ(), other.max.getZ()));
			return new AABB(newMin, newMax);
		}
	}

}
