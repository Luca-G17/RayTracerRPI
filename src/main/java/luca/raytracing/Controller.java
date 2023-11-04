package luca.raytracing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class Controller {
    private Camera camera;
    private final RayTracer tracer;
    private final int HEIGHT = 600;
    private final int WIDTH = 1000;
    private final int SAMPLES = 30000;
    private static final Map<String, Material> materials = MaterialMap();
    private static Map<String, Material> MaterialMap() {
        Map<String, Material> materialMap = new HashMap<>();
        materialMap.put("WHITE", new Lambertian(new Point3D(1.0f, 1.0f, 1.0f), 0.75f));
        materialMap.put("RED", new Lambertian(new Point3D(1.0f, 0.0f, 0.0f), 0.75f));
        materialMap.put("GREEN", new Lambertian(new Point3D(0.0f, 1.0f, 0.0f), 0.75f));
        materialMap.put("BLUE", new Lambertian(new Point3D(0.0f, 0.0f, 1.0f), 0.75f));
        materialMap.put("MIRROR", new Mirror(new Point3D(1f, 1f, 1f), Point3D.ZERO));
        materialMap.put("WHITE-EMITTER", new Lambertian(new Point3D(1.0f, 1.0f, 1.0f), new Point3D(8.0f, 8.0f, 8.0f)));
        materialMap.put("DIELECTRIC", new Dielectric(1.2));
        return materialMap;
    }
    enum Scene {
        POLYGONS,
        TRIANGLES
    }
    public Controller() {
        List<MeshObject> meshes = new ArrayList<>();
        List<Sphere> spheres = new ArrayList<>();

        Scene scene = Scene.TRIANGLES;
        switch (scene) {
            case TRIANGLES: {
                double boxHeight = 10;
                double boxWidth = 15;
                double boxDepth = 15;
                Point3D floorBackLeft = new Point3D(-boxWidth / 2, -2, boxDepth);
                Point3D floorBackRight = new Point3D(boxWidth / 2, -2, boxDepth);
                Point3D floorFrontLeft = new Point3D(-boxWidth / 2, -2, 0);
                Point3D floorFrontRight = new Point3D(boxWidth / 2, -2, 0);
                Point3D roofBackLeft = floorBackLeft.add(new Point3D(0, boxHeight, 0));
                Point3D roofBackRight = floorBackRight.add(new Point3D(0, boxHeight, 0));
                Point3D roofFrontLeft = floorFrontLeft.add(new Point3D(0, boxHeight, 0));
                Point3D roofFrontRight = floorFrontRight.add(new Point3D(0, boxHeight, 0));

                // Box
                TriPlane top = new TriPlane(materials.get("WHITE"), new Point3D(0, 0, 0), roofBackLeft, roofFrontLeft, roofBackRight, roofFrontRight);
                meshes.add(new TriPlane(materials.get("WHITE"), new Point3D(0, 0, 0), floorFrontLeft, floorBackLeft, floorFrontRight, floorBackRight)); // Floor
                meshes.add(new TriPlane(materials.get("RED"), new Point3D(0, 0, 0), floorFrontLeft, roofFrontLeft, floorBackLeft, roofBackLeft)); // Left
                meshes.add(new TriPlane(materials.get("WHITE"), new Point3D(0, 0, 0), floorBackLeft, roofBackLeft, floorBackRight, roofBackRight)); // Back
                meshes.add(top); // Top
                meshes.add(new TriPlane(materials.get("GREEN"), new Point3D(0, 0, 0), floorBackRight, roofBackRight, floorFrontRight, roofFrontRight)); // Right
                meshes.add(new TriPlane(materials.get("WHITE"), new Point3D(0, 0, 0), floorFrontRight, roofFrontRight, floorFrontLeft, roofFrontLeft)); // Front
                //world.add(new TriPlane(materials.get("WHITE-EMITTER"), new Point3D(0, 0, 0), new Point3D(-boxWidth / 6, boxHeight,  2 * boxDepth / 3)));

                // World Objects
                TriPlane topLight = top.Scale(0.3, 0, 0.3).Translate(0.0, -0.001, 0.0);
                topLight.id = "light";
                topLight.SetMaterial(materials.get("WHITE-EMITTER"));
                meshes.add(topLight);
                //world.add(new TriCube(materials.get("MIRROR"), 2, new Point3D(-2, 1, 10), new Point3D(0, 0, 0)));
                meshes.add(new TriCube(materials.get("WHITE"), 2, new Point3D(-1, 1, 10), new Point3D(0, Math.PI / 8, 0)));
                spheres.add(new Sphere(materials.get("DIELECTRIC"), new Point3D(1.3, 0.7, 6), 0.5));
                meshes.add(new TriCube(materials.get("MIRROR"), 2, new Point3D(3, 1, 11), new Point3D(0, 0, 0)));
                // Camera
                this.camera = new Camera(new Point3D(0, 3, 1), 0 * Math.PI / 180, 0 * Math.PI / 180, 80 * Math.PI / 180);
                break;
            }
        }
        tracer = new RayTracer(meshes, spheres);
    }

    private void OutputBitmap(int width, int height, int samples, Color[][] bitmap) {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                bufferedImage.setRGB(x, y, ColorToIntRGB(bitmap[y][x]));
            }
        }
        try {
            String filename;
            filename = String.format("Renders/Render-(%dx%dx%d).png", width, height, samples);
            File outputFile = new File(filename);
            ImageIO.write(bufferedImage, "png", outputFile);
            System.out.printf("Image written to %s\n", filename);
        } catch (IOException e) {
            System.out.printf("ERROR Writing to File: %s\n", e.getMessage());
        }
    }
    private int ColorToIntRGB(Color c) {
        int rgba = c.hashCode();
        int a = rgba & 0x000000FF;
        return ((rgba >> 8) & 0x00FFFFFF) + (a << 24);
    }
    private Color rollingColorAverage(Point3D newCol, Color average, int n) {
        if (n == 0) {
            average = new Color(0, 0, 0, 0);
        }
        Point3D pointAverage;
        n += 1;
        Point3D prevAverage = new Point3D(average.getRed(), average.getGreen(), average.getBlue());
        pointAverage = prevAverage.add((newCol.subtract(prevAverage)).multiply(1.0 / n));
        return new Color(Math.min(pointAverage.getX(), 1) , Math.min(pointAverage.getY(), 1), Math.min(pointAverage.getZ(), 1), 1);
    }
    public Color[][] initBitmap(int height, int width) {
        Color[][] b = new Color[height][width];
        for (int i = 0; i < height; i++) {
            for (int f = 0; f < width; f++) {
                b[i][f] = new Color(0, 0, 0, 1);
            }
        }
        return b;
    }
    public void startTracing() throws InterruptedException {
        double uScale = 1;
        double vScale = 1;


        if (WIDTH > HEIGHT) uScale = (double)WIDTH / HEIGHT;
        else if (HEIGHT > WIDTH) vScale = (double)HEIGHT / WIDTH;

        Color[][] bitmap = initBitmap(HEIGHT, WIDTH);
        // Startup Threads:
        final int threads = 4;
        final Semaphore full = new Semaphore(0);
        final Semaphore empty = new Semaphore(WIDTH);
        final CountDownLatch finished = new CountDownLatch(WIDTH * HEIGHT);
        Object queueMutex = new Object();
        final Object bitmapMutex = new Object();
        final Deque<int[]> workQueue = new ArrayDeque<>();
        for (int i = 0; i < threads; i++) {
            Thread t = new Thread(new Worker(full, empty, finished, workQueue, queueMutex, bitmapMutex, uScale, vScale, bitmap));
            t.start();
        }
        long delta = 0;
        // Producer Loop:
        for (int s = 0; s < SAMPLES; s++) {
            long start = System.currentTimeMillis();
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    try {
                        empty.acquire();
                        synchronized (queueMutex) {
                            workQueue.add(new int[]{x, y, s});
                        }
                        full.release();
                    } catch (InterruptedException e) {
                        System.out.println("Producer Interrupted, EXCEPTION: " + e);
                    }
                }
            }
            finished.await();
            long end = System.currentTimeMillis();
            long averageSampleDuration = ((delta * s) + (end - start)) / (s + 1);
            delta = end - start;
            System.out.printf("Samples Rendered: %d/%d -- Average Duration: %ds\r", s + 1, SAMPLES, averageSampleDuration / 1000);
            if (s % 1000 == 0) {
                OutputBitmap(WIDTH, HEIGHT, s, bitmap);
            }
        }
    }
    private class Worker implements Runnable {

        private final Semaphore full;
        private final Semaphore empty;
        private final CountDownLatch finished;
        private final Deque<int[]> workQueue;
        private final Object queueMutex;
        private final Object bitmapMutex;
        private final double UScale;
        private final double VScale;
        private final Color[][] bitmap;
        Worker(final Semaphore full,
               final Semaphore empty,
               final CountDownLatch finished,
               final Deque<int[]> workQueue,
               final Object queueMutex,
               final Object bitmapMutex,
               final double UScale,
               final double VScale,
               final Color[][] bitmap) {
            this.full = full;
            this.empty = empty;
            this.finished = finished;
            this.workQueue = workQueue;
            this.queueMutex = queueMutex;
            this.bitmapMutex = bitmapMutex;
            this.UScale = UScale;
            this.VScale = VScale;
            this.bitmap = bitmap;
        }
        @Override
        public void run() {
            while (true) {
                try {
                    full.acquire();
                    int[] coords;
                    synchronized (queueMutex) {
                        coords = workQueue.pop();
                    }
                    int x = coords[0];
                    int y = coords[1];
                    int s = coords[2];
                    double u = 2 * (((double)x + 0.5) / (WIDTH - 1)) - 1;
                    double v = 1 - (2 * (((double)y + 0.5) / (HEIGHT - 1)));
                    u *= UScale;
                    v *= VScale;
                    Ray ray = camera.transformRay(u, v);
                    Point3D color = tracer.traceRayRecursive(ray, 0);

                    synchronized (bitmapMutex) {
                        Color average = rollingColorAverage(color, bitmap[y][x], s);
                        bitmap[y][x] = average;
                    }
                    empty.release();
                    finished.countDown();
                } catch (InterruptedException e) {
                    System.out.println("Worker interrupted, EXCEPTION: " + e.getMessage());
                }
            }
        }
    }
}