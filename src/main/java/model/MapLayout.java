package model;

import javafx.scene.canvas.GraphicsContext;
import sim.Camera2D;
import sim.World;
import util.Vec2;

/**
 * Layout strategy controls drawing, startup agents, and simple traffic rules.
 */
public interface MapLayout {
    String getName();

    void draw(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight, long tickCount);

    void seed(World world);

    boolean shouldVehicleYield(Agent agent, long tickCount);

    boolean shouldPedestrianYield(Agent agent, long tickCount);

    boolean isBusStop(Agent agent);

    void keepAgentInBounds(Agent agent);

    Vec2 getSuggestedCameraOffset();
}
