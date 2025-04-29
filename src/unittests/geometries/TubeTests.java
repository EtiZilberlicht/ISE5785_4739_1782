/**
 * 
 */
package unittests.geometries;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.util.List;
import geometries.Tube;
import primitives.*;

/**
 * Unit tests for geometries.Tube class
 * 
 * @author Eti and Meitav
 */
class TubeTests {

	/**
	 * Default constructor
	 */
	public TubeTests() {
	}

	/**
	 * Delta value for accuracy when comparing the numbers of type 'double' in
	 * assertEquals
	 */
	private static final double DELTA = 0.000001;

	/**
	 * Test method for {@link geometries.Tube#getNormal(primitives.Point)}.
	 */
	@Test
	void testGetNormal() {
		// Tube on Z axis
		Point head = new Point(0, 0, 0);
		Vector direction = new Vector(0, 0, 1);
		Tube tube = new Tube(new Ray(head, direction), 1);

		// ============ Equivalence Partitions Tests ==============

		// TC01: A point not orthogonal to the head
		Point onSurface1 = new Point(1, 0, 1);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> tube.getNormal(onSurface1), "");
		// generate the test result
		Vector normal = tube.getNormal(onSurface1);
		// ensure |normal| = 1
		assertEquals(1, normal.length(), DELTA, "Tube normal is not a unit vector");
		// correction of normal
		assertEquals(new Vector(1, 0, 0), normal, "Tube normal wrong value");

		// =============== Boundary Values Tests ==================

