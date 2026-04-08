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
    public String getShortLabel() {
        return "EV";
    }

    @Override
    protected double getSpeedMultiplier(World world) {
        return 1.25;
    }
}
