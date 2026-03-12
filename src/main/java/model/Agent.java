package model;

import javafx.scene.paint.Color;
import sim.World;
import util.Vec2;

/**
 * Core contract for any simulated entity drawn in the world.
 * Polymorphism keeps type-specific behavior out of giant switch statements.
 */
public interface Agent {
    long getId();

    String getTypeName();

    Vec2 getPosition();

    Vec2 getVelocity();

    void setPosition(Vec2 position);

    void setVelocity(Vec2 velocity);

    double getRenderRadius();

    Color getColor();

    String getShortLabel();

    void update(World world, double dtSeconds);

    default boolean containsPoint(Vec2 worldPoint) {
        return getPosition().distanceSquared(worldPoint) <= getRenderRadius() * getRenderRadius();
    }
}
