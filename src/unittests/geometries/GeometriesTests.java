/**
 * 
 */
package unittests.geometries;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import primitives.*;
import geometries.*;

/**
 * Unit tests for geometries.Geometries class
 * 
 * @author Eti and Meitav
 */
class GeometriesTests {
	/**
	 * default constructor
	 */
	public GeometriesTests() {
	}

	/**
	 * Test method for
	 * {@link geometries.Geometries#findIntersections(primitives.Ray)}.
	 */
	@Test
	void testFindIntersections() {
		Plane plane = new Plane(new Point(1, 0, 0), new Point(2, 0, 0), new Point(1.5, 0, 1));
		Sphere sphere = new Sphere(1, new Point(1, 0, 1));
		Triangle triangle = new Triangle(new Point(0, 2, 0), new Point(2, 2, 0), new Point(1.5, 2, 2));
		Geometries geometries = new Geometries(plane, sphere, triangle);

		// ============ Equivalence Partitions Tests ==============
		// Tc01: some geometries are intersected
		Ray rayManyObjectIntersect = new Ray(new Point(1, 1.5, 1), new Vector(0, -1, 0));
		assertEquals(3, geometries.findIntersections(rayManyObjectIntersect).size(),
				"More then one object intersect (but not all the objects)");

		// =============== Boundary Values Tests ==================
		// TC02: empty geometries list
		Geometries geometries2 = new Geometries();
		assertNull(geometries2.findIntersections(null), "empty geometries list");

		// TC03: no geometry is intersected
		Ray rayNoIntersections = new Ray(new Point(1, -1, 1), new Vector(0, -1, 0));

		assertNull(geometries.findIntersections(rayNoIntersections), "The ray suppose not intersect the objects");

		// TC04: one geometry is intersected
		Ray rayOneObjectIntersect = new Ray(new Point(1.5, 1.5, 0.5), new Vector(0, 1, 0));
		assertEquals(1, geometries.findIntersections(rayOneObjectIntersect).size(),
				"Suppose to be one intersection point (one object intersect)");

		// TC05: all geometries are intersected
		Ray rayAllObjectIntersect = new Ray(new Point(1, 2.5, 1), new Vector(0, -1, 0));
		assertEquals(4, geometries.findIntersections(rayAllObjectIntersect).size(),
				"Suppose to be 4 intersection points");
	}

}
