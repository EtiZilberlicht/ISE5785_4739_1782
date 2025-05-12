package unittests.renderer;

import org.junit.jupiter.api.Test;

import primitives.Color;
import renderer.ImageWriter;

/**
 * Unit test for {@link ImageWriter}.
 * <p>
 * This test verifies the functionality of writing an image to a file, including
 * coloring the entire image and drawing a grid (net) at regular intervals.
 * </p>
 */
class ImageWriterTest {

	/**
	 * default constructor
	 */
	public ImageWriterTest() {
	}

	/**
	 * Test method for {@link ImageWriter#writeToImage(String)}.
	 * <p>
	 * Creates an image of size 801x501 pixels. Fills the background with a light
	 * pink color and draws vertical and horizontal grid lines every 50 pixels using
	 * a darker pink color. The resulting image is written to a file named
	 * {@code "image1.png"}.
	 * </p>
	 */
	@Test
	void writeToImageTest() {
		final int width = 801;
		final int height = 501;
		final int interval = 50;

		ImageWriter imageWriter = new ImageWriter(width, height);

		Color backgroundColor = new Color(245, 186, 186);
		Color net = new Color(245, 142, 142);

		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				imageWriter.writePixel(x, y, x % interval == 0 || y % interval == 0 ? net : backgroundColor);

		imageWriter.writeToImage("image1");
	}
}
