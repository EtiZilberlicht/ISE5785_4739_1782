/**
 * 
 */
package unittests.primitives;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for primitives.Vector class
 * @author Eti and Meitav
 */
class VectorTests {
	/**
	 * Delta value for accuracy when comparing the numbers of type 'double' in
	 * assertEquals
	 */
	private static final double DELTA = 0.000001;

	/** A vector for tests to (1,2,3) */
	private static final Vector V1 = new Vector(1, 2, 3);
	/** A vector for tests to (-1,-2,-3) (opposite to V1) */
	private static final Vector V1_OPPOSITE = new Vector(-1, -2, -3);
	/** A vector for tests to (-2,-4,-6) */
	private static final Vector V2 = new Vector(-2, -4, -6);
	/** A vector for tests to (0,3,-2) */
	private static final Vector V3 = new Vector(0, 3, -2);
	/** A vector for tests to (1,2,2) */
	private static final Vector V4 = new Vector(1, 2, 2);

	/**
	 * Test method for {@link primitives.Vector#add(primitives.Vector)}.
	 */
	@Test
	void testAddVector() {
		// ============ Equivalence Partitions Tests ==============

		// TC01: Checks the correctness of adding vectors
		assertEquals(V1_OPPOSITE, V1.add(V2), "Vector + Vector does not work correctly");

		// =============== Boundary Values Tests ==================

		// TC10: Adding a vector to its opposite
		assertThrows(IllegalArgumentException.class, //
				() -> V1.add(V1_OPPOSITE), "Vector + -itself does not throw an exception");
	}

	/**
	 * Test method for {@link primitives.Vector#scale(double)}.
	 */
	@Test
	void testScale() {
		// ============ Equivalence Partitions Tests ==============

		// TC01: Checks the correctness of multiplying a vector by a scalar number
		assertEquals(V2, V1.scale(-2), "Vector * scale number does not work correctly");

		// =============== Boundary Values Tests ==================

		// TC10: Checks the correctness of multiplying a vector by zero
		assertThrows(IllegalArgumentException.class, //
				() -> V1.scale(0), "Vector * 0 does not throw an exception");
	}

	/**
	 * Test method for {@link primitives.Vector#dotProduct(primitives.Vector)}.
	 */
	@Test
	void testDotProduct() {
		// ============ Equivalence Partitions Tests ==============

		// TC01: Checks the correctness of dot product
		assertEquals(-28, V1.dotProduct(V2), "dotProduct() wrong value");

		// =============== Boundary Values Tests ==================

		// TC10: Checks the correctness of dot product to orthogonal vectors
		assertEquals(0, V1.dotProduct(V3), "dotProduct() for orthogonal vectors is not zero");
	}

	/**
	 * Test method for {@link primitives.Vector#crossProduct(primitives.Vector)}.
	 */
	@Test
	void testCrossProduct() {
		// ============ Equivalence Partitions Tests ==============

		// TC01: Checks the correctness of cross product
		assertEquals(new Vector(-13, 2, 3), V1.crossProduct(V3), "crossProduct() wrong value");

		// =============== Boundary Values Tests ==================

		// TC10: Checks the correctness of cross product with two parallel vectors
		assertThrows(IllegalArgumentException.class, //
				() -> V1.crossProduct(V2), "crossProduct() for parallel vectors does not throw an exception");
	}

	/**
	 * Test method for {@link primitives.Vector#lengthSquared()}.
	 */
	@Test
	void testLengthSquared() {
		// ============ Equivalence Partitions Tests ==============

		// TC01: Checks the correctness of lengthSquared
		assertEquals(9, V4.lengthSquared(), "lengthSquared() wrong value");
	}

	/**
	 * Test method for {@link primitives.Vector#length()}.
	 */
	@Test
	void testLength() {
		// ============ Equivalence Partitions Tests ==============

		// TC01: Checks the correctness of length
		assertEquals(3, V4.lengthSquared(), "length() wrong value");
	}

	/**
	 * Test method for {@link primitives.Vector#normalize()}.
	 */
	@Test
	void testNormalize() {
		// ============ Equivalence Partitions Tests ==============

		// TC01: Checks the correctness of normalize vector as unit vector
		Vector v4_normalized = V4.normalize();
		assertEquals(1, v4_normalized.length(), "the normalized vector is not a unit vector");
		
		//Checks if the unit vector is parallel to the original vector
		assertThrows(IllegalArgumentException.class, //
				() -> V4.crossProduct(v4_normalized), "the normalized vector is not parallel to the original one");
		
		//Checks if the unit vector is parallel to the original vector
				assertThrows(IllegalArgumentException.class, //
						() -> V4.crossProduct(v4_normalized), "the normalized vector is not parallel to the original one");
	}

	/**
	 * Test method for {@link primitives.Point#subtract(primitives.Point)}.
	 */
	@Test
	void testSubtract() {
		// ============ Equivalence Partitions Tests ==============

		// TC01: Checks the correctness of subtracting vectors
		assertEquals(new Vector(3, 6, 9), V1.subtract(V2), "Vector - Vector does not work correctly");

		// =============== Boundary Values Tests ==================

		// TC10: Subtracting a vector to itself
		assertThrows(IllegalArgumentException.class, //
				() -> V1.subtract(V1), "Vector - itself does not throw an exception");
	}

}
