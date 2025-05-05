/**
 * 
 */
package unittests.renderer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import geometries.*;
import primitives.*;
import renderer.Camera;

/**
 * Integration tests for {@link Camera} class to ensure correct intersection
 * behavior between camera rays and geometric shapes such as {@link Sphere},
 * {@link Plane}, and {@link Triangle}.
 * <p>
 * Each test constructs a 3x3 grid of rays from the camera's view plane and
 * counts how many of those rays intersect with a given geometry.
 */
class CameraIntersectionsIntegrationTests {

	/**
	 * default constructor
	 */
	public CameraIntersectionsIntegrationTests() {
	}

	/** Builder used to initialize camera objects with common parameters. */
	private final Camera.Builder cameraBuilder = Camera.getBuilder().setVpDistance(1)
			.setDirection(new Vector(0, 0, -1), Vector.AXIS_Y).setVpSize(3, 3);

	/** Camera located at the origin (0,0,0). */
	private final Camera camera1 = cameraBuilder.setLocation(Point.ZERO).build();

	/** Camera slightly closer to the view plane, located at (0,0,0.5). */
	private final Camera camera2 = cameraBuilder.setLocation(new Point(0, 0, 0.5)).build();

	/**
	 * Helper method to test the number of intersection points between a camera's
	 * constructed rays and a given geometry. The method constructs a 3x3 grid of
	 * rays through the view plane and accumulates the number of intersection points
	 * with the geometry.
	 *
	 * @param geometry      the geometry to intersect with
	 * @param camera        the camera used to construct the rays
	 * @param desiredresult the expected number of intersection points
	 */
	private void testNumberOfIntersections(Geometry geometry, Camera camera, int desiredresult) {
		int countIntersections = 0;
		for (int j = 0; j < 3; j++) {
			for (int i = 0; i < 3; i++) {
				Ray ray = camera.constructRay(3, 3, j, i);
				List<Point> intersections = geometry.findIntersections(ray);
				countIntersections += intersections != null ? intersections.size() : 0;
			}
		}
		assertEquals(desiredresult, countIntersections, "Wrong number of intersections");
	}

	/**
	 * Tests intersections between camera rays and spheres with different positions
	 * and radii. Uses various test cases to validate expected behavior.
	 */
	@Test
	void testConstructRaySphere() {
		// TC01: Sphere intersects with 1 ray (2 points)
		testNumberOfIntersections(new Sphere(1, new Point(0, 0, -3)), camera1, 2);

		// TC02: Sphere intersects with all 9 rays (18 points)
		testNumberOfIntersections(new Sphere(2.5, new Point(0, 0, -2.5)), camera2, 18);

		// TC03: Sphere intersects with some rays (10 points)
		testNumberOfIntersections(new Sphere(2, new Point(0, 0, -2)), camera2, 10);

		// TC04: Camera inside the sphere (9 points)
		testNumberOfIntersections(new Sphere(4, new Point(0, 0, -2)), camera1, 9);

		// TC05: Sphere behind the camera (0 points)
		testNumberOfIntersections(new Sphere(0.5, new Point(0, 0, 1)), camera1, 0);
	}

	/**
	 * Tests intersections between camera rays and planes with different
	 * orientations. Includes parallel, angled, and edge-aligned planes.
	 */
	@Test
	void testConstructRayPlane() {
		// TC01: Plane parallel to view plane (9 points)
		testNumberOfIntersections(new Plane(new Point(0, 0, -2), new Vector(0, 0, 1)), camera1, 9);

		// TC02: Plane slightly tilted (9 points)
		testNumberOfIntersections(new Plane(new Point(0, 0, -4), new Vector(0, 0.5, 1)), camera1, 9);

		// TC03: Some rays are parallel and miss the plane (6 points)
		testNumberOfIntersections(new Plane(new Point(0, 0, -4), new Vector(0, -1, -1)), camera1, 6);
	}

	/**
	 * Tests intersections between camera rays and triangles. Includes narrow and
	 * wide triangles intersecting different subsets of rays.
	 */
	@Test
	void testConstructRayTriangle() {
		Point p1 = new Point(1, -1, -2);
		Point p2 = new Point(-1, -1, -2);

		// TC01: Triangle intersects only one pixel (1 point)
		testNumberOfIntersections(new Triangle(p1, p2, new Point(0, 1, -2)), camera1, 1);

		// TC02: Triangle intersects two rays (2 points)
		testNumberOfIntersections(new Triangle(p1, p2, new Point(0, 20, -2)), camera1, 2);
	}
}
