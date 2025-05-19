package lighting;

import primitives.Color;

/**
 * An abstract base class for all types of lights in the scene. Holds the common
 * property of light intensity.
 */
abstract class Light {

	/**
	 * The intensity (color) of the light.
	 */
	protected final Color intensity;

	/**
	 * Constructs a light with the given intensity.
	 *
	 * @param intensity the {@link Color} representing the light's intensity
	 */
	protected Light(Color intensity) {
		this.intensity = intensity;
	}

	/**
	 * Returns the intensity (color) of the light.
	 *
	 * @return the {@link Color} of the light
	 */
	public Color getIntensity() {
		return intensity;
	}
}
