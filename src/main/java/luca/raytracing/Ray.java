package luca.raytracing;

public class Ray {
    private final Point3D origin;
    private final Point3D direction;
    private final Point3D directionInv;
    private final boolean insideMesh;
    Ray(Point3D origin, Point3D direction, boolean insideMesh) {
        this.origin = origin;
        this.direction = direction.normalize();
        this.insideMesh = insideMesh;
        this.directionInv = VectorMath.Inverse(direction);
    }
    public Point3D getDirection() { return direction; }
    public Point3D getOrigin() { return origin; }

    public Point3D getDirectionInv() {
        return directionInv;
    }

    public Ray transform(Matrix transform, Point3D translate) {
        Point3D p0 = transform.MultiplyPoint3D(origin).add(translate);
        Point3D p1 = transform.MultiplyPoint3D(origin.add(direction)).add(translate);
        return new Ray(p0, p1.subtract(p0), insideMesh);
    }
    public boolean IsInsideMesh() {
        return insideMesh;
    }
}
