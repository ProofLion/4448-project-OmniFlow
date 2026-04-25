package sim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;
import javafx.scene.canvas.GraphicsContext;
import model.Agent;
import model.AgentFactory;
import model.AgentTypes;
import model.MapLayout;
import model.layouts.DowntownIntersectionLayout;
import org.junit.jupiter.api.Test;
import util.Vec2;

class SimulationEngineTest {
    @Test
    void stepAdvancesTickCount() {
        World world = new World(new DowntownIntersectionLayout());
        SimulationEngine engine = new SimulationEngine(world);

        assertEquals(0, engine.getTickCount());
        engine.step();
        assertEquals(1, engine.getTickCount());
    }

    @Test
    void disabledTypeDoesNotUpdatePosition() {
        World world = new World(new DowntownIntersectionLayout());
        SimulationEngine engine = new SimulationEngine(world);
        world.clearAgents();
        world.addAgent(AgentFactory.defaultFactory().createWithId(AgentTypes.CAR, 100, new Vec2(0, 0), new Vec2(30, 0)));

        engine.setUpdatingEnabledTypes(Set.of());
        engine.step();

        assertEquals(0.0, world.getAgents().get(0).getPosition().x, 0.0001);
        assertEquals(1, engine.getTickCount());
    }

    @Test
    void enabledTypeUpdatesPosition() {
        World world = new World(new DowntownIntersectionLayout());
        SimulationEngine engine = new SimulationEngine(world);
        world.clearAgents();
        world.addAgent(AgentFactory.defaultFactory().createWithId(AgentTypes.CAR, 101, new Vec2(0, 0), new Vec2(30, 0)));

        engine.setUpdatingEnabledTypes(Set.of(AgentTypes.CAR));
        engine.step();

        assertEquals(1.0, world.getAgents().get(0).getPosition().x, 0.0001);
    }

    @Test
    void emergencyVehicleCanIgnoreRedLightRules() {
        World world = new World(new StopLightTestLayout());
        SimulationEngine engine = new SimulationEngine(world);
        world.clearAgents();
        world.addAgent(AgentFactory.defaultFactory().createWithId(AgentTypes.EMERGENCY_VEHICLE, 103, new Vec2(0, 0), new Vec2(0, 30)));

        engine.setUpdatingEnabledTypes(Set.of(AgentTypes.EMERGENCY_VEHICLE));
        engine.step();

        assertEquals(1.25, world.getAgents().get(0).getPosition().y, 0.0001);
    }

    @Test
    void regularVehicleStopsAtRedLight() {
        World world = new World(new StopLightTestLayout());
        SimulationEngine engine = new SimulationEngine(world);
        world.clearAgents();
        world.addAgent(AgentFactory.defaultFactory().createWithId(AgentTypes.CAR, 104, new Vec2(0, 0), new Vec2(0, 30)));

        engine.setUpdatingEnabledTypes(Set.of(AgentTypes.CAR));
        engine.step();

        assertEquals(0.0, world.getAgents().get(0).getPosition().y, 0.0001);
    }

    @Test
    void speedMultiplierIsClamped() {
        World world = new World(new DowntownIntersectionLayout());
        SimulationEngine engine = new SimulationEngine(world);

        engine.setSpeedMultiplier(99.0);
        assertEquals(4.0, engine.getSpeedMultiplier());

        engine.setSpeedMultiplier(0.01);
        assertEquals(0.25, engine.getSpeedMultiplier());
    }

    @Test
    void randomDowntownSpawnsDoNotCreateEmergencyVehicles() {
        World world = new World(new DowntownIntersectionLayout());
        SimulationEngine engine = new SimulationEngine(world);
        world.clearAgents();

        engine.addRandomAgents(60);

        assertFalse(world.getAgents().stream().anyMatch(agent -> AgentTypes.EMERGENCY_VEHICLE.equals(agent.getTypeName())));
    }

    @Test
    void spawnEmergencyVehicleAddsOneEmergencyVehicle() {
        World world = new World(new DowntownIntersectionLayout());
        SimulationEngine engine = new SimulationEngine(world);
        long before = world.getAgents().stream().filter(agent -> AgentTypes.EMERGENCY_VEHICLE.equals(agent.getTypeName())).count();

        engine.spawnEmergencyVehicle();

        long after = world.getAgents().stream().filter(agent -> AgentTypes.EMERGENCY_VEHICLE.equals(agent.getTypeName())).count();
        assertEquals(before + 1, after);
    }

    @Test
    void spawnEmergencyVehicleDoesNotAddSecondEmergencyWhileOneExists() {
        World world = new World(new DowntownIntersectionLayout());
        SimulationEngine engine = new SimulationEngine(world);

        engine.spawnEmergencyVehicle();
        engine.spawnEmergencyVehicle();

        long count = world.getAgents().stream().filter(agent -> AgentTypes.EMERGENCY_VEHICLE.equals(agent.getTypeName())).count();
        assertEquals(1, count);
    }

    @Test
    void useLayoutResetsTickAndPausesEngine() {
        World world = new World(new DowntownIntersectionLayout());
        SimulationEngine engine = new SimulationEngine(world);
        engine.step();

        MapLayout replacement = new MapLayout() {
            @Override
            public String getName() {
                return "TestLayout";
            }

            @Override
            public void draw(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight, long tickCount) {
            }

            @Override
            public void seed(World targetWorld) {
                targetWorld.clearAgents();
                targetWorld.addAgent(
                    AgentFactory.defaultFactory().createWithId(AgentTypes.BIKE, 102, new Vec2(10, 10), new Vec2(20, 0))
                );
            }

            @Override
            public boolean shouldVehicleYield(Agent agent, long tickCount) {
                return false;
            }

            @Override
            public boolean shouldPedestrianYield(Agent agent, long tickCount) {
                return false;
            }

            @Override
            public boolean isBusStop(Agent agent) {
                return false;
            }

            @Override
            public void keepAgentInBounds(Agent agent) {
            }

            @Override
            public Vec2 getSuggestedCameraOffset() {
                return new Vec2(0, 0);
            }
        };

        engine.useLayout(replacement);

        assertEquals(0, engine.getTickCount());
        assertEquals("TestLayout", world.getLayout().getName());
        assertEquals(1, world.getAgents().size());
        assertEquals(AgentTypes.BIKE, world.getAgents().get(0).getTypeName());
    }

    private static class StopLightTestLayout implements MapLayout {
        @Override
        public String getName() {
            return "StopLightTest";
        }

        @Override
        public void draw(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight, long tickCount) {
        }

        @Override
        public void seed(World world) {
        }

        @Override
        public boolean shouldVehicleYield(Agent agent, long tickCount) {
            return true;
        }

        @Override
        public boolean shouldPedestrianYield(Agent agent, long tickCount) {
            return false;
        }

        @Override
        public boolean isBusStop(Agent agent) {
            return false;
        }

        @Override
        public void keepAgentInBounds(Agent agent) {
        }

        @Override
        public Vec2 getSuggestedCameraOffset() {
            return new Vec2(0, 0);
        }
    }
}
