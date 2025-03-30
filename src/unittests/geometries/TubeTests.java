/**
 * 
 */
package unittests.geometries;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import geometries.Tube;
import primitives.*;

/**
 * Unit tests for geometries.Tube class
 * @author Eti and Meitav
 */
class TubeTests {
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
		//Tube on Z axis
		Point head = new Point(0, 0, 0);
		Vector direction = new Vector(0, 0, 1);
		Tube tube = new Tube(1, new Ray(head, direction));
		
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

}
