package model.agenttypes;

import javafx.scene.paint.Color;
import model.BaseAgent;
import sim.World;
import util.Vec2;

public class EmergencyVehicleAgent extends BaseAgent {
    public EmergencyVehicleAgent(long id, Vec2 position, Vec2 velocity) {
        super(id, position, velocity);
    }

    @Override
    public String getTypeName() {
        return "EmergencyVehicle";
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
