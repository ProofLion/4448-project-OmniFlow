package model.layouts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import model.Agent;
import model.AgentFactory;
import model.AgentTypes;
import org.junit.jupiter.api.Test;
import sim.World;
import util.Vec2;

class LayoutBehaviorTest {
    @Test
    void downtownVehicleStopsBeforeIntersectionOnRed() {
        DowntownIntersectionLayout layout = new DowntownIntersectionLayout();
        Agent car = AgentFactory.defaultFactory().createWithId(AgentTypes.CAR, 401, new Vec2(-130, 18), new Vec2(72, 0));
        World world = new World(layout);
        world.addAgent(car);
        world.setTickCount(130);

        car.update(world, 1.0 / 30.0);

        assertEquals(-130.0, car.getPosition().x, 0.0001);
    }

    @Test
    void downtownYellowPreventsLateEntryNearStopLine() {
        DowntownIntersectionLayout layout = new DowntownIntersectionLayout();
        Agent bus = AgentFactory.defaultFactory().createWithId(AgentTypes.BUS, 402, new Vec2(-130, 54), new Vec2(54, 0));
        World world = new World(layout);
        world.addAgent(bus);
        world.setTickCount(138);

        bus.update(world, 1.0 / 30.0);

        assertEquals(-130.0, bus.getPosition().x, 0.0001);
    }

    @Test
    void downtownPedestrianAlreadyInCrosswalkKeepsMovingDuringRed() {
        DowntownIntersectionLayout layout = new DowntownIntersectionLayout();
        Agent pedestrian = AgentFactory.defaultFactory().createWithId(
            AgentTypes.PEDESTRIAN,
            404,
            new Vec2(-102, -20),
            new Vec2(0, 30)
        );
        World world = new World(layout);
        world.addAgent(pedestrian);
        world.setTickCount(20);

        pedestrian.update(world, 1.0 / 30.0);

        assertFalse(pedestrian.getPosition().y <= -20.0);
    }

    @Test
    void downtownHorizontalPedestrianGetsWalkWhileVerticalPedestrianWaits() {
        DowntownIntersectionLayout layout = new DowntownIntersectionLayout();
        Agent horizontalPed = AgentFactory.defaultFactory().createWithId(
            AgentTypes.PEDESTRIAN,
            414,
            new Vec2(-118, -102),
            new Vec2(26, 0)
        );
        Agent verticalPed = AgentFactory.defaultFactory().createWithId(
            AgentTypes.PEDESTRIAN,
            415,
            new Vec2(-102, -118),
            new Vec2(0, 28)
        );
        World world = new World(layout);
        world.addAgent(horizontalPed);
        world.addAgent(verticalPed);
        world.setTickCount(180);

        horizontalPed.update(world, 1.0 / 30.0);
        verticalPed.update(world, 1.0 / 30.0);

        assertFalse(horizontalPed.getPosition().x <= -118.0);
        assertEquals(-118.0, verticalPed.getPosition().y, 0.0001);
    }

    @Test
    void downtownPedestrianDoesNotStartDuringFlashingWalk() {
        DowntownIntersectionLayout layout = new DowntownIntersectionLayout();
        Agent pedestrian = AgentFactory.defaultFactory().createWithId(
            AgentTypes.PEDESTRIAN,
            416,
            new Vec2(-118, -102),
            new Vec2(26, 0)
        );
        World world = new World(layout);
        world.addAgent(pedestrian);
        world.setTickCount(280);

        pedestrian.update(world, 1.0 / 30.0);

        assertEquals(-118.0, pedestrian.getPosition().x, 0.0001);
    }

    @Test
    void downtownVehicleQueuesBehindBusAtStop() {
        DowntownIntersectionLayout layout = new DowntownIntersectionLayout();
        Agent bus = AgentFactory.defaultFactory().createWithId(AgentTypes.BUS, 405, new Vec2(-185, 54), new Vec2(52, 0));
        Agent car = AgentFactory.defaultFactory().createWithId(AgentTypes.CAR, 406, new Vec2(-235, 54), new Vec2(60, 0));
        World world = new World(layout);
        world.addAgent(bus);
        world.addAgent(car);
        world.setTickCount(30);

        car.update(world, 1.0 / 30.0);

        assertEquals(-235.0, car.getPosition().x, 0.0001);
    }

