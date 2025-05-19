package lighting;

import static primitives.Util.alignZero;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * A spotlight is a light source that radiates light in a specific direction
 * from a specific point in space, with intensity decreasing as the angle
 * between the direction and the point increases.
 */
public class SpotLight extends PointLight {

	/**
	 * The normalized direction in which the spotlight is pointing.
	 */
	private final Vector direction;

	/**
	 * Controls the focus of the light beam.
	 * <p>
	 * A value greater than 1 narrows the light beam, simulating a spotlight effect.
	 * The higher the value, the more concentrated the light is toward the beam
	 * direction. A value of 1 means no narrowing (uniform spread).
	 */
	private double narrowBeam = 1;

	/**
	 * Constructs a spotlight with specified intensity, position, and direction.
	 *
	 * @param intensity the {@link Color} representing the light's intensity
	 * @param position  the {@link Point} representing the light's position
	 * @param direction the {@link Vector} representing the light's direction (will
	 *                  be normalized)
	 */
	public SpotLight(Color intensity, Point position, Vector direction) {
		super(intensity, position);
		this.direction = direction.normalize();
	}

	/**
	 * Sets the constant attenuation factor kC.
	 *
	 * @param kC the constant attenuation coefficient
	 * @return this spotlight instance for method chaining
	 */
	public SpotLight setKC(double kC) {
		super.setkC(kC);
		return this;
	}

	/**
	 * Sets the linear attenuation factor kL.
	 *
	 * @param kL the linear attenuation coefficient
	 * @return this spotlight instance for method chaining
	 */
	public SpotLight setKL(double kL) {
		super.setKL(kL);
		return this;
	}

	/**
	 * Sets the quadratic attenuation factor kQ.
	 *
	 * @param kQ the quadratic attenuation coefficient
	 * @return this spotlight instance for method chaining
	 */
	public SpotLight setKQ(double kQ) {
		super.setKQ(kQ);
		return this;
	}

	@Override
	public Color getIntensity(Point p) {
		double dirL = alignZero(direction.dotProduct(getL(p)));
		dirL = Math.pow(dirL, narrowBeam);
		return dirL <= 0 ? Color.BLACK : super.getIntensity(p).scale(dirL);
	}

	/**
	 * Sets the narrow beam factor of the spotlight.
	 * <p>
	 * This parameter controls how focused the spotlight is. A higher value creates
	 * a narrower, more concentrated beam of light, similar to a focused flashlight.
	 * A value of 1 means the light spreads evenly without narrowing.
	 *
	 * @param narrowBeam the factor by which to narrow the light beam; must be ≥ 1
	 * @return the current {@code SpotLight} instance for method chaining
	 */
	public SpotLight setNarrowBeam(double narrowBeam) {
		this.narrowBeam = narrowBeam;
		return this;
	}

}
