package model.agenttypes;

import javafx.scene.paint.Color;
import model.BaseAgent;
import model.AgentTypes;
import model.layouts.DowntownIntersectionLayout;
import sim.World;
import util.Vec2;

public class EmergencyVehicleAgent extends BaseAgent {
    public EmergencyVehicleAgent(long id, Vec2 position, Vec2 velocity) {
        super(id, position, velocity);
    }

    @Override
    public String getTypeName() {
        return AgentTypes.EMERGENCY_VEHICLE;
    }

    @Override
    public Color getColor() {
        return Color.CRIMSON;
    }

    @Override
    public String getShortLabel() {
        return "EV";
    }

    @Override
    protected double getSpeedMultiplier(World world) {
        if (world.getLayout() instanceof DowntownIntersectionLayout downtown) {
            return downtown.getEmergencySpeedMultiplier(this, world);
        }
        return 1.25;
    }

    @Override
    protected boolean shouldPause(World world) {
        return world.getLayout() instanceof DowntownIntersectionLayout
            && world.getLayout().shouldVehicleYield(this, world, world.getTickCount());
    }

    @Override
    protected void afterMove(World world) {
        if (world.getLayout() instanceof DowntownIntersectionLayout downtown && downtown.isEmergencyOutOfBounds(this)) {
            world.removeAgent(this);
        }
    }
}
