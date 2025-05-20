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
		var intersections = scene.geometries.calculateIntersections(ray);
		var point = ray.findClosestIntersection(intersections);
		return point == null ? scene.background : calcColor(point, ray);
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
		if (!preprocessIntersection(intersection, ray.getDirection()))
			return Color.BLACK;
		return scene.ambientLight.getIntensity().scale(intersection.material.kA)
				.add(calcColorLocalEffects(intersection));
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
		return alignZero(intersection.lNormal * intersection.vNormal) > 0;
	}

	/**
	 * Calculates the local lighting effects (diffuse and specular) at a given
	 * intersection.
	 *
	 * @param intersection the intersection containing surface and lighting
	 *                     information
	 * @return the color resulting from local light effects
	 */
	Color calcColorLocalEffects(Intersection intersection) {
		Color color = intersection.geometry.getEmission();
		for (LightSource lightSource : scene.lights) {
			if (setLightSource(intersection, lightSource)) {
				Color iL = lightSource.getIntensity(intersection.point);
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
}