    @Test
    void downtownEmergencyApproachForcesAllDirectionsToHold() {
        DowntownIntersectionLayout layout = new DowntownIntersectionLayout();
        Agent emergency = AgentFactory.defaultFactory().createWithId(
            AgentTypes.EMERGENCY_VEHICLE,
            407,
            new Vec2(-18, -120),
            new Vec2(0, 80)
        );
        Agent car = AgentFactory.defaultFactory().createWithId(AgentTypes.CAR, 408, new Vec2(-170, 18), new Vec2(68, 0));
        World world = new World(layout);
        world.addAgent(emergency);
        world.addAgent(car);
        world.setTickCount(30);

        car.update(world, 1.0 / 30.0);

        assertEquals(-170.0, car.getPosition().x, 0.0001);
    }

    @Test
    void downtownCommittedCarKeepsClearingIntersectionDuringEmergencyPreemption() {
        DowntownIntersectionLayout layout = new DowntownIntersectionLayout();
        Agent emergency = AgentFactory.defaultFactory().createWithId(
            AgentTypes.EMERGENCY_VEHICLE,
            409,
            new Vec2(-18, -120),
            new Vec2(0, 80)
        );
        Agent car = AgentFactory.defaultFactory().createWithId(AgentTypes.CAR, 410, new Vec2(-52, 18), new Vec2(68, 0));
        World world = new World(layout);
        world.addAgent(emergency);
        world.addAgent(car);
        world.setTickCount(30);

        car.update(world, 1.0 / 30.0);

        assertFalse(car.getPosition().x <= -52.0);
    }

    @Test
    void downtownEmergencyVehicleDoesNotOverlapStoppedCar() {
        DowntownIntersectionLayout layout = new DowntownIntersectionLayout();
        Agent emergency = AgentFactory.defaultFactory().createWithId(
            AgentTypes.EMERGENCY_VEHICLE,
            411,
            new Vec2(-18, -120),
            new Vec2(0, 80)
        );
        Agent car = AgentFactory.defaultFactory().createWithId(AgentTypes.CAR, 412, new Vec2(-18, -76), new Vec2(0, 58));
        World world = new World(layout);
        world.addAgent(emergency);
        world.addAgent(car);
        world.setTickCount(30);

        emergency.update(world, 1.0 / 30.0);

        assertFalse(emergency.getPosition().y > -76.0);
    }

    @Test
    void downtownBikeStopsBeforeCrosswalkInsteadOfEnteringIt() {
        DowntownIntersectionLayout layout = new DowntownIntersectionLayout();
        Agent bike = AgentFactory.defaultFactory().createWithId(
            AgentTypes.BIKE,
            413,
            new Vec2(-118, -102),
            new Vec2(30, 0)
        );
        World world = new World(layout);
        world.addAgent(bike);
        world.setTickCount(140);

        bike.update(world, 1.0 / 30.0);

        assertEquals(-118.0, bike.getPosition().x, 0.0001);
    }

    @Test
    void downtownPedestrianMovesFasterDuringFlashingPhaseWhenAlreadyCrossing() {
        DowntownIntersectionLayout layout = new DowntownIntersectionLayout();
        Agent pedestrian = AgentFactory.defaultFactory().createWithId(
            AgentTypes.PEDESTRIAN,
            417,
            new Vec2(-40, -102),
            new Vec2(26, 0)
        );
        World world = new World(layout);
        world.addAgent(pedestrian);
        world.setTickCount(275);

        pedestrian.update(world, 1.0 / 30.0);

        assertEquals(-39.22, pedestrian.getPosition().x, 0.01);
    }

    @Test
    void downtownEmergencyVehicleDespawnsInsteadOfRecyclingAtMapEdge() {
        DowntownIntersectionLayout layout = new DowntownIntersectionLayout();
        Agent emergency = AgentFactory.defaultFactory().createWithId(
            AgentTypes.EMERGENCY_VEHICLE,
            418,
            new Vec2(381, -18),
            new Vec2(80, 0)
        );
        World world = new World(layout);
        world.addAgent(emergency);
        world.setTickCount(10);

        emergency.update(world, 1.0 / 30.0);

        assertFalse(world.getAgents().contains(emergency));
    }

    @Test
    void schoolZoneVehicleAlreadyInIntersectionDoesNotFreeze() {
        SchoolZoneLayout layout = new SchoolZoneLayout();
        Agent car = AgentFactory.defaultFactory().createWithId(AgentTypes.CAR, 403, new Vec2(-20, -16), new Vec2(48, 0));
        World world = new World(layout);
        world.addAgent(car);
        world.setTickCount(80);

        car.update(world, 1.0 / 30.0);

        assertFalse(car.getPosition().x <= -20.0);
    }
}
