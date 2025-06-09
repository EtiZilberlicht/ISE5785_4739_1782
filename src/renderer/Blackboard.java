package renderer;

import static primitives.Util.isZero;

import java.util.ArrayList;
import java.util.List;

import geometries.Plane;
import primitives.Point;
import primitives.Vector;

/**
 * The Blackboard class generates a grid of rays (a beam) from a source point
 * through a plane, simulating effects like soft shadows or blurry reflections
 * by spreading rays across a jittered grid.
 */
public class Blackboard {

	/**
	 * The size of the grid over the plane in world units.
	 */
	int gridSize = 81;

	/**
	 * The number of rays to be generated (typically a square number like 16, 25,
	 * 36...).
	 */
	int numOfRays = 81;

	/**
	 * Number of divisions in one axis of the grid, calculated as sqrt(numOfRays).
	 */
	double dividedRays;

	/**
	 * Size of each cell in the grid, calculated as gridSize / dividedRays.
	 */
	double cellSize;

	/**
	 * Default constructor. Initializes the internal grid parameters based on
	 * default values.
	 */
	public Blackboard() {
		updateGrid();
	}

	/**
	 * Updates internal values for dividedRays and cellSize based on current
	 * gridSize and numOfRays.
	 */
	private void updateGrid() {
		this.dividedRays = Math.sqrt(this.numOfRays);
		this.cellSize = this.gridSize / this.dividedRays;
	}

	/**
	 * Sets the size of the grid used by the blackboard.
	 *
	 * @param gridSize the new size for the grid
	 * @return this blackboard instance for method chaining
	 */
	public Blackboard setGridSize(int gridSize) {
		this.gridSize = gridSize;
		updateGrid();
		return this;
	}

	/**
	 * Sets the number of rays to be used in rendering or calculations.
	 *
	 * @param numOfRays the number of rays to set
	 * @return this blackboard instance for method chaining
	 */
	public Blackboard setNumOfRays(int numOfRays) {
		this.numOfRays = numOfRays;
		updateGrid();
		return this;
	}

	/**
	 * Generates a beam of vectors from a source point through jittered positions on
	 * a grid placed over a given plane. The grid is centered around the provided
	 * position.
	 * 
	 * Each ray is directed from the source toward a slightly jittered point in the
	 * cell.
	 * 
	 * @param position the center point of the grid on the plane
	 * @param source   the origin of the rays
	 * @param plane    the plane on which the grid is defined
	 * @return list of normalized direction vectors representing the beam
	 */
	public List<Vector> vectorBeam(Point position, Point source, Plane plane) {
		List<Vector> vectors = new ArrayList<>();
		Vector v1 = plane.getV1();
		Vector v2 = plane.getV2();
		Point startPoint = position.add(v1.scale(-gridSize / 2.0)).add(v2.normalize().scale(-gridSize / 2.0));
		for (int i = 0; i < dividedRays; i++) {
			for (int j = 0; j < dividedRays; j++) {

				double jitter1 = (i + Math.random()) * cellSize;
				double jitter2 = (j + Math.random()) * cellSize;
				Point p = startPoint;
				if (!isZero(jitter1))
					p = p.add(v1.scale(jitter1));
				if (!isZero(jitter2))
					p = p.add(v2.scale(jitter2));

				vectors.add(source.subtract(p).normalize());
			}
		}
		return vectors;
	}

}
