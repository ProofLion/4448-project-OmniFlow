package model;

import javafx.scene.paint.Color;
import sim.World;
import util.Vec2;

/**
 * Shared fields and helpers for all agent types.
 */
public abstract class BaseAgent implements Agent {
    private final long id;
    private Vec2 position;
    private Vec2 velocity;

    protected BaseAgent(long id, Vec2 position, Vec2 velocity) {
        this.id = id;
        this.position = position;
        this.velocity = velocity;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Vec2 getPosition() {
        return position;
    }

    @Override
    public Vec2 getVelocity() {
        return velocity;
    }

    @Override
    public void setPosition(Vec2 position) {
        this.position = position;
    }

    @Override
    public void setVelocity(Vec2 velocity) {
        this.velocity = velocity;
    }

    protected void integrate(double dtSeconds) {
        position = position.add(velocity.scale(dtSeconds));
    }

    protected void bounceWithin(double minX, double minY, double maxX, double maxY) {
        if (position.x < minX || position.x > maxX) {
            velocity = new Vec2(-velocity.x, velocity.y);
        }
        if (position.y < minY || position.y > maxY) {
            velocity = new Vec2(velocity.x, -velocity.y);
        }
        position = new Vec2(
            Math.max(minX, Math.min(maxX, position.x)),
            Math.max(minY, Math.min(maxY, position.y))
        );
    }

    @Override
    public double getRenderRadius() {
        return 8.0;
    }

    @Override
    public String getShortLabel() {
        return String.valueOf(id);
    }

    @Override
    public abstract String getTypeName();

    @Override
    public abstract Color getColor();

    @Override
    public abstract void update(World world, double dtSeconds);
}
