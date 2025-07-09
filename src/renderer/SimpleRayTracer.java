package renderer;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

import java.util.List;

import geometries.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

/**
 * A simple ray tracer implementation that calculates the color of pixels by
 * tracing rays into the scene. It accounts for ambient light, diffuse, and
 * specular lighting on the closest intersection point.
 * <p>
 * This implementation does not handle advanced global effects such as shadows,
 * reflections, or transparency beyond a basic local illumination model. It is
 * suitable for rendering simple scenes quickly.
 */
public class SimpleRayTracer extends RayTracerBase {

	/**
	 * Maximum recursion level for color calculation to avoid infinite recursion.
	 */
	protected static final int MAX_CALC_COLOR_LEVEL = 10;

	/**
	 * Minimum light contribution factor below which the calculation stops to save
	 * computation.
	 */
	protected static final double MIN_CALC_COLOR_K = 0.001;

	/**
	 * Initial attenuation factor for color calculations.
	 */
	protected static final Double3 INITIAL_K = Double3.ONE;

	/**
	 * Constructs a SimpleRayTracer for the given scene.
	 *
	 * @param scene the scene to be rendered by this ray tracer
	 */
	public SimpleRayTracer(Scene scene) {
		super(scene);
	}

	/**
	 * Traces a ray into the scene, finds the closest intersection, and calculates
	 * the resulting color. Returns the scene's background color if no intersections
	 * are found.
	 *
	 * @param ray the ray to be traced into the scene
	 * @return the color computed from tracing the ray
	 */
	@Override
	public Color traceRay(Ray ray) {
		var point = findClosestIntersection(ray);
		return point == null ? scene.background : calcColor(point, ray);
	}

	/**
	 * Recursively calculates the color at an intersection point including local and
	 * global effects (reflection and refraction) up to a maximum recursion level,
	 * and considering attenuation factors.
	 *
	 * @param intersection the closest intersection point to calculate color for
	 * @param level        the current recursion depth
	 * @param k            the attenuation factor for global effects
	 * @return the calculated color at the intersection point
	 */
	private Color calcColor(Intersection intersection, int level, Double3 k) {
		Color color = calcColorLocalEffects(intersection, k);
		return 1 == level ? color : color.add(calcGlobalEffects(intersection, level, k));
	}

