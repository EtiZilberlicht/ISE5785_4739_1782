package renderer.grid;

import java.util.List;

import geometries.Intersectable; // או geometries.Geometry בהתאם למה שיש לך

/**
 * Represents a single voxel (3D cell) in a voxel grid. Each voxel contains a
 * list of geometries (Intersectables) that overlap this voxel.
 */
public class Voxel {

	/** List of geometries contained in this voxel */
	private final List<Intersectable> geometries;

	/**
	 * Constructs a voxel containing the specified geometries.
	 *
	 * @param geometries the list of geometries in this voxel
	 */
	public Voxel(List<Intersectable> geometries) {
		this.geometries = geometries;
	}

	/**
	 * Returns the list of geometries in this voxel.
	 *
	 * @return list of geometries, possibly empty but never null if initialized
	 *         properly
	 */
	public List<Intersectable> getGeometries() {
		return geometries;
	}

	/**
	 * Checks whether this voxel is empty (contains no geometries).
	 *
	 * @return true if geometries list is null or empty, false otherwise
	 */
	public boolean isEmpty() {
		return geometries == null || geometries.isEmpty();
	}
}
