/**
 * 
 */
package unittests.geometries;

import static org.junit.jupiter.api.Assertions.*;

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
	 * Delta value for accuracy when comparing the numbers of type 'double' in
	 * assertEquals
	 */
	private static final double DELTA = 0.000001;

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
		// ensure |normal| = 1
		assertEquals(1, normal.length(), DELTA, "Cylinder normal is not a unit vector");
		// correction of normal
		assertEquals(new Vector(1, 0, 0), normal, "Cylinder normal wrong value");
		
		// TC02: A point on bottom base
		Point onSurface2 = new Point(0.5, 0, 0);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> cylinder.getNormal(onSurface2), "");
		// generate the test result
		normal = cylinder.getNormal(onSurface2);
		// ensure |normal| = 1
		assertEquals(1, normal.length(), DELTA, "Cylinder normal is not a unit vector");
		// correction of normal
		assertEquals(new Vector(0, 0, -1), normal, "Cylinder normal wrong value");
		
		// TC03: A point on top base
		Point onSurface3 = new Point(0.5, 0, 2);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> cylinder.getNormal(onSurface3), "");
		// generate the test result
		normal = cylinder.getNormal(onSurface3);
		// ensure |normal| = 1
		assertEquals(1, normal.length(), DELTA, "Cylinder normal is not a unit vector");
		// correction of normal
		assertEquals(new Vector(0, 0, 1), normal, "Cylinder normal wrong value");
		
		// =============== Boundary Values Tests ==================

		// TC10: A point on the center of the bottom base
		Point onSurface4 = new Point(0, 0, 0);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> cylinder.getNormal(onSurface4), "");
		// generate the test result
		normal = cylinder.getNormal(onSurface4);
		// ensure |normal| = 1
		assertEquals(1, normal.length(), DELTA, "Cylinder normal is not a unit vector");
		// correction of normal
		assertEquals(new Vector(0, 0, -1), normal, "Cylinder normal wrong value");
		
		// TC11: A point on the center of the top base
		Point onSurface5 = new Point(0, 0, 2);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> cylinder.getNormal(onSurface5), "");
		// generate the test result
		normal = cylinder.getNormal(onSurface5);
		// ensure |normal| = 1
		assertEquals(1, normal.length(), DELTA, "Cylinder normal is not a unit vector");
		// correction of normal
		assertEquals(new Vector(0, 0, 1), normal, "Cylinder normal wrong value");
		
		// TC12: A point on the edge of the bottom base
		Point onSurface6 = new Point(1, 0, 0);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> cylinder.getNormal(onSurface6), "");
		// generate the test result
		normal = cylinder.getNormal(onSurface6);
		// ensure |normal| = 1
		assertEquals(1, normal.length(), DELTA, "Cylinder normal is not a unit vector");
		// correction of normal
		assertEquals(new Vector(0, 0, -1), normal, "Cylinder normal wrong value");
		
		// TC13: A point on the edge of the top base
		Point onSurface7 = new Point(1, 0, 2);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> cylinder.getNormal(onSurface7), "");
		// generate the test result
		normal = cylinder.getNormal(onSurface7);
		// ensure |normal| = 1
		assertEquals(1, normal.length(), DELTA, "Cylinder normal is not a unit vector");
		// correction of normal
		assertEquals(new Vector(0, 0, 1), normal, "Cylinder normal wrong value");

	}

}
