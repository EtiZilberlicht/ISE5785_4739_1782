package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * A directional light source that represents light with constant direction and
 * intensity, like sunlight. The direction is normalized and does not depend on
 * position.
 */
public class DirectionalLight extends Light implements LightSource {

	/**
	 * The normalized direction from which the light comes.
	 */
	private final Vector direction;

	/**
	 * Constructs a directional light with the given intensity and direction.
	 *
	 * @param intensity the {@link Color} representing the light's intensity
	 * @param direction the {@link Vector} representing the light's direction (will
	 *                  be normalized)
	 */
	public DirectionalLight(Color intensity, Vector direction) {
		super(intensity);
		this.direction = direction.normalize();
	}

	@Override
	public Color getIntensity(Point p) {
		return intensity;
	}

	@Override
	public Vector getL(Point p) {
		return direction;
	}

	@Override
	public double getDistance(Point point) {
		return Double.POSITIVE_INFINITY;
	}
}
