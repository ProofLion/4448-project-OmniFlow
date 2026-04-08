package model;

import javafx.scene.paint.Color;
import sim.World;
import util.Vec2;

/**
 * Shared fields and helpers for all agent types.
 */
public abstract class BaseAgent implements Agent {
    private final long id;
    private final TravelAxis travelAxis;
    private final Vec2 heading;
    private final double cruiseSpeed;
    private final double routeAnchor;
    private Vec2 position;
    private Vec2 velocity;

    protected BaseAgent(long id, Vec2 position, Vec2 velocity) {
        this.id = id;
        this.position = position;
        this.velocity = velocity;
        this.travelAxis = TravelAxis.fromVelocity(velocity);
        this.heading = normalize(velocity);
        this.cruiseSpeed = magnitude(velocity);
        this.routeAnchor = travelAxis == TravelAxis.HORIZONTAL ? position.y : position.x;
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

    protected final TravelAxis getTravelAxis() {
        return travelAxis;
    }

    protected final Vec2 getHeading() {
        return heading;
    }

    protected final double getCruiseSpeed() {
        return cruiseSpeed;
    }

    protected final double getRouteAnchor() {
        return routeAnchor;
    }

    protected double getSpeedMultiplier(World world) {
        return 1.0;
    }

    protected boolean shouldPause(World world) {
        return false;
    }

    protected void beforeUpdate(World world) {
    }

    protected void afterMove(World world) {
    }

    protected Vec2 createVelocity(double speedMultiplier) {
        return heading.scale(cruiseSpeed * speedMultiplier);
    }

    protected final void lockToRoute() {
        if (travelAxis == TravelAxis.HORIZONTAL) {
            position = new Vec2(position.x, routeAnchor);
        } else {
            position = new Vec2(routeAnchor, position.y);
        }
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
    public final void update(World world, double dtSeconds) {
        beforeUpdate(world);

        if (shouldPause(world)) {
            setVelocity(new Vec2(0, 0));
            return;
        }

        setVelocity(createVelocity(getSpeedMultiplier(world)));
        integrate(dtSeconds);
        lockToRoute();
        afterMove(world);
        world.getLayout().keepAgentInBounds(this);
        lockToRoute();
    }

    private static double magnitude(Vec2 vector) {
        return Math.hypot(vector.x, vector.y);
    }

    private static Vec2 normalize(Vec2 vector) {
        double length = magnitude(vector);
        if (length == 0) {
            return new Vec2(1, 0);
        }
        return new Vec2(vector.x / length, vector.y / length);
    }
}
