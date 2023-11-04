package luca.raytracing;

import java.util.Optional;

public abstract class WorldObject {
    private Material mat;
    private Point3D pos;
    public String id; // TODO: Remove this testing id
    WorldObject(Material mat, Point3D pos) {
        this.mat = mat;
        this.pos = pos;
    }
    WorldObject() {
    }
    public Material getMat() {
        return mat;
    }
    public Point3D getPos() {
        return pos;
    }
    public void setPos(Point3D pos) {
        this.pos = pos;
    }
    protected void setMat(Material mat) {
        this.mat = mat;
    }
    public static Line PointsToLine(Point3D p1, Point3D p2) {
        Point3D u = p2.subtract(p1);
        return new Line(p1, u, u.magnitude());
    }
    public abstract Optional<WorldObject.Collision> Collision(Ray ray);

    public static class Collision implements Comparable<Collision> {
        public final Point3D point;
        public final Material mat;
        public final Point3D normal;
        public final double dist;
        public Collision(Point3D point, Material mat, Point3D normal, double dist) {
            this.point = point;
            this.mat = mat;
            this.normal = normal;
            this.dist = dist;
        }
        Collision() {
            this(Point3D.ZERO, Material.EMPTY, Point3D.ZERO, 0.0);
        }

        @Override
        public int compareTo(Collision o) {
            if (o == null) throw new NullPointerException("Collision is null");
            return Double.compare(this.dist, ((Collision) o).dist);
        }
    }
}
