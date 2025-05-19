package geometries;

import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;

/**
 * The {@code Geometry} class is an abstract base class representing geometric
 * shapes in 3D space. It extends {@link Intersectable} and provides common
 * properties such as emission color and material. Subclasses must implement the
 * method {@link #getNormal(Point)} to return the normal vector at a given point
 * on the surface of the geometry.
 */
public abstract class Geometry extends Intersectable {

	/**
	 * The emission color of the geometry (its inherent light emission).
	 */
	protected Color emission = Color.BLACK;

	/**
	 * The material properties of the geometry.
	 */
	private Material material = new Material();

	/**
	 * Default constructor for Geometry.
	 */
	public Geometry() {
	}

	/**
	 * Gets the emission color of the geometry.
	 *
	 * @return the emission {@link Color} of the geometry.
	 */
	public Color getEmission() {
		return emission;
	}

	/**
	 * Gets the material properties of the geometry.
	 *
	 * @return the {@link Material} of the geometry.
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * Sets the material properties of the geometry.
	 *
	 * @param material the {@link Material} to set.
	 * @return the current {@code Geometry} instance (for method chaining).
	 */
	public Geometry setMaterial(Material material) {
		this.material = material;
		return this;
	}

	/**
	 * Sets the emission color of the geometry.
	 *
	 * @param emission the {@link Color} to set as emission.
	 * @return the current {@code Geometry} instance (for method chaining).
	 */
	public Geometry setEmission(Color emission) {
		this.emission = emission;
		return this;
	}

	/**
	 * Returns the normal vector to the geometry's surface at a specified point.
	 * This method must be implemented by any subclass.
	 *
	 * @param point the {@link Point} on the geometry's surface.
	 * @return the normal {@link Vector} at the specified point.
	 */
	public abstract Vector getNormal(Point point);
}
