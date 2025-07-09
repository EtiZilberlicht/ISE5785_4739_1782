package renderer.grid;

import static java.lang.Math.max;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import geometries.Intersectable;
import geometries.Intersectable.AABB;
import primitives.Point;

/**
 * Represents a uniform 3D grid of voxels covering a bounding box. Each voxel
 * contains a list of geometries intersecting that voxel, enabling efficient
 * spatial queries like ray-voxel traversal.
 */
public class VoxelGrid {

	/** The bounding box enclosing the entire grid */
	private final AABB boundingBox;

	/** Edge length of each cubic voxel */
	private final double voxelSize;

	/** Number of voxels along each axis */
	private final int sizeX, sizeY, sizeZ;

	/** Map from voxel indices (i,j,k) to the Voxel holding geometries */
	private final Map<Index3D, Voxel> voxels = new HashMap<>();

	/**
	 * Constructs a VoxelGrid covering the given bounding box, subdivided into the
	 * specified number of voxels per axis.
	 *
	 * @param boundingBox The bounding box to cover by the grid.
	 * @param sizeX       Number of voxels along the X-axis.
	 * @param sizeY       Number of voxels along the Y-axis.
	 * @param sizeZ       Number of voxels along the Z-axis.
	 */
	public VoxelGrid(AABB boundingBox, int sizeX, int sizeY, int sizeZ) {
		this.boundingBox = Objects.requireNonNull(boundingBox, "Bounding box cannot be null");
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
		// Calculate voxel size assuming uniform cubic voxels fitting the bounding box
		// exactly
		this.voxelSize = max(
				max((boundingBox.maxCorner().getX() - boundingBox.minCorner().getX()) / sizeX,
						(boundingBox.maxCorner().getY() - boundingBox.minCorner().getY()) / sizeY),
				(boundingBox.maxCorner().getZ() - boundingBox.minCorner().getZ()) / sizeZ);
	}

	/**
	 * Converts a 3D point to the corresponding voxel index within the grid.
	 *
	 * @param p The 3D point.
	 * @return The voxel index corresponding to the point, or null if the point is
	 *         outside the bounding box.
	 */
	public Index3D pointToIndex(Point p) {
		if (!pointInsideBoundingBox(p))
			return null;

		double x = p.getX();
		double y = p.getY();
		double z = p.getZ();

		int i = (int) Math.floor((x - boundingBox.minCorner().getX()) / voxelSize);
		int j = (int) Math.floor((y - boundingBox.minCorner().getY()) / voxelSize);
		int k = (int) Math.floor((z - boundingBox.minCorner().getZ()) / voxelSize);

		// Fix boundary cases where point lies exactly on max edge
		if (i == sizeX)
			i = sizeX - 1;
		if (j == sizeY)
			j = sizeY - 1;
		if (k == sizeZ)
			k = sizeZ - 1;

		return new Index3D(i, j, k);
	}

	/**
	 * Adds a geometry to all voxels intersected by its bounding box.
	 *
	 * @param geometry The geometry to add.
	 */
	public void addGeometry(Intersectable geometry) {
		AABB geomBox = geometry.getBoundingBox();
		if (geomBox == null)
			return;

		Index3D minIndex = pointToIndex(geomBox.minCorner());

		// 🛠 הקטנת קצה עליון של הקוביה כדי למנוע חריגה
		Point adjustedMax = new Point(geomBox.maxCorner().getX() - 1e-5, geomBox.maxCorner().getY() - 1e-5,
				geomBox.maxCorner().getZ() - 1e-5);
		Index3D maxIndex = pointToIndex(adjustedMax);

		if (minIndex == null || maxIndex == null)
			return;

		for (int i = minIndex.i; i <= maxIndex.i; i++) {
			for (int j = minIndex.j; j <= maxIndex.j; j++) {
				for (int k = minIndex.k; k <= maxIndex.k; k++) {
					Index3D idx = new Index3D(i, j, k);
					voxels.computeIfAbsent(idx, key -> new Voxel()).getGeometries().add(geometry);
				}
			}
		}
	}

	/**
	 * Retrieves the voxel at the specified index.
	 *
	 * @param index The voxel index.
	 * @return The voxel if present, or null if no voxel exists at the index.
	 */
	public Voxel getVoxel(Index3D index) {
		return voxels.get(index);
	}

	/**
	 * Checks whether a point lies inside the bounding box of the grid.
	 *
	 * @param p The point to check.
	 * @return True if inside bounding box, false otherwise.
	 */
	private boolean pointInsideBoundingBox(Point p) {
		double epsilon = 1e-5;
		return p.getX() >= boundingBox.minCorner().getX() - epsilon
				&& p.getX() <= boundingBox.maxCorner().getX() + epsilon
				&& p.getY() >= boundingBox.minCorner().getY() - epsilon
				&& p.getY() <= boundingBox.maxCorner().getY() + epsilon
				&& p.getZ() >= boundingBox.minCorner().getZ() - epsilon
				&& p.getZ() <= boundingBox.maxCorner().getZ() + epsilon;
	}

	/**
	 * Returns the bounding box enclosing the entire grid.
	 *
	 * @return The bounding box.
	 */
	public AABB getBoundingBox() {
		return boundingBox;
	}

	/**
	 * Returns the size (edge length) of each voxel.
	 *
	 * @return The voxel size.
	 */
	public double getVoxelSize() {
		return voxelSize;
	}

	/**
	 * Returns the number of voxels along the X axis.
	 *
	 * @return The number of voxels on X axis.
	 */
	public int getSizeX() {
		return sizeX;
	}

	/**
	 * Returns the number of voxels along the Y axis.
	 *
	 * @return The number of voxels on Y axis.
	 */
	public int getSizeY() {
		return sizeY;
	}

	/**
	 * Returns the number of voxels along the Z axis.
	 *
	 * @return The number of voxels on Z axis.
	 */
	public int getSizeZ() {
		return sizeZ;
	}

	/**
	 * Returns the internal mapping of voxel indices to voxels.
	 *
	 * @return Map of voxels.
	 */
	public Map<Index3D, Voxel> getVoxels() {
		return voxels;
	}
}
