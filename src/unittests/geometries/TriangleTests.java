/**
 * 
 */
package unittests.geometries;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import geometries.Triangle;
import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for geometries.Triangle class
 * 
 * @author Eti and Meitav
 */
class TriangleTests {

	/**
	 * Default constructor
	 */
	public TriangleTests() {
	}

	/**
	 * Delta value for accuracy when comparing the numbers of type 'double' in
	 * assertEquals
	 */
	private static final double DELTA = 0.000001;

	/**
	 * Test method for {@link geometries.Polygon#getNormal(primitives.Point)}.
	 */
	@Test
	void testGetNormal() {
		// ============ Equivalence Partitions Tests ==============

		// TC01: Triangle on plane XY
		Point p1 = new Point(0, 0, 0);
		Point p2 = new Point(0, 1, 0);
		Point p3 = new Point(1, 0, 0);
		Triangle triangle = new Triangle(p1, p2, p3);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> triangle.getNormal(p1), "");
		// generate the test result
		Vector normal = triangle.getNormal(p1);
		// Check if the calculated normal matches one of the expected directions
		assertTrue(normal.equals(new Vector(0, 0, 1)) || normal.equals(new Vector(0, 0, -1)),
				"Normal vector should be in one of the two possible directions");
	}

}
