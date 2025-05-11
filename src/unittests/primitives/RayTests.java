/**
 * 
 */
package unittests.primitives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

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

	/**
	 * Test method for {@link primitives.Ray#findClosesetPoint(List<Point>)}.
	 */
	@Test
	void testFindClosestPoint() {

		Ray ray = new Ray(new Point(1, 0, 0), new Vector(0, 1, 0));
		Point p1 = new Point(1, 2, 0);
		Point p2 = new Point(1, 3, 0);
		Point p3 = new Point(1, 4, 0);

		// ============ Equivalence Partitions Tests ==============

		// TC01: The closet point is in the middle of the list
		assertEquals(p1, ray.findClosestPoint(List.of(p2, p1, p3)), "wrong closest point");

		// =============== Boundary Values Tests ==================

		// TC11: Empty list

		assertNull(ray.findClosestPoint(List.of()), "Empty list");

		// TC12: The closet point is in the beginning of the list

		assertEquals(p1, ray.findClosestPoint(List.of(p1, p2, p3)), "wrong closest point");

		// TC13: The closet point is at the end of the list

		assertEquals(p1, ray.findClosestPoint(List.of(p2, p3, p1)), "wrong closest point");

	}
}
