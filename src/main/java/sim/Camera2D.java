package sim;

import javafx.scene.canvas.GraphicsContext;
import util.Vec2;

/**
 * Camera maps world-space coordinates to screen-space coordinates.
 * World origin is arbitrary (center of the simulated scene), while screen
 * coordinates are Canvas pixels.
 */
public class Camera2D {
    private double offsetX = -220;
    private double offsetY = -160;
    private double zoom = 1.6;

    public Vec2 worldToScreen(Vec2 world) {
        return new Vec2((world.x - offsetX) * zoom, (world.y - offsetY) * zoom);
    }

    public Vec2 screenToWorld(double screenX, double screenY) {
        return new Vec2((screenX / zoom) + offsetX, (screenY / zoom) + offsetY);
    }

    /**
     * Dragging by pixels changes camera offset inversely to zoom.
     */
    public void panByScreenDelta(double dx, double dy) {
        offsetX -= dx / zoom;
        offsetY -= dy / zoom;
    }

    /**
     * Zoom around cursor by keeping the cursor's world coordinate stable.
     */
    public void zoomAroundScreenPoint(double factor, double screenX, double screenY) {
        Vec2 before = screenToWorld(screenX, screenY);
        zoom = clamp(zoom * factor, 0.2, 6.0);
        Vec2 after = screenToWorld(screenX, screenY);
        offsetX += before.x - after.x;
        offsetY += before.y - after.y;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    public double getZoom() {
        return zoom;
    }

    public void setState(double offsetX, double offsetY, double zoom) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.zoom = clamp(zoom, 0.2, 6.0);
    }

    public void fillWorldRect(GraphicsContext gc, double canvasWidth, double canvasHeight,
                              double x, double y, double w, double h, javafx.scene.paint.Paint paint) {
        gc.setFill(paint);
        Vec2 screen = worldToScreen(new Vec2(x, y));
        gc.fillRect(screen.x, screen.y, w * zoom, h * zoom);
    }

    public void strokeWorldLine(GraphicsContext gc, double canvasWidth, double canvasHeight,
                                double x1, double y1, double x2, double y2) {
        Vec2 a = worldToScreen(new Vec2(x1, y1));
        Vec2 b = worldToScreen(new Vec2(x2, y2));
        gc.strokeLine(a.x, a.y, b.x, b.y);
    }

    public void fillWorldCircle(GraphicsContext gc, double canvasWidth, double canvasHeight,
                                double x, double y, double radius, javafx.scene.paint.Paint paint) {
        gc.setFill(paint);
        Vec2 c = worldToScreen(new Vec2(x, y));
        double r = radius * zoom;
        gc.fillOval(c.x - r, c.y - r, r * 2, r * 2);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
