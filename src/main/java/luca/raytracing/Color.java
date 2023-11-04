package luca.raytracing;

public class Color {

    private final double r;
    private final double g;
    private final double b;
    private final double a;

    public Color(final double r, final double g, final double b, final double a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public double getRed() {
        return r;
    }
    public double getGreen() {
        return g;
    }
    public double getBlue() {
        return b;
    }

    @Override
    public int hashCode() {
        return ((int)(r * 255) << 24) + ((int)(g * 255) << 16) + ((int)(b * 255) << 8) + (int)(a * 255);
    }
}
