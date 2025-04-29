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
	private static final Point P100 = new Point(1, 0, 0);
	/** A point used in some tests */
	private static final Point P020 = new Point(0, 2, 0);
	/** A point used in some tests */
	private static final Point P010 = new Point(0, 1, 0);
	/** A vector used in some tests */
	private static final Vector VM100 = new Vector(-1, 0, 0);
	/** A vector used in some tests */
	private static final Vector VM101 = new Vector(-1, 0, 1);
	/** A vector used in some tests */
	private static final Vector V111 = new Vector(1, 1, 1);
	/** A vector used in some tests */
	private static final Vector V001 = new Vector(0, 0, 1);

	/**
	 * Test method for {@link geometries.Sphere#findIntersections(primitives.Ray)}.
	 */
	@Test
	public void testFindIntersections() {
		Triangle triangle = new Triangle(P010, new Point(0, 5, 0), new Point(0, 3, 5));

		// ============ Equivalence Partitions Tests ==============

		// TC01: The intersection point is in the triangle
		assertEquals(List.of(new Point(0, 3, 1)), triangle.findIntersections(new Ray(new Point(1, 3, 0), VM101)),
				"The point is not in the triangle");

		// TC02: The intersection point is outside the triangle, against edge
		assertNull(triangle.findIntersections(new Ray(P100, VM101)),
				"The point is not outside the triangle, against edge");

		// TC03: The intersection point is outside the triangle, against vertex
		assertNull(triangle.findIntersections(new Ray(P100, new Vector(-1, 0.1, -0.1))),
				"The point is not outside the triangle, against vertex");

		// TC04: Ray does not intersect the plane
		assertNull(triangle.findIntersections(new Ray(P100, V111)), "The point is not in the triangle");

		// =============== Boundary Values Tests ==================

		// **** Group: Ray intersects the plane

		// TC11: The point is on edge
		assertNull(triangle.findIntersections(new Ray(new Point(1, 3, 0), VM100)), "The point is not on edge");

		// TC12: The point is in vertex
		assertNull(triangle.findIntersections(new Ray(new Point(1, 1, 0), VM100)), "The point is not in vertex");

		// TC13: The point is on edge's continuation
		assertNull(triangle.findIntersections(new Ray(P100, new Vector(-1, 0.1, 0))),
				"The point is not on edge's continuation");

		// **** Group: Ray does not intersect the plane

		// TC21: The ray included in the plane
		assertNull(triangle.findIntersections(new Ray(P020, V001)), "The point is not in the triangle");

		// TC22: Ray is parallel to the plane
		assertNull(triangle.findIntersections(new Ray(P100, V001)), "The point is not in the triangle");

		// TC23: Ray is orthogonal to the plane on the plane
		assertNull(triangle.findIntersections(new Ray(P020, VM100)), "The point is not in the triangle");

		// TC24: Ray is orthogonal to the plane after the plane
		assertNull(triangle.findIntersections(new Ray(new Point(-1, 2, 0), VM100)), "The point is not in the triangle");

		// TC25: Ray begins at the plane
		assertNull(triangle.findIntersections(new Ray(P020, V111)), "The point is not in the triangle");

		// TC26: Ray begins in the same point which appears as reference point in the
		// plane
		assertNull(triangle.findIntersections(new Ray(P010, V111)), "The point is not in the triangle");

	}

}
