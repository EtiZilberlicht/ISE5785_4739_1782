package unittests.renderer;

import static java.awt.Color.WHITE;

import java.util.List;

import org.junit.jupiter.api.Test;

import geometries.Cylinder;
import geometries.Polygon;
import geometries.Sphere;
import geometries.Triangle;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import renderer.Camera;
import renderer.RayTracerType;
import scene.Scene;

/**
 * This class contains unit tests for rendering a billiards scene with all
 * visual effects: lighting, materials, reflections, transparency, and geometric
 * arrangements. It sets up a complete scene including billiard balls, a cue
 * stick, a pool table, and lighting sources. The rendered scene demonstrates
 * advanced rendering capabilities.
 */
public class AllEffectsTests {
	/** Default constructor to satisfy JavaDoc generator */
	AllEffectsTests() {
		/* to satisfy JavaDoc generator */ }

	/** Scene for the tests */
	private final Scene scene = new Scene("Test scene");
	/** Camera builder for the tests with triangles */
	private final Camera.Builder cameraBuilder = Camera.getBuilder() //
			.setRayTracer(scene, RayTracerType.SIMPLE).setMultithreading(-1).setDebugPrint(1);

	/**
	 * An array holding the Sphere objects representing billiard balls. Each Sphere
	 * is assigned a specific color from ballColors and positioned in a classic
	 * triangular rack formation on the table.
	 */
	private static Point[] balls = {
			// First row - 5 balls (closest to the camera)
			new Point(-24.0, -34.0, 20.0), new Point(-12.0, -34.0, 20.0), new Point(0.0, -34.0, 20.0),
			new Point(12.0, -34.0, 20.0), new Point(24.0, -34.0, 20.0),

			// Second row - 4 balls
			new Point(-18.0, -34.0, 9.61), new Point(-6.0, -34.0, 9.61), new Point(6.0, -34.0, 9.61),
			new Point(18.0, -34.0, 9.61),

			// Third row - 3 balls
			new Point(-12.0, -34.0, -0.78), new Point(0.0, -34.0, -0.78), new Point(12.0, -34.0, -0.78),

			// Fourth row - 2 balls
			new Point(-6.0, -34.0, -11.17), new Point(6.0, -34.0, -11.17),

			// Fifth row - 1 ball
			new Point(0.0, -34.0, -21.56),

			// Lone ball (in the back, smaller z)
			new Point(0.0, -34.0, -60.0) };

	/**
	 * An array of colors representing the standard billiard ball colors. The colors
	 * are ordered to correspond with the ball numbers and typical billiards set.
	 */
	private static Color[] ballColors = {
			// First row (5 balls)
			new Color(255, 127, 0), // 13 - Orange with stripe
			new Color(128, 0, 128), // 9 - Yellow with stripe
			new Color(255, 165, 0), // 5 - Solid orange
			new Color(255, 0, 0), // 11 - Red with stripe
			new Color(0, 128, 0), // 6 - Solid green

			// Second row (4 balls)
			new Color(128, 0, 128), // 4 - Solid purple
			new Color(0, 0, 255), // 10 - Blue with stripe
			new Color(128, 0, 0), // 7 - Solid maroon
			new Color(139, 0, 0), // 15 - Maroon with stripe

			// Third row (3 balls)
			new Color(255, 0, 0), // 3 - Solid red
			new Color(0, 0, 0), // 8 - Black
			new Color(148, 0, 211), // 12 - Purple with stripe

			// Fourth row (2 balls)
			new Color(0, 128, 0), // 14 - Green with stripe
			new Color(0, 0, 255), // 2 - Solid blue

			// Fifth row (1 ball)
			new Color(255, 215, 0), // 1 - Solid yellow

			// Lone back ball (can be white or context-dependent)
			new Color(210, 205, 190) // White ball
	};

