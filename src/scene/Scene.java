package scene;

import static scene.XmlScene.importScene;

import java.util.LinkedList;
import java.util.List;

import geometries.Geometries;
import lighting.AmbientLight;
import lighting.LightSource;
import primitives.Color;

/**
 * Represents a 3D scene including background color, ambient lighting,
 * geometries, and light sources.
 */
public class Scene {

	/**
	 * The name of the scene.
	 */
	public final String name;

	/**
	 * Background color of the scene (default is black).
	 */
	public Color background = Color.BLACK;

	/**
	 * Ambient lighting in the scene (default is none).
	 */
	public AmbientLight ambientLight = AmbientLight.NONE;

	/**
	 * Geometries included in the scene.
	 */
	public Geometries geometries = new Geometries();

	/**
	 * List of light sources in the scene.
	 */
	public List<LightSource> lights = new LinkedList<>();

	/**
	 * Folder path for loading XML scene files.
	 */
	private static final String FOLDER_PATH = System.getProperty("user.dir") + "/xml";

	/**
	 * Constructs a new Scene with the specified name.
	 *
	 * @param name the name of the scene
	 */
	public Scene(String name) {
		this.name = name;
	}

	/**
	 * Updates the current scene by importing data from an XML file.
	 * <p>
	 * Loads scene information from the specified XML file located in the
	 * {@code FOLDER_PATH} directory and updates this Scene instance.
	 * </p>
	 *
	 * @param fileName the name of the XML file (without the ".xml" extension)
	 * @return this Scene instance for method chaining
	 * @throws RuntimeException if an error occurs during XML import
	 */
	public Scene updateXML(String fileName) {
		try {
			importScene(FOLDER_PATH + '/' + fileName + ".xml", this);
			return this;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Sets the background color of the scene.
	 *
	 * @param background the background color to set
	 * @return this Scene instance for method chaining
	 */
	public Scene setBackground(Color background) {
		this.background = background;
		return this;
	}

	/**
	 * Sets the ambient light of the scene.
	 *
	 * @param ambientLight the ambient light to set
	 * @return this Scene instance for method chaining
	 */
	public Scene setAmbientLight(AmbientLight ambientLight) {
		this.ambientLight = ambientLight;
		return this;
	}

	/**
	 * Sets the geometries of the scene.
	 *
	 * @param geometries the geometries to set
	 * @return this Scene instance for method chaining
	 */
	public Scene setGeometries(Geometries geometries) {
		this.geometries = geometries;
		return this;
	}

	/**
	 * Sets the list of light sources in the scene.
	 *
	 * @param lights the list of light sources to set
	 * @return this Scene instance for method chaining
	 */
	public Scene setLights(List<LightSource> lights) {
		this.lights = lights;
		return this;
	}
}
