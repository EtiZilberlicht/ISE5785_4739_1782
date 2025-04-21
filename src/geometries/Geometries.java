package geometries;

import java.util.List;
import java.util.LinkedList;
import primitives.Point;
import primitives.Ray;

/**
 * A composite class representing a collection of intersectable geometries.
 * This class allows grouping multiple geometric objects that implement the
 * {@link Intersectable} interface, and provides a unified way to operate on all of them.
 */
public class Geometries implements Intersectable {

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
     * @param geometries one or more {@link Intersectable} objects to add to the collection.
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
	public List<Point> findIntersections(Ray ray) {
		List<Point> totalList = null;
		for (Intersectable geometry : geometries) {
			var list = geometry.findIntersections(ray);
			if (list != null)
				if (totalList == null)
					totalList = new LinkedList<>(list);
				else
					totalList.addAll(list);
		}
		return totalList;
	}

}
