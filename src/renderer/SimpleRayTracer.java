package renderer;

import java.util.List;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import scene.Scene;

public class SimpleRayTracer extends RayTracerBase {

	public SimpleRayTracer(Scene scene) {
		super(scene);
	}

	private Color calcColor(Point point) {
		return scene.ambientLight.getIntensity();
	}

	@Override
	public Color traceRay(Ray ray) {
		List<Point> intersections = scene.geometries.findIntersections(ray);
		Point point = ray.findClosestPoint(intersections);
		return point == null ? scene.background : calcColor(point);
	}

}
