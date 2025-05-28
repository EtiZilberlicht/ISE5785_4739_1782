/**
 * 
 */
package unittests.geometries;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import geometries.Cylinder;
import geometries.Tube;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

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
		Vector direction = V001;
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
		assertEquals(V00M1, normal, "Cylinder normal wrong value");

		// TC03: A point on top base
		Point onSurface3 = new Point(0.5, 0, 2);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> cylinder.getNormal(onSurface3), "");
		// generate the test result
		normal = cylinder.getNormal(onSurface3);
		// correction of normal
		assertEquals(V001, normal, "Cylinder normal wrong value");

		// =============== Boundary Values Tests ==================

		// TC10: A point on the center of the bottom base
		Point onSurface4 = new Point(0, 0, 0);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> cylinder.getNormal(onSurface4), "");
		// generate the test result
		normal = cylinder.getNormal(onSurface4);
		// correction of normal
		assertEquals(V00M1, normal, "Cylinder normal wrong value");

		// TC11: A point on the center of the top base
		Point onSurface5 = new Point(0, 0, 2);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> cylinder.getNormal(onSurface5), "");
		// generate the test result
		normal = cylinder.getNormal(onSurface5);
		// correction of normal
		assertEquals(V001, normal, "Cylinder normal wrong value");

		// TC12: A point on the edge of the bottom base
		Point onSurface6 = new Point(1, 0, 0);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> cylinder.getNormal(onSurface6), "");
		// generate the test result
		normal = cylinder.getNormal(onSurface6);
		// correction of normal
		assertEquals(V00M1, normal, "Cylinder normal wrong value");

		// TC13: A point on the edge of the top base
		Point onSurface7 = new Point(1, 0, 2);
		// ensure there are no exceptions
		assertDoesNotThrow(() -> cylinder.getNormal(onSurface7), "");
		// generate the test result
		normal = cylinder.getNormal(onSurface7);
		// correction of normal
		assertEquals(V001, normal, "Cylinder normal wrong value");

	}

	/** A point used in some tests */
	private static final Point P402 = new Point(4, 0, 2);
	/** A vector used in some tests */
	private static final Vector V001 = new Vector(0, 0, 1);
	/** A vector used in some tests */
	private static final Vector V00M1 = new Vector(0, 0, -1);
	/** A vector used in some tests */
	private static final Vector V100 = new Vector(1, 0, 0);
	/** A vector used in some tests */
	private static final Vector VM10M01 = new Vector(-1, 0, -0.1);

	/**
	 * Test method for
	 * {@link geometries.Cylinder#findIntersections(primitives.Ray)}.
	 */
	@Test
	void testFindIntersections() {
		Cylinder cylinder = new Cylinder(2, new Ray(new Point(0, 0, 1), V001), 2);

		// ============ Equivalence Partitions Tests ==============

		// **** Group: Intersect the side of the cylinder

		// TC01: Ray's line is outside the cylinder (0 points)
		assertNull(cylinder.findIntersections(new Ray(P402, new Vector(1, 0, 1))), "Ray's line out of cylinder");

		// TC02: Ray starts before and crosses the cylinder (2 points)
		Point p1 = new Point(2, 0, 1.8);
		Point p2 = new Point(-2, 0, 1.4);
		assertEquals(List.of(p1, p2), cylinder.findIntersections(new Ray(P402, VM10M01)), "Wrong intersections");

		// TC03: Ray starts inside the cylinder (1 point)
		assertEquals(p2, cylinder.findIntersections(new Ray(new Point(1, 0, 1.7), VM10M01)).get(0),
				"Wrong intersections");

		// **** Group: Intersect the bases of the cylinder

		// TC11: Intersect the top (1 point)
		assertEquals(new Point(0, 0, 3),
				cylinder.findIntersections(new Ray(new Point(1, 1, 2), new Vector(-1, -1, 1))).get(0),
				"Wrong intersections");

		// TC12: Intersect the bottom (1 point)
		assertEquals(new Point(0, 0, 1),
				cylinder.findIntersections(new Ray(new Point(1, 1, 2), new Vector(-1, -1, -1))).get(0),
				"Wrong intersections");

		// TC13: Intersect both of the bases (2 point)
		Point p3 = new Point(0.75, 0.75, 1);
		Point p4 = new Point(-0.25, -0.25, 3);
		assertEquals(List.of(p3, p4), cylinder.findIntersections(new Ray(new Point(1, 1, 0.5), new Vector(-1, -1, 2))),
				"Wrong intersections");

		// =============== Boundary Values Tests ==================

		// **** Group: Ray is parallel to the axis ray

		// TC10: Ray is outside and does not intersect
		assertNull(cylinder.findIntersections(new Ray(P402, V001)), "Ray's line out of cylinder");

		// TC11: Ray is outside and intersects (2 points)
		Point p5 = new Point(1, 0, 3);
		Point p6 = new Point(1, 0, 1);
		assertEquals(List.of(p5, p6), cylinder.findIntersections(new Ray(new Point(1, 0, 4), V00M1)),
				"Wrong intersections");

		// TC12: Ray is inside (1 point)
		assertEquals(new Point(1, 0, 3), cylinder.findIntersections(new Ray(new Point(1, 0, 2), V001)).get(0),
				"Wrong intersections");

		// TC13: The Ray lay on the cylinder
		assertNull(cylinder.findIntersections(new Ray(new Point(0, 2, 0), V001)), "Ray's line out of cylinder");

		// **** Group: Ray is tangent

		// TC21: The side surface in 1 point
		assertNull(cylinder.findIntersections(new Ray(new Point(-3, 2, 2), V100)), "Ray's line out of cylinder");

		// TC22: The top
		assertNull(cylinder.findIntersections(new Ray(new Point(-3, 0, 3), V100)), "Ray's line out of cylinder");

		// TC23: The bottom
		assertNull(cylinder.findIntersections(new Ray(new Point(-3, 0, 1), V100)), "Ray's line out of cylinder");

		// TC24: A vertex
		assertNull(cylinder.findIntersections(new Ray(new Point(-3, 2, 3), V100)), "Ray's line out of cylinder");

		// **** Group: Ray begin on the surface

		// TC31: On the side and go inside (1 point)
		assertEquals(new Point(0, -2, 2),
				cylinder.findIntersections(new Ray(new Point(0, 2, 2), new Vector(0, -1, 0))).get(0),
				"Wrong intersections");

		// TC32: On the side and go outside
		assertNull(cylinder.findIntersections(new Ray(new Point(0, 2, 2), new Vector(0, 1, 0))),
				"Ray's line out of cylinder");

		// TC33: On the top and go inside (1 point)
		assertEquals(new Point(1, 1, 1), cylinder.findIntersections(new Ray(new Point(1, 1, 3), V00M1)).get(0),
				"Wrong intersections");

		// TC34: On the top and go outside
		assertNull(cylinder.findIntersections(new Ray(new Point(1, 1, 3), V001)), "Ray's line out of cylinder");

		// TC35: On the bottom and go inside (1 point)
		assertEquals(new Point(1, 1, 3), cylinder.findIntersections(new Ray(new Point(1, 1, 1), V001)).get(0),
				"Wrong intersections");

		// TC36: On the bottom and go outside
		assertNull(cylinder.findIntersections(new Ray(new Point(1, 1, 1), V00M1)), "Ray's line out of cylinder");

		// **** Group: Interact a Vertex (The transition between the side and the base)

		// TC41: intersect from inside (1 point)
		Point p7 = new Point(0, 2, 3);
		assertEquals(p7, cylinder.findIntersections(new Ray(new Point(0, 0, 2), new Vector(0, 2, 1))).get(0),
				"Wrong intersections");

		// TC42: intersect from outside (2 point)
		Point p8 = new Point(0, -2, 2);
		assertEquals(List.of(p7, p8), cylinder.findIntersections(new Ray(new Point(0, 6, 4), new Vector(0, -4, -1))),
				"Wrong intersections");

		// TC43: tangent
		assertNull(cylinder.findIntersections(new Ray(new Point(-3, 2, 3), V100)), "Ray's line out of cylinder");

	}

	/**
	 * Test method for {@link Cylinder#calculateIntersections(Ray, double)}.
	 */
	@Test
	void testCalculateIntersections() {
		// A cylinder for test
		final Tube cylinder = new Cylinder(3, new Ray(new Point(-3, 0, 0), new Vector(1, 0, 0)), 6);
		// A vector used in some test cases to (1,0,0)
		Vector v100 = new Vector(1, 0, 0);

		// ============ Equivalence Partitions Tests ==============
		// TC01: Ray "stops" before the cylinder
		assertNull(cylinder.calculateIntersections(new Ray(new Point(-6, 2.5, 0), v100), 2),
				"ray stops before the cylinder");

		// TC02: Ray starts before the cylinder and "stops" inside it
		assertEquals(1, cylinder.calculateIntersections(new Ray(new Point(-4, 1.5, 0), v100), 3.5).size(),
				"Wrong number of points");

		// TC03: Ray starts and "stops" inside the cylinder
		assertNull(cylinder.calculateIntersections(new Ray(new Point(-2, 0.5, 0), v100), 3.5),
				"ray starts and stops inside the sphere");

		// TC04: Ray starts inside the cylinder and "stops" after it
		assertEquals(1, cylinder.calculateIntersections(new Ray(new Point(2, -1.5, 0), v100), 3.5).size(),
				"Wrong number of points");

		// TC05: Ray starts after the cylinder
		assertNull(cylinder.calculateIntersections(new Ray(new Point(4, -2.5, 0), v100), 3.5),
				"ray starts after the cylinder");

		// TC06: Ray crosses the cylinder, starts before it and "stops" after it
		assertEquals(2, cylinder.calculateIntersections(new Ray(new Point(-4, 1.5, 0), v100), 7).size(),
				"Wrong number of points");

		// =============== Boundary Values Tests ==================
		// TC11: Ray starts before the cylinder and "stops" at the first intersection
		// point
		assertEquals(1, cylinder.calculateIntersections(new Ray(new Point(-4, 0, 0), v100), 1).size(),
				"Wrong number of points");

		// TC11: Ray starts before the cylinder and "stops" at the second intersection
		// point
		assertEquals(2, cylinder.calculateIntersections(new Ray(new Point(-4, 0, 0), v100), 7).size(),
				"Wrong number of points");

		// TC11: Ray starts inside the cylinder and "stops" at the intersection point
		assertEquals(1, cylinder.calculateIntersections(new Ray(new Point(-2, 0, 0), v100), 5).size(),
				"Wrong number of points");

	}

}
