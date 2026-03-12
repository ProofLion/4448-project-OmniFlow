package model.agenttypes;

import javafx.scene.paint.Color;
import model.BaseAgent;
import sim.World;
import util.Vec2;

public class EmergencyAgent extends BaseAgent {
    public EmergencyAgent(long id, Vec2 position, Vec2 velocity) {
        super(id, position, velocity);
    }

    @Override
    public String getTypeName() {
        return "Emergency";
    }

    @Override
    public Color getColor() {
        return Color.CRIMSON;
    }

    @Override
    public void update(World world, double dtSeconds) {
        integrate(dtSeconds * 1.25);
        world.wrap(getPosition(), -240, -150, 240, 150);
    }
}
