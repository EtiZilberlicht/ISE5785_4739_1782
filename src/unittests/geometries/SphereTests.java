/**
 * 
 */
package unittests.geometries;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import geometries.Sphere;
import primitives.*;

/**
 * Unit tests for geometries.Sphere class
 * 
 * @author Eti and Meitav
 */
class SphereTests {

	/**
	 * Default constructor
	 */
	public SphereTests() {
	}

	/**
	 * Test method for {@link geometries.Sphere#getNormal(primitives.Point)}.
	 */
	@Test
	void testGetNormal() {
		// ============ Equivalence Partitions Tests ==============

		// TC01: Sphere on center of coordinates
		Point center = new Point(0, 0, 0);
		Sphere sphere = new Sphere(1, center);
		Point onSurface = new Point(0, 0, 1);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> sphere.getNormal(onSurface), "");
		// generate the test result
		Vector normal = sphere.getNormal(onSurface);
		// correction of normal
		assertEquals(new Vector(0, 0, 1), normal, "Sphere normal wrong value");

	}

	/** A point used in some tests */
	private final Point p100 = new Point(1, 0, 0);
	/** A vector used in some tests */
	private final Vector v001 = new Vector(0, 0, 1);

	/**
	 * Test method for {@link geometries.Sphere#findIntersections(primitives.Ray)}.
	 */
	@Test
	void testFindIntersections() {
		Sphere sphere = new Sphere(1d, p100);
		final Point gp1 = new Point(0.0651530771650466, 0.355051025721682, 0);
		final Point gp2 = new Point(1.53484692283495, 0.844948974278318, 0);
		final Vector v310 = new Vector(3, 1, 0);
		final Vector v110 = new Vector(1, 1, 0);
		final Vector v010 = new Vector(0, 1, 0);
		final Vector v0m10 = v010.scale(-1);
		final Vector v011 = new Vector(0, 1, 1);
		final Point p01 = new Point(-1, 0, 0);
		final Point p101 = new Point(1, 0, 1);
		final Point p210 = new Point(2, 1, 0);
		final Point p10m1 = new Point(1, 0, -1);
		final Point p1m20 = new Point(1, -2, 0);
		final Point p1m10 = new Point(1, -1, 0);
		final Point p110 = new Point(1, 1, 0);

		// ============ Equivalence Partitions Tests ==============

		// TC01: Ray's line is outside the sphere (0 points)
		assertNull(sphere.findIntersections(new Ray(p01, v110)), "Ray's line out of sphere");
		// TC02: Ray starts before and crosses the sphere (2 points)
		final var exp1 = List.of(gp1, gp2);
		final var result1 = sphere.findIntersections(new Ray(p01, v310));
		assertNotNull(result1, "Can't be empty list");
		assertEquals(2, result1.size(), "Wrong number of points");
		assertEquals(exp1, result1, "Ray crosses sphere");
		// TC03: Ray starts inside the sphere (1 point)
		final var exp2 = List.of(p101);
		final var result2 = sphere.findIntersections(new Ray(p100, v001));
		assertNotNull(result2, "Can't be empty list");
		assertEquals(1, result2.size(), "Wrong number of points");
		assertEquals(exp2, result2, "Ray crosses sphere");
		// TC04: Ray starts after the sphere (0 points)
		assertNull(sphere.findIntersections(new Ray(p210, v310)), "Ray's line out of sphere");
		// =============== Boundary Values Tests ==================
		// **** Group 1: Ray's line crosses the sphere (but not the center)
		// TC11: Ray starts at sphere and goes inside (1 points)
		final var exp3 = List.of(p10m1);
		final var result3 = sphere.findIntersections(new Ray(p101, v001.scale(-1)));
		assertNotNull(result3, "Can't be empty list");
		assertEquals(1, result3.size(), "Wrong number of points");
		assertEquals(exp3, result3, "Ray crosses sphere");
		// TC12: Ray starts at sphere and goes outside (0 points)
		assertNull(sphere.findIntersections(new Ray(p101, v001)), "Ray's line out of sphere");
		// **** Group 2: Ray's line goes through the center
		// TC21: Ray starts before the sphere (2 points)
		final var exp4 = List.of(p1m10, p110);
		final var result4 = sphere.findIntersections(new Ray(p1m20, v010));
		assertNotNull(result4, "Can't be empty list");
		assertEquals(2, result4.size(), "Wrong number of points");
		assertEquals(exp4, result4, "Ray crosses sphere");
		// TC22: Ray starts at sphere and goes inside (1 points)
		final var exp5 = List.of(p110);
		final var result5 = sphere.findIntersections(new Ray(p1m10, v010));
		assertEquals(exp5, result5, "Ray crosses sphere");
		// TC23: Ray starts inside (1 points)
		final var exp6 = List.of(p110);
		final var result6 = sphere.findIntersections(new Ray(new Point(1, -0.5d, 0), v010));
		assertEquals(exp6, result6, "Ray crosses sphere");
		// TC24: Ray starts at the center (1 points)
		final var exp7 = List.of(p110);
		final var result7 = sphere.findIntersections(new Ray(p100, v010));
		assertEquals(exp7, result7, "Ray crosses sphere");
		// TC25: Ray starts at sphere and goes outside (0 points)
		assertNull(sphere.findIntersections(new Ray(p1m10, v0m10)), "Ray's line out of sphere");
		// TC26: Ray starts after sphere (0 points)
		assertNull(sphere.findIntersections(new Ray(new Point(1, -2, 0), v0m10)), "Ray's line out of sphere");
		// **** Group 3: Ray's line is tangent to the sphere (all tests 0 points)
		// TC31: Ray starts before the tangent point
		assertNull(sphere.findIntersections(new Ray(new Point(2, -1, -1), v011)), "Ray's line out of sphere");
		// TC32: Ray starts at the tangent point
		assertNull(sphere.findIntersections(new Ray(new Point(2, 0, 0), v011)), "Ray's line out of sphere");
		// TC33: Ray starts after the tangent point
		assertNull(sphere.findIntersections(new Ray(new Point(2, 2, 2), v011)), "Ray's line out of sphere");
		// **** Group 4: Special cases
		// TC41: Ray's line is outside sphere, ray is orthogonal to ray start to
		// sphere's center line
		assertNull(sphere.findIntersections(new Ray(new Point(3, 0, 0), v001)),
				"Ray's line out of sphere");
		// TC42: Ray's starts inside, ray is orthogonal to ray start to sphere's center
		// line
		final var exp8 = List.of(new Point (0.5,0.0,0.8660254037844386));
		final var result8 = sphere.findIntersections(new Ray(new Point(0.5, 0, 0), v001));
		assertEquals(exp8, result8, "Ray crosses sphere");
	}

}
