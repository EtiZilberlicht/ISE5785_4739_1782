package renderer.grid;

import geometries.Geometries;

/**
 * Represents a single voxel (3D cell) in a voxel grid. Each voxel contains a
 * list of geometries (Intersectables) that overlap this voxel.
 */
public class Voxel {

	/** List of geometries contained in this voxel */
	private Geometries geometries = new Geometries();

	/**
	 * Returns the list of geometries in this voxel.
	 *
	 * @return list of geometries, possibly empty but never null if initialized
	 *         properly
	 */
	public Geometries getGeometries() {
		return geometries;
	}

}
