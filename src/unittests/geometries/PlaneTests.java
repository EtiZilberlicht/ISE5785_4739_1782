/**
 * 
 */
package unittests.geometries;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.util.List;

import geometries.Plane;
import primitives.*;

/**
 * Unit tests for geometries.Plane class
 * 
 * @author Eti and Meitav
 */
class PlaneTests {

	/**
	 * Default constructor
	 */
	public PlaneTests() {
	}

	/**
	 * Delta value for accuracy when comparing the numbers of type 'double' in
	 * assertEquals
	 */
	private static final double DELTA = 0.000001;

	/**
	 * Test method for
	 * {@link geometries.Plane#Plane(primitives.Point, primitives.Point, primitives.Point)}.
	 */
	@Test
	void testPlanePointPointPoint() {
		// ============ Equivalence Partitions Tests ==============

		// TC01: Checking if the normal is correct
		Point p1 = new Point(0, 0, 0);
		Point p2 = new Point(2, 0, 0);
		Point p3 = new Point(0, 2, 0);

		Plane plane = new Plane(p1, p2, p3);
		Vector normal = plane.getNormal(p1);
		Vector v1 = p1.subtract(p2);
		Vector v2 = p1.subtract(p3);

		assertEquals(1, normal.length(), DELTA, "Normal vector should have length 1");
		assertEquals(0, normal.dotProduct(v1), DELTA, "Normal should be perpendicular to first vector");
		assertEquals(0, normal.dotProduct(v2), DELTA, "Normal should be perpendicular to second vector");

		// =============== Boundary Values Tests ==================

		Point p4 = new Point(1, 2, 3);
		Point p5 = new Point(4, 5, 6);
		// TC10: First point and second point converge
		assertThrows(IllegalArgumentException.class, //
				() -> new Plane(p4, p4, p5), "Two identical points should throw exception");
		// TC11: First point and third point converge
		assertThrows(IllegalArgumentException.class, //
				() -> new Plane(p4, p5, p4), "Two identical points should throw exception");
		// TC12: Second point and third point converge
		assertThrows(IllegalArgumentException.class, //
				() -> new Plane(p4, p5, p5), "Two identical points should throw exception");
		// TC13: Second point and third point converge
		assertThrows(IllegalArgumentException.class, //
				() -> new Plane(p5, p5, p5), "All identical points should throw exception");
		// TC14: Second point and third point converge
		Point p6 = new Point(0, 0, 0);
		Point p7 = new Point(1, 1, 1);
		Point p8 = new Point(2, 2, 2);
		assertThrows(IllegalArgumentException.class, //
				() -> new Plane(p6, p7, p8), "The three points are on the same line");
	}

	/**
	 * Test method for {@link geometries.Plane#getNormal(primitives.Point)}.
	 */
	@Test
	void testGetNormal() {
		// ============ Equivalence Partitions Tests ==============

		// TC01: Plane XY
		Point p1 = new Point(0, 0, 0);
		Point p2 = new Point(0, 1, 0);
		Point p3 = new Point(1, 0, 0);
		Plane plane = new Plane(p1, p2, p3);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> plane.getNormal(p1), "");
		// generate the test result
		Vector normal = plane.getNormal(p1);
		// ensure |normal| = 1
		assertEquals(1, normal.length(), DELTA, "Plane's normal is not a unit vector");
		// Check if the calculated normal matches one of the expected directions
		assertTrue(normal.equals(new Vector(0, 0, 1)) || normal.equals(new Vector(0, 0, -1)),
				"Normal vector should be in one of the two possible directions");

	}

	/** A point used in some tests */
	private static final Point P122 = new Point(1, 2, 2);
	/** A point used in some tests */
	private static final Point P121 = new Point(1, 2, 1);
	/** A vector used in some tests */
	private static final Vector V001 = new Vector(0, 0, 1);
	/** A vector used in some tests */
	private static final Vector V100 = new Vector(1, 0, 0);
	/** A vector used in some tests */
	private static final Vector V235 = new Vector(2, 3, 5);

	/**
	 * Test method for {@link geometries.Sphere#findIntersections(primitives.Ray)}.
	 */
	@Test
	public void testFindIntersections() {
		Plane plane = new Plane(new Point(1, 0, 1), new Point(0, 1, 1), new Point(1, 1, 1));
		// ================ EP: The Ray must be neither orthogonal nor parallel to the
		// plane ==================
		// TC01: Ray intersects the plane
		assertEquals(List.of(new Point(1, 0.5, 1)),
				plane.findIntersections(new Ray(new Point(0, 0.5, 0), new Vector(1, 0, 1))),
				"Ray does not intersects the plane");

		// TC02: Ray does not intersect the plane
		assertNull(plane.findIntersections(new Ray(new Point(1, 0.5, 2), new Vector(1, 2, 5))),
				"Ray intersects the plane");
		// ====================== Boundary Values Tests =======================//
		// **** Group: Ray is parallel to the plane
		// TC10: The ray included in the plane
		assertNull(plane.findIntersections(new Ray(P121, V100)),
				"Does not return null- when ray included in the plane");

		// TC11: The ray not included in the plane
		assertNull(plane.findIntersections(new Ray(P122, V100)),
				"Does not return null- when ray not included in the plane");

		// **** Group: Ray is orthogonal to the plane
		// TC12: before the plane (1 point)
		assertEquals(List.of(new Point(1, 1, 1)), plane.findIntersections(new Ray(new Point(1, 1, 0), V001)),
				"Ray is orthogonal to the plane, before the plane");

		// TC13: on the plane
		assertNull(plane.findIntersections(new Ray(P121, V001)),
				"Does not return null- when ray is orthogonal to the plane, on the plane");

		// TC14: after the plane
		assertNull(plane.findIntersections(new Ray(P122, V001)),
				"Does not return null- when ray is orthogonal to the plane, after the plane");

		// **** Group: Ray is neither orthogonal nor parallel to
		// TC15: Ray begins at the plane
		assertNull(plane.findIntersections(new Ray(new Point(2, 4, 1), V235)),
				"Does not return null- when ray is neither orthogonal nor parallel to ray and begin at the plane");

		// TC16: Ray begins in the same point which appears as reference point in the
		// plane
		assertNull(plane.findIntersections(new Ray(new Point(1, 0, 1), V235)),
				"Does not return null- when ray begins in the same point which appears as reference point in the plane");

	}
}
