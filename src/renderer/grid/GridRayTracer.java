package renderer.grid;

import static java.lang.Math.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import geometries.Geometries;
import geometries.Intersectable;
import geometries.Intersectable.AABB;
import geometries.Intersectable.Intersection;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import renderer.SimpleRayTracer;
import scene.Scene;

/**
 * Ray tracer using a fixed-size voxel grid (e.g., 100×100×100) to accelerate
 * intersection checks. Falls back to default tracing for infinite geometries.
 */
public class GridRayTracer extends SimpleRayTracer {
	/** The voxel grid used for spatial acceleration. */
	private VoxelGrid grid;
	/** List of geometries without bounding boxes (e.g., infinite geometries). */
	private Geometries infiniteGeometries = null;

	/**
	 * Constructs a GridRayTracer with the given scene.
	 *
	 * @param scene the scene to trace rays in
	 */
	public GridRayTracer(Scene scene) {
		super(scene);
		AABB originalBox = scene.geometries.getBoundingBox();
		if (originalBox == null) {
			// No bounding box means all geometries infinite or empty scene
			var temp = scene.geometries.getAll();
			if (temp != null)
				infiniteGeometries = new Geometries(temp);
			return;
		}

		AABB expandedBox = getExpandedBoundingBox(originalBox);
		this.grid = new VoxelGrid(expandedBox, 100, 100, 100);

		for (Intersectable geo : scene.geometries.getAll()) {
			AABB geoBox = geo.getBoundingBox();
			if (geoBox == null)
				if (infiniteGeometries == null)
					infiniteGeometries = new Geometries(geo);
				else
					infiniteGeometries.add(geo);
			else
				grid.addGeometry(geo);
		}
	}

	/**
	 * Returns an expanded version of the given bounding box to avoid precision
	 * errors on edges.
	 *
	 * @param originalBox the original bounding box
	 * @return a slightly larger bounding box
	 */
	private AABB getExpandedBoundingBox(AABB originalBox) {
		double epsilon = 1e-4;
		Point newMin = new Point(originalBox.minCorner().getX() - epsilon, originalBox.minCorner().getY() - epsilon,
				originalBox.minCorner().getZ() - epsilon);
		Point newMax = new Point(originalBox.maxCorner().getX() + epsilon, originalBox.maxCorner().getY() + epsilon,
				originalBox.maxCorner().getZ() + epsilon);
		return new AABB(newMin, newMax);
	}

	/**
	 * Clamps the given point to be within the bounds of the specified bounding box.
	 *
	 * @param p   the point to clamp
	 * @param box the bounding box
	 * @return the clamped point
	 */
	private Point clampToBox(Point p, AABB box) {
		double x = max(box.minCorner().getX(), min(box.maxCorner().getX(), p.getX()));
		double y = max(box.minCorner().getY(), min(box.maxCorner().getY(), p.getY()));
		double z = max(box.minCorner().getZ(), min(box.maxCorner().getZ(), p.getZ()));
		return new Point(x, y, z);
	}

