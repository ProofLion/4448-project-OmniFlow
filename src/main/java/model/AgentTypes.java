package model;

import java.util.List;

/**
 * Shared agent type names used across the project.
 */
public final class AgentTypes {
    public static final String CAR = "Car";
    public static final String BUS = "Bus";
    public static final String EMERGENCY_VEHICLE = "EmergencyVehicle";
    public static final String BIKE = "Bike";
    public static final String PEDESTRIAN = "Pedestrian";

    public static final List<String> ALL_CITY_TYPES = List.of(
        CAR,
        BUS,
        EMERGENCY_VEHICLE,
        BIKE,
        PEDESTRIAN
    );

    private AgentTypes() {
    }
}
