package scene;

import static scene.XmlScene.importScene;

import geometries.Geometries;
import lighting.AmbientLight;
import primitives.Color;

/**
 * Represents a 3D scene including background color, ambient lighting, and
 * geometries.
 */
public class Scene {

	/** The name of the scene */
	public String name;

	/** Background color of the scene (default is black) */
	public Color background = Color.BLACK;

	/** Ambient lighting in the scene (default is none) */
	public AmbientLight ambientLight = AmbientLight.NONE;

	/** Geometries in the scene */
	public Geometries geometries = new Geometries();

	/** Folder path for loading XML scene files */
	private static final String FOLDER_PATH = System.getProperty("user.dir") + "/xml";

	/**
	 * Constructs a new Scene with the given name.
	 *
	 * @param name the name of the scene
	 */
	public Scene(String name) {
		this.name = name;
	}

	/**
	 * Updates the scene using data from an XML file.
	 *
	 * @param fileName the name of the XML file (without ".xml" extension)
	 * @throws RuntimeException if there is an error during XML import
	 */
	public void updateXML(String fileName) {
		try {
			importScene(FOLDER_PATH + '/' + fileName + ".xml", this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Sets the background color of the scene.
	 *
	 * @param background the background color
	 * @return this Scene object for method chaining
	 */
	public Scene setBackground(Color background) {
		this.background = background;
		return this;
	}

	/**
	 * Sets the ambient light of the scene.
	 *
	 * @param ambientLight the ambient light
	 * @return this Scene object for method chaining
	 */
	public Scene setAmbientLight(AmbientLight ambientLight) {
		this.ambientLight = ambientLight;
		return this;
	}

	/**
	 * Sets the geometries of the scene.
	 *
	 * @param geometries the geometries to set
	 * @return this Scene object for method chaining
	 */
	public Scene setGeometries(Geometries geometries) {
		this.geometries = geometries;
		return this;
	}
}
