package scene;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import geometries.Geometries;
import geometries.Geometry;
import geometries.Sphere;
import geometries.Triangle;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.LightSource;
import lighting.PointLight;
import lighting.SpotLight;
import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * The {@code XmlScene} class provides functionality to load a 3D scene
 * configuration from an XML file and populate a {@link Scene} object with
 * background color, ambient light, and geometries.
 */
public class XmlScene {

	/**
	 * default constructor
	 */
	public XmlScene() {
	}

	/**
	 * Imports a scene from an XML file and updates the given {@link Scene} object.
	 *
	 * @param path  the full file path to the XML scene file
	 * @param scene the Scene object to be populated with data from the XML
	 * @throws Exception if an error occurs while reading or parsing the file
	 */
	public static void importScene(String path, Scene scene) throws Exception {
		File xmlFile = new File(path);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(xmlFile);
		doc.getDocumentElement().normalize();

		Element sceneElem = (Element) doc.getElementsByTagName("scene").item(0);

		if (sceneElem.hasAttribute("background-color")) {
			String bgColor = sceneElem.getAttribute("background-color");
			scene.setBackground(parseColor(bgColor));
		}

		if (sceneElem.getElementsByTagName("ambient-light").getLength() > 0) {
			Element amb = (Element) sceneElem.getElementsByTagName("ambient-light").item(0);
			if (amb.hasAttribute("color")) {
				String colorStr = amb.getAttribute("color");
				scene.setAmbientLight(new AmbientLight(parseColor(colorStr)));
			}
		}

		if (sceneElem.getElementsByTagName("geometries").getLength() > 0) {
			Element geometriesElem = (Element) sceneElem.getElementsByTagName("geometries").item(0);
			scene.setGeometries(parseGeometries(geometriesElem));
		}

		if (sceneElem.getElementsByTagName("lights").getLength() > 0) {
			Element lightsElem = (Element) sceneElem.getElementsByTagName("lights").item(0);
			scene.setLights(parseLights(lightsElem));
		}
	}

	/**
	 * Parses a {@link Geometries} element and returns a populated Geometries
	 * object.
	 *
	 * @param geometriesElem the XML element containing geometry definitions
	 * @return a Geometries object containing parsed geometry instances
	 */
	private static Geometries parseGeometries(Element geometriesElem) {
		Geometries geometries = new Geometries();
		NodeList geometryNodes = geometriesElem.getChildNodes();
		List<Element> elements = IntStream.range(0, geometryNodes.getLength()).mapToObj(i -> geometryNodes.item(i))
				.filter(n -> n instanceof Element).map(n -> (Element) n).toList();
		Geometry g;
		for (Element geomElem : elements) {
			switch (geomElem.getTagName()) {
			case "sphere":
				Point center = parsePoint(geomElem.getAttribute("center"));
				double radius = Double.parseDouble(geomElem.getAttribute("radius"));
				g = new Sphere(radius, center);
				break;
			case "triangle":
				Point p0 = parsePoint(geomElem.getAttribute("p0"));
				Point p1 = parsePoint(geomElem.getAttribute("p1"));
				Point p2 = parsePoint(geomElem.getAttribute("p2"));
				g = new Triangle(p0, p1, p2);
				break;
			default:
				throw new IllegalArgumentException("Unknown geometry: " + geomElem.getTagName());
			}
			geometries.add(g);
		}
		return geometries;
	}

	/**
	 * Parses the given XML element representing a list of lights and constructs a
	 * list of corresponding LightSource objects.
	 * <p>
	 * Supports three types of lights identified by their XML tags:
	 * <ul>
	 * <li><b>directional</b> - requires "intensity" and "direction"
	 * attributes.</li>
	 * <li><b>point</b> - requires "intensity" and "position" attributes.</li>
	 * <li><b>spot</b> - requires "intensity", "position", and "direction"
	 * attributes.</li>
	 * </ul>
	 * Each light is created by parsing its attributes and instantiating the
	 * corresponding LightSource subclass.
	 * <p>
	 * Throws IllegalArgumentException if an unknown light type is encountered.
	 *
	 * @param lightsElem the XML element containing light source child elements
	 * @return a list of LightSource objects parsed from the XML element
	 * @throws IllegalArgumentException if an unknown light type is specified
	 */
	private static List<LightSource> parseLights(Element lightsElem) {
		List<LightSource> lightSources = new LinkedList<>();
		NodeList lightNodes = lightsElem.getChildNodes();
		List<Element> elements = IntStream.range(0, lightNodes.getLength()).mapToObj(i -> lightNodes.item(i))
				.filter(n -> n instanceof Element).map(n -> (Element) n).toList();
		LightSource l;
		for (Element lightElem : elements) {
			switch (lightElem.getTagName()) {
			case "directional":
				Color intensityDirect = parseColor(lightElem.getAttribute("intensity"));
				Vector directionDirect = parseVector(lightElem.getAttribute("direction"));
				l = new DirectionalLight(intensityDirect, directionDirect);
				break;
			case "point":
				Color intensityPoint = parseColor(lightElem.getAttribute("intensity"));
				Point positionPoint = parsePoint(lightElem.getAttribute("position"));
				l = new PointLight(intensityPoint, positionPoint);
				break;
			case "spot":
				Color intensitySpot = parseColor(lightElem.getAttribute("intensity"));
				Point positionSpot = parsePoint(lightElem.getAttribute("position"));
				Vector directionSpot = parseVector(lightElem.getAttribute("direction"));
				l = new SpotLight(intensitySpot, positionSpot, directionSpot);
			default:
				throw new IllegalArgumentException("Unknown geometry: " + lightElem.getTagName());
			}
			lightSources.add(l);
		}
		return lightSources;
	}

	/**
	 * Parses a color string in the format "R G B" into a {@link Color} object.
	 *
	 * @param s the string representing RGB components separated by spaces
	 * @return the corresponding Color object
	 */
	private static Color parseColor(String s) {
		double[] vals = parseDoubles(s);
		return new Color(vals[0], vals[1], vals[2]);
	}

	/**
	 * Parses a string representing a 3D point in the format "x y z".
	 *
	 * @param s the string representing a point
	 * @return the corresponding {@link Point} object
	 */
	private static Point parsePoint(String s) {
		double[] vals = parseDoubles(s);
		return new Point(vals[0], vals[1], vals[2]);
	}

	/**
	 * Parses a string representing a 3D vector and returns the corresponding Vector
	 * object.
	 * <p>
	 * The input string is expected to contain three double values separated by
	 * commas or spaces, which represent the x, y, and z components of the vector.
	 *
	 * @param s the string containing the vector components
	 * @return a Vector constructed from the parsed components
	 * @throws IllegalArgumentException if the input string does not contain exactly
	 *                                  three numeric values
	 */
	private static Vector parseVector(String s) {
		double[] vals = parseDoubles(s);
		return new Vector(vals[0], vals[1], vals[2]);
	}

	/**
	 * Converts a string of whitespace-separated numbers into a double array.
	 *
	 * @param str the input string (e.g., "1.0 2.0 3.0")
	 * @return an array of doubles parsed from the string
	 */
	private static double[] parseDoubles(String str) {
		String[] parts = str.trim().split("\\s+");
		double[] nums = new double[parts.length];
		for (int i = 0; i < parts.length; i++) {
			nums[i] = Double.parseDouble(parts[i]);
		}
		return nums;
	}
}
