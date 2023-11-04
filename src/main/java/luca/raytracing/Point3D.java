package luca.raytracing;

public class Point3D {
    private final double x;
    private final double y;
    private final double z;

    public static final Point3D ZERO = new Point3D(0, 0, 0);

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }
    public double dotProduct(Point3D p) {
        return (p.getX() * x) + (p.getY() * y) + (p.getZ() * z);
    }
    public Point3D crossProduct(Point3D p) {
        return new Point3D(
                (y * p.getZ()) - (z * p.getY()),
                (z * p.getX()) - (x * p.getZ()),
                (x * p.getY()) - (y * p.getX())
        );
    }
    public Point3D add(Point3D p) {
        return new Point3D(x + p.getX(), y + p.getY(), z + p.getZ());
    }

    public Point3D multiply(double d) {
        return new Point3D(x * d, y * d, z * d);
    }
    public double magnitude() {
        return Math.sqrt(dotProduct(this));
    }
    public Point3D subtract(Point3D p) {
        return new Point3D(x - p.getX(), y - p.getY(), z - p.getZ());
    }
    public Point3D normalize() {
        return this.multiply(1.0 / this.magnitude());
    }
    public Point3D midpoint(Point3D p) {
        return (this.add(p)).multiply(0.5);
    }
}