	/**
	 * An array of Sphere objects used in the scene. Typically represents the
	 * billiard balls or other spherical objects placed in the scene.
	 * 
	 * @return an array of 16 spheres with set positions, colors, and material
	 */
	private Sphere[] spheres() {
		Material ballMaterial = new Material().setKD(0.5).setKS(0.5).setShininess(200).setKR(0.5);
		Sphere[] spheres = new Sphere[16];
		for (int i = 0; i < 16; i++) {
			spheres[i] = new Sphere(6, balls[i]);
			spheres[i].setEmission(ballColors[i]).setMaterial(ballMaterial);
		}
		return spheres;
	}

	/**
	 * Constructs and renders a complete billiards scene with a table, balls, cue
	 * stick, and multiple light sources.
	 * 
	 * This method creates the table components (felt, frame, legs), sets up all
	 * billiard balls in position, applies materials, adds lighting, sets up
	 * cameras, and renders multiple views with different camera movements.
	 */
	@Test
	void billiards() {
		// Green felt material
		Material feltMaterial = new Material().setKD(0.8) // High diffuse reflection
				.setKS(0.1) // Slight specular reflection
				.setKT(0.05).setShininess(100);

		// Black frame material
		Material frameMaterial = new Material().setKD(0.2).setKS(0.7).setShininess(200).setKR(0.1);

		// Board base material (black)
		Material baseMaterial = new Material().setKD(0.4).setKS(0.2).setKT(0.3).setShininess(80);

		// Decorative triangle material
		Material triangleMaterial = new Material().setKD(0.2).setKS(0.7).setShininess(200).setKT(0.4) // Transparency
				.setKR(0.3);

		// Cue stick material
		Material stickMaterial = new Material().setKD(0.6).setKS(0.3).setShininess(100);

		// Floor material
		Material wallMaterial = new Material().setKD(0.1).setKS(0.9).setShininess(300).setKR(0.7);

		// Front right leg
		Point p1a = new Point(49, -44, 49);
		Point p2a = new Point(49, -44, 47);
		Point p3a = new Point(47, -44, 47);
		Point p4a = new Point(47, -44, 49);
		Point tipA = new Point(48, -50, 48);

		// Front left leg
		Point p1b = new Point(-47, -44, 49);
		Point p2b = new Point(-47, -44, 47);
		Point p3b = new Point(-49, -44, 47);
		Point p4b = new Point(-49, -44, 49);
		Point tipB = new Point(-48, -50, 48);

		// Back right leg
		Point p1c = new Point(49, -44, -97);
		Point p2c = new Point(49, -44, -99);
		Point p3c = new Point(47, -44, -99);
		Point p4c = new Point(47, -44, -97);
		Point tipC = new Point(48, -50, -98);

		// Back left leg
		Point p1d = new Point(-47, -44, -97);
		Point p2d = new Point(-47, -44, -99);
		Point p3d = new Point(-49, -44, -99);
		Point p4d = new Point(-49, -44, -97);
		Point tipD = new Point(-48, -50, -98);

		// A stick made of several sticks of different colors
		Vector stickVector = new Vector(-90, 0, -75).normalize();
		Point stickPoint = new Point(18, -35.5, 69);
		Ray stick1 = new Ray(stickPoint, stickVector);
		Ray stick2 = new Ray(stick1.getPoint(10), stickVector);
		Ray stick3 = new Ray(stick2.getPoint(2), stickVector);
		Ray stick4 = new Ray(stick3.getPoint(2), stickVector);
		Ray stick5 = new Ray(stick4.getPoint(6), stickVector);

		scene.geometries.add(

				// Green board
				new Polygon(new Point(50, -40, -100), new Point(-50, -40, -100), new Point(-50, -40, 50),
						new Point(50, -40, 50)).setEmission(new Color(21, 88, 67)).setMaterial(feltMaterial),

				// Black sides
				new Polygon(new Point(50, -44, -100), new Point(-50, -44, -100), new Point(-50, -44, 50),
						new Point(50, -44, 50)).setEmission(Color.BLACK).setMaterial(baseMaterial),

				new Polygon(new Point(50, -44, 50), new Point(-50, -44, 50), new Point(-50, -37, 50),
						new Point(50, -37, 50)).setEmission(Color.BLACK).setMaterial(frameMaterial),

				new Polygon(new Point(50, -44, -100), new Point(-50, -44, -100), new Point(-50, -37, -100),
						new Point(50, -37, -100)).setEmission(Color.BLACK).setMaterial(frameMaterial),

				new Polygon(new Point(50, -44, -100), new Point(50, -44, 50), new Point(50, -37, 50),
						new Point(50, -37, -100)).setEmission(Color.BLACK).setMaterial(frameMaterial),

				new Polygon(new Point(-50, -44, -100), new Point(-50, -44, 50), new Point(-50, -37, 50),
						new Point(-50, -37, -100)).setEmission(Color.BLACK).setMaterial(frameMaterial),

				// Pyramid legs
				new Triangle(p1a, p2a, tipA).setMaterial(triangleMaterial),
				new Triangle(p2a, p3a, tipA).setMaterial(triangleMaterial),
				new Triangle(p3a, p4a, tipA).setMaterial(triangleMaterial),
				new Triangle(p4a, p1a, tipA).setMaterial(triangleMaterial),

				new Triangle(p1b, p2b, tipB).setMaterial(triangleMaterial),
				new Triangle(p2b, p3b, tipB).setMaterial(triangleMaterial),
				new Triangle(p3b, p4b, tipB).setMaterial(triangleMaterial),
				new Triangle(p4b, p1b, tipB).setMaterial(triangleMaterial),

				new Triangle(p1c, p2c, tipC).setMaterial(triangleMaterial),
				new Triangle(p2c, p3c, tipC).setMaterial(triangleMaterial),
				new Triangle(p3c, p4c, tipC).setMaterial(triangleMaterial),
				new Triangle(p4c, p1c, tipC).setMaterial(triangleMaterial),

				new Triangle(p1d, p2d, tipD).setMaterial(triangleMaterial),
				new Triangle(p2d, p3d, tipD).setMaterial(triangleMaterial),
				new Triangle(p3d, p4d, tipD).setMaterial(triangleMaterial),
				new Triangle(p4d, p1d, tipD).setMaterial(triangleMaterial),

				// A ball at the end of each leg
				new Sphere(3, new Point(48, -52, 48)).setMaterial(triangleMaterial),
				new Sphere(3, new Point(-48, -52, 48)).setMaterial(triangleMaterial),
				new Sphere(3, new Point(-48, -52, -98)).setMaterial(triangleMaterial),
				new Sphere(3, new Point(48, -52, -98)).setMaterial(triangleMaterial),

				// Colorful game stick
				new Cylinder(1.5, stick1, 10).setEmission(new Color(205, 133, 63)).setMaterial(stickMaterial),
				new Cylinder(1.5, stick2, 2).setEmission(new Color(180, 30, 30)).setMaterial(stickMaterial),
				new Cylinder(1.5, stick3, 2).setEmission(new Color(205, 133, 63)).setMaterial(stickMaterial),
				new Cylinder(1.5, stick4, 6).setEmission(new Color(211, 211, 211)).setMaterial(stickMaterial),
				new Cylinder(1.5, stick5, 110).setEmission(new Color(205, 133, 63)).setMaterial(stickMaterial),

				// Walls
				new Polygon(new Point(100, -55, -200), new Point(-100, -55, -200), new Point(-100, -55, 60),
						new Point(100, -55, 60)).setEmission(new Color(80, 80, 80)).setMaterial(wallMaterial)

				, new Polygon(new Point(100, 45, -200), new Point(-100, 45, -200), new Point(-100, -55, -200),
						new Point(100, -55, -200)).setEmission(new Color(80, 80, 80)).setMaterial(wallMaterial)

		);

		scene.geometries.add(spheres());

		scene.setBackground(/* new Color(0, 128, 128) */Color.BLACK);

		scene.lights.addAll(List.of(

				new DirectionalLight(new Color(WHITE), new Vector(-50, 30, -20)),
				new DirectionalLight(new Color(WHITE), new Vector(60, 30, -10)),
				new PointLight(new Color(WHITE), new Point(30, 70, 0)).setKL(0.0001).setKQ(0.0002),
				new SpotLight(new Color(WHITE), new Point(25, 15, 10), new Vector(1, 1, -2)).setKL(0.0002)
						.setKQ(0.0003),
				new SpotLight(new Color(WHITE), new Point(0, 100, -100), new Vector(0, -1, 1)).setNarrowBeam(15)
						.setKL(0.0003).setKQ(0.00005)

		));

		Camera oldCamera = cameraBuilder.setLocation(new Point(0, 0, 150))
				.setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0)).setVpDistance(300).setVpSize(500, 500)
				.setResolution(2000, 2000).build().renderImage().writeToImage("Original image_billiards");

		Camera.getBuilder(oldCamera) //
				.setTransition(new Vector(400, 100, 100)).build().renderImage()
				.writeToImage("Transition image_billiard");

		Camera.getBuilder(oldCamera) //
				.setTransition(new Vector(-400, -100, -100)).setRotation(30).build().renderImage()
				.writeToImage("Rotation image_billiard");

		Camera.getBuilder(oldCamera) //
				.setTransition(new Vector(100, 400, 200)).build().renderImage()
				.writeToImage("Both Transition and Rotation image_billiard");

	}
}
//	private Geometry rectangle(double i, double j, double len, double z, Color c) {
//		return new Polygon(new Point(i, j, z), new Point(i, j + 5, z), new Point(i + len, j + 5, z),
//				new Point(i + len, j, z)).setEmission(c).setMaterial(new Material().setKR(0.4));
//	}
//
//	private Geometry[] allFrame(Point[] points, double surface, double back, Color c) {
//		Geometry[] frame = new Geometry[17];
//		for (int i = 0; i < points.length - 1; i++)
//			frame[i] = thickness(points[i].getX(), points[i].getY(), points[i + 1].getX(), points[i + 1].getY(),
//					surface, back, c);
//		return frame;
//	}
//
//	private Geometry thickness(double ax, double ay, double bx, double by, double surface, double back, Color c) {
//		return new Polygon(new Point(ax, ay, surface), new Point(bx, by, surface), new Point(bx, by, back),
//				new Point(ax, ay, back)).setEmission(c);
//	}

