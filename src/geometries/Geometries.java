package geometries;

import static geometries.GeometriesObj.readObjToPolygons;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import primitives.Ray;

/**
 * A composite class representing a collection of intersectable geometries. This
 * class allows grouping multiple geometric objects that implement the
 * {@link Intersectable} interface, and provides a unified way to operate on all
 * of them.
 */
public class Geometries extends Intersectable {

	/**
	 * The internal list holding all the geometric objects in this group.
	 */
	private final List<Intersectable> geometries = new LinkedList<>();

	/**
	 * Constructs an empty collection of geometries.
	 */
	public Geometries() {
	}

	/**
	 * Constructs a collection of geometries from the given array of intersectables.
	 *
	 * @param geometries one or more {@link Intersectable} objects to add to the
	 *                   collection.
	 */
	public Geometries(Intersectable... geometries) {
		add(geometries);
	}

	public Geometries(List<Intersectable> geometries) {
		this.geometries.addAll(geometries);
	}

	/**
	 * Adds one or more intersectable geometries to the collection.
	 *
	 * @param geometries the {@link Intersectable} objects to add.
	 */
	public void add(Intersectable... geometries) {
		this.geometries.addAll(List.of(geometries));
	}

	@Override
	protected List<Intersection> calculateIntersectionsHelper(Ray ray, double maxDistance) {

		List<Intersection> intersections = null;

		for (Intersectable geometry : geometries) {
			var geometryIntersections = geometry.calculateIntersections(ray, maxDistance);
			if (geometryIntersections != null)
				if (intersections == null)
					intersections = new LinkedList<>(geometryIntersections);
				else
					intersections.addAll(geometryIntersections);
		}
		return intersections;
	}

	@Override
	protected AABB computeBoundingBox() {
		if (geometries.isEmpty())
			return null;

		AABB result = null;
		for (Intersectable geo : geometries) {
			AABB box = geo.getBoundingBox();
			if (box != null)
				result = (result == null) ? box : result.union(box);
		}
		return result;

	}

	/**
	 * Returns the list of all intersectable geometries in this collection.
	 *
	 * @return list of intersectables
	 */
	public List<Intersectable> getAll() {
		return geometries.isEmpty() ? null : getStream().toList();
	}

	private Stream<Intersectable> getStream() {
		return geometries.stream().flatMap(
				geometry -> (geometry instanceof Geometries composite) ? composite.getStream() : Stream.of(geometry));
	}

	/**
	 * Loads polygon geometries from an OBJ file and adds them to the scene.
	 *
	 * @param filename the name of the OBJ file (without extension or path)
	 */
	public void addObjPolygons(String filename) {
		try {
			List<Geometry> polygons = readObjToPolygons(filename);
			add(polygons.toArray(new Geometry[0]));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isEmpty() {
		return geometries.isEmpty();
	}

}
