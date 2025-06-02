package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

/**
 * An interface for light sources that can provide light intensity and direction
 * at a given point in the scene.
 */
public interface LightSource {

	/**
	 * Returns the intensity (color) of the light at the given point.
	 *
	 * @param p the point in space to evaluate the light intensity at
	 * @return the {@link Color} representing the light intensity at point {@code p}
	 */
	public Color getIntensity(Point p);

	/**
	 * Returns the normalized direction vector from the light source to the given
	 * point.
	 *
	 * @param p the point in space to which the light direction is computed
	 * @return the normalized {@link Vector} from the light to point {@code p}
	 */
	public Vector getL(Point p);

	/**
	 * Returns the distance from the light source to the given point.
	 *
	 * @param point the point to measure distance to
	 * @return the distance between the light source and the given point
	 */
	public double getDistance(Point point);

}
