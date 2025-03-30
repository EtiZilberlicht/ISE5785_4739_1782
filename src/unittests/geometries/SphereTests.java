/**
 * 
 */
package unittests.geometries;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import geometries.Sphere;
import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for geometries.Sphere class
 * 
 * @author Eti and Meitav
 */
class SphereTests {
	/**
	 * Delta value for accuracy when comparing the numbers of type 'double' in
	 * assertEquals
	 */
	private static final double DELTA = 0.000001;

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
		// ensure |normal| = 1
		assertEquals(1, normal.length(), DELTA, "Sphere normal is not a unit vector");
		// correction of normal
		assertEquals(new Vector(0, 0, 1), normal, "Sphere normal wrong value");

	}

}
