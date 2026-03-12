package model;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
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
public final class AgentFactory {
    @FunctionalInterface
    public interface AgentCreator {
        Agent create(long id, Vec2 position, Vec2 velocity);
    }

    private static final Map<String, AgentCreator> CREATORS = new HashMap<>();

    static {
        register("Car", CarAgent::new);
        register("Bus", BusAgent::new);
        register("Emergency", EmergencyAgent::new);
        register("Pedestrian", PedestrianAgent::new);
        register("Boat", BoatAgent::new);
        register("Aircraft", AircraftAgent::new);
    }

    private AgentFactory() {
    }

    public static void register(String typeName, AgentCreator creator) {
        CREATORS.put(typeName, creator);
    }

    public static Agent create(String typeName, Vec2 position, Vec2 velocity) {
        AgentCreator creator = CREATORS.get(typeName);
        if (creator == null) {
            throw new IllegalArgumentException("Unknown agent type: " + typeName);
        }
        return creator.create(Ids.next(), position, velocity);
    }

    public static Agent createWithId(String typeName, long id, Vec2 position, Vec2 velocity) {
        AgentCreator creator = CREATORS.get(typeName);
        if (creator == null) {
            throw new IllegalArgumentException("Unknown agent type: " + typeName);
        }
        Ids.register(id);
        return creator.create(id, position, velocity);
    }
}
