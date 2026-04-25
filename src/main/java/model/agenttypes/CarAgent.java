package model.agenttypes;

import javafx.scene.paint.Color;
import model.BaseAgent;
import model.AgentTypes;
import model.layouts.DowntownIntersectionLayout;
import sim.World;
import util.Vec2;

public class CarAgent extends BaseAgent {
    public CarAgent(long id, Vec2 position, Vec2 velocity) {
        super(id, position, velocity);
    }

    @Override
    public String getTypeName() {
        return AgentTypes.CAR;
    }

    @Override
    public Color getColor() {
        return Color.DODGERBLUE;
    }

    @Override
    public String getShortLabel() {
        return "C" + getId();
    }

    @Override
    protected double getSpeedMultiplier(World world) {
        if (world.getLayout() instanceof DowntownIntersectionLayout downtown) {
            return downtown.getVehicleSpeedMultiplier(this, world.getTickCount());
        }
        return 1.0;
    }

    @Override
    protected boolean shouldPause(World world) {
        return world.getLayout().shouldVehicleYield(this, world, world.getTickCount());
    }
}
