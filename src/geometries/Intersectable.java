package geometries;

import static java.lang.Math.*;

import java.util.List;

import lighting.LightSource;
import primitives.*;

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
    * @param  ray the {@link Ray} to check for intersections
    * @return     a list of {@link Intersection} objects, or {@code null} if none
    *             found
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
      /** The geometry object that was intersected */
      public final Geometry geometry;
      /** The point at which the intersection occurred */
      public final Point    point;
      /** The material of the intersected geometry */
      public final Material material;

      /** Direction vector from the camera/viewer to the intersection point */
      public Vector         v;
      /** Surface normal at the point of intersection */
      public Vector         normal;
      /** Dot product of view vector and surface normal */
      public double         vNormal;

      /** The light source currently being processed in shading */
      public LightSource    light;
      /** Direction vector from to the light source to the intersection point */
      public Vector         l;
      /** Dot product of light vector and surface normal */
      public double         lNormal;
      /** Direction vector from the intersection point to the light source */
      public Vector         pointToLight;

      /**
       * Constructs an {@code Intersection} object with the given geometry and point.
       * @param geometry the intersected geometry
       * @param point    the point of intersection
       */
      public Intersection(Geometry geometry, Point point) {
         this.geometry = geometry;
         this.point    = point;
         this.material = geometry != null ? geometry.getMaterial() : null;
      }

      @Override
      public boolean equals(Object obj) {
         return this == obj || //
               (obj instanceof Intersection other && //
                     this.geometry == other.geometry && this.point.equals(other.point));
      }

      @Override
      public String toString() { return "Intersection [geometry=" + geometry + ", point=" + point + "]"; }
   }

   /** The bounding box for this geometry */
   protected AABB    box            = null;

   /**
    * Returns the bounding box of the geometry (if enabled).
    *
    * @return the bounding box, or null if not used
    */
   public AABB getBoundingBox() {
      return box == null ? box = computeBoundingBox() : box;
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
   public static record AABB(
         /** The minimum corner point of the bounding box */
         Point minCorner,
         /** The maximum corner point of the bounding box */
         Point maxCorner) {

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
         double[] minArr = { minCorner.getX(), minCorner.getY(), minCorner.getZ() };
         double[] maxArr = { maxCorner.getX(), maxCorner.getY(), maxCorner.getZ() };

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
         Point newMin = new Point(min(this.minCorner.getX(), other.minCorner.getX()),
               min(this.minCorner.getY(), other.minCorner.getY()),
               min(this.minCorner.getZ(), other.minCorner.getZ()));
         Point newMax = new Point(max(this.maxCorner.getX(), other.maxCorner.getX()),
               max(this.maxCorner.getY(), other.maxCorner.getY()),
               max(this.maxCorner.getZ(), other.maxCorner.getZ()));
         return new AABB(newMin, newMax);
      }

      /**
       * Expands the bounding box to include the given point. Updates the minimum and
       * maximum corners if necessary.
       *
       * @param p the point to include in the bounding box
       */
      public AABB expandToInclude(Point p) {
         double minX = min(this.minCorner.getX(), p.getX());
         double minY = min(this.minCorner.getY(), p.getY());
         double minZ = min(this.minCorner.getZ(), p.getZ());

         double maxX = max(this.maxCorner.getX(), p.getX());
         double maxY = max(this.maxCorner.getY(), p.getY());
         double maxZ = max(this.maxCorner.getZ(), p.getZ());

         return new AABB(new Point(minX, minY, minZ), new Point(maxX, maxY, maxZ));
      }

   }

}

