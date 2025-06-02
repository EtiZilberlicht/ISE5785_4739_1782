package primitives;

/**
 * Represents the material properties of a surface in the scene.
 * <p>
 * These properties affect how the surface interacts with light, including
 * ambient, diffuse, and specular reflections, as well as shininess.
 */
public class Material {

	/**
	 * Ambient reflection coefficient (how much ambient light the material
	 * reflects).
	 */
	public Double3 kA = Double3.ONE;

	/**
	 * Specular reflection coefficient (how much specular highlight is reflected).
	 */
	public Double3 kS = Double3.ZERO;

	/**
	 * Diffuse reflection coefficient (how much light is scattered diffusely).
	 */
	public Double3 kD = Double3.ZERO;

	/**
	 * Shininess coefficient (controls the size and sharpness of specular
	 * highlights).
	 */
	public int nSH = 0;

	/**
	 * Transparency coefficient.
	 * <p>
	 * Represents how much light passes through the material. A value of
	 * {@link Double3#ZERO} means the material is fully opaque.
	 */
	public Double3 kT = Double3.ZERO;

	/**
	 * Reflection coefficient.
	 * <p>
	 * Represents how much light is reflected from the material surface. A value of
	 * {@link Double3#ZERO} means no reflection.
	 */
	public Double3 kR = Double3.ZERO;

	/**
	 * Default constructor that initializes material with default values.
	 */
	public Material() {
	}

	/**
	 * Sets the ambient reflection coefficient.
	 *
	 * @param kA the {@link Double3} ambient coefficient to set
	 * @return the current {@code Material} object for method chaining
	 */
	public Material setKA(Double3 kA) {
		this.kA = kA;
		return this;
	}

	/**
	 * Sets the ambient reflection coefficient with a uniform value.
	 *
	 * @param kA the ambient coefficient as a single {@code double} value
	 * @return the current {@code Material} object for method chaining
	 */
	public Material setKA(double kA) {
		this.kA = new Double3(kA);
		return this;
	}

	/**
	 * Sets the specular reflection coefficient.
	 *
	 * @param kS the {@link Double3} specular coefficient to set
	 * @return the current {@code Material} object for method chaining
	 */
	public Material setKS(Double3 kS) {
		this.kS = kS;
		return this;
	}

	/**
	 * Sets the specular reflection coefficient with a uniform value.
	 *
	 * @param kS the specular coefficient as a single {@code double} value
	 * @return the current {@code Material} object for method chaining
	 */
	public Material setKS(double kS) {
		this.kS = new Double3(kS);
		return this;
	}

	/**
	 * Sets the diffuse reflection coefficient.
	 *
	 * @param kD the {@link Double3} diffuse coefficient to set
	 * @return the current {@code Material} object for method chaining
	 */
	public Material setKD(Double3 kD) {
		this.kD = kD;
		return this;
	}

	/**
	 * Sets the diffuse reflection coefficient with a uniform value.
	 *
	 * @param kD the diffuse coefficient as a single {@code double} value
	 * @return the current {@code Material} object for method chaining
	 */
	public Material setKD(double kD) {
		this.kD = new Double3(kD);
		return this;
	}

	/**
	 * Sets the shininess coefficient.
	 *
	 * @param nSH the shininess value to set
	 * @return the current {@code Material} object for method chaining
	 */
	public Material setShininess(int nSH) {
		this.nSH = nSH;
		return this;
	}

	/**
	 * Sets the transparency coefficient.
	 *
	 * @param kT a {@link Double3} representing the transparency level
	 * @return the current {@link Material} instance (for method chaining)
	 */
	public Material setKT(Double3 kT) {
		this.kT = kT;
		return this;
	}

	/**
	 * Sets the transparency coefficient using a scalar value.
	 *
	 * @param kT a double value representing uniform transparency in all directions
	 * @return the current {@link Material} instance (for method chaining)
	 */
	public Material setKT(double kT) {
		this.kT = new Double3(kT);
		return this;
	}

	/**
	 * Sets the reflection coefficient.
	 *
	 * @param kR a {@link Double3} representing the reflection level
	 * @return the current {@link Material} instance (for method chaining)
	 */
	public Material setKR(Double3 kR) {
		this.kR = kR;
		return this;
	}

	/**
	 * Sets the reflection coefficient using a scalar value.
	 *
	 * @param kR a double value representing uniform reflection in all directions
	 * @return the current {@link Material} instance (for method chaining)
	 */
	public Material setKR(double kR) {
		this.kR = new Double3(kR);
		return this;
	}

}
