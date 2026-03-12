package model;

import javafx.scene.canvas.GraphicsContext;
import sim.Camera2D;
import sim.World;

/**
 * Layout contract controls static scene drawing and startup agent defaults.
 */
public interface MapLayout {
    String getName();

    void drawStatic(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight);

    void seed(World world);
}