	@Override
	protected Intersection findClosestIntersection(Ray ray) {
		if (grid == null)
			return super.findClosestIntersection(ray);

		Intersection closest = infiniteGeometries == null ? null
				: ray.findClosestIntersection(infiniteGeometries.calculateIntersections(ray));

		AABB box = grid.getBoundingBox();
		if (!box.intersects(ray))
			return closest;

		Point startPoint = clampToBox(ray.getPoint(findEntryT(ray, box) + 1e-5), box);
		Index3D voxelIdx = grid.pointToIndex(startPoint);
		if (voxelIdx == null)
			return null;

		Vector dir = ray.getDirection();
		double voxelSize = grid.getVoxelSize();

		int stepX = dir.getX() >= 0 ? 1 : -1;
		int stepY = dir.getY() >= 0 ? 1 : -1;
		int stepZ = dir.getZ() >= 0 ? 1 : -1;

		double nextVoxelX = box.minCorner().getX() + (voxelIdx.i + (stepX > 0 ? 1 : 0)) * voxelSize;
		double nextVoxelY = box.minCorner().getY() + (voxelIdx.j + (stepY > 0 ? 1 : 0)) * voxelSize;
		double nextVoxelZ = box.minCorner().getZ() + (voxelIdx.k + (stepZ > 0 ? 1 : 0)) * voxelSize;

		double tMaxX = (dir.getX() == 0) ? Double.POSITIVE_INFINITY : (nextVoxelX - ray.getHead().getX()) / dir.getX();
		double tMaxY = (dir.getY() == 0) ? Double.POSITIVE_INFINITY : (nextVoxelY - ray.getHead().getY()) / dir.getY();
		double tMaxZ = (dir.getZ() == 0) ? Double.POSITIVE_INFINITY : (nextVoxelZ - ray.getHead().getZ()) / dir.getZ();

		double tDeltaX = (dir.getX() == 0) ? Double.POSITIVE_INFINITY : voxelSize / Math.abs(dir.getX());
		double tDeltaY = (dir.getY() == 0) ? Double.POSITIVE_INFINITY : voxelSize / Math.abs(dir.getY());
		double tDeltaZ = (dir.getZ() == 0) ? Double.POSITIVE_INFINITY : voxelSize / Math.abs(dir.getZ());

		double minDistance = closest == null ? Double.POSITIVE_INFINITY : ray.getHead().distance(closest.point);

		Set<Intersectable> check = new HashSet<>();
		while (voxelIdx.i >= 0 && voxelIdx.i < grid.getSizeX() && voxelIdx.j >= 0 && voxelIdx.j < grid.getSizeY()
				&& voxelIdx.k >= 0 && voxelIdx.k < grid.getSizeZ()) {
			Voxel voxel = grid.getVoxel(voxelIdx);
			if (voxel != null) {
				var mapped = voxel.getGeometries().getAll();
				for (var geometry : mapped) {
					if (check.add(geometry)) {
						var temp = ray.findClosestIntersection(geometry.calculateIntersections(ray, minDistance));
						if (temp != null) {
							closest = temp;
							minDistance = ray.getHead().distance(closest.point);
						}
					}
				}
			}

			if (tMaxX < tMaxY) {
				if (tMaxX < tMaxZ) {
					voxelIdx = voxelIdx.add(stepX, 0, 0);
					tMaxX += tDeltaX;
				} else {
					voxelIdx = voxelIdx.add(0, 0, stepZ);
					tMaxZ += tDeltaZ;
				}
			} else {
				if (tMaxY < tMaxZ) {
					voxelIdx = voxelIdx.add(0, stepY, 0);
					tMaxY += tDeltaY;
				} else {
					voxelIdx = voxelIdx.add(0, 0, stepZ);
					tMaxZ += tDeltaZ;
				}
			}
		}

		return closest;
	}

