package lighting;

import primitives.Color;

/**
 * The {@code AmbientLight} class represents ambient (background) light in a 3D
 * scene. This light affects all objects in the scene uniformly, regardless of
 * their position or orientation. It is commonly used to simulate a base level
 * of illumination in the scene.
 */
public class AmbientLight extends Light {

	/**
	 * A predefined instance of {@code AmbientLight} with no intensity (black). Can
	 * be used when a scene has no ambient light.
	 */
	public static final AmbientLight NONE = new AmbientLight(Color.BLACK);

	/**
	 * Constructs an {@code AmbientLight} with the specified intensity.
	 *
	 * @param intensity the color and brightness of the ambient light
	 */
	public AmbientLight(Color intensity) {
		super(intensity);
	}

}