	/**
	 * Calculates the color at an intersection point including ambient light and
	 * local lighting effects.
	 *
	 * @param intersection the intersection point
	 * @param ray          the ray that caused the intersection
	 * @return the resulting color at the intersection point
	 */
	private Color calcColor(Intersection intersection, Ray ray) {
		return !preprocessIntersection(intersection, ray.getDirection()) ? Color.BLACK
				: scene.ambientLight.getIntensity().scale(intersection.material.kA)
						.add(calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K));
	}

	/**
	 * Prepares data needed for lighting calculations at the intersection point,
	 * including the surface normal and the dot product between the ray direction
	 * and the normal. Returns whether the intersection is valid for lighting.
	 *
	 * @param intersection the intersection to prepare
	 * @param v            the direction vector of the incoming ray
	 * @return true if the intersection is valid for lighting calculations, false
	 *         otherwise
	 */
	private boolean preprocessIntersection(Intersection intersection, Vector v) {
		intersection.v = v;
		intersection.normal = intersection.geometry.getNormal(intersection.point);
		intersection.vNormal = intersection.v.dotProduct(intersection.normal);
		return !isZero(intersection.vNormal);
	}

	/**
	 * Sets the light source and direction information on the given intersection,
	 * and calculates the dot product between the light direction and the surface
	 * normal.
	 *
	 * @param intersection the {@link Intersection} object to update
	 * @param light        the {@link LightSource} illuminating the intersection
	 * @param l            the direction {@link Vector} from the light source to the
	 *                     intersection point
	 * @return {@code true} if the dot product of the light direction and the
	 *         surface normal has the same sign as the dot product of the view
	 *         vector and the surface normal, indicating the light is on the visible
	 *         side of the surface; {@code false} otherwise
	 */
	private boolean setLightSource(Intersection intersection, LightSource light, Vector l) {
		intersection.light = light;
		intersection.l = l;
		intersection.lNormal = intersection.l.dotProduct(intersection.normal);
		return alignZero(intersection.lNormal) * intersection.vNormal > 0;
	}

	/**
	 * Calculates the local lighting color at the intersection point due to all
	 * light sources in the scene, including diffuse and specular components,
	 * attenuated by transparency.
	 *
	 * @param intersection the intersection with geometry and lighting data
	 * @param k            attenuation factor to scale light intensity
	 * @return the resulting color from local lighting effects
	 */

	Color calcColorLocalEffects(Intersection intersection, Double3 k) {

		Color color = intersection.geometry.getEmission();

		for (LightSource lightSource : scene.lights) {
			List<Vector> vectors = lightSource.getLBeam(intersection.point);
			if (vectors.size() == 1) {
				Vector l = vectors.getFirst();
				if (!setLightSource(intersection, lightSource, l))
					continue;
				Double3 ktr = transparency(intersection);
				if (!ktr.product(k).lowerThan(MIN_CALC_COLOR_K)) {
					Color iL = lightSource.getIntensity(intersection.point).scale(ktr);
					color = color.add(iL.scale(calcDiffusive(intersection).add(calcSpecular(intersection))));
				}
			} else {
				Color tempColor = Color.BLACK;
				for (Vector l : vectors) {
					if (!setLightSource(intersection, lightSource, l))
						continue;
					Double3 ktr = transparency(intersection);
					if (!ktr.product(k).lowerThan(MIN_CALC_COLOR_K)) {
						Color iL = lightSource.getIntensity(intersection.point).scale(ktr);
						tempColor = tempColor
								.add(iL.scale(calcDiffusive(intersection).add(calcSpecular(intersection))));
					}
				}
				int reduceBy = vectors.size();
				color = color.add(tempColor.reduce(reduceBy));
			}
		}
		return color;

	}

	/**
	 * Calculates the specular reflection component intensity at the intersection.
	 *
	 * @param intersection the intersection with lighting data
	 * @return the specular reflection intensity as a Double3 scaling factor
	 */
	Double3 calcSpecular(Intersection intersection) {
		Vector r = intersection.l.subtract(intersection.normal.scale(2 * intersection.lNormal)); // reflected vector
		double vr = alignZero(intersection.v.dotProduct(r));
		return vr >= 0 ? Double3.ZERO : intersection.material.kS.scale(Math.pow(-vr, intersection.material.nSH));
	}

	/**
	 * Calculates the diffuse reflection component intensity at the intersection.
	 *
	 * @param intersection the intersection with lighting data
	 * @return the diffuse reflection intensity as a Double3 scaling factor
	 */
	Double3 calcDiffusive(Intersection intersection) {
		double nl = intersection.lNormal;
		return intersection.material.kD.scale(nl < 0 ? -nl : nl);
	}

	/**
	 * Determines whether the intersection point is unshaded by checking if any
	 * geometry obstructs the light source (i.e., shadow ray test).
	 *
	 * @param intersection the intersection to test for shading
	 * @return true if no occluding geometry blocks the light, false otherwise
	 */
	@SuppressWarnings("unused")
	boolean unshaded(Intersection intersection) {
		Vector pointToLight = intersection.l.scale(-1);
		Ray shadowRay = new Ray(intersection.point, pointToLight, intersection.normal);
		var intersections = scene.geometries.calculateIntersections(shadowRay,
				intersection.light.getDistance(intersection.point));

		if (intersections == null)
			return true;

		for (Intersection i : intersections)
			if (i.material.kT.lowerThan(MIN_CALC_COLOR_K))
				return false;

		return true;
	}

	/**
	 * Constructs the reflected ray based on the intersection's normal and incoming
	 * ray.
	 *
	 * @param intersection the intersection point with normal and incoming vector
	 * @return the reflected ray starting at the intersection point
	 */
	private Ray constructReflectedRay(Intersection intersection) {
		return new Ray(intersection.point, intersection.v.subtract(intersection.normal.scale(2 * intersection.vNormal)),
				intersection.normal);
	}

	/**
	 * Constructs the refracted ray (transmitted ray) continuing in the same
	 * direction.
	 *
	 * @param intersection the intersection point with normal and incoming vector
	 * @return the refracted ray starting at the intersection point
	 */
	private Ray constructRefractedRay(Intersection intersection) {
		return new Ray(intersection.point, intersection.v, intersection.normal);
	}

	/**
	 * Calculates the contribution of global effects such as reflection and
	 * refraction recursively, attenuated by given factors.
	 *
	 * @param ray   the ray representing the reflection or refraction
	 * @param level current recursion depth
	 * @param k     accumulated attenuation factor from previous recursion
	 * @param kx    attenuation factor of the current effect (reflection/refraction)
	 * @return the color contributed by the global effect
	 */
	private Color calcGlobalEffect(Ray ray, int level, Double3 k, Double3 kx) {
		Double3 kkx = k.product(kx);
		if (kkx.lowerThan(MIN_CALC_COLOR_K))
			return Color.BLACK;
		Intersection intersection = findClosestIntersection(ray);
		if (intersection == null)
			return scene.background.scale(kx);
		return preprocessIntersection(intersection, ray.getDirection())
				? calcColor(intersection, level - 1, kkx).scale(kx)
				: Color.BLACK;
	}

	/**
	 * Calculates the total global lighting effects (reflection and refraction) at
	 * the intersection recursively.
	 *
	 * @param intersection the intersection to calculate global effects for
	 * @param level        the current recursion depth
	 * @param k            the attenuation factor from previous steps
	 * @return the color resulting from global lighting effects
	 */
	private Color calcGlobalEffects(Intersection intersection, int level, Double3 k) {
		return calcGlobalEffect(constructRefractedRay(intersection), level, k, intersection.material.kT)
				.add(calcGlobalEffect(constructReflectedRay(intersection), level, k, intersection.material.kR));
	}

	/**
	 * Finds the closest intersection of the given ray with scene geometries.
	 *
	 * @param ray the ray to intersect with the scene
	 * @return the closest intersection or null if none found
	 */
	protected Intersection findClosestIntersection(Ray ray) {
		return ray.findClosestIntersection(scene.geometries.calculateIntersections(ray));
	}

	/**
	 * Finds all intersections of a given ray with geometries in the scene, up to a
	 * specified maximum distance.
	 *
	 * @param ray         the ray to trace from the origin point in a given
	 *                    direction
	 * @param maxDistance the maximum distance from the ray's origin to consider for
	 *                    intersections
	 * @return a list of intersections within the given distance limit, or an empty
	 *         list if none found
	 */
	protected List<Intersection> findAllIntersections(Ray ray, double maxDistance) {
		return scene.geometries.calculateIntersections(ray, maxDistance);
	}

	/**
	 * Computes the transparency attenuation factor between the intersection point
	 * and the light source, considering all objects that the shadow ray intersects.
	 *
	 * @param intersection the intersection point receiving light
	 * @return the product of transparency coefficients along the path to the light
	 */
	protected Double3 transparency(Intersection intersection) {
		Vector pointToLight = intersection.l.scale(-1); // vector from point to light source
		Ray ray = new Ray(intersection.point, pointToLight, intersection.normal); // shadow ray towards light
//		var intersections = scene.geometries.calculateIntersections(ray,
//				intersection.light.getDistance(intersection.point));
		var intersections = findAllIntersections(ray, intersection.light.getDistance(intersection.point));

		Double3 ktr = Double3.ONE;

		if (intersections == null)
			return ktr;

		for (Intersection i : intersections) {
			if (ktr.lowerThan(MIN_CALC_COLOR_K))
				return Double3.ZERO;
			ktr = ktr.product(i.material.kT);
		}
		return ktr;
	}
}
