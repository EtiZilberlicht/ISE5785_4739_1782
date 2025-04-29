package unittests.geometries;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import geometries.Polygon;
import primitives.*;

/**
 * Testing Polygons
 * 
 * @author Dan
 */
class PolygonTests {

	/**
	 * Default constructor
	 */
	public PolygonTests() {
	}

	/**
	 * Delta value for accuracy when comparing the numbers of type 'double' in
	 * assertEquals
	 */
	private static final double DELTA = 0.000001;

	/** Test method for {@link geometries.Polygon#Polygon(primitives.Point...)}. */
	@Test
	void testConstructor() {
		// ============ Equivalence Partitions Tests ==============

		// TC01: Correct concave quadrangular with vertices in correct order
		assertDoesNotThrow(
				() -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(-1, 1, 1)),
				"Failed constructing a correct polygon");

		// TC02: Wrong vertices order
		assertThrows(IllegalArgumentException.class, //
				() -> new Polygon(new Point(0, 0, 1), new Point(0, 1, 0), new Point(1, 0, 0), new Point(-1, 1, 1)), //
				"Constructed a polygon with wrong order of vertices");

		// TC03: Not in the same plane
		assertThrows(IllegalArgumentException.class, //
				() -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 2, 2)), //
				"Constructed a polygon with vertices that are not in the same plane");

		// TC04: Concave quadrangular
		assertThrows(IllegalArgumentException.class, //
				() -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0),
						new Point(0.5, 0.25, 0.5)), //
				"Constructed a concave polygon");

		// =============== Boundary Values Tests ==================

		// TC10: Vertex on a side of a quadrangular
		assertThrows(IllegalArgumentException.class, //
				() -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 0.5, 0.5)),
				"Constructed a polygon with vertix on a side");

		// TC11: Last point = first point
		assertThrows(IllegalArgumentException.class, //
				() -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 0, 1)),
				"Constructed a polygon with vertice on a side");

		// TC12: Co-located points
		assertThrows(IllegalArgumentException.class, //
				() -> new Polygon(new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(0, 1, 0)),
				"Constructed a polygon with vertice on a side");

	}

	/** Test method for {@link geometries.Polygon#getNormal(primitives.Point)}. */
	@Test
	void testGetNormal() {
		// ============ Equivalence Partitions Tests ==============
		// TC01: There is a simple single test here - using a quad
		Point[] pts = { new Point(0, 0, 1), new Point(1, 0, 0), new Point(0, 1, 0), new Point(-1, 1, 1) };
		Polygon pol = new Polygon(pts);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> pol.getNormal(new Point(0, 0, 1)), "");
		// generate the test result
		Vector result = pol.getNormal(new Point(0, 0, 1));
		// ensure |result| = 1
		assertEquals(1, result.length(), DELTA, "Polygon's normal is not a unit vector");
		// ensure the result is orthogonal to all the edges
		for (int i = 0; i < 3; ++i)
			assertEquals(0d, result.dotProduct(pts[i].subtract(pts[i == 0 ? 3 : i - 1])), DELTA,
					"Polygon's normal is not orthogonal to one of the edges");
	}

	/** A point used in some tests */
	private static final Point P010 = new Point(0, 1, 0);
	/** A point used in some tests */
	private static final Point P005 = new Point(0, 0, 5);
	/** A vector used in some tests */
	private static final Vector VM101 = new Vector(-1, 0, 1);
	/** A vector used in some tests */
	private static final Vector VM100 = new Vector(-1, 0, 0);
	/** A vector used in some tests */
	private static final Vector V001 = new Vector(0, 0, 1);
	/** A vector used in some tests */
	private static final Vector V1M11 = new Vector(1, -1, 1);

	/**
	 * Test method for {@link geometries.Polygon#findIntersections(primitives.Ray)}.
	 */
	@Test
	void testFindIntersections() {
		Polygon polygon = new Polygon(P010, new Point(0, 1, 1), new Point(0, 3, 5), new Point(0, 5, 0));

		// ============ Equivalence Partitions Tests ==============

		// TC01: Ray's line is Inside polygon (1 points)
		assertEquals(List.of(new Point(0, 3, 2)), polygon.findIntersections(new Ray(new Point(1, 3, 1), VM101)),
				"The point is not in the polygon");

		// TC02: Ray's line is Outside against edge (0 points)
		assertNull(polygon.findIntersections(new Ray(new Point(1, 5, 2), VM101)),
				"Bad intersects to polygon - line is Outside against edge");

		// TC03: Ray's line is Outside against vertex (0 points)
		assertNull(polygon.findIntersections(new Ray(new Point(1, 3, 7), VM101)),
				"Bad intersects to polygon - line is Outside against vertex");

		// TC04: Ray does not intersect the plane
		assertNull(polygon.findIntersections(new Ray(new Point(1, 3, 7), V1M11)),
				"Bad intersects to polygon - line is Outside against vertex");

		// =============== Boundary Values Tests ==================

		// **** Group: Ray intersects the plane

		// TC11: Ray's line is On edge (0 points)
		assertNull(polygon.findIntersections(new Ray(new Point(2, 4, 0.5), VM101)),
				"Bad intersects to polygon - line is On edge");

		// TC12: Ray's line is In vertex (0 points)
		assertNull(polygon.findIntersections(new Ray(new Point(1, 1, 0), VM101)),
				"Bad intersects to polygon - line is In vertex");

		// TC13: Ray's line is On edge's continuation (0 points)
		assertNull(polygon.findIntersections(new Ray(new Point(1, 1, 1), VM101)),
				"Bad intersects to polygon - line is On edge's continuation");

		// **** Group: Ray does not intersect the plane

		// TC21: The ray included in the plane
		assertNull(polygon.findIntersections(new Ray(P005, V001)),
				"Does not return null- when ray included in the plane");

		// TC22: The ray is parallel and not included in the plane
		assertNull(polygon.findIntersections(new Ray(new Point(1, 0, 5), V001)),
				"Does not return null- when ray not included in the plane");

		// TC23: Ray is orthogonal and on the plane
		assertNull(polygon.findIntersections(new Ray(P005, VM100)),
				"Does not return null- when ray is orthogonal to the plane, on the plane");

		// TC24: Ray is orthogonal and after the plane
		assertNull(polygon.findIntersections(new Ray(new Point(-1, 0, 5), VM100)),
				"Does not return null- when ray is orthogonal to the plane, after the plane");
		// TC25: Ray begins at the plane
		assertNull(polygon.findIntersections(new Ray(P005, V1M11)),
				"Does not return null- when ray is neither orthogonal nor parallel to ray and begin at the plane");

		// TC26: Ray begins in the same point which appears as reference point in the
		// plane
		assertNull(polygon.findIntersections(new Ray(P010, V1M11)),
				"Does not return null- when ray begins in the same point which appears as reference point in the plane");

	}

}
