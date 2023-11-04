package luca.raytracing;

import java.util.Optional;

public interface Hittable {
    public AABB GetBoundingBox();
    public void GenerateBoundingBox();
    public Optional<WorldObject.Collision> Collision(Ray ray);
    Point3D GetCentre();
}
