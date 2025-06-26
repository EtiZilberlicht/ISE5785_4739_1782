package renderer.grid;

import java.util.List;

import geometries.Geometries;
import geometries.Intersectable;
import geometries.Intersectable.AABB;
import geometries.Intersectable.Intersection;
import primitives.Double3;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import renderer.SimpleRayTracer;
import scene.Scene;

/**
 * Ray tracer implementation that accelerates ray-scene intersection using a
 * uniform voxel grid (3D spatial subdivision).
 * <p>
 * Divides the scene bounding box into a 10x10x10 voxel grid, assigns geometries
 * to voxels, and performs ray traversal using 3D DDA to find intersections
 * efficiently.
 */
public class GridRayTracer extends SimpleRayTracer {

	/**
	 * The voxel grid used for accelerating ray-geometry intersection tests. Divides
	 * the scene's bounding box into discrete voxels containing geometries, enabling
	 * efficient traversal with the 3D DDA algorithm.
	 */
	private final VoxelGrid grid;

	/**
	 * Constructs a GridRayTracer for the given scene. Initializes the voxel grid
	 * based on the scene's bounding box, and inserts all geometries into the
	 * appropriate voxels.
	 *
	 * @param scene the scene to be ray-traced (must have a bounding box)
	 * @throws IllegalArgumentException if the scene does not have a bounding box
	 */
	public GridRayTracer(Scene scene) {
		super(scene);

		Geometries geometries = scene.geometries;
		AABB sceneBox = geometries.getBoundingBox();
		if (sceneBox == null) {
			throw new IllegalArgumentException("Scene must have bounding box");
		}

		this.grid = new VoxelGrid(sceneBox, 10, 10, 10);
		for (Intersectable geo : geometries.getAll()) {
			grid.addGeometry(geo);
		}

	}

	/**
	 * Clamps a given point to lie within the given axis-aligned bounding box
	 * (AABB). For each coordinate, if the point is outside the box, it is clamped
	 * to the nearest boundary.
	 *
	 * @param p   the point to clamp
	 * @param box the bounding box to clamp against
	 * @return a new Point clamped inside the bounding box
	 */
	private Point clampToBox(Point p, AABB box) {
		double x = Math.max(box.getMin().getX(), Math.min(box.getMax().getX(), p.getX()));
		double y = Math.max(box.getMin().getY(), Math.min(box.getMax().getY(), p.getY()));
		double z = Math.max(box.getMin().getZ(), Math.min(box.getMax().getZ(), p.getZ()));
		return new Point(x, y, z);
	}

