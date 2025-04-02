/**
 * 
 */
package unittests.primitives;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for primitives.Point class
 * 
 * @author Eti and Meitav
 */
class PointTests {

	/**
	 * Default constructor
	 */
	public PointTests() {
	}

	/**
	 * Delta value for accuracy when comparing the numbers of type 'double' in
	 * assertEquals
	 */
	private static final double DELTA = 0.000001;

	/** A point for tests at (1,2,3) */
	private static final Point P1 = new Point(1, 2, 3);
	/** A point for tests at (2,4,6) */
	private static final Point P2 = new Point(2, 4, 6);
	/** A point for tests at (2,4,5) */
	private static final Point P3 = new Point(2, 4, 5);

	/** A vector for tests to (1,2,3) */
	private static final Vector V1 = new Vector(1, 2, 3);
	/** A vector for tests to (-1,-2,-3) (opposite to V1) */
	private static final Vector V1_OPPOSITE = new Vector(-1, -2, -3);

	/**
	 * Test method for {@link primitives.Point#subtract(primitives.Point)}.
	 */
	@Test
	void testSubtract() {
		// ============ Equivalence Partitions Tests ==============

		// TC01: Checks the correctness of the subtraction
		assertEquals(V1, P2.subtract(P1), "(point2 - point1) does not work correctly");

		// =============== Boundary Values Tests ==================

		// TC10: Subtracting a point from itself
		assertThrows(IllegalArgumentException.class, //
				() -> P1.subtract(P1), "Subtracting a point from itself creates the zero vector");

	}

	/**
	 * Test method for {@link primitives.Point#add(primitives.Vector)}.
	 */
	@Test
	void testAdd() {
		// ============ Equivalence Partitions Tests ==============

		// TC01: Checks the correctness of adding points
		assertEquals(P2, P1.add(V1), "(point + vector) = other point does not work correctly");

		// =============== Boundary Values Tests ==================

		// TC10: Adding a point with its opposite vector
		assertEquals(Point.ZERO, P1.add(V1_OPPOSITE),
				"(point + vector) = center of coordinates does not work correctly");
	}

	/**
	 * Test method for {@link primitives.Point#distanceSquared(primitives.Point)}.
	 */
	@Test
	void testDistanceSquared() {
		// ============ Equivalence Partitions Tests ==============

		// TC01: Checks the correctness of the squared distance
		assertEquals(9, P1.distanceSquared(P3), DELTA, "squared distance between points is wrong");

		// TC02: Checks the correctness of the squared distance
		assertEquals(9, P3.distanceSquared(P1), DELTA, "squared distance between points is wrong");

		// =============== Boundary Values Tests ==================

		// TC10: Squared distance of a point from itself
		assertEquals(0, P1.distanceSquared(P1), DELTA, "point squared distance to itself is not zero");
	}

	/**
	 * Test method for {@link primitives.Point#distance(primitives.Point)}.
	 */
	@Test
	void testDistance() {
		// ============ Equivalence Partitions Tests ==============

		// TC01: Checks the correctness of the distance
		assertEquals(3, P1.distance(P3), DELTA, "distance between points is wrong");

		// TC02: Checks the correctness of the distance
		assertEquals(3, P3.distance(P1), DELTA, "distance between points is wrong");

		// =============== Boundary Values Tests ==================

		// TC10: Distance of a point from itself
		assertEquals(0, P1.distance(P1), DELTA, "point distance to itself is not zero");
	}

}
