package geometries;

import primitives.Point;

/**
 * The {@code Triangle} class represents a triangle in 3D space. It extends the
 * {@code Polygon} class, inheriting its properties and behaviors.
 */
public class Triangle extends Polygon {

	public Triangle(Point vertex1, Point vertex2, Point vertex3){// 3 vertices for polygon
		super(vertex1, vertex2, vertex3);
	}

}
