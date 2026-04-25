package model.agenttypes;

import javafx.scene.paint.Color;
import model.BaseAgent;
import model.AgentTypes;
import model.layouts.DowntownIntersectionLayout;
import sim.World;
import util.Vec2;

public class PedestrianAgent extends BaseAgent {
    public PedestrianAgent(long id, Vec2 position, Vec2 velocity) {
        super(id, position, velocity);
    }

    @Override
    public String getTypeName() {
        return AgentTypes.PEDESTRIAN;
    }

    @Override
    public Color getColor() {
        return Color.FORESTGREEN;
    }

    @Override
    public double getRenderRadius() {
        return 6.0;
    }

    @Override
    public String getShortLabel() {
        return "P" + getId();
    }

    @Override
    protected double getSpeedMultiplier(World world) {
        if (world.getLayout() instanceof DowntownIntersectionLayout downtown && downtown.isPedestrianFlashing(this, world.getTickCount())) {
            return 0.9;
        }
        return 0.55;
    }

    @Override
    protected boolean shouldPause(World world) {
        return world.getLayout().shouldPedestrianYield(this, world, world.getTickCount());
    }

    @Override
    protected void afterMove(World world) {
        if (world.getLayout() instanceof DowntownIntersectionLayout downtown && downtown.shouldDespawnBusPassenger(this)) {
            world.removeAgent(this);
        }
    }
}
