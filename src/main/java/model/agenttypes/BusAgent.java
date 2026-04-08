package model.agenttypes;

import javafx.scene.paint.Color;
import model.BaseAgent;
import sim.World;
import util.Vec2;

public class BusAgent extends BaseAgent {
    private int stopTicksRemaining;
    private int stopCooldownTicks;

    public BusAgent(long id, Vec2 position, Vec2 velocity) {
        super(id, position, velocity);
    }

    @Override
    public String getTypeName() {
        return "Bus";
    }

    @Override
    public Color getColor() {
        return Color.DARKORANGE;
    }

    @Override
    public double getRenderRadius() {
        return 11.0;
    }

    @Override
    public String getShortLabel() {
        return "Bus";
    }

    @Override
    protected void beforeUpdate(World world) {
        if (stopCooldownTicks > 0) {
            stopCooldownTicks--;
        }
        if (stopTicksRemaining > 0) {
            stopTicksRemaining--;
        }
    }

    @Override
    protected double getSpeedMultiplier(World world) {
        return 0.85;
    }

    @Override
    protected boolean shouldPause(World world) {
        return stopTicksRemaining > 0 || world.getLayout().shouldVehicleYield(this, world.getTickCount());
    }

    @Override
    protected void afterMove(World world) {
        if (stopCooldownTicks == 0 && world.getLayout().isBusStop(this)) {
            stopTicksRemaining = 22;
            stopCooldownTicks = 120;
        }
    }
}
