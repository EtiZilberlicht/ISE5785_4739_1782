package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * Represents a point light source that emits light from a specific position in
 * space.
 * <p>
 * The light intensity decreases with distance according to the attenuation
 * formula: {@code 1 / (kC + kL * d + kQ * d²)}, where d is the distance to the
 * point.
 */
public class PointLight extends Light implements LightSource {

	/**
	 * The position of the point light in 3D space.
	 */
	protected final Point position;

	/**
	 * Constant attenuation factor.
	 */
	private double kC = 1;

	/**
	 * Linear attenuation factor.
	 */
	private double kL = 0;

	/**
	 * Quadratic attenuation factor.
	 */
	private double kQ = 0;

	/**
	 * Constructs a new {@code PointLight} with the given intensity and position.
	 *
	 * @param intensity the color intensity of the light
	 * @param position  the position of the light source
	 */
	public PointLight(Color intensity, Point position) {
		super(intensity);
		this.position = position;
	}

	/**
	 * Sets the constant attenuation factor.
	 *
	 * @param kC the constant attenuation value
	 * @return this {@code PointLight} instance for method chaining
	 */
	public PointLight setKC(double kC) {
		this.kC = kC;
		return this;
	}

	/**
	 * Sets the linear attenuation factor.
	 *
	 * @param kL the linear attenuation value
	 * @return this {@code PointLight} instance for method chaining
	 */
	public PointLight setKL(double kL) {
		this.kL = kL;
		return this;
	}

	/**
	 * Sets the quadratic attenuation factor.
	 *
	 * @param kQ the quadratic attenuation value
	 * @return this {@code PointLight} instance for method chaining
	 */
	public PointLight setKQ(double kQ) {
		this.kQ = kQ;
		return this;
	}

	@Override
	public Color getIntensity(Point p) {
		double dSquared = p.distanceSquared(position);
		double denominator = 1d / (kC + kL * Math.sqrt(dSquared) + kQ * dSquared);
		return intensity.scale(denominator);
	}

	@Override
	public Vector getL(Point p) {
		try {
			return p.subtract(position).normalize();
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	@Override
	public double getDistance(Point point) {
		return point.distance(position);
	}
}
