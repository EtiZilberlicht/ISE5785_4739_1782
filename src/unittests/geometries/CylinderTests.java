/**
 * 
 */
package unittests.geometries;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import geometries.Cylinder;
import primitives.*;

/**
 * Unit tests for geometries.Cylinder class
 * 
 * @author Eti and Meitav
 */
class CylinderTests {

	/**
	 * Default constructor
	 */
	public CylinderTests() {
	}

	/**
	 * Test method for {@link geometries.Cylinder#getNormal(primitives.Point)}.
	 */
	@Test
	void testGetNormal() {
		// Cylinder on Z axis
		Point head = new Point(0, 0, 0);
		Vector direction = new Vector(0, 0, 1);
		Cylinder cylinder = new Cylinder(1, new Ray(head, direction), 2);

		// ============ Equivalence Partitions Tests ==============

		// TC01: A point on the round surface
		Point onSurface1 = new Point(1, 0, 1);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> cylinder.getNormal(onSurface1), "");
		// generate the test result
		Vector normal = cylinder.getNormal(onSurface1);
		// correction of normal
		assertEquals(new Vector(1, 0, 0), normal, "Cylinder normal wrong value");

		// TC02: A point on bottom base
		Point onSurface2 = new Point(0.5, 0, 0);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> cylinder.getNormal(onSurface2), "");
		// generate the test result
		normal = cylinder.getNormal(onSurface2);
		// correction of normal
		assertEquals(new Vector(0, 0, -1), normal, "Cylinder normal wrong value");

		// TC03: A point on top base
		Point onSurface3 = new Point(0.5, 0, 2);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> cylinder.getNormal(onSurface3), "");
		// generate the test result
		normal = cylinder.getNormal(onSurface3);
		// correction of normal
		assertEquals(new Vector(0, 0, 1), normal, "Cylinder normal wrong value");

		// =============== Boundary Values Tests ==================

		// TC10: A point on the center of the bottom base
		Point onSurface4 = new Point(0, 0, 0);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> cylinder.getNormal(onSurface4), "");
		// generate the test result
		normal = cylinder.getNormal(onSurface4);
		// correction of normal
		assertEquals(new Vector(0, 0, -1), normal, "Cylinder normal wrong value");

		// TC11: A point on the center of the top base
		Point onSurface5 = new Point(0, 0, 2);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> cylinder.getNormal(onSurface5), "");
		// generate the test result
		normal = cylinder.getNormal(onSurface5);
		// correction of normal
		assertEquals(new Vector(0, 0, 1), normal, "Cylinder normal wrong value");

		// TC12: A point on the edge of the bottom base
		Point onSurface6 = new Point(1, 0, 0);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> cylinder.getNormal(onSurface6), "");
		// generate the test result
		normal = cylinder.getNormal(onSurface6);
		// correction of normal
		assertEquals(new Vector(0, 0, -1), normal, "Cylinder normal wrong value");

		// TC13: A point on the edge of the top base
		Point onSurface7 = new Point(1, 0, 2);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> cylinder.getNormal(onSurface7), "");
		// generate the test result
		normal = cylinder.getNormal(onSurface7);
		// correction of normal
		assertEquals(new Vector(0, 0, 1), normal, "Cylinder normal wrong value");

	}

	/**
	 * Test method for
	 * {@link geometries.Cylinder#findIntersections(primitives.Ray)}.
	 */
	@Test
	void testFindIntersections() {
		Cylinder cylinder2 = new Cylinder(2, new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)), 2);

		// ============ Equivalence Partitions Tests ==============
		// TC01: Ray's line is outside the cylinder (0 points)
		assertNull(cylinder2.findIntersections(new Ray(new Point(0, 0, 2), new Vector(0, 0, 1))),
				"Ray's line out of cylinder");

		// TC02: Ray starts before and crosses the cylinder (2 points)
		List<Point> result = cylinder2.findIntersections(new Ray(new Point(0, 0, -1), new Vector(0, 0, 1)));
		assertEquals(2, result.size(), "Wrong number of points");

		// TC03: Ray starts inside the cylinder (1 point)
		result = cylinder2.findIntersections(new Ray(new Point(0, 0, 1), new Vector(0, 0, 1)));
		assertEquals(1, result.size(), "Wrong number of points");

		// TC04: Ray starts after the cylinder (0 points)
		assertNull(cylinder2.findIntersections(new Ray(new Point(0, 0, 3), new Vector(0, 0, 1))),
				"Ray's line out of cylinder");

		// TC05: Ray starts at the cylinder and goes outside (0 points)
		assertNull(cylinder2.findIntersections(new Ray(new Point(0, 0, 0), new Vector(0, 0, -1))),
				"Ray's line out of cylinder");

		// TC06: Ray starts at the cylinder and goes inside (1 point)
		result = cylinder2.findIntersections(new Ray(new Point(0, 0, 0), new Vector(0, 0, 1)));
		assertEquals(1, result.size(), "Wrong number of points");

		// TC07: Ray intersects the cylinder's top surface (1 point)
		result = cylinder2.findIntersections(new Ray(new Point(0, 0, 3), new Vector(0, 0, -1)));
		assertEquals(2, result.size(), "Wrong number of points");

		// =============== Boundary Values Tests ==================

		// TC10: Ray starts at the cylinder's top surface and goes inside (1 point)
		result = cylinder2.findIntersections(new Ray(new Point(0, 0, 2), new Vector(0, 0, 1)));
		assertNull(result, "Wrong number of points");

		// TC11: Ray intersects the tube but not the cylinder (0 points)
		assertNull(cylinder2.findIntersections(new Ray(new Point(0, 0, 3), new Vector(0, 1, 0))),
				"Ray's line out of cylinder");

		// TC12: Ray tangent to the cylinder's top surface (0 points)
		assertNull(cylinder2.findIntersections(new Ray(new Point(0, 0, 2), new Vector(0, 1, 0))),
				"Ray's line out of cylinder");

		// TC13: Ray tangent to the cylinder's bottom surface (0 points)
		assertNull(cylinder2.findIntersections(new Ray(new Point(0, 0, 0), new Vector(0, 1, 0))),
				"Ray's line out of cylinder");

		// TC14: Ray tangent to the cylinder's side surface (0 points)
		assertNull(cylinder2.findIntersections(new Ray(new Point(0, 2, -1), new Vector(0, 0, 1))),
				"Ray's line out of cylinder");
	}

}
