package lighting;

import static java.lang.Math.pow;
import static primitives.Util.alignZero;
import static primitives.Util.isZero;

import java.util.List;

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
	 * A constant vector lying on the plane behind the spotlight direction. Used as
	 * one axis of the sampling grid for soft shadow generation.
	 */
	private Vector v1;

	/**
	 * A constant vector lying on the plane behind the spotlight direction,
	 * orthogonal to {@code v1}, forming the second axis of the sampling grid.
	 */
	private Vector v2;

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
		var vectors = direction.getVectors();
		this.v1 = vectors.getFirst();
		this.v2 = vectors.get(1);
	}

	/**
	 * Sets the constant attenuation factor kC.
	 *
	 * @param kC the constant attenuation coefficient
	 * @return this spotlight instance for method chaining
	 */
	public SpotLight setKC(double kC) {
		if (alignZero(kC) <= 0)
			throw new IllegalArgumentException("Constant propagation factor must be positive");
		return (SpotLight) super.setKC(kC);
	}

	/**
	 * Sets the linear attenuation factor kL.
	 *
	 * @param kL the linear attenuation coefficient
	 * @return this spotlight instance for method chaining
	 */
	public SpotLight setKL(double kL) {
		if (kL < 0)
			throw new IllegalArgumentException("Linear propagation factor must be non-negative");
		return (SpotLight) super.setKL(kL);
	}

	/**
	 * Sets the quadratic attenuation factor kQ.
	 *
	 * @param kQ the quadratic attenuation coefficient
	 * @return this spotlight instance for method chaining
	 */
	public SpotLight setKQ(double kQ) {
		if (kQ < 0)
			throw new IllegalArgumentException("Quadratic propagation factor must be non-negative");
		return (SpotLight) super.setKQ(kQ);
	}

	@Override
	public SpotLight setSize(double size) {
		return (SpotLight) super.setSize(size);
	}

	@Override
	public Color getIntensity(Point p) {
		double dirL = alignZero(direction.dotProduct(getL(p)));
		return dirL <= 0 ? Color.BLACK : super.getIntensity(p).scale(pow(dirL, narrowBeam));
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

	public List<Vector> getLBeam(Point p) {
		return isZero(size) ? List.of(getL(p)) : blackboard.vectorBeam(this.position, p, v1, v2);
	}

}
