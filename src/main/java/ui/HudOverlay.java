package ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Lightweight HUD painter for top-left overlay text.
 */
public final class HudOverlay {
    private HudOverlay() {
    }

    public static void draw(GraphicsContext gc, double fps, long tickCount) {
        gc.setFill(Color.color(0, 0, 0, 0.65));
        gc.fillRoundRect(8, 8, 150, 44, 8, 8);
        gc.setFill(Color.WHITE);
        gc.fillText(String.format("FPS: %.1f", fps), 16, 26);
        gc.fillText("Tick: " + tickCount, 16, 42);
    }
}
