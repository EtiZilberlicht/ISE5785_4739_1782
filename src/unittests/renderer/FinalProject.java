package unittests.renderer;

import static java.awt.Color.YELLOW;

import java.util.List;

import org.junit.Test;

import geometries.Cylinder;
import geometries.Plane;
import geometries.Sphere;
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

public class FinalProject {

	/**
	 * This class contains unit tests for rendering a billiards scene with all
	 * visual effects: lighting, materials, reflections, transparency, and geometric
	 * arrangements. It sets up a complete scene including billiard balls, a cue
	 * stick, a pool table, and lighting sources. The rendered scene demonstrates
	 * advanced rendering capabilities.
	 */
	/** Default constructor to satisfy JavaDoc generator */
	FinalProject() {
		/* to satisfy JavaDoc generator */ }

	/** Scene for the tests */
	private final Scene scene = new Scene("Test scene");
	/** Camera builder for the tests with triangles */
	private final Camera.Builder cameraBuilder = Camera.getBuilder() //
			.setMultithreading(-2).setDebugPrint(0.1);

	/**
	 * Builds and renders a 3D Minecraft-themed scene.
	 * <p>
	 * The scene includes various geometric objects such as cylinders (legs), a
	 * sphere (sun or decoration), planes (ground and sky), and multiple light
	 * sources including point, spot, and directional lights. Several camera
	 * transitions are used to render the scene from different angles.
	 */
	@Test
	void minecraft() {

		Material legsM = new Material().setKD(0.2).setKS(0.8).setShininess(300);
		Color legsC = Color.BLACK;

		Material skyM = new Material().setKD(0.1).setKS(0.1).setShininess(100);
		Color skyC = new Color(97, 178, 231);
		scene.geometries.addObjPolygons("minecraft");
		scene.geometries.add(/* new Sphere(10, new Point(0, 15, 20)), */

				new Sphere(25, new Point(0, 40, -160)).setEmission(new Color(30, 20, 5))
						.setMaterial(new Material().setKD(0.05).setKS(0.95).setShininess(300).setKT(0.9).setKR(0.1)),
				new Cylinder(7, new Ray(new Point(65, 1, -45), new Vector(0, -1, 0)), 30).setEmission(legsC)
						.setMaterial(legsM),
				new Cylinder(7, new Ray(new Point(65, 1, 60), new Vector(0, -1, 0)), 30).setEmission(legsC)
						.setMaterial(legsM),
				new Cylinder(7, new Ray(new Point(-25, 1, -45), new Vector(0, -1, 0)), 30).setEmission(legsC)
						.setMaterial(legsM),
				new Cylinder(7, new Ray(new Point(-25, 1, 60), new Vector(0, -1, 0)), 30).setEmission(legsC)
						.setMaterial(legsM),

				new Plane(new Point(-25, -27, 60), new Vector(0, 1, 0)).setEmission(new Color(181, 101, 29))
						.setMaterial(skyM),
				new Plane(new Point(0, 40, -800), new Vector(0, 0, 1)).setEmission(skyC).setMaterial(skyM));

		scene.lights
				.addAll(List.of(new PointLight(new Color(YELLOW), new Point(0, 40, -160)).setKL(0.0001).setKQ(0.00001),

						new SpotLight(new Color(1000, 800, 600), new Point(-200, 20, -100), new Vector(2, -1, 1))

								.setKL(0.00002).setKQ(0.00003).setShape("cycle").setSize(9).setNumOfRays(49),

						new DirectionalLight(new Color(1000, 800, 600), new Vector(2, -1, 1)),
						new DirectionalLight(new Color(300, 240, 180), new Vector(0, -1, -1)),
						new SpotLight(new Color(500, 500, 600), new Point(-10, 20, 80), new Vector(16, -18, -57))
								.setKL(0.00002).setKQ(0.00003).setKL(0.00002).setKQ(0.00003).setShape("cycle")
								.setSize(9).setNumOfRays(49)));
		scene.setBackground(new Color(97, 178, 231));
		scene.geometries.setBoundingBoxEnabled(true);

		Camera firstCamera = cameraBuilder.setRayTracer(scene, RayTracerType.GRID).setLocation(new Point(10, 20, 70))
				.setTransition(new Vector(20, 13, 0)).setRotation(7)
				.setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0)).setVpDistance(130).setVpSize(500, 500)
				.setResolution(2000, 2000).build().renderImage().writeToImage("minecraft1");

		Camera.getBuilder(firstCamera).setTransition(new Vector(50, 30, 70)).build().renderImage()
				.writeToImage("minecraft2");

		Camera.getBuilder(firstCamera).setTransition(new Vector(-170, -10, 0)).build().renderImage()
				.writeToImage("minecraft3");

	}
}
