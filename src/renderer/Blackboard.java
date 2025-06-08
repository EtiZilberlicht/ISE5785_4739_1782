package renderer;

import static primitives.Util.isZero;

import java.util.ArrayList;
import java.util.List;

import geometries.Plane;
import primitives.Point;
import primitives.Vector;

public class Blackboard {

	int gridSize = 9;

	int numOfRays = 9;

	double dividedRays;

	double cellSize;

	public Blackboard() {
		this.dividedRays = Math.sqrt(this.numOfRays);
		this.cellSize = this.gridSize / this.dividedRays;
	}

	public List<Vector> vectorBeam(Point position, Point source, Plane plane) {
		List<Vector> vectors = new ArrayList<>();
		Vector v1 = plane.getV1();
		Vector v2 = plane.getV2();
		Point startPoint = position.add(v1.scale(-gridSize / 2.0)).add(v2.normalize().scale(-gridSize / 2.0));
		for (int i = 0; i < dividedRays; i++) {
			for (int j = 0; j < dividedRays; j++) {

				double jitter1 = (i + Math.random()) * cellSize;
				double jitter2 = (j + Math.random()) * cellSize;
				Point p = startPoint;
				if (!isZero(jitter1))
					p = p.add(v1.scale(jitter1));
				if (!isZero(jitter2))
					p = p.add(v2.scale(jitter2));

				vectors.add(source.subtract(p).normalize());
			}
		}
		return vectors;
	}

}
