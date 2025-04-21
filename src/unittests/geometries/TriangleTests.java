/**
 * 
 */
package unittests.geometries;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import primitives.*;
import java.util.List;
import geometries.Triangle;

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
	/** A point used in some tests */
    private final Point p100 = new Point(1, 0, 0);
	/** A vector used in some tests */
    private final Vector vm100 = new Vector(-1, 0, 0);
	/** A vector used in some tests */
    private final Vector vm101 = new Vector(-1, 0, 1);

    /**
     * Test method for {@link geometries.Sphere#findIntersections(primitives.Ray)}.
     */
    @Test
    public void testFindIntersections() {
        Triangle triangle = new Triangle(new Point(0, 1, 0),
                new Point(0, 5, 0),
                new Point(0, 3, 5));

        // ============ Equivalence Partitions Tests ==============
        // TC01: The intersection point is in the triangle
        assertEquals(List.of(new Point(0, 3, 1)),
                triangle.findIntersections(new Ray(new Point(1, 3, 0), vm101)),
                "The point is not in the triangle");

        // TC02: The intersection point is outside the triangle, against edge
        assertNull(triangle.findIntersections(new Ray(p100, vm101)),
                "The point is not outside the triangle, against edge");

        // TC03: The intersection point is outside the triangle, against vertex
        assertNull(triangle.findIntersections(new Ray(p100,
                        new Vector(-1, 0.1, -0.1))),
                "The point is not outside the triangle, against vertex");

        // =============== Boundary Values Tests ==================
        // TC10: The point is on edge
        assertNull(triangle.findIntersections(new Ray(new Point(1, 3, 0), vm100)),
                "The point is not on edge");

        // TC11: The point is in vertex
        assertNull(triangle.findIntersections(new Ray(new Point(1, 1, 0), vm100)),
                "The point is not in vertex");

        // TC12: The point is on edge's continuation
        assertNull(triangle.findIntersections(new Ray(p100,
                        new Vector(-1, 0.1, 0))),
                "The point is not on edge's continuation");
    }

}