//	@Test
//	void dino() {
//		double surface_z = -100;
//		Color surface = new Color(66, 95, 235);
//		double back_z = -120;
//		Color back = new Color(235, 0, 0);
//		Color frameColor = new Color(0, 255, 0);
//
//		scene.geometries.add(rectangle(5, 30, 40, surface_z, surface), rectangle(0, 25, 50, surface_z, surface),
//				rectangle(0, 20, 10, surface_z, surface), rectangle(15, 20, 35, surface_z, surface),
//				rectangle(0, 15, 50, surface_z, surface), rectangle(0, 10, 50, surface_z, surface),
//				rectangle(0, 5, 25, surface_z, surface), rectangle(0, 0, 40, surface_z, surface),
//				rectangle(-45, -5, 5, surface_z, surface), rectangle(-5, -5, 25, surface_z, surface),
//				rectangle(-45, -10, 5, surface_z, surface), rectangle(-10, -10, 30, surface_z, surface),
//				rectangle(-45, -15, 10, surface_z, surface), rectangle(-20, -15, 50, surface_z, surface),
//				rectangle(-45, -20, 15, surface_z, surface), rectangle(-25, -20, 45, surface_z, surface),
//				rectangle(25, -20, 5, surface_z, surface), rectangle(0, 15, 50, surface_z, surface),
//				rectangle(-45, -25, 65, surface_z, surface), rectangle(-45, -30, 65, surface_z, surface),
//				rectangle(-40, -35, 55, surface_z, surface), rectangle(-35, -40, 50, surface_z, surface),
//				rectangle(-30, -45, 40, surface_z, surface), rectangle(-25, -50, 15, surface_z, surface),
//				rectangle(-5, -50, 10, surface_z, surface), rectangle(-25, -55, 10, surface_z, surface),
//				rectangle(0, -55, 5, surface_z, surface), rectangle(-25, -60, 5, surface_z, surface),
//				rectangle(0, -60, 5, surface_z, surface), rectangle(-25, -65, 10, surface_z, surface),
//				rectangle(0, -65, 10, surface_z, surface));
//
//		scene.geometries.add(rectangle(5, 30, 40, back_z, back), rectangle(0, 25, 50, back_z, back),
//				rectangle(0, 20, 10, back_z, back), rectangle(15, 20, 35, back_z, back),
//				rectangle(0, 15, 50, back_z, back), rectangle(0, 10, 50, back_z, back),
//				rectangle(0, 5, 25, back_z, back), rectangle(0, 0, 40, back_z, back),
//				rectangle(-45, -5, 5, back_z, back), rectangle(-5, -5, 25, back_z, back),
//				rectangle(-45, -10, 5, back_z, back), rectangle(-10, -10, 30, back_z, back),
//				rectangle(-45, -15, 10, back_z, back), rectangle(-20, -15, 50, back_z, back),
//				rectangle(-45, -20, 15, back_z, back), rectangle(-25, -20, 45, back_z, back),
//				rectangle(25, -20, 5, back_z, back), rectangle(0, 15, 50, back_z, back),
//				rectangle(-45, -25, 65, back_z, back), rectangle(-45, -30, 65, back_z, back),
//				rectangle(-40, -35, 55, back_z, back), rectangle(-35, -40, 50, back_z, back),
//				rectangle(-30, -45, 40, back_z, back), rectangle(-25, -50, 15, back_z, back),
//				rectangle(-5, -50, 10, back_z, back), rectangle(-25, -55, 10, back_z, back),
//				rectangle(0, -55, 5, back_z, back), rectangle(-25, -60, 5, back_z, back),
//				rectangle(0, -60, 5, back_z, back), rectangle(-25, -65, 10, back_z, back),
//				rectangle(0, -65, 10, back_z, back));
//
//		Point[] frame = { new Point(0, 30, surface_z), new Point(5, 30, surface_z), new Point(5, 35, surface_z),
//				new Point(45, 35, surface_z), new Point(45, 30, surface_z), new Point(50, 30, surface_z),
//				new Point(50, 10, surface_z), new Point(25, 10, surface_z), new Point(25, 5, surface_z),
//				new Point(40, 5, surface_z), new Point(40, 0, surface_z), new Point(20, 0, surface_z),
//				new Point(20, -10, surface_z), new Point(30, -10, surface_z), new Point(30, -20, surface_z),
//				new Point(25, -20, surface_z), new Point(25, -15, surface_z), new Point(20, -15, surface_z)
//
//		};
//		scene.geometries.add(allFrame(frame, surface_z, back_z, frameColor));
////		scene.geometries.add(thickness(0, 30, 5, 30, surface_z, back_z, frameColor),
////				thickness(5, 30, 5, 35, surface_z, back_z, frameColor),
////				thickness(5, 35, 45, 35, surface_z, back_z, frameColor),
////				thickness(45, 35, 45, 30, surface_z, back_z, frameColor),
////				thickness(45, 30, 50, 30, surface_z, back_z, frameColor),
////				thickness(50, 30, 50, 10, surface_z, back_z, frameColor),
////				thickness(50, 10, 25, 10, surface_z, back_z, frameColor),
////				thickness(25, 10, 25, 5, surface_z, back_z, frameColor),
////				thickness(25, 5, 40, 5, surface_z, back_z, frameColor),
////				thickness(40, 5, 40, 0, surface_z, back_z, frameColor),
////				thickness(40, 0, 20, 0, surface_z, back_z, frameColor),
////				thickness(20, 0, 20, -10, surface_z, back_z, frameColor),
////				thickness(20, -10, 30, -10, surface_z, back_z, frameColor),
////				thickness(30, -10, 30, -20, surface_z, back_z, frameColor),
////				thickness(30, -20, 25, -20, surface_z, back_z, frameColor));
//
//		cameraBuilder.setLocation(new Point(0, 0, 150)).setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
//				.setVpDistance(300).setVpSize(500, 500).setResolution(500, 500).setTransition(new Vector(100, 0, 0))
//				.build().renderImage().writeToImage("dino");
//
//	}
//}
