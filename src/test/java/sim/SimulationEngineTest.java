package sim;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import javafx.scene.canvas.GraphicsContext;
import model.Agent;
import model.AgentFactory;
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
        world.addAgent(AgentFactory.defaultFactory().createWithId("Car", 100, new Vec2(0, 0), new Vec2(30, 0)));

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
        world.addAgent(AgentFactory.defaultFactory().createWithId("Car", 101, new Vec2(0, 0), new Vec2(30, 0)));

        engine.setUpdatingEnabledTypes(Set.of("Car"));
        engine.step();

        assertEquals(1.0, world.getAgents().get(0).getPosition().x, 0.0001);
    }

    @Test
    void emergencyVehicleCanIgnoreRedLightRules() {
        World world = new World(new StopLightTestLayout());
        SimulationEngine engine = new SimulationEngine(world);
        world.clearAgents();
        world.addAgent(AgentFactory.defaultFactory().createWithId("EmergencyVehicle", 103, new Vec2(0, 0), new Vec2(0, 30)));

        engine.setUpdatingEnabledTypes(Set.of("EmergencyVehicle"));
        engine.step();

        assertEquals(1.25, world.getAgents().get(0).getPosition().y, 0.0001);
    }

    @Test
    void regularVehicleStopsAtRedLight() {
        World world = new World(new StopLightTestLayout());
        SimulationEngine engine = new SimulationEngine(world);
        world.clearAgents();
        world.addAgent(AgentFactory.defaultFactory().createWithId("Car", 104, new Vec2(0, 0), new Vec2(0, 30)));

        engine.setUpdatingEnabledTypes(Set.of("Car"));
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
                    AgentFactory.defaultFactory().createWithId("Bike", 102, new Vec2(10, 10), new Vec2(20, 0))
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
        assertEquals("Bike", world.getAgents().get(0).getTypeName());
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
