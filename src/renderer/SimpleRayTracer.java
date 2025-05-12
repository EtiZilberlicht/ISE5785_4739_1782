package renderer;

import java.util.List;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;

/**
 * A simple ray tracer implementation that returns only the ambient light color
 * for the closest intersection point, or the background color if no
 * intersection occurs.
 * 
 * <p>
 * This is a basic implementation of a ray tracer, useful for testing or
 * rendering simple scenes without shading, reflections, or shadows.
 */
public class SimpleRayTracer extends RayTracerBase {

	/**
	 * Constructs a SimpleRayTracer with the given scene.
	 *
	 * @param scene the scene to render
	 */
	public SimpleRayTracer(Scene scene) {
		super(scene);
	}

	/**
	 * Computes the color at a given point.
	 * 
	 * <p>
	 * In this simple implementation, only the ambient light is considered.
	 *
	 * @param point the point in the scene (currently unused)
	 * @return the ambient light color of the scene
	 */
	private Color calcColor(Point point) {
		return scene.ambientLight.getIntensity();
	}

	/**
	 * Traces a ray and returns the color at the closest intersection point, or the
	 * background color if the ray does not intersect any geometry.
	 *
	 * @param ray the ray to trace
	 * @return the resulting color at the closest intersection or background
	 */
	@Override
	public Color traceRay(Ray ray) {
		List<Point> intersections = scene.geometries.findIntersections(ray);
		Point point = ray.findClosestPoint(intersections);
		return point == null ? scene.background : calcColor(point);
	}
}
