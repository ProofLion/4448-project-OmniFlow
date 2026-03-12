package model.agenttypes;

import javafx.scene.paint.Color;
import model.BaseAgent;
import sim.World;
import util.Vec2;

public class AircraftAgent extends BaseAgent {
    public AircraftAgent(long id, Vec2 position, Vec2 velocity) {
        super(id, position, velocity);
    }

    @Override
    public String getTypeName() {
        return "Aircraft";
    }

    @Override
    public Color getColor() {
        return Color.MEDIUMPURPLE;
    }

    @Override
    public double getRenderRadius() {
        return 10.0;
    }

    @Override
    public String getShortLabel() {
        return "A" + getId();
    }

    @Override
    public void update(World world, double dtSeconds) {
        integrate(dtSeconds);
        world.wrap(getPosition(), -300, -220, 300, -120);
    }
}
