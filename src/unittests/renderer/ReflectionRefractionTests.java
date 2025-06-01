package unittests.renderer;

import static java.awt.Color.BLUE;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;

import java.util.List;

import org.junit.jupiter.api.Test;

import geometries.Cylinder;
import geometries.Plane;
import geometries.Polygon;
import geometries.Sphere;
import geometries.Triangle;
import geometries.Tube;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import primitives.Color;
import primitives.Double3;
import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import renderer.Camera;
import renderer.RayTracerType;
import scene.Scene;

/**
 * Tests for reflection and transparency functionality, test for partial shadows
 * (with transparency)
 * 
 * @author Dan Zilberstein
 */
class ReflectionRefractionTests {
	/** Default constructor to satisfy JavaDoc generator */
	ReflectionRefractionTests() {
		/* to satisfy JavaDoc generator */ }

	/** Scene for the tests */
	private final Scene scene = new Scene("Test scene");
	/** Camera builder for the tests with triangles */
	private final Camera.Builder cameraBuilder = Camera.getBuilder() //
			.setRayTracer(scene, RayTracerType.SIMPLE);

	/** Produce a picture of a sphere lighted by a spot light */
	@Test
	void twoSpheres() {
		scene.geometries.add( //
				new Sphere(50d, new Point(0, 0, -50)).setEmission(new Color(BLUE)) //
						.setMaterial(new Material().setKD(0.4).setKS(0.3).setShininess(100).setKT(0.3)), //
				new Sphere(25d, new Point(0, 0, -50)).setEmission(new Color(RED)) //
						.setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(100))); //
		scene.lights.add( //
				new SpotLight(new Color(1000, 600, 0), new Point(-100, -100, 500), new Vector(-1, -1, -2)) //
						.setKL(0.0004).setKQ(0.0000006));

