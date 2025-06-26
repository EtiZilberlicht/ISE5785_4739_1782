package renderer.grid;

import java.util.Objects;

/**
 * Represents a 3D integer index, typically used to identify the coordinates of
 * a voxel within a 3D grid.
 */
public class Index3D {
	/** The X-axis index */
	public final int i;

	/** The Y-axis index */
	public final int j;

	/** The Z-axis index */
	public final int k;

	/**
	 * Constructs a 3D index with the given coordinates.
	 *
	 * @param i the X-axis index
	 * @param j the Y-axis index
	 * @param k the Z-axis index
	 */
	public Index3D(int i, int j, int k) {
		this.i = i;
		this.j = j;
		this.k = k;
	}

	/**
	 * Returns a new Index3D which is the sum of this index and the given offsets.
	 *
	 * @param di offset in the i (X) direction
	 * @param dj offset in the j (Y) direction
	 * @param dk offset in the k (Z) direction
	 * @return a new Index3D with updated coordinates
	 */
	public Index3D add(int di, int dj, int dk) {
		return new Index3D(i + di, j + dj, k + dk);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Index3D))
			return false;
		Index3D index3D = (Index3D) o;
		return i == index3D.i && j == index3D.j && k == index3D.k;
	}

	@Override
	public int hashCode() {
		return Objects.hash(i, j, k);
	}

	@Override
	public String toString() {
		return "Index3D(" + i + ", " + j + ", " + k + ")";
	}
}
