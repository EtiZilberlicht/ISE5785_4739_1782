package renderer.grid;

import java.util.ArrayList;
import java.util.List;

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
	/**
	 * The voxel grid used for spatial acceleration.
	 */
	private VoxelGrid grid;
	/**
	 * List of geometries without bounding boxes (e.g., infinite geometries).
	 */
	private final List<Intersectable> infiniteGeometries = new ArrayList<>();

	/**
	 * Constructs a GridRayTracer with the given scene.
	 *
	 * @param scene the scene to trace rays in
	 */
	public GridRayTracer(Scene scene) {
		super(scene);
		this.grid = null;
	}

	/**
	 * Initializes the voxel grid and assigns geometries to the appropriate voxels.
	 * Geometries without bounding boxes are stored separately as infinite
	 * geometries.
	 */
	public void setupGrid() {
		AABB originalBox = scene.geometries.getBoundingBox();
		if (originalBox == null) {
			// No bounding box means all geometries infinite or empty scene
			grid = null;
			infiniteGeometries.clear();
			infiniteGeometries.addAll(scene.geometries.getAll());
			return;
		}

		AABB expandedBox = getExpandedBoundingBox(originalBox);
		this.grid = new VoxelGrid(expandedBox, 100, 100, 100);

		infiniteGeometries.clear();

		for (Intersectable geo : scene.geometries.getAll()) {
			AABB geoBox = geo.getBoundingBox();
			if (geoBox == null) {
				infiniteGeometries.add(geo);
			} else {
				grid.addGeometry(geo);
			}
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
		Point newMin = new Point(originalBox.getMin().getX() - epsilon, originalBox.getMin().getY() - epsilon,
				originalBox.getMin().getZ() - epsilon);
		Point newMax = new Point(originalBox.getMax().getX() + epsilon, originalBox.getMax().getY() + epsilon,
				originalBox.getMax().getZ() + epsilon);
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
		double x = Math.max(box.getMin().getX(), Math.min(box.getMax().getX(), p.getX()));
		double y = Math.max(box.getMin().getY(), Math.min(box.getMax().getY(), p.getY()));
		double z = Math.max(box.getMin().getZ(), Math.min(box.getMax().getZ(), p.getZ()));
		return new Point(x, y, z);
	}

	@Override
	protected Intersection findClosestIntersection(Ray ray) {
		if (grid == null) {
			// אם הגריד לא מאותחל – fallback ל־SimpleRayTracer
			return super.findClosestIntersection(ray);
		}

		AABB box = grid.getBoundingBox();
		if (!box.intersects(ray)) {
			// אם הקרן לא חותכת את הקופסה – בודקים רק גופים אינסופיים
			Intersection closest = null;
			double minDist = Double.POSITIVE_INFINITY;
			for (Intersectable geo : infiniteGeometries) {
				List<Intersection> inters = geo.calculateIntersections(ray);
				if (inters != null) {
					for (Intersection inter : inters) {
						double t = inter.point.subtract(ray.getHead()).dotProduct(ray.getDirection());
						if (t >= 0 && t < minDist) {
							minDist = t;
							closest = inter;
						}
					}
				}
			}
			return closest;
		}

		Point startPoint = clampToBox(ray.getPoint(findEntryT(ray, box) + 1e-5), box);
		Index3D voxelIdx = grid.pointToIndex(startPoint);
		if (voxelIdx == null)
			return null;

		Vector dir = ray.getDirection();
		double voxelSize = grid.getVoxelSize();

		int stepX = dir.getX() >= 0 ? 1 : -1;
		int stepY = dir.getY() >= 0 ? 1 : -1;
		int stepZ = dir.getZ() >= 0 ? 1 : -1;

		double nextVoxelX = box.getMin().getX() + (voxelIdx.i + (stepX > 0 ? 1 : 0)) * voxelSize;
		double nextVoxelY = box.getMin().getY() + (voxelIdx.j + (stepY > 0 ? 1 : 0)) * voxelSize;
		double nextVoxelZ = box.getMin().getZ() + (voxelIdx.k + (stepZ > 0 ? 1 : 0)) * voxelSize;

		double tMaxX = (dir.getX() == 0) ? Double.POSITIVE_INFINITY : (nextVoxelX - ray.getHead().getX()) / dir.getX();
		double tMaxY = (dir.getY() == 0) ? Double.POSITIVE_INFINITY : (nextVoxelY - ray.getHead().getY()) / dir.getY();
		double tMaxZ = (dir.getZ() == 0) ? Double.POSITIVE_INFINITY : (nextVoxelZ - ray.getHead().getZ()) / dir.getZ();

		double tDeltaX = (dir.getX() == 0) ? Double.POSITIVE_INFINITY : voxelSize / Math.abs(dir.getX());
		double tDeltaY = (dir.getY() == 0) ? Double.POSITIVE_INFINITY : voxelSize / Math.abs(dir.getY());
		double tDeltaZ = (dir.getZ() == 0) ? Double.POSITIVE_INFINITY : voxelSize / Math.abs(dir.getZ());

		Intersection closest = null;
		double minDist = Double.POSITIVE_INFINITY;

		while (voxelIdx.i >= 0 && voxelIdx.i < grid.getSizeX() && voxelIdx.j >= 0 && voxelIdx.j < grid.getSizeY()
				&& voxelIdx.k >= 0 && voxelIdx.k < grid.getSizeZ()) {

			Voxel voxel = grid.getVoxel(voxelIdx);
			if (voxel != null && !voxel.isEmpty()) {
				for (Intersectable geo : voxel.getGeometries()) {
					List<Intersection> inters = geo.calculateIntersections(ray);
					if (inters != null) {
						for (Intersection inter : inters) {
							double t = inter.point.subtract(ray.getHead()).dotProduct(ray.getDirection());
							if (t >= 0 && t < minDist) {
								minDist = t;
								closest = inter;
							}
						}
					}
				}
			}

			// אם כבר מצאנו חיתוך הכי קרוב שאי אפשר לעבור דרכו – אפשר לעצור
			if (closest != null)
				break;

			// מעבר ל־voxel הבא
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

		// לבדוק גם גופים אינסופיים – תמיד!
		for (Intersectable geo : infiniteGeometries) {
			List<Intersection> inters = geo.calculateIntersections(ray);
			if (inters != null) {
				for (Intersection inter : inters) {
					double t = inter.point.subtract(ray.getHead()).dotProduct(ray.getDirection());
					if (t >= 0 && t < minDist) {
						minDist = t;
						closest = inter;
					}
				}
			}
		}

		return closest;
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

	@Override
	protected List<Intersection> findAllIntersections(Ray ray, double maxDistance) {
		if (grid == null)
			return List.of();

		AABB box = grid.getBoundingBox();
		if (!box.intersects(ray))
			return List.of();

		double tMin = findEntryT(ray, box);
		if (tMin < 0)
			tMin = 0;

		Point startPoint = clampToBox(ray.getPoint(tMin + 1e-5), box);
		Index3D voxelIdx = grid.pointToIndex(startPoint);
		if (voxelIdx == null)
			return List.of();

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

		List<Intersection> allIntersections = new ArrayList<>();

		while (voxelIdx.i >= 0 && voxelIdx.i < grid.getSizeX() && voxelIdx.j >= 0 && voxelIdx.j < grid.getSizeY()
				&& voxelIdx.k >= 0 && voxelIdx.k < grid.getSizeZ()) {

			Voxel voxel = grid.getVoxel(voxelIdx);

			if (voxel != null && !voxel.isEmpty()) {
				for (Intersectable geo : voxel.getGeometries()) {
					List<Intersection> intersections = geo.calculateIntersections(ray);
					if (intersections != null) {
						for (Intersection inter : intersections) {
							double t = inter.point.distance(ray.getHead());
							if (t >= tMin && t <= maxDistance) {
								allIntersections.add(inter);
							}
						}
					}
				}
			}

			double currentT = Math.min(Math.min(tMaxX, tMaxY), tMaxZ);
			if (currentT > maxDistance)
				break;

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

		for (Intersectable geo : infiniteGeometries) {
			List<Intersection> intersections = geo.calculateIntersections(ray);
			if (intersections != null) {
				for (Intersection inter : intersections) {
					double t = inter.point.distance(ray.getHead());
					if (t >= tMin && t <= maxDistance) {
						allIntersections.add(inter);
					}
				}
			}
		}

		return allIntersections;
	}

}