		// TC01: A point orthogonal to the head
		Point onSurface2 = new Point(1, 0, 0);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> tube.getNormal(onSurface2), "");
		// generate the test result
		normal = tube.getNormal(onSurface2);
		// ensure |normal| = 1
		assertEquals(1, normal.length(), DELTA, "Tube normal is not a unit vector");
		// correction of normal
		assertEquals(new Vector(1, 0, 0), normal, "Tube normal wrong value");

	}

	/** A point used in some tests */
	private static final Point P020 = new Point(0, 2, 0);
	/** A point used in some tests */
	private static final Point P021 = new Point(0, 2, 1);
	/** A vector used in some tests */
	private static final Vector V100 = new Vector(1, 0, 0);
	/** A vector used in some tests */
	private static final Vector VM100 = new Vector(-1, 0, 0);
	/** A vector used in some tests */
	private static final Vector V001 = new Vector(0, 0, 1);
	/** A vector used in some tests */
	private static final Vector V1540 = new Vector(1.5, 4, 0);
	/** A vector used in some tests */
	private static final Vector V13M3 = new Vector(1, 3, -3);
	/** A vector used in some tests */
	private static final Vector V1251 = new Vector(1, 2.5, 1);

	/**
	 * Test method for {@link geometries.Tube#findIntersections(primitives.Ray)}.
	 */
	@Test
	public void testFindIntersections() {
		Tube tube = new Tube(new Ray(P020, V100), 1);

		// ============ Equivalence Partitions Tests ==============

		// TC01: the ray starts from inside, intersects with the tube (1 point)
		assertEquals(new Point(2.4, 1.2, 0.6),
				tube.findIntersections(new Ray(new Point(2, 1.5, 0.5), new Vector(2, -1.5, 0.5))).get(0),
				"Ray crosses tube from inside incorrectly.");

		// TC02: the ray starts from outside, intersects with the tube (2 point)
		Point p1 = new Point(1.4409964637611044, 1.10249115940276, 0.440996463761104);
		Point p2 = new Point(1.938313881066482, 2.345784702666205, 0.938313881066482);
		assertEquals(List.of(p1, p2), tube.findIntersections(new Ray(new Point(1, 0, 0), V1251)),
				"Ray crosses tube from inside incorrectly.");

		// TC03: the ray starts from outside, doesn't intersect with the tube (0 points)
		assertNull(tube.findIntersections(new Ray(new Point(2, 2.5, 1), V1251)), "Ray out of tube");

		// =============== Boundary Values Tests ==================

		// **** Group: Ray is parallel to the axis ray at the same direction

		// TC11: the ray is parallel to the tube from the outside
		assertNull(tube.findIntersections(new Ray(new Point(1, 2, 2), V100)), "Ray's parallel to tube");

		// TC12: the ray is on top of the tube's surface and is parallel to the tube
		// axis ray
		assertNull(tube.findIntersections(new Ray(new Point(1, 2, 1), new Vector(1, 0, 0))),
				"Ray's parallel to tube and on top of its surface");

		// TC13: the ray is inside and is parallel to the tube
		// axis ray
		assertNull(tube.findIntersections(new Ray(new Point(1, 2, 0.5), V100)),
				"Ray's parallel to tube and on top of its surface");

		// TC14: the ray merges after axis ray point
		assertNull(tube.findIntersections(new Ray(new Point(1, 2, 0), V100)),
				"Ray's parallel to tube and on top of its surface");

		// TC15: the ray merges before axis ray point
		assertNull(tube.findIntersections(new Ray(new Point(-1, 2, 0), V100)),
				"Ray's parallel to tube and on top of its surface");

		// TC16: the ray merges with axis ray (point and ray)
		assertNull(tube.findIntersections(new Ray(P020, V100)), "Ray's parallel to tube and on top of its surface");

		// **** Group: Ray is parallel to the axis ray at the opposite direction

		// TC21: the ray is parallel to the tube from the outside
		assertNull(tube.findIntersections(new Ray(new Point(1, 2, 2), VM100)), "Ray's parallel to tube");

		// TC22: the ray is on top of the tube's surface and is parallel to the tube
		// axis ray
		assertNull(tube.findIntersections(new Ray(new Point(1, 2, 1), VM100)),
				"Ray's parallel to tube and on top of its surface");

		// TC23: the ray is inside and is parallel to the tube
		// axis ray
		assertNull(tube.findIntersections(new Ray(new Point(1, 2, 0.5), VM100)),
				"Ray's parallel to tube and on top of its surface");

		// TC24: the ray merges after axis ray point
		assertNull(tube.findIntersections(new Ray(new Point(1, 2, 0), VM100)),
				"Ray's parallel to tube and on top of its surface");

		// TC25: the ray merges before axis ray point
		assertNull(tube.findIntersections(new Ray(new Point(-1, 2, 0), VM100)),
				"Ray's parallel to tube and on top of its surface");

		// TC26: same point, opposite direction
		assertNull(tube.findIntersections(new Ray(P020, VM100)), "Ray's parallel to tube and on top of its surface");

		// **** Group: Ray is orthogonal and perpendicular

		// TC31: the ray is perpendicular to the tube from the outside (0 points)
		assertNull(tube.findIntersections(new Ray(new Point(0, 2, 2), V001)), "Ray's perpendicular to tube");

		// TC32: the ray is orthogonal to the tube (0 points)
		assertNull(tube.findIntersections(new Ray(P021, V001)), "Ray's orthogonal to tube");

		// TC33: the ray is perpendicular to the tube from the inside (1 points)
		assertEquals(P021, tube.findIntersections(new Ray(new Point(0, 2, 0.5), V001)).get(0),
				"Ray crosses tube from inside incorrectly.");

		// TC34: the ray is orthogonal from the tube's axis ray point (1 points)
		assertEquals(P021, tube.findIntersections(new Ray(P020, V001)).get(0),
				"Ray crosses tube from inside incorrectly.");

		// TC35: the ray is perpendicular to the tube from the inside before axis ray
		// point (1 points)
		assertEquals(P021, tube.findIntersections(new Ray(new Point(0, 2, -0.5), V001)).get(0),
				"Ray crosses tube from inside incorrectly.");

		// TC36: the ray is orthogonal to the tube (1 point)
		assertEquals(P021, tube.findIntersections(new Ray(new Point(0, 2, -1), V001)).get(0),
				"Ray crosses tube from inside incorrectly.");

		// TC37: the ray is perpendicular to the tube from the inside before axis ray
		// point (2 points)
		assertEquals(List.of(new Point(0, 2, -1), P021), tube.findIntersections(new Ray(new Point(0, 2, -2), V001)),
				"Ray crosses through the tube incorrectly.");

		// **** Group: Ray is tangent to tube

		// TC41: before tangent point (0 points)
		assertNull(tube.findIntersections(new Ray(new Point(1.5, 0, 1), V1540)),
				"Ray's tangent to tube, before tangent point");

		// TC42: at tangent point (0 points)
		assertNull(tube.findIntersections(new Ray(new Point(2.25, 2, 1), V1540)),
				"Ray's tangent to tube, at tangent point");

		// TC43: after tangent point (0 points)
		assertNull(tube.findIntersections(new Ray(new Point(3, 4, 1), V1540)),
				"Ray's tangent to tube, after tangent point");

		// **** Group: Ray go through axis ray

		// TC51: outside of tube, facing the tube (2 points)
		Point p3 = new Point(1.097631072937818, 1.292893218813453, 0.707106781186547);
		Point p4 = new Point(1.569035593728849, 2.707106781186548, -0.707106781186548);
		assertEquals(List.of(p3, p4), tube.findIntersections(new Ray(new Point(1, 1, 1), V13M3)),
				"Ray crosses through the tube incorrectly.");

		// TC52: on tube surface, facing the tube (1 point)
		assertEquals(p4, tube.findIntersections(new Ray(p3, V13M3)).get(0),
				"Ray crosses through the tube incorrectly.");

		// TC53: inside tube, before tube axis ray (1 point)
		Point p5 = new Point(1.238240344551198, 1.714721033653591, 0.285278966346409);
		assertEquals(p4, tube.findIntersections(new Ray(p5, V13M3)).get(0),
				"Ray crosses through the tube incorrectly.");

		// TC54: on tube axis ray (1 point)
		Point p6 = new Point(1.333333333333334, 2, 0);
		assertEquals(p4, tube.findIntersections(new Ray(p6, V13M3)).get(0),
				"Ray crosses through the tube incorrectly.");

		// TC55: inside tube after axis ray (1 point)
		Point p7 = new Point(1.465399710013591, 2.396199130040771, -0.396199130040771);
		assertEquals(p4, tube.findIntersections(new Ray(p7, V13M3)).get(0),
				"Ray crosses through the tube incorrectly.");

		// TC56: on tube surface, facing the outside (0 points)
		assertNull(tube.findIntersections(new Ray(p4, V13M3)), "Ray's point on tube surface");

		// TC57: outside the tube, facing the outside (0 points)
		assertNull(tube.findIntersections(new Ray(new Point(2, 4, -2), V13M3)), "Ray's point outside the tube");

		// **** Group: Ray go through axis ray and point

		// TC61: outside of tube, facing the tube (2 points)
		Point p8 = new Point(-0.33333333333333, 1, 1);
		Point p9 = new Point(-0.235702260395515, 1.292893218813453, 0.707106781186547);
		Point p10 = new Point(0.235702260395516, 2.707106781186548, -0.707106781186548);
		assertEquals(List.of(p9, p10), tube.findIntersections(new Ray(p8, V13M3)),
				"Ray crosses through the tube incorrectly.");

		// TC62: on tube surface, facing the tube (1 point)
		assertEquals(p10, tube.findIntersections(new Ray(p9, V13M3)).get(0),
				"Ray crosses through the tube incorrectly.");

		// TC63: inside tube, before tube axis ray (1 point)
		Point p11 = new Point(-0.095092988781802, 1.714721033653591, 0.285278966346409);
		assertEquals(p10, tube.findIntersections(new Ray(p11, V13M3)).get(0),
				"Ray crosses through the tube incorrectly.");

		// TC64: on tube axis ray (1 point)
		assertEquals(p10, tube.findIntersections(new Ray(P020, V13M3)).get(0),
				"Ray crosses through the tube incorrectly.");

		// TC65: inside tube after axis ray (1 point)
		Point p12 = new Point(0.132066376680591, 2.396199130040771, -0.396199130040771);
		assertEquals(p10, tube.findIntersections(new Ray(p12, V13M3)).get(0),
				"Ray crosses through the tube incorrectly.");

		// TC66: on tube surface, facing the outside (0 points)
		assertNull(tube.findIntersections(new Ray(p10, V13M3)), "Ray's point on tube surface");

		// TC67: outside the tube, facing the outside (0 points)
		Point p13 = new Point(0.66666666667, 4, -2);
		assertNull(tube.findIntersections(new Ray(p13, V13M3)), "Ray's point outside the tube");

		// **** Group: Surface general bva

		// TC71: the ray starts from tube surface, intersects with the tube (1 point)
		// using p1 and p2
		assertEquals(p2, tube.findIntersections(new Ray(p1, V1251)).get(0),
				"Ray crosses tube from inside incorrectly.");

		// TC72: the ray starts from tube surface, doesn't intersect with the tube (0
		// point)
		assertNull(tube.findIntersections(new Ray(p2, V1251)), "Ray's point outside the tube");

	}
}