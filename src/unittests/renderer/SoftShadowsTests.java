package unittests.renderer;

import static java.awt.Color.WHITE;

import java.util.List;

import org.junit.jupiter.api.Test;

import geometries.Cylinder;
import geometries.Plane;
import geometries.Polygon;
import geometries.Sphere;
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
 * Unit tests for verifying the rendering of soft shadows in a 3D scene.
 * <p>
 * This test builds a scene with various geometries and light sources with sizes
 * to simulate soft shadows using area lights and beam tracing. It renders the
 * scene and writes the output image to a file named "softShadow".
 * </p>
 */
public class SoftShadowsTests {
	/** Default constructor to satisfy JavaDoc generator */
	SoftShadowsTests() {
		/* to satisfy JavaDoc generator */ }

	/** Scene for the tests */
	private final Scene scene = new Scene("Test scene");
	/** Camera builder for the tests with triangles */

	private final Camera.Builder cameraBuilder = Camera.getBuilder() //
			.setRayTracer(scene, RayTracerType.SIMPLE).setMultithreading(-1).setDebugPrint(0.1);

	/**
	 * Tests the rendering of soft shadows by constructing a scene with several
	 * geometries and area lights.
	 * <p>
	 * The scene contains:
	 * <ul>
	 * <li>A large plane with reflective and diffuse material</li>
	 * <li>Spheres and cylinders with varying shininess</li>
	 * <li>Polygons forming complex shapes</li>
	 * <li>SpotLight and PointLight with size set for soft shadows</li>
	 * </ul>
	 * The camera is configured to capture the scene from a distance with a high
	 * resolution. The rendered image is saved as "softShadow.png".
	 */
	@Test
//	@Disabled
	void softShadow() {
		Material planeMaterial = new Material().setKD(0.6) // פיזור אור
				.setKS(0.2) // החזר מבריק קל
				.setShininess(100).setKR(0.05); //
		Material matteMaterial = new Material().setKD(0.6) // פיזור אור
				.setKS(0.2) // החזר מבריק קל
				.setShininess(30); // ברק מתון
		Material shinyBall = new Material().setKD(0.5).setKS(0.8).setShininess(100); // ברק חזק
		Material smoothCylinder = new Material().setKD(0.6).setKS(0.5).setShininess(80);

		Point p1 = new Point(-15, -40, 0);
		Point p2 = new Point(27, -40, -42);
		Point p3 = new Point(27, 15, -42);
		Point p4 = new Point(-15, 15, 0);
		Point p5 = new Point(-57, -40, -42);
		Point p6 = new Point(-15, -40, -84);
		Point p7 = new Point(-15, 15, -84);
		Point p8 = new Point(-57, 15, -42);

		Color generalColor = new Color(40, 40, 40);
		scene.geometries.add(

				new Plane(new Point(50, -40, -100), new Point(-50, -40, -100), new Point(-50, -40, 50))
						.setEmission(generalColor).setMaterial(planeMaterial),
				new Sphere(20d, new Point(0, -20, 50)).setEmission(generalColor).setMaterial(shinyBall),
				new Cylinder(12d, new Ray(new Point(50, -40, 0), new Vector(0, 1, 0)), 75).setEmission(generalColor)
						.setMaterial(smoothCylinder),
				new Polygon(p1, p2, p3, p4).setEmission(generalColor).setMaterial(matteMaterial),
				new Polygon(p2, p6, p7, p3).setEmission(generalColor).setMaterial(matteMaterial),
				new Polygon(p6, p5, p8, p7).setEmission(generalColor).setMaterial(matteMaterial),
				new Polygon(p5, p1, p4, p8).setEmission(generalColor).setMaterial(matteMaterial),
				new Polygon(p4, p3, p7, p8).setEmission(generalColor).setMaterial(matteMaterial));

		scene.lights.addAll(List.of(
				new SpotLight(new Color(800, 800, 800), new Point(110, 75, 50), new Vector(-1, -0.5, 0)).setKL(0.0004)
						.setKQ(0.0002).setSize(81).setShape("circle"),
				new PointLight(new Color(WHITE), new Point(0, 75, -200)).setKL(0.004).setKQ(0.0002).setSize(81)
						.setShape("circle")));

		cameraBuilder.setLocation(new Point(0, 0, 1000)) //
				.setDirection(Point.ZERO, Vector.AXIS_Y) //
				.setVpDistance(1000).setVpSize(200, 200) //
				.setResolution(2000, 2000).setTransition(new Vector(150, 500, 0)) //
				.build() //
				.renderImage() //
				.writeToImage("softShadow");

	}

}
