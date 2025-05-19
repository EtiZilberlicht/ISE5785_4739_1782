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
}
