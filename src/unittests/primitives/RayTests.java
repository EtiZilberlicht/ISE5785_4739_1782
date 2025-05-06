/**
 * 
 */
package unittests.primitives;

import static org.junit.jupiter.api.Assertions.*;
import primitives.*;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for primitives.Ray class
 */
class RayTests {
	/**
	 * Default constructor
	 */
	public RayTests() {
	}

	/**
	 * Test method for {@link primitives.Ray#getPoint(double)}.
	 */
	@Test
	void testGetPoint() {

		Point p100 = new Point(1, 0, 0);
		Vector v001 = new Vector(0, 0, 1);
		Ray ray = new Ray(p100, v001);

		// ============ Equivalence Partitions Tests ==============

		// TC01: Positive distance
		assertEquals(new Point(1, 0, 3), ray.getPoint(3), "Positive distance does not work correctly");

		// TC02: Negative distance
		assertEquals(new Point(1, 0, -3), ray.getPoint(-3), "Negative distance does not work correctly");

		// =============== Boundary Values Tests ==================

		// TC11: distance 0
		assertEquals(p100, ray.getPoint(0), "0 distance does not work correctly");

	}

}
