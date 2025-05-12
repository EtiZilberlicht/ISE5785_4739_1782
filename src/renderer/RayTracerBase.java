package renderer;

import primitives.Color;
import primitives.Ray;
import scene.Scene;

/**
 * Abstract base class for ray tracing engines.
 * 
 * <p>
 * This class defines the core interface for tracing a {@link Ray} and computing
 * its resulting {@link Color} based on a given {@link Scene}. Specific
 * rendering algorithms (like recursive ray tracing, path tracing, etc.) should
 * extend this class and implement the {@link #traceRay(Ray)} method.
 */
public abstract class RayTracerBase {

	/** The scene to be rendered */
	protected final Scene scene;

	/**
	 * Constructs a RayTracerBase with a given scene.
	 *
	 * @param scene the scene to use for ray tracing
	 */
	public RayTracerBase(Scene scene) {
		this.scene = scene;
	}

	/**
	 * Traces a single ray through the scene and computes the resulting color.
	 *
	 * @param ray the ray to trace
	 * @return the color resulting from tracing the ray
	 */
	public abstract Color traceRay(Ray ray);
}
