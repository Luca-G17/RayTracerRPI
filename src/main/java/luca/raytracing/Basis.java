package luca.raytracing;

public class Basis {
    private Matrix transform;

    Basis(Point3D normal) {
        Point3D tangent;
        if (normal.getX() == 0)
            tangent = new Point3D(1, 0, 0);
        else {
            // (z, 0, -x) = cross((0, 1, 0), (x, y , z))
            tangent = new Point3D(normal.getZ(), 0, -normal.getX());
        }
        Point3D bitangent = tangent.crossProduct(normal);
        transform = new Matrix(tangent.normalize(), normal.normalize(), bitangent.normalize());
    }
    Basis(Point3D normal, Point3D tangent, Point3D bitangent) {
        transform = new Matrix(tangent, normal, bitangent);
    }
    public Matrix getTransform() { return transform; }
    public Point3D getTangent() { return transform.getU(); }
    public Point3D getNormal() { return transform.getV(); }
    public Point3D getBitangent() { return transform.getW(); }
}