	@Override
	protected Intersection findClosestIntersection(Ray ray) {
		AABB box = grid.getBoundingBox();
		if (!box.intersects(ray)) {
			return null;
		}

		double tMin = findEntryT(ray, box);
		if (tMin < 0)
			tMin = 0;

		// נוודא שהנקודה נצמדת פנימה לתוך ה־box
		Point startPoint = clampToBox(ray.getPoint(tMin + 1e-5), box);
		Index3D voxelIdx = grid.pointToIndex(startPoint);
		if (voxelIdx == null) {
			return null;
		}

		Vector dir = ray.getDirection();
		double voxelSize = grid.getVoxelSize();

		int stepX = dir.getX() >= 0 ? 1 : -1;
		int stepY = dir.getY() >= 0 ? 1 : -1;
		int stepZ = dir.getZ() >= 0 ? 1 : -1;

		double nextVoxelBoundaryX = box.getMin().getX() + (voxelIdx.i + (stepX > 0 ? 1 : 0)) * voxelSize;
		double nextVoxelBoundaryY = box.getMin().getY() + (voxelIdx.j + (stepY > 0 ? 1 : 0)) * voxelSize;
		double nextVoxelBoundaryZ = box.getMin().getZ() + (voxelIdx.k + (stepZ > 0 ? 1 : 0)) * voxelSize;

		double tMaxX = (dir.getX() == 0) ? Double.POSITIVE_INFINITY
				: (nextVoxelBoundaryX - ray.getHead().getX()) / dir.getX();
		double tMaxY = (dir.getY() == 0) ? Double.POSITIVE_INFINITY
				: (nextVoxelBoundaryY - ray.getHead().getY()) / dir.getY();
		double tMaxZ = (dir.getZ() == 0) ? Double.POSITIVE_INFINITY
				: (nextVoxelBoundaryZ - ray.getHead().getZ()) / dir.getZ();

		double tDeltaX = (dir.getX() == 0) ? Double.POSITIVE_INFINITY : voxelSize / Math.abs(dir.getX());
		double tDeltaY = (dir.getY() == 0) ? Double.POSITIVE_INFINITY : voxelSize / Math.abs(dir.getY());
		double tDeltaZ = (dir.getZ() == 0) ? Double.POSITIVE_INFINITY : voxelSize / Math.abs(dir.getZ());

		Intersectable.Intersection closestIntersection = null;
		double closestDistance = Double.POSITIVE_INFINITY;

		while (voxelIdx.i >= 0 && voxelIdx.i < grid.getSizeX() && voxelIdx.j >= 0 && voxelIdx.j < grid.getSizeY()
				&& voxelIdx.k >= 0 && voxelIdx.k < grid.getSizeZ()) {

			Voxel voxel = grid.getVoxel(voxelIdx);
			if (voxel != null && !voxel.isEmpty()) {
				for (Intersectable geo : voxel.getGeometries()) {
					List<Intersectable.Intersection> intersections = geo.calculateIntersections(ray);
					if (intersections != null) {
						for (Intersectable.Intersection inter : intersections) {
							double dist = inter.point.distance(ray.getHead());
							if (dist < closestDistance && dist >= tMin) {
								closestDistance = dist;
								closestIntersection = inter;
							}
						}
					}
				}
			}

			// מעבר לווקסל הבא לפי כיוון הקרן
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

		return closestIntersection;
	}

	/**
	 * Calculates the parameter t at which the ray first enters the given bounding
	 * box. If the ray misses the box, returns -1.
	 *
	 * @param ray the ray to test
	 * @param box the axis-aligned bounding box
	 * @return the entry t parameter along the ray, or -1 if no intersection
	 */
	private double findEntryT(Ray ray, AABB box) {
		Point origin = ray.getHead();
		Vector dir = ray.getDirection();

		double tMin = Double.NEGATIVE_INFINITY;
		double tMax = Double.POSITIVE_INFINITY;

		double[] originArr = { origin.getX(), origin.getY(), origin.getZ() };
		double[] dirArr = { dir.getX(), dir.getY(), dir.getZ() };
		double[] minArr = { box.getMin().getX(), box.getMin().getY(), box.getMin().getZ() };
		double[] maxArr = { box.getMax().getX(), box.getMax().getY(), box.getMax().getZ() };

		for (int i = 0; i < 3; i++) {
			if (dirArr[i] == 0) {
				if (originArr[i] < minArr[i] || originArr[i] > maxArr[i])
					return -1;
			} else {
				double t1 = (minArr[i] - originArr[i]) / dirArr[i];
				double t2 = (maxArr[i] - originArr[i]) / dirArr[i];
				double tNear = Math.min(t1, t2);
				double tFar = Math.max(t1, t2);

				tMin = Math.max(tMin, tNear);
				tMax = Math.min(tMax, tFar);

				if (tMin > tMax)
					return -1;
			}
		}
		return tMin;
	}

	/**
	 * Finds the closest intersection between the ray and the scene geometries by
	 * traversing the voxel grid using 3D DDA, but only up to a maximum distance. If
	 * an intersection is found closer than maxDistance, returns it immediately.
	 *
	 * @param ray         the ray to test for intersections
	 * @param maxDistance the maximum distance along the ray to consider
	 *                    intersections
	 * @return the closest Intersection within maxDistance, or null if none found
	 */
	protected Intersection findClosestIntersection(Ray ray, double maxDistance) {
		AABB box = grid.getBoundingBox();
		if (!box.intersects(ray))
			return null;

		double tMin = findEntryT(ray, box);
		if (tMin < 0)
			tMin = 0;

		Point startPoint = clampToBox(ray.getPoint(tMin + 1e-5), box);
		Index3D voxelIdx = grid.pointToIndex(startPoint);
		if (voxelIdx == null)
			return null;

		Vector dir = ray.getDirection();
		double voxelSize = grid.getVoxelSize();

		int stepX = dir.getX() >= 0 ? 1 : -1;
		int stepY = dir.getY() >= 0 ? 1 : -1;
		int stepZ = dir.getZ() >= 0 ? 1 : -1;

		double nextVoxelBoundaryX = box.getMin().getX() + (voxelIdx.i + (stepX > 0 ? 1 : 0)) * voxelSize;
		double nextVoxelBoundaryY = box.getMin().getY() + (voxelIdx.j + (stepY > 0 ? 1 : 0)) * voxelSize;
		double nextVoxelBoundaryZ = box.getMin().getZ() + (voxelIdx.k + (stepZ > 0 ? 1 : 0)) * voxelSize;

		double tMaxX = (dir.getX() == 0) ? Double.POSITIVE_INFINITY
				: (nextVoxelBoundaryX - ray.getHead().getX()) / dir.getX();
		double tMaxY = (dir.getY() == 0) ? Double.POSITIVE_INFINITY
				: (nextVoxelBoundaryY - ray.getHead().getY()) / dir.getY();
		double tMaxZ = (dir.getZ() == 0) ? Double.POSITIVE_INFINITY
				: (nextVoxelBoundaryZ - ray.getHead().getZ()) / dir.getZ();

		double tDeltaX = (dir.getX() == 0) ? Double.POSITIVE_INFINITY : voxelSize / Math.abs(dir.getX());
		double tDeltaY = (dir.getY() == 0) ? Double.POSITIVE_INFINITY : voxelSize / Math.abs(dir.getY());
		double tDeltaZ = (dir.getZ() == 0) ? Double.POSITIVE_INFINITY : voxelSize / Math.abs(dir.getZ());

		Intersectable.Intersection closestIntersection = null;
		double closestDistance = Double.POSITIVE_INFINITY;

		while (voxelIdx.i >= 0 && voxelIdx.i < grid.getSizeX() && voxelIdx.j >= 0 && voxelIdx.j < grid.getSizeY()
				&& voxelIdx.k >= 0 && voxelIdx.k < grid.getSizeZ()) {

			Voxel voxel = grid.getVoxel(voxelIdx);
			if (voxel != null && !voxel.isEmpty()) {
				for (Intersectable geo : voxel.getGeometries()) {
					List<Intersectable.Intersection> intersections = geo.calculateIntersections(ray);
					if (intersections != null) {
						for (Intersectable.Intersection inter : intersections) {
							double dist = inter.point.distance(ray.getHead());
							if (dist < closestDistance && dist >= tMin) {
								closestDistance = dist;
								closestIntersection = inter;
							}
						}
					}
				}
			}

			if (closestDistance <= maxDistance)
				break;

			// advance to next voxel
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

		return (closestDistance <= maxDistance) ? closestIntersection : null;
	}

	@Override
	protected Double3 transparency(Intersection intersection) {
		Vector l = intersection.l;
		Ray shadowRay = new Ray(intersection.point, l.scale(-1), intersection.normal);

		Double3 ktr = Double3.ONE;
		double maxDistance = intersection.light.getDistance(intersection.point);

		Intersectable.Intersection shadowHit = findClosestIntersection(shadowRay, maxDistance);
		if (shadowHit == null)
			return ktr;

		return shadowHit.material.kT.lowerThan(0.001) ? Double3.ZERO : shadowHit.material.kT;
	}

}
