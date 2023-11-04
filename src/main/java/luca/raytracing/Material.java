package luca.raytracing;

public interface Material {

    Material EMPTY = null;
    Point3D weightPDF(Direction outgoing, Basis basis);
    PostCollision samplePDF(Direction outgoing, Basis basis, boolean isInsideMesh);
    Point3D emittance(Direction outgoing, Basis basis);
    public class PostCollision {
        final Point3D outVector;
        final boolean isRefracted;
        public PostCollision(Point3D outVector, boolean isRefracted) {
            this.outVector = outVector;
            this.isRefracted = isRefracted;
        }
    }
}
