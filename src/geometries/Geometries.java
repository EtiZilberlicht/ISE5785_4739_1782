package geometries;

import java.util.LinkedList;
import java.util.List;

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

	/**
	 * Adds one or more intersectable geometries to the collection.
	 *
	 * @param geometries the {@link Intersectable} objects to add.
	 */
	public void add(Intersectable... geometries) {
		this.geometries.addAll(List.of(geometries));
	}

	@Override
	protected List<Intersection> calculateIntersectionsHelper(Ray ray) {

		List<Intersection> intersections = null;

		for (Intersectable geometry : geometries) {
			var geometryIntersections = geometry.calculateIntersections(ray);
			if (geometryIntersections != null)
				if (intersections == null)
					intersections = new LinkedList<>(geometryIntersections);
				else
					intersections.addAll(geometryIntersections);
		}
		return intersections;
	}

}
