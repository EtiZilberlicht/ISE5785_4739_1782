package geometries;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import primitives.Double3;
import primitives.Material;
import primitives.Point;

/**
 * Utility class for reading 3D geometry data from OBJ and MTL files.
 */
public class GeometriesObj {

	/**
	 * default constructor
	 */
	public GeometriesObj() {

	}

	/**
	 * Base folder path where OBJ and MTL files are stored.
	 */
	private static final String FOLDER_PATH = System.getProperty("user.dir") + "/obj";

	/**
	 * Reads an OBJ file and returns a list of polygon geometries. Supports basic
	 * triangulation and material parsing from associated MTL file.
	 *
	 * @param filename the name of the OBJ file (without extension)
	 * @return list of geometries parsed from the OBJ file
	 * @throws IOException if the file is not found or cannot be read
	 */
	public static List<Geometry> readObjToPolygons(String filename) throws IOException {
		List<Point> vertices = new ArrayList<>();
		List<Geometry> polygons = new ArrayList<>();
		Map<String, Material> materials = new HashMap<>();
		String currentMtl = null;

		Path objPath = Paths.get(FOLDER_PATH, filename + ".obj");
		Path parentDir = objPath.getParent();

		if (!objPath.toFile().exists()) {
			throw new IOException("OBJ file not found: " + objPath);
		}

		try (BufferedReader br = new BufferedReader(new FileReader(objPath.toFile()))) {
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();

				if (line.isEmpty() || line.startsWith("#"))
					continue;

				if (line.startsWith("mtllib ")) {
					String mtlFileName = line.substring(7).trim();
					Path mtlPath = parentDir.resolve(mtlFileName);
					parseMtlFile(mtlPath, materials);
				} else if (line.startsWith("usemtl ")) {
					currentMtl = line.substring(7).trim();
				} else if (line.startsWith("v ")) {
					String[] parts = line.split("\\s+");
					double x = Double.parseDouble(parts[1]);
					double y = Double.parseDouble(parts[2]);
					double z = Double.parseDouble(parts[3]);
					vertices.add(new Point(x, y, z));
				} else if (line.startsWith("f ")) {
					String[] parts = line.split("\\s+");
					List<Point> polygonPoints = new ArrayList<>();

					for (int i = 1; i < parts.length; i++) {
						String part = parts[i].split("/")[0];
						int index = Integer.parseInt(part) - 1;
						if (index >= 0 && index < vertices.size()) {
							polygonPoints.add(vertices.get(index));
						}
					}

					if (polygonPoints.size() < 3)
						continue;

					Material mat = materials.getOrDefault(currentMtl,
							new Material().setKD(0.8).setKS(0.5).setShininess(250).setKR(0.3).setKA(1));

					if (polygonPoints.size() == 3) {
						try {
							polygons.add(new Polygon(polygonPoints.get(0), polygonPoints.get(1), polygonPoints.get(2))
									.setMaterial(mat));
						} catch (IllegalArgumentException ignored) {
						}
					} else {
						// Triangulate the polygon
						for (int i = 1; i < polygonPoints.size() - 1; i++) {
							try {
								polygons.add(new Polygon(polygonPoints.get(0), polygonPoints.get(i),
										polygonPoints.get(i + 1)).setMaterial(mat));
							} catch (IllegalArgumentException ignored) {
							}
						}
					}
				}
			}
		}

		return polygons;
	}

	/**
	 * Parses an MTL file and fills a map with material properties.
	 *
	 * @param mtlPath     path to the MTL file
	 * @param materialMap map to populate with material name and corresponding
	 *                    Material object
	 * @throws IOException if the MTL file cannot be read
	 */
	private static void parseMtlFile(Path mtlPath, Map<String, Material> materialMap) throws IOException {
		if (!mtlPath.toFile().exists())
			return;

		try (BufferedReader br = new BufferedReader(new FileReader(mtlPath.toFile()))) {
			String line;
			Material currentMaterial = null;
			String currentName = null;

			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("#"))
					continue;

				if (line.startsWith("newmtl ")) {
					if (currentName != null && currentMaterial != null) {
						materialMap.put(currentName, currentMaterial);
					}
					currentName = line.substring(7).trim();
					currentMaterial = new Material();
				} else if (line.startsWith("Ka ")) {
					currentMaterial.setKA(parseDouble3(line));
				} else if (line.startsWith("Kd ")) {
					currentMaterial.setKD(parseDouble3(line));
				} else if (line.startsWith("Ks ")) {
					currentMaterial.setKS(parseDouble3(line));
				} else if (line.startsWith("Ns ")) {
					currentMaterial.setShininess((int) Double.parseDouble(line.substring(3).trim()));
				} else if (line.startsWith("d ")) {
					double transparency = 1.0 - Double.parseDouble(line.substring(2).trim());
					currentMaterial.setKT(transparency);
				} else if (line.startsWith("Tr ")) {
					double transparency = Double.parseDouble(line.substring(3).trim());
					currentMaterial.setKT(transparency);
				} else if (line.startsWith("Kr ")) {
					double kr = Double.parseDouble(line.substring(3).trim());
					currentMaterial.setKR(kr);
				}
			}

			if (currentName != null && currentMaterial != null) {
				materialMap.put(currentName, currentMaterial);
			}
		}
	}

	/**
	 * Parses a line containing three doubles (e.g., Ka/Kd/Ks) into a Double3
	 * object.
	 *
	 * @param line the line to parse
	 * @return a Double3 object representing the parsed values
	 */
	private static Double3 parseDouble3(String line) {
		String[] parts = line.substring(3).trim().split("\\s+");
		double x = Double.parseDouble(parts[0]);
		double y = Double.parseDouble(parts[1]);
		double z = Double.parseDouble(parts[2]);
		return new Double3(x, y, z);
	}
}
