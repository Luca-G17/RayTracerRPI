package luca.raytracing;

public class Light {
    private final Point3D position;
    private final double intensity;
    public Light(Point3D position, Double intensity) {
        this.position = position;
        this.intensity = intensity;
    }
    public double getIntensity() {
        return intensity;
    }
    public Point3D getPosition() {
        return position;
    }
}
