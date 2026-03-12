package util;

/**
 * Simple mutable 2D vector used by model and camera math.
 * This keeps math explicit and avoids passing raw doubles around.
 */
public class Vec2 {
    public double x;
    public double y;

    public Vec2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2 copy() {
        return new Vec2(x, y);
    }

    public Vec2 add(Vec2 other) {
        return new Vec2(x + other.x, y + other.y);
    }

    public Vec2 scale(double factor) {
        return new Vec2(x * factor, y * factor);
    }

    public double distanceSquared(Vec2 other) {
        double dx = x - other.x;
        double dy = y - other.y;
        return dx * dx + dy * dy;
    }

    @Override
    public String toString() {
        return "Vec2{" + "x=" + x + ", y=" + y + '}';
    }
}
