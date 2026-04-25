package model.agenttypes;

import javafx.scene.paint.Color;
import model.BaseAgent;
import model.AgentTypes;
import model.layouts.DowntownIntersectionLayout;
import sim.World;
import util.Vec2;

public class BikeAgent extends BaseAgent {
    public BikeAgent(long id, Vec2 position, Vec2 velocity) {
        super(id, position, velocity);
    }

    @Override
    public String getTypeName() {
        return AgentTypes.BIKE;
    }

    @Override
    public Color getColor() {
        return Color.web("#2AA876");
    }

    @Override
    public double getRenderRadius() {
        return 7.0;
    }

    @Override
    public String getShortLabel() {
        return "Bk";
    }

    @Override
    protected double getSpeedMultiplier(World world) {
        if (world.getLayout() instanceof DowntownIntersectionLayout downtown && downtown.isPedestrianFlashing(this, world.getTickCount())) {
            return 0.95;
        }
        return 0.65;
    }

    @Override
    protected boolean shouldPause(World world) {
        return world.getLayout().shouldPedestrianYield(this, world, world.getTickCount());
    }
}
