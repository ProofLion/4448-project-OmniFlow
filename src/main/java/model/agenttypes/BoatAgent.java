package model.agenttypes;

import javafx.scene.paint.Color;
import model.BaseAgent;
import sim.World;
import util.Vec2;

public class BoatAgent extends BaseAgent {
    public BoatAgent(long id, Vec2 position, Vec2 velocity) {
        super(id, position, velocity);
    }

    @Override
    public String getTypeName() {
        return "Boat";
    }

    @Override
    public Color getColor() {
        return Color.DEEPSKYBLUE;
    }

    @Override
    public void update(World world, double dtSeconds) {
        integrate(dtSeconds);
        world.wrap(getPosition(), -260, 80, 260, 220);
    }
}
