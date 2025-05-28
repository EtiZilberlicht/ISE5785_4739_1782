package renderer;

import static primitives.Util.alignZero;
import static primitives.Util.isZero;

import geometries.Intersectable.Intersection;
import lighting.LightSource;
import primitives.Color;
import primitives.Double3;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

/**
 * A simple ray tracer implementation that returns only the ambient light color
 * and basic local lighting (diffuse and specular) for the closest intersection
 * point, or the background color if no intersection occurs.
 * <p>
 * This class is useful for rendering simple scenes without advanced effects
 * like shadows, reflections, or transparency.
 */
public class SimpleRayTracer extends RayTracerBase {

	/**
	 * Constructs a SimpleRayTracer for the specified scene.
	 *
	 * @param scene the scene to be rendered
	 */
	public SimpleRayTracer(Scene scene) {
		super(scene);
	}

	private static final int MAX_CALC_COLOR_LEVEL = 10;
	private static final double MIN_CALC_COLOR_K = 0.001;
	private static final Double3 INITIAL_K = Double3.ONE;

	/**
	 * Traces a ray into the scene and computes the color of the closest
	 * intersection point. If no intersection occurs, returns the background color
	 * of the scene.
	 *
	 * @param ray the ray to trace
	 * @return the resulting color from tracing the ray
	 */
	@Override
	public Color traceRay(Ray ray) {
		var point = findClosestIntersection(ray);
		return point == null ? scene.background : calcColor(point, ray);
	}

	private Color calcColor(Intersection intersection, int level, Double3 k) {
		Color color = calcColorLocalEffects(intersection, k);
		return 1 == level ? color : color.add(calcGlobalEffects(intersection, level, k));
	}

	/**
	 * Computes the final color at a given intersection point. This includes the
	 * ambient light and local lighting effects (diffuse and specular).
	 *
	 * @param intersection the intersection point with geometry and hit location
	 * @param ray          the ray that caused the intersection
	 * @return the resulting color at the intersection point
	 */
	private Color calcColor(Intersection intersection, Ray ray) {
		return !preprocessIntersection(intersection, ray.getDirection()) ? Color.BLACK
				: scene.ambientLight.getIntensity().scale(intersection.material.kA)
						.add(calcColor(intersection, MAX_CALC_COLOR_LEVEL, INITIAL_K));
	}

	/**
	 * Prepares necessary data at the intersection point for lighting calculations.
	 * Computes the normal vector and checks if the angle between ray direction and
	 * normal is valid.
	 *
	 * @param intersection the intersection data to prepare
	 * @param v            the direction vector of the incoming ray
	 * @return true if the intersection is valid for lighting calculations
	 */
	private boolean preprocessIntersection(Intersection intersection, Vector v) {
		intersection.v = v;
		intersection.normal = intersection.geometry.getNormal(intersection.point);
		intersection.vNormal = intersection.v.dotProduct(intersection.normal);
		return !isZero(intersection.vNormal);
	}

	/**
	 * Sets the light source data for the given intersection point. Computes the
	 * light direction and angle with the surface normal.
	 *
	 * @param intersection the intersection to update
	 * @param light        the light source affecting the point
	 * @return true if the light contributes to the color (i.e., same side as view)
	 */
	private boolean setLightSource(Intersection intersection, LightSource light) {
		intersection.light = light;
		intersection.l = light.getL(intersection.point);
		intersection.lNormal = intersection.l.dotProduct(intersection.normal);
		return alignZero(intersection.lNormal) * intersection.vNormal > 0;
	}

	/**
	 * Calculates the local lighting effects (diffuse and specular) at a given
	 * intersection.
	 *
	 * @param intersection the intersection containing surface and lighting
	 *                     information
	 * @return the color resulting from local light effects
	 */
	Color calcColorLocalEffects(Intersection intersection, Double3 k) {
		Color color = intersection.geometry.getEmission();
		for (LightSource lightSource : scene.lights) {

			if (!setLightSource(intersection, lightSource))
				continue;
			Double3 ktr = transparency(intersection);
			if (!ktr.product(k).lowerThan(MIN_CALC_COLOR_K)) {
				Color iL = lightSource.getIntensity(intersection.point).scale(ktr);
				color = color.add(iL.scale(calcDiffusive(intersection).add(calcSpecular(intersection))));
			}
		}
		return color;
	}

	/**
	 * Computes the specular reflection component at the intersection.
	 *
	 * @param intersection the intersection with lighting data
	 * @return the specular reflection intensity as a Double3
	 */
	Double3 calcSpecular(Intersection intersection) {
		Vector r = intersection.l.subtract(intersection.normal.scale(2 * intersection.lNormal)); // reflected vector
		double vr = alignZero(intersection.v.dotProduct(r));
		return vr >= 0 ? Double3.ZERO : intersection.material.kS.scale(Math.pow(-vr, intersection.material.nSH));
	}

	/**
	 * Computes the diffuse reflection component at the intersection.
	 *
	 * @param intersection the intersection with lighting data
	 * @return the diffuse reflection intensity as a Double3
	 */
	Double3 calcDiffusive(Intersection intersection) {
		double nl = intersection.lNormal;
		return intersection.material.kD.scale(nl < 0 ? -nl : nl);
	}

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

	private Ray constructReflectedRay(Intersection intersection) {
		return new Ray(intersection.point, intersection.v.subtract(intersection.normal.scale(2 * intersection.vNormal)),
				intersection.normal);
	}

	private Ray constructRefractedRay(Intersection intersection) {
		return new Ray(intersection.point, intersection.v, intersection.normal);
	}

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

	private Color calcGlobalEffects(Intersection intersection, int level, Double3 k) {
		return calcGlobalEffect(constructRefractedRay(intersection), level, k, intersection.material.kT)
				.add(calcGlobalEffect(constructReflectedRay(intersection), level, k, intersection.material.kR));
	}

	private Intersection findClosestIntersection(Ray ray) {
		return ray.findClosestIntersection(scene.geometries.calculateIntersections(ray));
	}

	private Double3 transparency(Intersection intersection) {

		Vector pointToLight = intersection.l.scale(-1); // from the point to the light source

		Ray ray = new Ray(intersection.point, pointToLight, intersection.normal); // create a ray from the point to the
																					// light source
		var intersections = scene.geometries.calculateIntersections(ray,
				intersection.light.getDistance(intersection.point));

		Double3 ktr = Double3.ONE;

		if (intersections == null)
			return ktr;
		else {
			for (Intersection i : intersections) {
				if (ktr.lowerThan(MIN_CALC_COLOR_K))
					return Double3.ZERO;
				ktr = ktr.product(i.material.kT);
			}
		}
		return ktr;
	}

}
