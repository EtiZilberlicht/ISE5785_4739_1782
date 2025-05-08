package renderer;

import org.junit.jupiter.api.Test;

import primitives.Color;

class ImageWriterTest {
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