	@Override
	protected List<Intersection> findAllIntersections(Ray ray, double maxDistance) {
		if (grid == null)
			return super.findAllIntersections(ray, maxDistance);

		var allIntersections = infiniteGeometries == null ? null
				: infiniteGeometries.calculateIntersections(ray, maxDistance);

		AABB box = grid.getBoundingBox();
		if (!box.intersects(ray))
			return allIntersections;

		Point startPoint = clampToBox(ray.getPoint(findEntryT(ray, box) + 1e-5), box);
		Index3D voxelIdx = grid.pointToIndex(startPoint);
		if (voxelIdx == null)
			return null;

		Vector dir = ray.getDirection();
		double voxelSize = grid.getVoxelSize();

		int stepX = dir.getX() >= 0 ? 1 : -1;
		int stepY = dir.getY() >= 0 ? 1 : -1;
		int stepZ = dir.getZ() >= 0 ? 1 : -1;

		double nextVoxelX = box.minCorner().getX() + (voxelIdx.i + (stepX > 0 ? 1 : 0)) * voxelSize;
		double nextVoxelY = box.minCorner().getY() + (voxelIdx.j + (stepY > 0 ? 1 : 0)) * voxelSize;
		double nextVoxelZ = box.minCorner().getZ() + (voxelIdx.k + (stepZ > 0 ? 1 : 0)) * voxelSize;

		double tMaxX = (dir.getX() == 0) ? Double.POSITIVE_INFINITY : (nextVoxelX - ray.getHead().getX()) / dir.getX();
		double tMaxY = (dir.getY() == 0) ? Double.POSITIVE_INFINITY : (nextVoxelY - ray.getHead().getY()) / dir.getY();
		double tMaxZ = (dir.getZ() == 0) ? Double.POSITIVE_INFINITY : (nextVoxelZ - ray.getHead().getZ()) / dir.getZ();

		double tDeltaX = (dir.getX() == 0) ? Double.POSITIVE_INFINITY : voxelSize / Math.abs(dir.getX());
		double tDeltaY = (dir.getY() == 0) ? Double.POSITIVE_INFINITY : voxelSize / Math.abs(dir.getY());
		double tDeltaZ = (dir.getZ() == 0) ? Double.POSITIVE_INFINITY : voxelSize / Math.abs(dir.getZ());

		Set<Intersectable> check = new HashSet<>();
		while (voxelIdx.i >= 0 && voxelIdx.i < grid.getSizeX() && voxelIdx.j >= 0 && voxelIdx.j < grid.getSizeY()
				&& voxelIdx.k >= 0 && voxelIdx.k < grid.getSizeZ()) {
			Voxel voxel = grid.getVoxel(voxelIdx);
			if (voxel != null) {
				var mapped = voxel.getGeometries().getAll();
				for (var geometry : mapped) {
					if (check.add(geometry)) {
						var intersections = geometry.calculateIntersections(ray, maxDistance);
						if (intersections != null)
							if (allIntersections == null)
								allIntersections = new LinkedList<>(intersections);
							else
								allIntersections.addAll(intersections);
					}
				}
			}

			if (tMaxX < tMaxY) {
				if (tMaxX < tMaxZ) {
					voxelIdx = voxelIdx.add(stepX, 0, 0);
					tMaxX += tDeltaX;
				} else {
					voxelIdx = voxelIdx.add(0, 0, stepZ);
					tMaxZ += tDeltaZ;
				}
			} else {
				if (tMaxY < tMaxZ) {
					voxelIdx = voxelIdx.add(0, stepY, 0);
					tMaxY += tDeltaY;
				} else {
					voxelIdx = voxelIdx.add(0, 0, stepZ);
					tMaxZ += tDeltaZ;
				}
			}
		}

		return allIntersections;
	}

	/**
	 * Calculates the entry point parameter t where the ray first intersects the
	 * bounding box.
	 *
	 * @param ray the ray to check
	 * @param box the bounding box
	 * @return the entry t value, or -1 if no intersection
	 */
	private double findEntryT(Ray ray, AABB box) {
		Point origin = ray.getHead();
		Vector dir = ray.getDirection();

		double tMin = Double.NEGATIVE_INFINITY;
		double tMax = Double.POSITIVE_INFINITY;

		double[] originArr = { origin.getX(), origin.getY(), origin.getZ() };
		double[] dirArr = { dir.getX(), dir.getY(), dir.getZ() };
		double[] minArr = { box.minCorner().getX(), box.minCorner().getY(), box.minCorner().getZ() };
		double[] maxArr = { box.maxCorner().getX(), box.maxCorner().getY(), box.maxCorner().getZ() };

		for (int i = 0; i < 3; i++) {
			if (dirArr[i] == 0) {
				if (originArr[i] < minArr[i] || originArr[i] > maxArr[i])
					return -1;
			} else {
				double t1 = (minArr[i] - originArr[i]) / dirArr[i];
				double t2 = (maxArr[i] - originArr[i]) / dirArr[i];
				double tNear = min(t1, t2);
				double tFar = max(t1, t2);

				tMin = max(tMin, tNear);
				tMax = min(tMax, tFar);

				if (tMin > tMax)
					return -1;
			}
		}
		return tMin;
	}

}
