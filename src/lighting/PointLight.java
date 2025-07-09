package lighting;

import static primitives.Util.isZero;

import java.util.List;

import primitives.Color;
import primitives.Point;
import primitives.Vector;
import renderer.Blackboard;

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
	 * The size representing the dimension or scale relevant to this geometry or
	 * object. Initial value is 0.
	 */
	protected double size = 0;

	/**
	 * The blackboard instance used for managing ray grids and calculations, such as
	 * beam generation for rendering effects.
	 */
	protected final Blackboard blackboard = new Blackboard();

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

	/**
	 * Sets the size parameter and updates the blackboard's grid size accordingly.
	 *
	 * @param size the new size to set
	 * @return this {@code PointLight} instance for method chaining
	 */
	public PointLight setSize(double size) {
		this.size = size;
		this.blackboard.setGridSize(size);
		return this;
	}

	/**
	 * Sets the number of rays to be used for soft shadow calculations by delegating
	 * to the internal blackboard.
	 *
	 * @param numOfRays the number of rays to generate
	 * @return this PointLight instance for method chaining
	 */
	public PointLight setNumOfRays(int numOfRays) {
		this.blackboard.setNumOfRays(numOfRays);
		return this;
	}

	/**
	 * Sets the shape of the ray grid (e.g., "square" or "circle") for shadow
	 * generation, by delegating to the internal blackboard.
	 *
	 * @param shape the desired shape of the ray grid
	 * @return this PointLight instance for method chaining
	 */
	public PointLight setShape(String shape) {
		this.blackboard.setShape(shape);
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

	@Override
	public List<Vector> getLBeam(Point p) {
		var vectors = getL(p).getVectors();
		return isZero(size) ? List.of(getL(p)) : blackboard.vectorBeam(position, p, vectors.getFirst(), vectors.get(1));
	}

}
