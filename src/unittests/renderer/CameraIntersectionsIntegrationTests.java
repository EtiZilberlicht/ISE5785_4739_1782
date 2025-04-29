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
 * 
 */
class CameraIntersectionsIntegrationTests {
	/** Camera builder for the tests */
	private final Camera.Builder cameraBuilder = Camera.getBuilder().setVpDistance(1)
			.setDirection(new Vector(0, 0, -1), Vector.AXIS_Y).setVpSize(3, 3);

	private final Camera camera1 = cameraBuilder.setLocation(Point.ZERO).build();
	private final Camera camera2 = cameraBuilder.setLocation(new Point(0, 0, 0.5)).build();

	private void testNumberOfIntersections(Geometry geometry, Camera camera, int desiredresult) {
		int countIntersections = 0;
		for (int j = 0; j < 3; j++)
			for (int i = 0; i < 3; i++) {
				Ray ray = camera.constructRay(3, 3, j, i);
				List<Point> intersections = geometry.findIntersections(ray);
				countIntersections += intersections != null ? intersections.size() : 0;
			}
		assertEquals(desiredresult, countIntersections, "Wrong rays");
	}

	/**
	 * Test method for {@link renderer.Camera#constructRay(int, int, int, int)}.
	 */
	@Test
	void testConstructRaySphere() {

		// TC01: Sphere against exactly one pixel (2 points)
		testNumberOfIntersections(new Sphere(1, new Point(0, 0, -3)), camera1, 2);

		// TC02: Sphere against all the pixels (18 points)
		testNumberOfIntersections(new Sphere(2.5, new Point(0, 0, -2.5)), camera2, 18);

		// TC03: Sphere against some of the pixels (10 points)
		testNumberOfIntersections(new Sphere(2, new Point(0, 0, -2)), camera2, 10);

		// TC04: The camera is inside the sphere (9 points)
		testNumberOfIntersections(new Sphere(4, new Point(0, 0, -2)), camera1, 9);

		// TC05: The sphere is behind the camera (0 points)
		testNumberOfIntersections(new Sphere(0.5, new Point(0, 0, 1)), camera1, 0);

	}

	/**
	 * Test method for {@link renderer.Camera#constructRay(int, int, int, int)}.
	 */
	@Test
	void testConstructRayPlane() {

		// TC01: The plane is parallel to the Plane View (9 points)
		testNumberOfIntersections(new Plane(new Point(0, 0, -2), new Vector(0, 0, 1)), camera1, 9);

		// TC02: The plane is not parallel to the Plane View (9 points)
		testNumberOfIntersections(new Plane(new Point(0, 0, -4), new Vector(0, 0.5, 1)), camera1, 9);

		// TC03: The three lower rays are parallel to the plane (6 points)
		testNumberOfIntersections(new Plane(new Point(0, 0, -4), new Vector(0, -1, -1)), camera1, 6);

	}

	/**
	 * Test method for {@link renderer.Camera#constructRay(int, int, int, int)}.
	 */
	@Test
	void testConstructRayTriangle() {

		Point p1 = new Point(1, -1, -2);
		Point p2 = new Point(-1, -1, -2);

		// TC01: The triangle against one pixel (1 points)
		testNumberOfIntersections(new Triangle(p1, p2, new Point(0, 1, -2)), camera1, 1);

		// TC03: Two hits (2 points)
		testNumberOfIntersections(new Triangle(p1, p2, new Point(0, 20, -2)), camera1, 2);

	}

}
