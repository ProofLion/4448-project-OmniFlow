package model;

import java.util.HashMap;
import java.util.Map;
import model.agenttypes.AircraftAgent;
import model.agenttypes.BoatAgent;
import model.agenttypes.BusAgent;
import model.agenttypes.CarAgent;
import model.agenttypes.EmergencyAgent;
import model.agenttypes.PedestrianAgent;
import util.Ids;
import util.Vec2;

/**
 * Registry-based factory avoids a type switch and makes extension straightforward.
 */
public final class AgentFactory implements AgentProvider {
    @FunctionalInterface
    public interface AgentCreator {
        Agent create(long id, Vec2 position, Vec2 velocity);
    }

    private static final AgentFactory DEFAULT = new AgentFactory();
    private final Map<String, AgentCreator> creators = new HashMap<>();

    public AgentFactory() {
        register("Car", CarAgent::new);
        register("Bus", BusAgent::new);
        register("Emergency", EmergencyAgent::new);
        register("Pedestrian", PedestrianAgent::new);
        register("Boat", BoatAgent::new);
        register("Aircraft", AircraftAgent::new);
    }

    public static AgentFactory defaultFactory() {
        return DEFAULT;
    }

    public void register(String typeName, AgentCreator creator) {
        creators.put(typeName, creator);
    }

    @Override
    public Agent create(String typeName, Vec2 position, Vec2 velocity) {
        AgentCreator creator = creators.get(typeName);
        if (creator == null) {
            throw new IllegalArgumentException("Unknown agent type: " + typeName);
        }
        return creator.create(Ids.next(), position, velocity);
    }

    @Override
    public Agent createWithId(String typeName, long id, Vec2 position, Vec2 velocity) {
        AgentCreator creator = creators.get(typeName);
        if (creator == null) {
            throw new IllegalArgumentException("Unknown agent type: " + typeName);
        }
        Ids.register(id);
        return creator.create(id, position, velocity);
    }
}
