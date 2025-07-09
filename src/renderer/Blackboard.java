package renderer;

import static java.lang.Math.PI;
import static java.lang.Math.random;
import static java.lang.Math.sqrt;
import static primitives.Util.isZero;

import java.util.LinkedList;
import java.util.List;

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
	double gridSize = 0;

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
	 * The shape of the beam area: "square" (default) or "circle".
	 */
	String shape = "square";

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
		this.dividedRays = sqrt(this.numOfRays);
		this.cellSize = this.gridSize / this.dividedRays;
	}

	/**
	 * Sets the size of the grid used by the blackboard.
	 *
	 * @param gridSize the new size for the grid
	 * @return this blackboard instance for method chaining
	 */
	public Blackboard setGridSize(double gridSize) {
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
		if ("circle".equals(shape)) {
			this.numOfRays = (int) (numOfRays * (PI / 4));
		} else {
			this.numOfRays = numOfRays;
		}
		updateGrid();
		return this;
	}

	/**
	 * Sets the shape of the grid area used for ray generation. Valid values are
	 * "square" and "circle". If the shape is "circle", the number of rays is
	 * adjusted by multiplying the current value by π/4 to compensate for the
	 * reduced area. If an invalid value is provided, the shape defaults to
	 * "square".
	 *
	 * @param shape the desired shape of the ray grid ("square" or "circle")
	 * @return this blackboard instance for method chaining
	 */
	public Blackboard setShape(String shape) {
		if ("circle".equals(shape) || "square".equals(shape)) {
			this.shape = shape;
			this.numOfRays = (int) (numOfRays * (PI / 4));
		} else
			this.shape = "square";

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
	 * @param v1       the vector on which the grid is defined
	 * @param v2       the vector on which the grid is defined
	 * @return list of normalized direction vectors representing the beam
	 */
	public List<Vector> vectorBeam(Point position, Point source, Vector v1, Vector v2) {
		List<Vector> vectors = new LinkedList<>();
		Point startPoint = position.add(v1.scale(-gridSize / 2d)).add(v2.normalize().scale(-gridSize / 2d));
		double radius = gridSize / 2d;

		for (int i = 0; i < dividedRays; i++) {
			for (int j = 0; j < dividedRays; j++) {
				double jitter1 = (i + random()) * cellSize;
				double jitter2 = (j + random()) * cellSize;

				double x = jitter1 - radius;
				double y = jitter2 - radius;

				// Circle filtering
				if ("circle".equals(shape) && (x * x + y * y > radius * radius)) {
					continue;
				}

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