		cameraBuilder.setLocation(new Point(0, 0, 1000)) //
				.setDirection(Point.ZERO, Vector.AXIS_Y) //
				.setVpDistance(1000).setVpSize(150, 150) //
				.setResolution(500, 500) //
				.build() //
				.renderImage() //
				.writeToImage("refractionTwoSpheres");
	}

	/** Produce a picture of a sphere lighted by a spot light */
	@Test
	void twoSpheresOnMirrors() {
		scene.geometries.add( //
				new Sphere(400d, new Point(-950, -900, -1000)).setEmission(new Color(0, 50, 100)) //
						.setMaterial(new Material().setKD(0.25).setKS(0.25).setShininess(20) //
								.setKT(new Double3(0.5, 0, 0))), //
				new Sphere(200d, new Point(-950, -900, -1000)).setEmission(new Color(100, 50, 20)) //
						.setMaterial(new Material().setKD(0.25).setKS(0.25).setShininess(20)), //
				new Triangle(new Point(1500, -1500, -1500), new Point(-1500, 1500, -1500), //
						new Point(670, 670, 3000)) //
						.setEmission(new Color(20, 20, 20)) //
						.setMaterial(new Material().setKR(1)), //
				new Triangle(new Point(1500, -1500, -1500), new Point(-1500, 1500, -1500), //
						new Point(-1500, -1500, -2000)) //
						.setEmission(new Color(20, 20, 20)) //
						.setMaterial(new Material().setKR(new Double3(0.5, 0, 0.4))));
		scene.setAmbientLight(new AmbientLight(new Color(26, 26, 26)));
		scene.lights.add(new SpotLight(new Color(1020, 400, 400), new Point(-750, -750, -150), new Vector(-1, -1, -4)) //
				.setKL(0.00001).setKQ(0.000005));

		cameraBuilder.setLocation(new Point(0, 0, 10000)) //
				.setDirection(Point.ZERO, Vector.AXIS_Y) //
				.setVpDistance(10000).setVpSize(2500, 2500) //
				.setResolution(500, 500) //
				.build() //
				.renderImage() //
				.writeToImage("reflectionTwoSpheresMirrored");
	}

	/**
	 * Produce a picture of a two triangles lighted by a spot light with a partially
	 * transparent Sphere producing partial shadow
	 */
	@Test
	void trianglesTransparentSphere() {
		scene.geometries.add(
				new Triangle(new Point(-150, -150, -115), new Point(150, -150, -135), new Point(75, 75, -150))
						.setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(60)),
				new Triangle(new Point(-150, -150, -115), new Point(-70, 70, -140), new Point(75, 75, -150))
						.setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(60)),
				new Sphere(30d, new Point(60, 50, -50)).setEmission(new Color(BLUE))
						.setMaterial(new Material().setKD(0.2).setKS(0.2).setShininess(30).setKT(0.6)));
		scene.setAmbientLight(new AmbientLight(new Color(38, 38, 38)));
		scene.lights.add(new SpotLight(new Color(700, 400, 400), new Point(60, 50, 0), new Vector(0, 0, -1)).setKL(4E-5)
				.setKQ(2E-7));

		cameraBuilder.setLocation(new Point(0, 0, 1000)) //
				.setDirection(Point.ZERO, Vector.AXIS_Y) //
				.setVpDistance(1000).setVpSize(200, 200) //
				.setResolution(600, 600) //
				.build() //
				.renderImage() //
				.writeToImage("refractionShadow");
	}

	@Test
	void artisticBalancedScene() {
		// צבעים רכים אך בעלי עומק
		Color skyBlue = new Color(120, 180, 220);
		Color rose = new Color(220, 100, 130);
		Color mint = new Color(130, 220, 170);
		Color softGold = new Color(230, 200, 120);
		Color lavender = new Color(170, 140, 210);
		Color darkBackground = new Color(30, 30, 50); // רקע כהה

		// רקע (מישור רצפה כהה)
		scene.geometries.add(
				new Plane(new Point(0, -40, 0), new Vector(0, 1, 0)).setEmission(darkBackground)
						.setMaterial(new Material().setKD(0.5).setKS(0.2).setKR(0.1)),

				new Plane(new Point(0, 0, 250), new Vector(0, 0, -1)).setEmission(new Color(50, 50, 80))
						.setMaterial(new Material().setKD(0.5).setKS(0.3)));

		// בועות שקופות – עם ניגוד
		scene.geometries.add(
				new Sphere(25, new Point(-60, 0, 100)).setEmission(Color.BLACK)
						.setMaterial(new Material().setKD(0.1).setKS(0.9).setShininess(300).setKT(0.92)),

				new Sphere(25, new Point(0, 20, 120)).setEmission(Color.BLACK)
						.setMaterial(new Material().setKD(0.1).setKS(0.9).setShininess(300).setKT(0.92)),

				new Sphere(25, new Point(60, 0, 100)).setEmission(Color.BLACK)
						.setMaterial(new Material().setKD(0.1).setKS(0.9).setShininess(300).setKT(0.92)));

		// צורות נוספות – בולטים מול הרקע
		scene.geometries.add(
				new Triangle(new Point(-90, -30, 80), new Point(-70, -5, 90), new Point(-50, -30, 80)).setEmission(rose)
						.setMaterial(new Material().setKD(0.5).setKS(0.4).setShininess(100)),

				new Polygon(new Point(-30, -30, 60), new Point(-10, -30, 60), new Point(-10, -10, 60),
						new Point(-30, -10, 60)).setEmission(skyBlue)
						.setMaterial(new Material().setKD(0.5).setKS(0.4).setShininess(100)),

				new Tube(new Ray(new Point(30, -20, 90), new Vector(0, 1, 0)), 5).setEmission(lavender)
						.setMaterial(new Material().setKD(0.3).setKS(0.4).setShininess(100)),

				new Cylinder(8, new Ray(new Point(60, -30, 130), new Vector(0, 1, 0)), 60).setEmission(mint)
						.setMaterial(new Material().setKD(0.4).setKS(0.5).setShininess(150)),

				new Triangle(new Point(80, -30, 70), new Point(100, -5, 80), new Point(120, -30, 70))
						.setEmission(softGold).setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(120)),

				new Polygon(new Point(-20, 50, 140), new Point(0, 60, 145), new Point(20, 50, 140),
						new Point(0, 40, 135)).setEmission(lavender)
						.setMaterial(new Material().setKD(0.4).setKS(0.6).setShininess(120)));

		// תאורה: מקור אחד מודגש + אור רקע
		scene.lights.add(new SpotLight(new Color(800, 700, 600), new Point(0, 100, -100), new Vector(0, -1, 1))
				.setNarrowBeam(15).setKL(0.0003).setKQ(0.000005));

		scene.lights.add(new DirectionalLight(new Color(100, 100, 120), new Vector(-1, -1, -1)));

		// מצלמה
		cameraBuilder.setLocation(new Point(0, 0, -300)).setDirection(new Vector(0, 0, 1), new Vector(0, 1, 0))
				.setVpDistance(1000).setVpSize(500, 500).setResolution(600, 600).build().renderImage()
				.writeToImage("artistic_composition_scene");
	}

	private static Point[] balls = {
			// שורה ראשונה - 5 כדורים (קרובה למצלמה)
			new Point(-24.0, -34.0, 20.0), new Point(-12.0, -34.0, 20.0), new Point(0.0, -34.0, 20.0),
			new Point(12.0, -34.0, 20.0), new Point(24.0, -34.0, 20.0),

			// שורה שנייה - 4 כדורים
			new Point(-18.0, -34.0, 9.61), new Point(-6.0, -34.0, 9.61), new Point(6.0, -34.0, 9.61),
			new Point(18.0, -34.0, 9.61),

			// שורה שלישית - 3 כדורים
			new Point(-12.0, -34.0, -0.78), new Point(0.0, -34.0, -0.78), new Point(12.0, -34.0, -0.78),

			// שורה רביעית - 2 כדורים
			new Point(-6.0, -34.0, -11.17), new Point(6.0, -34.0, -11.17),

			// שורה חמישית - 1 כדור
			new Point(0.0, -34.0, -21.56),

			// הכדור הבודד (מאחור, z קטן)
			new Point(0.0, -34.0, -60.0) };

	private static Color[] ballColors = {
			// שורה ראשונה (5 כדורים)
			new Color(255, 127, 0), // 13 - כתום עם פס
			new Color(128, 0, 128), // 9 - צהוב עם פס
			new Color(255, 165, 0), // 5 - כתום מלא
			new Color(255, 0, 0), // 11 - אדום עם פס
			new Color(0, 128, 0), // 6 - ירוק מלא

			// שורה שנייה (4 כדורים)
			new Color(128, 0, 128), // 4 - סגול מלא
			new Color(0, 0, 255), // 10 - כחול עם פס
			new Color(128, 0, 0), // 7 - בורדו מלא
			new Color(139, 0, 0), // 15 - בורדו עם פס

			// שורה שלישית (3 כדורים)
			new Color(255, 0, 0), // 3 - אדום מלא
			new Color(0, 0, 0), // 8 - שחור
			new Color(148, 0, 211), // 12 - סגול עם פס

			// שורה רביעית (2 כדורים)
			new Color(0, 128, 0), // 14 - ירוק עם פס
			new Color(0, 0, 255), // 2 - כחול מלא

			// שורה חמישית (1 כדור)
			new Color(255, 215, 0), // 1 - צהוב מלא

			// הכדור הבודד מאחור (יכול להיות הלבן או תלוי בקונטקסט שלך)
			new Color(210, 205, 190) // כדור לבן
	};

	private Sphere[] spheres() {
		// חומר לכדורים (מבריקים)
		Material ballMaterial = new Material().setKD(0.5).setKS(0.5).setShininess(200).setKR(0.5);
		Sphere[] spheres = new Sphere[16];
		for (int i = 0; i < 16; i++) {
			spheres[i] = new Sphere(6, balls[i]);
			spheres[i].setEmission(ballColors[i]).setMaterial(ballMaterial);
		}
		return spheres;
	}

	@Test
	void billiards() {
		// חומר לבד ירוק
		Material feltMaterial = new Material().setKD(0.8) // פיזור אור גבוה
				.setKS(0.1). // מעט הברקה
				setKT(0.05).setShininess(100);

		// חומר למסגרת שחורה
		Material frameMaterial = new Material().setKD(0.2).setKS(0.7).setShininess(200).setKR(0.1);
		;
		// חומר לבסיס הלוח (שחור)
		Material baseMaterial = new Material().setKD(0.4).setKS(0.2).setKT(0.3).setShininess(80);

		// חומר למשולשים דקורטיביים
		Material triangleMaterial = new Material().setKD(0.4).setKD(0.2).setKS(0.7).setShininess(200).setKT(0.4) // שקיפות
				.setKR(0.3);

		// חומר למקל
		Material stickMaterial = new Material().setKD(0.6).setKS(0.3).setShininess(100);

		// חומר לרצפה
		Material wallMaterial = new Material().setKD(0.1).setKS(0.9).setShininess(300).setKR(0.7);

		// רגל קדמית ימין
		Point p1a = new Point(49, -44, 49);
		Point p2a = new Point(49, -44, 47);
		Point p3a = new Point(47, -44, 47);
		Point p4a = new Point(47, -44, 49);
		Point tipA = new Point(48, -50, 48);

		// רגל קדמית שמאל
		Point p1b = new Point(-47, -44, 49);
		Point p2b = new Point(-47, -44, 47);
		Point p3b = new Point(-49, -44, 47);
		Point p4b = new Point(-49, -44, 49);
		Point tipB = new Point(-48, -50, 48);

		// רגל אחורית ימין
		Point p1c = new Point(49, -44, -97);
		Point p2c = new Point(49, -44, -99);
		Point p3c = new Point(47, -44, -99);
		Point p4c = new Point(47, -44, -97);
		Point tipC = new Point(48, -50, -98);

		// רגל אחורית שמאל
		Point p1d = new Point(-47, -44, -97);
		Point p2d = new Point(-47, -44, -99);
		Point p3d = new Point(-49, -44, -99);
		Point p4d = new Point(-49, -44, -97);
		Point tipD = new Point(-48, -50, -98);

		Vector stickVector = new Vector(-90, 0, -75).normalize();
		Point stickPoint = new Point(18, -35.5, 69);
		Ray stick1 = new Ray(stickPoint, stickVector);
		Ray stick2 = new Ray(stick1.getPoint(10), stickVector);
		Ray stick3 = new Ray(stick2.getPoint(2), stickVector);
		Ray stick4 = new Ray(stick3.getPoint(2), stickVector);
		Ray stick5 = new Ray(stick4.getPoint(6), stickVector);

		// רקע (מישור רצפה כהה)
		scene.geometries.add(
				new Polygon(new Point(50, -40, -100), new Point(-50, -40, -100), new Point(-50, -40, 50),
						new Point(50, -40, 50)).setEmission(new Color(21, 88, 67)).setMaterial(feltMaterial),

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

				new Sphere(3, new Point(48, -52, 48)).setMaterial(triangleMaterial),
				new Sphere(3, new Point(-48, -52, 48)).setMaterial(triangleMaterial),
				new Sphere(3, new Point(-48, -52, -98)).setMaterial(triangleMaterial),
				new Sphere(3, new Point(48, -52, -98)).setMaterial(triangleMaterial),

				new Cylinder(1.5, stick1, 10).setEmission(new Color(205, 133, 63)).setMaterial(stickMaterial),
				new Cylinder(1.5, stick2, 2).setEmission(new Color(180, 30, 30)).setMaterial(stickMaterial),
				new Cylinder(1.5, stick3, 2).setEmission(new Color(205, 133, 63)).setMaterial(stickMaterial),
				new Cylinder(1.5, stick4, 6).setEmission(new Color(211, 211, 211)).setMaterial(stickMaterial),
				new Cylinder(1.5, stick5, 110).setEmission(new Color(205, 133, 63)).setMaterial(stickMaterial),

				new Polygon(new Point(100, -55, -200), new Point(-100, -55, -200), new Point(-100, -55, 60),
						new Point(100, -55, 60)).setEmission(new Color(80, 80, 80)).setMaterial(wallMaterial)

				, new Polygon(new Point(100, 45, -200), // ימין למעלה
						new Point(-100, 45, -200), // שמאל למעלה
						new Point(-100, -55, -200), // שמאל למטה (חיבור לרצפה)
						new Point(100, -55, -200) // ימין למטה (חיבור לרצפה)
				) // ימין עליון
						.setEmission(new Color(80, 80, 80)).setMaterial(wallMaterial)

		);
		scene.geometries.add(spheres());

		scene.setBackground(new Color(0, 128, 128));

		scene.lights.addAll(List.of(

				new DirectionalLight(new Color(WHITE), new Vector(-50, 30, -20)),
				new DirectionalLight(new Color(WHITE), new Vector(60, 30, -10)),
				new PointLight(new Color(WHITE), new Point(30, 70, 0)).setKL(0.0001).setKQ(0.0002),
				new SpotLight(new Color(WHITE), new Point(25, 15, 10), new Vector(1, 1, -2)).setKL(0.0002)
						.setKQ(0.0003),
				new SpotLight(new Color(WHITE), new Point(0, 100, -100), new Vector(0, -1, 1)).setNarrowBeam(15)
						.setKL(0.0003).setKQ(0.00005)

		));

		cameraBuilder.setLocation(new Point(0, 0, 300))
				.setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0)/* new Point(-6.0, -34.0, 9.61) */)
				.setVpDistance(1000).setVpSize(500, 500).setResolution(2000, 2000).build().renderImage()
				.writeToImage("aaa");
	}

}
