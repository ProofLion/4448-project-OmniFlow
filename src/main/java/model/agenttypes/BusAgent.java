package model.agenttypes;

import javafx.scene.paint.Color;
import model.BaseAgent;
import sim.World;
import util.Vec2;

public class BusAgent extends BaseAgent {
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
    public void update(World world, double dtSeconds) {
        integrate(dtSeconds);
        world.wrap(getPosition(), -220, -130, 220, 130);
    }
}
