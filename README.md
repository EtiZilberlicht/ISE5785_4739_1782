# 3D Ray Tracing Engine

[![Java Version](https://img.shields.io/badge/Java-17%2B-orange.svg?style=flat-square)](https://www.oracle.com/java/)
[![JUnit 5](https://img.shields.io/badge/Testing-JUnit%205-green.svg?style=flat-square)](https://junit.org/junit5/)
[![IDE](https://img.shields.io/badge/IDE-Eclipse-blue.svg?style=flat-square)](https://www.eclipse.org/)

A high-performance, CPU-based 3D Ray Tracing Engine implemented in Java. Developed as part of a Software Engineering Mini-Project, this engine constructs 3D scenes using mathematically defined primitive geometries and complex polygonal meshes, rendering them with realistic lighting, shadows, reflection, refraction, and spatial acceleration.

---

## 🛠️ Key Features

*   **Diverse Geometries**: Renders primitives (`Sphere`, `Plane`, `Triangle`, `Polygon`, `Tube`, `Cylinder`) as well as complex 3D meshes imported from external Wavefront `.obj` files with material properties parsed from `.mtl` files.
*   **Phong Reflection Model**: Computes advanced surface shading including Ambient, Diffuse, and Specular light contributions.
*   **Global Illumination**: Simulates recursive reflections (mirrors) and refractions/transparencies (glass, water) with configurable recursion depth limits.
*   **Shadow System**:
    *   *Hard Shadows*: Clear shadow casting by projecting feeler rays to light sources.
    *   *Soft Shadows*: Simulates area light sources by casting jittered ray beams from a configurable grid (square or circular) to produce realistic penumbras.
*   **Performance Optimizations**:
    *   *Multi-Threading*: Dynamically allocates pixel rendering tasks across multiple CPU cores using custom thread pools (`PixelManager`) or parallel Java streams.
    *   *Voxel Grid Spatial Partitioning*: Utilizes a 3D Uniform Voxel Grid with the Amanatides-Woo ray traversal algorithm to reduce intersection tests from $O(N)$ to near $O(1)$ for high-density polygonal meshes.
*   **Scene Parsing**: Supports dynamic scene generation and configuration directly from XML files.

---

## 📂 Project Structure

```
src/
├── primitives/     # Points, Vectors, Colors, Rays, and Material definitions
├── geometries/     # Geometric shapes, bounding boxes (AABB), and OBJ mesh parsing
├── lighting/       # Light sources (Ambient, Directional, Point, Spot Lights)
├── renderer/       # Rendering pipeline, Camera builder, and ImageWriter
│   └── grid/       # Uniform voxel grid components for spatial acceleration
├── scene/          # Scene state, environment properties, and XML parser
└── unittests/      # JUnit 5 test suites covering validation and rendering
```

### Core Packages Description

| Package | Key Classes | Description |
| :--- | :--- | :--- |
| `primitives` | `Point`, `Vector`, `Color`, `Ray`, `Material` | Core mathematical structures and material coefficients ($K_d$, $K_s$, $K_r$, $K_t$, shininess). |
| `geometries` | `Geometry`, `Sphere`, `Plane`, `Polygon`, `GeometriesObj` | All shapes implementing intersection logic and bounding boxes (AABB) for grid mapping. |
| `lighting` | `PointLight`, `SpotLight`, `DirectionalLight`, `AmbientLight` | Direct lighting models with distance attenuation coefficients ($k_c$, $k_l$, $k_q$). |
| `renderer` | `Camera`, `SimpleRayTracer`, `Blackboard`, `PixelManager` | Manages viewport settings, coordinates rendering threads, and samples light areas. |
| `renderer.grid` | `GridRayTracer`, `VoxelGrid`, `Voxel`, `Index3D` | Implements spatial index structures to speed up ray casting on large scenes. |

---

## ⚡ Technical Highlights

### 1. Spatial Acceleration: Uniform Voxel Grid
To render high-polygon models (like the 3D Minecraft scene) without performance degradation, the engine divides the scene's bounding box into a 3D grid of voxels:
*   Geometries are pre-filtered and added only to the voxels they physically overlap.
*   Ray traversal uses the **Amanatides-Woo algorithm** to step through voxels sequentially.
*   This approach skips intersection tests for geometries in non-intersected voxels, achieving massive speedups (from hours to seconds for complex `.obj` files).

### 2. Multi-Threaded Rendering
The `PixelManager` handles dynamic workload distribution:
*   Instead of static pixel assignment, threads dynamically request the next available pixel index.
*   Allows the engine to scale linearly with the number of available CPU cores.
*   Configurable via the camera builder using `setMultithreading(int threads)`.

### 3. Distributed Ray Casting (Soft Shadows)
By using the `Blackboard` class, point and spot lights can act as area light sources:
*   Generates a jittered grid of vectors over the light source surface.
*   Samples the light's visibility from the intersection point using a circular or square beam layout.
*   Calculates the average shadowing factor to render smooth soft shadow transitions.

---

## 🚀 Getting Started

### Prerequisites
*   **Java SE Development Kit (JDK) 17** or higher.
*   **Eclipse IDE** (configured with JUnit 5) or any modern Java IDE.

### Running the Tests
To verify the engine features and render the demo scenes, navigate to the `unittests` directory and run the test suites:
1.  Open the project in your IDE.
2.  Locate `unittests.renderer.FinalProjectTests`.
3.  Run `testMinecraft()` as a JUnit test.
4.  The output images will be generated in the `/images/` directory in the root of the project.

```java
// Example configuration in FinalProjectTests.java
Camera camera = Camera.getBuilder()
    .setLocation(new Point(10, 20, 70))
    .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
    .setVpSize(500, 500)
    .setResolution(1000, 1000)
    .setMultithreading(-2) // Uses CPU cores minus 2
    .setRayTracer(scene, RayTracerType.GRID) // Enables voxel grid acceleration
    .build();

camera.renderImage();
camera.writeToImage("minecraft_output");
```

---

## 📸 Output Showcase
Rendered images are exported as lossless `.png` files. Run the JUnit suites to generate:
-   **Reflections & Transparencies**: Demonstrated in `ReflectionRefractionTests.java`.
-   **Soft Shadows**: Grid-based area light penumbras in `SoftShadowsTests.java`.
-   **Polygonal Meshes**: Large-scale spatial acceleration benchmarks in `FinalProjectTests.java` (using the imported `minecraft.obj` model).
