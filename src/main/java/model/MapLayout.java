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

    default void draw(
        GraphicsContext gc,
        Camera2D camera,
        double canvasWidth,
        double canvasHeight,
        long tickCount,
        World world
    ) {
        draw(gc, camera, canvasWidth, canvasHeight, tickCount);
    }

    void seed(World world);

    boolean shouldVehicleYield(Agent agent, long tickCount);

    default boolean shouldVehicleYield(Agent agent, World world, long tickCount) {
        return shouldVehicleYield(agent, tickCount);
    }

    boolean shouldPedestrianYield(Agent agent, long tickCount);

    default boolean shouldPedestrianYield(Agent agent, World world, long tickCount) {
        return shouldPedestrianYield(agent, tickCount);
    }

    boolean isBusStop(Agent agent);

    void keepAgentInBounds(Agent agent);

    default void keepAgentInBounds(Agent agent, World world) {
        keepAgentInBounds(agent);
    }

    Vec2 getSuggestedCameraOffset();
}
