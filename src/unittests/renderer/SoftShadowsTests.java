package unittests.renderer;

import static java.awt.Color.BLUE;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;

import java.util.List;

import org.junit.jupiter.api.Test;

import geometries.Polygon;
import geometries.Sphere;
import lighting.SpotLight;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Vector;
import renderer.Camera;
import renderer.RayTracerType;
import scene.Scene;

public class SoftShadowsTests {
	/** Default constructor to satisfy JavaDoc generator */
	SoftShadowsTests() {
		/* to satisfy JavaDoc generator */ }

	/** Scene for the tests */
	private final Scene scene = new Scene("Test scene");
	/** Camera builder for the tests with triangles */

	private final Camera.Builder cameraBuilder = Camera.getBuilder() //
			.setRayTracer(scene, RayTracerType.SIMPLE, true);

	@Test
	void firstTry() {
		Material feltMaterial = new Material().setKD(0.8) // High diffuse reflection
				.setKS(0.1) // Slight specular reflection
				.setKT(0.05).setShininess(100);

		Material ballMaterial = new Material().setKD(0.5).setKS(0.5).setShininess(200).setKR(0.3);
		scene.geometries.add(

				new Polygon(new Point(50, -40, -100), new Point(-50, -40, -100), new Point(-50, -40, 50),
						new Point(50, -40, 50)).setEmission(new Color(BLUE)).setMaterial(feltMaterial),
				new Sphere(10d, new Point(0, -20, 0)).setEmission(new Color(RED)).setMaterial(ballMaterial));

		scene.lights.addAll(List.of(

				new SpotLight(new Color(WHITE), new Point(30, 70, 0), new Vector(-5, -9, -3)).setNarrowBeam(10)
						.setKL(0.0001).setKQ(0.0002)
//				new SpotLight(new Color(WHITE), new Point(25, 15, 10), new Vector(1, 1, -2)).setKL(0.0002)
//						.setKQ(0.0003),
//				new SpotLight(new Color(WHITE), new Point(0, 100, -100), new Vector(0, -1, 1)).setNarrowBeam(15)
//						.setKL(0.0003).setKQ(0.00005)

		));
		cameraBuilder.setLocation(new Point(0, 0, 1000)) //
				.setDirection(Point.ZERO, Vector.AXIS_Y) //
				.setVpDistance(1000).setVpSize(200, 200) //
				.setResolution(2000, 2000).setTransition(new Vector(100, 300, 0)) //
				.build() //
				.renderImage() //
				.writeToImage("softShadow");

	}

}
