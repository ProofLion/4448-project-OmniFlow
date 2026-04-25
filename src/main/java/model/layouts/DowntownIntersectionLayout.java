package model.layouts;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Agent;
import model.AgentFactory;
import model.AgentTypes;
import model.MapLayout;
import model.TrafficSignalState;
import model.TravelAxis;
import sim.Camera2D;
import sim.World;
import util.Vec2;

public class DowntownIntersectionLayout implements MapLayout {
    private static final double MIN_X = -360;
    private static final double MAX_X = 360;
    private static final double MIN_Y = -280;
    private static final double MAX_Y = 280;

    private static final double ROAD_HALF_WIDTH = 84;
    private static final double LANE_OFFSET_OUTER = 54;
    private static final double LANE_OFFSET_INNER = 18;
    private static final double STOP_LINE_DISTANCE = 118;
    private static final double STOP_WINDOW_MIN = 118;
    private static final double STOP_WINDOW_MAX = 172;
    private static final double INTERSECTION_CLEARANCE = 72;
    private static final double EMERGENCY_PREEMPTION_DISTANCE = 132;
    private static final double VEHICLE_OVERLAP_BUFFER = 34;
    private static final double LARGE_VEHICLE_OVERLAP_BUFFER = 48;
    private static final double EMERGENCY_BUFFER = 62;
    private static final double BIKE_SIDEWALK_OFFSET = 130;

    private static final double WEST_CROSSWALK_X = -102;
    private static final double EAST_CROSSWALK_X = 102;
    private static final double NORTH_CROSSWALK_Y = -102;
    private static final double SOUTH_CROSSWALK_Y = 102;
    private static final double CROSSWALK_HALF_LENGTH = 64;
    private static final double CROSSWALK_HALF_WIDTH = 13;
    private static final double CROSSWALK_ENTRY_MARGIN = 38;

    private static final double BUS_STOP_START_X = -220;
    private static final double BUS_STOP_END_X = -160;
    private static final double BUS_LANE_Y = 54;
    private static final double BUS_PLATFORM_Y = 96;
    private static final double BUS_EXIT_X = -188;
    private static final double BUS_SHELTER_ENTRY_Y = 124;

    private static final long SIGNAL_CYCLE = 600;
    private static final long HORIZONTAL_GREEN_END = 120;
    private static final long HORIZONTAL_YELLOW_END = 150;
    private static final long HORIZONTAL_PED_WALK_END = 240;
    private static final long HORIZONTAL_PED_FLASH_END = 300;
    private static final long VERTICAL_GREEN_END = 420;
    private static final long VERTICAL_YELLOW_END = 450;
    private static final long VERTICAL_PED_WALK_END = 540;
    private static final long VERTICAL_PED_FLASH_END = 600;
    private static final long PEDESTRIAN_START_BUFFER_TICKS = 60;

    private static final double NORTH_WALKWAY_Y = -130;
    private static final double SOUTH_WALKWAY_Y = 130;
    private static final double WEST_BUILDING_DOOR_X = -188;
    private static final double EAST_BUILDING_DOOR_X = 188;
    private static final double NORTH_BLOCK_DOOR_Y = -178;
    private static final double SOUTH_BLOCK_DOOR_Y = 178;

    @Override
    public String getName() {
        return "Downtown Intersection";
    }

    @Override
    public void draw(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight, long tickCount) {
        draw(gc, camera, canvasWidth, canvasHeight, tickCount, null);
    }

    @Override
    public void draw(
        GraphicsContext gc,
        Camera2D camera,
        double canvasWidth,
        double canvasHeight,
        long tickCount,
        World world
    ) {
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, MIN_X, MIN_Y, MAX_X - MIN_X, MAX_Y - MIN_Y, Color.web("#D9E6CF"));
        drawBlocks(gc, camera, canvasWidth, canvasHeight);
        drawRoadbed(gc, camera, canvasWidth, canvasHeight);
        drawBusStop(gc, camera, canvasWidth, canvasHeight, world);
        drawSignals(gc, camera, canvasWidth, canvasHeight, tickCount, world);
    }

    @Override
    public void seed(World world) {
        world.clearAgents();

        world.addAgent(AgentFactory.defaultFactory().create(AgentTypes.CAR, new Vec2(-320, 18), new Vec2(68, 0)));
        world.addAgent(AgentFactory.defaultFactory().create(AgentTypes.CAR, new Vec2(-250, 54), new Vec2(62, 0)));
        world.addAgent(AgentFactory.defaultFactory().create(AgentTypes.CAR, new Vec2(320, -18), new Vec2(-66, 0)));
        world.addAgent(AgentFactory.defaultFactory().create(AgentTypes.CAR, new Vec2(250, -54), new Vec2(-60, 0)));

        world.addAgent(AgentFactory.defaultFactory().create(AgentTypes.CAR, new Vec2(-54, -250), new Vec2(0, 58)));
        world.addAgent(AgentFactory.defaultFactory().create(AgentTypes.CAR, new Vec2(-18, -320), new Vec2(0, 64)));
        world.addAgent(AgentFactory.defaultFactory().create(AgentTypes.CAR, new Vec2(18, 250), new Vec2(0, -56)));
        world.addAgent(AgentFactory.defaultFactory().create(AgentTypes.CAR, new Vec2(54, 320), new Vec2(0, -62)));

        world.addAgent(AgentFactory.defaultFactory().create(AgentTypes.BUS, new Vec2(-320, BUS_LANE_Y), new Vec2(52, 0)));

        world.addAgent(AgentFactory.defaultFactory().create(AgentTypes.PEDESTRIAN, new Vec2(WEST_CROSSWALK_X, -190), new Vec2(0, 28)));
        world.addAgent(AgentFactory.defaultFactory().create(AgentTypes.PEDESTRIAN, new Vec2(240, NORTH_CROSSWALK_Y), new Vec2(-26, 0)));
        world.addAgent(AgentFactory.defaultFactory().create(AgentTypes.PEDESTRIAN, new Vec2(WEST_BUILDING_DOOR_X, NORTH_WALKWAY_Y), new Vec2(24, 0)));
        world.addAgent(AgentFactory.defaultFactory().create(AgentTypes.PEDESTRIAN, new Vec2(EAST_BUILDING_DOOR_X, SOUTH_WALKWAY_Y), new Vec2(-24, 0)));
        world.addAgent(AgentFactory.defaultFactory().create(AgentTypes.BIKE, new Vec2(BIKE_SIDEWALK_OFFSET, 210), new Vec2(0, -34)));
        world.addAgent(AgentFactory.defaultFactory().create(AgentTypes.BIKE, new Vec2(-220, -BIKE_SIDEWALK_OFFSET), new Vec2(30, 0)));
    }

    @Override
    public boolean shouldVehicleYield(Agent agent, long tickCount) {
        return shouldVehicleYield(agent, null, tickCount);
    }

    @Override
    public boolean shouldVehicleYield(Agent agent, World world, long tickCount) {
        if (AgentTypes.EMERGENCY_VEHICLE.equals(agent.getTypeName())) {
            return world != null && hasBlockingVehicleAhead(agent, world);
        }

        if (world != null && shouldClearLaneForEmergency(agent, world)) {
            return false;
        }

        if (world != null && isEmergencyPreemptionActive(world) && shouldHoldForEmergency(agent)) {
            return true;
        }

        TrafficSignalState signal = getVehicleSignal(agent, tickCount);
        if (signal == TrafficSignalState.RED && shouldStopForRed(agent)) {
            return true;
        }
        if (signal == TrafficSignalState.YELLOW && shouldStopForYellow(agent, tickCount)) {
            return true;
        }

        return world != null && hasBlockingVehicleAhead(agent, world);
    }

    @Override
    public boolean shouldPedestrianYield(Agent agent, long tickCount) {
        return shouldPedestrianYield(agent, null, tickCount);
    }

    @Override
    public boolean shouldPedestrianYield(Agent agent, World world, long tickCount) {
        if (AgentTypes.BIKE.equals(agent.getTypeName())) {
            return isNearCrosswalkEntry(agent.getPosition()) || isInsideCrosswalk(agent.getPosition());
        }
        if (isInsideCrosswalk(agent.getPosition())) {
            return false;
        }
        if (!isNearCrosswalkEntry(agent.getPosition())) {
            return false;
        }
        if (!isPedestrianWalkActive(agent, tickCount)) {
            return true;
        }
        return getPedestrianWalkTicksRemaining(agent, tickCount) < PEDESTRIAN_START_BUFFER_TICKS;
    }

    @Override
    public boolean isBusStop(Agent agent) {
        Vec2 position = agent.getPosition();
        return Math.abs(position.y - BUS_LANE_Y) <= 3
            && position.x >= BUS_STOP_START_X
            && position.x <= BUS_STOP_END_X;
    }

    @Override
    public void keepAgentInBounds(Agent agent) {
        LayoutSupport.recycleAlongRoute(agent, MIN_X, MAX_X, MIN_Y, MAX_Y);
    }

    @Override
    public void keepAgentInBounds(Agent agent, World world) {
        if ((AgentTypes.PEDESTRIAN.equals(agent.getTypeName()) || AgentTypes.BIKE.equals(agent.getTypeName()))
            && isOutOfBounds(agent.getPosition())) {
            world.removeAgent(agent);
            return;
        }
        if (AgentTypes.EMERGENCY_VEHICLE.equals(agent.getTypeName())) {
            if (isEmergencyOutOfBounds(agent)) {
                world.removeAgent(agent);
            }
            return;
        }
        keepAgentInBounds(agent);
    }

    @Override
    public Vec2 getSuggestedCameraOffset() {
        return new Vec2(-255, -175);
    }

    public double getEmergencySpeedMultiplier(Agent agent, World world) {
        if (!isEmergencyPreemptionActive(world)) {
            return 1.0;
        }
        if (isIntersectionOccupied(world, agent)) {
            return 0.7;
        }
        return 1.3;
    }

    public double getVehicleSpeedMultiplier(Agent agent, long tickCount) {
        TrafficSignalState signal = getVehicleSignal(agent, tickCount);
        if (signal != TrafficSignalState.YELLOW || !isNearStopWindow(agent)) {
            return AgentTypes.BUS.equals(agent.getTypeName()) ? 0.85 : 1.0;
        }

        if (canClearIntersectionBeforeRed(agent, tickCount, true)) {
            return AgentTypes.BUS.equals(agent.getTypeName()) ? 1.0 : 1.18;
        }
        return AgentTypes.BUS.equals(agent.getTypeName()) ? 0.85 : 1.0;
    }

    public boolean shouldSpawnPedestrianFromBus(Agent agent) {
        return isBusStop(agent);
    }

    public Vec2 getBusExitSpawnPosition(Agent bus) {
        return new Vec2(bus.getPosition().x + 6, BUS_LANE_Y + 10);
    }

    public Vec2 getBusExitVelocity() {
        return new Vec2(0, 24);
    }

    public boolean shouldDespawnBusPassenger(Agent pedestrian) {
        Vec2 position = pedestrian.getPosition();
        return position.x >= BUS_STOP_START_X && position.x <= BUS_STOP_END_X && position.y >= BUS_SHELTER_ENTRY_Y;
    }

    public boolean isPedestrianFlashing(Agent agent, long tickCount) {
        return isPedestrianWalkFlashing(agent, tickCount);
    }

    public boolean isEmergencyOutOfBounds(Agent agent) {
        Vec2 position = agent.getPosition();
        return position.x < MIN_X - 20 || position.x > MAX_X + 20 || position.y < MIN_Y - 20 || position.y > MAX_Y + 20;
    }

    private boolean isOutOfBounds(Vec2 position) {
        return position.x < MIN_X - 2 || position.x > MAX_X + 2 || position.y < MIN_Y - 2 || position.y > MAX_Y + 2;
    }

    private void drawBlocks(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight) {
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -330, -250, 170, 120, Color.web("#BFD0DC"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, 160, -250, 150, 120, Color.web("#D9C2A8"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -330, 130, 170, 120, Color.web("#CDB7D8"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, 160, 130, 150, 120, Color.web("#B9D7B2"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -95, -250, 190, 72, Color.web("#8DB59A"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -108, 178, 216, 40, Color.web("#9FB7C7"));
        drawDoor(gc, camera, canvasWidth, canvasHeight, WEST_BUILDING_DOOR_X, -130);
        drawDoor(gc, camera, canvasWidth, canvasHeight, EAST_BUILDING_DOOR_X, -130);
        drawDoor(gc, camera, canvasWidth, canvasHeight, WEST_BUILDING_DOOR_X, 130);
        drawDoor(gc, camera, canvasWidth, canvasHeight, EAST_BUILDING_DOOR_X, 130);
        drawDoor(gc, camera, canvasWidth, canvasHeight, -38, NORTH_BLOCK_DOOR_Y);
        drawDoor(gc, camera, canvasWidth, canvasHeight, 38, NORTH_BLOCK_DOOR_Y);
        drawDoor(gc, camera, canvasWidth, canvasHeight, -38, SOUTH_BLOCK_DOOR_Y);
        drawDoor(gc, camera, canvasWidth, canvasHeight, 38, SOUTH_BLOCK_DOOR_Y);
    }

    private void drawDoor(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight, double centerX, double centerY) {
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, centerX - 8, centerY - 4, 16, 8, Color.web("#5A3B2A"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, centerX - 6, centerY - 2, 12, 4, Color.web("#C7A77A"));
    }

    private void drawRoadbed(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight) {
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, MIN_X, -ROAD_HALF_WIDTH, MAX_X - MIN_X, ROAD_HALF_WIDTH * 2, Color.web("#2E3238"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -ROAD_HALF_WIDTH, MIN_Y, ROAD_HALF_WIDTH * 2, MAX_Y - MIN_Y, Color.web("#2E3238"));

        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -8, -ROAD_HALF_WIDTH, 16, ROAD_HALF_WIDTH * 2, Color.web("#23272D"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -ROAD_HALF_WIDTH, -8, ROAD_HALF_WIDTH * 2, 16, Color.web("#23272D"));

        gc.setStroke(Color.web("#F8FAFC"));
        gc.setLineWidth(2.2);
        drawDashedWorldLine(camera, gc, canvasWidth, canvasHeight, MIN_X, -36, MAX_X, -36, 24, 14);
        drawDashedWorldLine(camera, gc, canvasWidth, canvasHeight, MIN_X, 36, MAX_X, 36, 24, 14);
        drawDashedWorldLine(camera, gc, canvasWidth, canvasHeight, -36, MIN_Y, -36, MAX_Y, 24, 14);
        drawDashedWorldLine(camera, gc, canvasWidth, canvasHeight, 36, MIN_Y, 36, MAX_Y, 24, 14);

        gc.setStroke(Color.web("#F2D45C"));
        gc.setLineWidth(2.8);
        camera.strokeWorldLine(gc, canvasWidth, canvasHeight, MIN_X, -6, MAX_X, -6);
        camera.strokeWorldLine(gc, canvasWidth, canvasHeight, MIN_X, 6, MAX_X, 6);
        camera.strokeWorldLine(gc, canvasWidth, canvasHeight, -6, MIN_Y, -6, MAX_Y);
        camera.strokeWorldLine(gc, canvasWidth, canvasHeight, 6, MIN_Y, 6, MAX_Y);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(4);
        camera.strokeWorldLine(gc, canvasWidth, canvasHeight, -ROAD_HALF_WIDTH, -ROAD_HALF_WIDTH, -ROAD_HALF_WIDTH, ROAD_HALF_WIDTH);
        camera.strokeWorldLine(gc, canvasWidth, canvasHeight, ROAD_HALF_WIDTH, -ROAD_HALF_WIDTH, ROAD_HALF_WIDTH, ROAD_HALF_WIDTH);
        camera.strokeWorldLine(gc, canvasWidth, canvasHeight, -ROAD_HALF_WIDTH, -ROAD_HALF_WIDTH, ROAD_HALF_WIDTH, -ROAD_HALF_WIDTH);
        camera.strokeWorldLine(gc, canvasWidth, canvasHeight, -ROAD_HALF_WIDTH, ROAD_HALF_WIDTH, ROAD_HALF_WIDTH, ROAD_HALF_WIDTH);

        drawCrosswalk(gc, camera, canvasWidth, canvasHeight, WEST_CROSSWALK_X, 0, false);
        drawCrosswalk(gc, camera, canvasWidth, canvasHeight, EAST_CROSSWALK_X, 0, false);
        drawCrosswalk(gc, camera, canvasWidth, canvasHeight, 0, NORTH_CROSSWALK_Y, true);
        drawCrosswalk(gc, camera, canvasWidth, canvasHeight, 0, SOUTH_CROSSWALK_Y, true);

        drawLaneArrow(gc, camera, canvasWidth, canvasHeight, -168, 18, true, true);
        drawLaneArrow(gc, camera, canvasWidth, canvasHeight, -168, 54, true, true);
        drawLaneArrow(gc, camera, canvasWidth, canvasHeight, 168, -18, true, false);
        drawLaneArrow(gc, camera, canvasWidth, canvasHeight, 168, -54, true, false);
        drawLaneArrow(gc, camera, canvasWidth, canvasHeight, -54, -168, false, true);
        drawLaneArrow(gc, camera, canvasWidth, canvasHeight, -18, -168, false, true);
        drawLaneArrow(gc, camera, canvasWidth, canvasHeight, 18, 168, false, false);
        drawLaneArrow(gc, camera, canvasWidth, canvasHeight, 54, 168, false, false);
    }

    private void drawBusStop(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight, World world) {
        boolean activeStop = world != null && isBusStoppedAtStop(world);
        Color platformColor = activeStop ? Color.web("#B8F0C6") : Color.web("#8DD2A8");
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, BUS_STOP_START_X, 84, BUS_STOP_END_X - BUS_STOP_START_X, 28, platformColor);
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, BUS_STOP_START_X + 10, 90, 14, 18, Color.web("#536878"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, BUS_STOP_START_X + 26, 92, 24, 16, Color.web("#D8E5F0"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, BUS_STOP_START_X + 24, 86, 28, 4, Color.web("#405263"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, BUS_STOP_START_X - 2, 56, BUS_STOP_END_X - BUS_STOP_START_X + 4, 20,
            activeStop ? Color.color(1.0, 0.9, 0.4, 0.32) : Color.color(1.0, 1.0, 1.0, 0.08));

        gc.setStroke(Color.web("#F8FAFC"));
        gc.setLineWidth(2);
        camera.strokeWorldLine(gc, canvasWidth, canvasHeight, BUS_STOP_START_X, 78, BUS_STOP_END_X, 78);
        camera.strokeWorldLine(gc, canvasWidth, canvasHeight, BUS_STOP_START_X, 84, BUS_STOP_END_X, 84);
        camera.strokeWorldLine(gc, canvasWidth, canvasHeight, BUS_STOP_START_X, 56, BUS_STOP_END_X, 56);

        gc.setFill(activeStop ? Color.web("#14532D") : Color.web("#1F2937"));
        drawWorldText(gc, camera, "BUS STOP", BUS_STOP_START_X + 6, 101, 11);
        drawWorldText(gc, camera, "BUS", BUS_STOP_START_X + 10, 70, 12);
    }

    private boolean isBusStoppedAtStop(World world) {
        for (Agent agent : world.getAgents()) {
            if (AgentTypes.BUS.equals(agent.getTypeName()) && isBusStop(agent) && agent.getVelocity().distanceSquared(new Vec2(0, 0)) < 0.01) {
                return true;
            }
        }
        return false;
    }

    private void drawSignals(
        GraphicsContext gc,
        Camera2D camera,
        double canvasWidth,
        double canvasHeight,
        long tickCount,
        World world
    ) {
        TrafficSignalState displayedHorizontal = world != null && isEmergencyPreemptionActive(world)
            ? TrafficSignalState.RED
            : horizontalSignal(tickCount);
        TrafficSignalState displayedVertical = world != null && isEmergencyPreemptionActive(world)
            ? TrafficSignalState.RED
            : verticalSignal(tickCount);
        Color eastWestWalkColor = world != null && isEmergencyPreemptionActive(world)
            ? Color.CRIMSON
            : pedestrianSignalColor(false, tickCount);
        Color northSouthWalkColor = world != null && isEmergencyPreemptionActive(world)
            ? Color.CRIMSON
            : pedestrianSignalColor(true, tickCount);

        drawVehicleSignal(gc, camera, canvasWidth, canvasHeight, -134, -138, displayedVertical);
        drawVehicleSignal(gc, camera, canvasWidth, canvasHeight, 112, 88, displayedVertical);
        drawVehicleSignal(gc, camera, canvasWidth, canvasHeight, 112, -138, displayedHorizontal);
        drawVehicleSignal(gc, camera, canvasWidth, canvasHeight, -134, 88, displayedHorizontal);

        drawPedSignal(gc, camera, canvasWidth, canvasHeight, -124, -16, eastWestWalkColor);
        drawPedSignal(gc, camera, canvasWidth, canvasHeight, 108, -16, eastWestWalkColor);
        drawPedSignal(gc, camera, canvasWidth, canvasHeight, -16, -124, northSouthWalkColor);
        drawPedSignal(gc, camera, canvasWidth, canvasHeight, -16, 108, northSouthWalkColor);
    }

    private void drawVehicleSignal(
        GraphicsContext gc,
        Camera2D camera,
        double canvasWidth,
        double canvasHeight,
        double x,
        double y,
        TrafficSignalState state
    ) {
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, x, y, 28, 64, Color.web("#111418"));
        camera.fillWorldCircle(gc, canvasWidth, canvasHeight, x + 14, y + 14, 5.5,
            state == TrafficSignalState.RED ? Color.RED : Color.web("#51161A"));
        camera.fillWorldCircle(gc, canvasWidth, canvasHeight, x + 14, y + 32, 5.5,
            state == TrafficSignalState.YELLOW ? Color.GOLD : Color.web("#5E470A"));
        camera.fillWorldCircle(gc, canvasWidth, canvasHeight, x + 14, y + 50, 5.5,
            state == TrafficSignalState.GREEN ? Color.LIMEGREEN : Color.web("#143B1E"));
    }

    private void drawPedSignal(
        GraphicsContext gc,
        Camera2D camera,
        double canvasWidth,
        double canvasHeight,
        double x,
        double y,
        Color walkColor
    ) {
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, x, y, 20, 20, Color.web("#14181D"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, x + 4, y + 4, 12, 12, walkColor);
    }

    private void drawCrosswalk(
        GraphicsContext gc,
        Camera2D camera,
        double canvasWidth,
        double canvasHeight,
        double centerX,
        double centerY,
        boolean horizontal
    ) {
        gc.setStroke(Color.web("#F8FAFC"));
        gc.setLineWidth(3.2);
        for (int i = -5; i <= 5; i++) {
            double offset = i * 10;
            if (horizontal) {
                camera.strokeWorldLine(gc, canvasWidth, canvasHeight,
                    centerX + offset, centerY - CROSSWALK_HALF_WIDTH,
                    centerX + offset, centerY + CROSSWALK_HALF_WIDTH);
            } else {
                camera.strokeWorldLine(gc, canvasWidth, canvasHeight,
                    centerX - CROSSWALK_HALF_WIDTH, centerY + offset,
                    centerX + CROSSWALK_HALF_WIDTH, centerY + offset);
            }
        }
    }

    private void drawLaneArrow(
        GraphicsContext gc,
        Camera2D camera,
        double canvasWidth,
        double canvasHeight,
        double x,
        double y,
        boolean horizontal,
        boolean forwardPositive
    ) {
        gc.setStroke(Color.color(1, 1, 1, 0.7));
        gc.setLineWidth(2);
        if (horizontal) {
            double dx = forwardPositive ? 18 : -18;
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, x - dx, y, x + dx, y);
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, x + dx, y, x + (forwardPositive ? 10 : -10), y - 7);
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, x + dx, y, x + (forwardPositive ? 10 : -10), y + 7);
            return;
        }

        double dy = forwardPositive ? 18 : -18;
        camera.strokeWorldLine(gc, canvasWidth, canvasHeight, x, y - dy, x, y + dy);
        camera.strokeWorldLine(gc, canvasWidth, canvasHeight, x, y + dy, x - 7, y + (forwardPositive ? 10 : -10));
        camera.strokeWorldLine(gc, canvasWidth, canvasHeight, x, y + dy, x + 7, y + (forwardPositive ? 10 : -10));
    }

    private void drawDashedWorldLine(
        Camera2D camera,
        GraphicsContext gc,
        double canvasWidth,
        double canvasHeight,
        double x1,
        double y1,
        double x2,
        double y2,
        double dashLength,
        double gapLength
    ) {
        if (Math.abs(y1 - y2) < 0.001) {
            for (double x = x1; x < x2; x += dashLength + gapLength) {
                camera.strokeWorldLine(gc, canvasWidth, canvasHeight, x, y1, Math.min(x + dashLength, x2), y2);
            }
            return;
        }

        for (double y = y1; y < y2; y += dashLength + gapLength) {
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, x1, y, x2, Math.min(y + dashLength, y2));
        }
    }

    private void drawWorldText(GraphicsContext gc, Camera2D camera, String text, double worldX, double worldY, double fontSize) {
        Vec2 screen = camera.worldToScreen(new Vec2(worldX, worldY));
        gc.save();
        gc.setFont(javafx.scene.text.Font.font("Consolas", fontSize * Math.max(0.7, camera.getZoom())));
        gc.fillText(text, screen.x, screen.y);
        gc.restore();
    }

    private TrafficSignalState getVehicleSignal(Agent agent, long tickCount) {
        return getAxis(agent) == TravelAxis.HORIZONTAL ? horizontalSignal(tickCount) : verticalSignal(tickCount);
    }

    private TrafficSignalState horizontalSignal(long tickCount) {
        long phase = tickCount % SIGNAL_CYCLE;
        if (phase < HORIZONTAL_GREEN_END) {
            return TrafficSignalState.GREEN;
        }
        if (phase < HORIZONTAL_YELLOW_END) {
            return TrafficSignalState.YELLOW;
        }
        return TrafficSignalState.RED;
    }

    private TrafficSignalState verticalSignal(long tickCount) {
        long phase = tickCount % SIGNAL_CYCLE;
        if (phase >= HORIZONTAL_PED_FLASH_END && phase < VERTICAL_GREEN_END) {
            return TrafficSignalState.GREEN;
        }
        if (phase >= VERTICAL_GREEN_END && phase < VERTICAL_YELLOW_END) {
            return TrafficSignalState.YELLOW;
        }
        return TrafficSignalState.RED;
    }

    private boolean isPedestrianWalkActive(Agent agent, long tickCount) {
        long phase = tickCount % SIGNAL_CYCLE;
        if (isVerticalPedestrian(agent)) {
            return phase >= VERTICAL_YELLOW_END && phase < VERTICAL_PED_WALK_END;
        }
        return phase >= HORIZONTAL_YELLOW_END && phase < HORIZONTAL_PED_WALK_END;
    }

    private boolean isPedestrianWalkFlashing(Agent agent, long tickCount) {
        long phase = tickCount % SIGNAL_CYCLE;
        if (isVerticalPedestrian(agent)) {
            return phase >= VERTICAL_PED_WALK_END && phase < VERTICAL_PED_FLASH_END;
        }
        return phase >= HORIZONTAL_PED_WALK_END && phase < HORIZONTAL_PED_FLASH_END;
    }

    private long getPedestrianWalkTicksRemaining(Agent agent, long tickCount) {
        long phase = tickCount % SIGNAL_CYCLE;
        if (isVerticalPedestrian(agent)) {
            return phase >= VERTICAL_YELLOW_END && phase < VERTICAL_PED_WALK_END ? VERTICAL_PED_WALK_END - phase : 0;
        }
        return phase >= HORIZONTAL_YELLOW_END && phase < HORIZONTAL_PED_WALK_END ? HORIZONTAL_PED_WALK_END - phase : 0;
    }

    private Color pedestrianSignalColor(boolean verticalCrossing, long tickCount) {
        long phase = tickCount % SIGNAL_CYCLE;
        if (verticalCrossing) {
            if (phase >= VERTICAL_YELLOW_END && phase < VERTICAL_PED_WALK_END) {
                return Color.LIMEGREEN;
            }
            if (phase >= VERTICAL_PED_WALK_END && phase < VERTICAL_PED_FLASH_END) {
                return (phase / 8) % 2 == 0 ? Color.web("#77DD77") : Color.web("#255D2A");
            }
            return Color.CRIMSON;
        }

        if (phase >= HORIZONTAL_YELLOW_END && phase < HORIZONTAL_PED_WALK_END) {
            return Color.LIMEGREEN;
        }
        if (phase >= HORIZONTAL_PED_WALK_END && phase < HORIZONTAL_PED_FLASH_END) {
            return (phase / 8) % 2 == 0 ? Color.web("#77DD77") : Color.web("#255D2A");
        }
        return Color.CRIMSON;
    }

    private boolean isNearStopWindow(Agent agent) {
        return LayoutSupport.isWithinDirectionalWindow(agent, STOP_WINDOW_MIN, STOP_WINDOW_MAX);
    }

    private boolean shouldStopForYellow(Agent agent, long tickCount) {
        return isNearStopWindow(agent)
            && !hasCommittedToIntersection(agent)
            && !canClearIntersectionBeforeRed(agent, tickCount, true);
    }

    private boolean shouldStopForRed(Agent agent) {
        return isNearStopWindow(agent) && !hasCommittedToIntersection(agent);
    }

    private boolean shouldClearLaneForEmergency(Agent agent, World world) {
        for (Agent other : world.getAgents()) {
            if (!AgentTypes.EMERGENCY_VEHICLE.equals(other.getTypeName())) {
                continue;
            }
            if (getAxis(agent) != getAxis(other) || !isSameLane(agent, other)) {
                continue;
            }

            double distanceAhead = getForwardDistance(other, agent);
            if (distanceAhead > 0 && distanceAhead < 128) {
                return true;
            }
        }
        return false;
    }

    private boolean canClearIntersectionBeforeRed(Agent agent, long tickCount, boolean allowBoost) {
        long yellowTicksRemaining = getYellowTicksRemaining(agent, tickCount);
        if (yellowTicksRemaining <= 0) {
            return false;
        }

        double speedPerTick = getTravelSpeedPerTick(agent, allowBoost);
        if (speedPerTick <= 0.01) {
            return false;
        }

        return distanceToClearIntersection(agent) / speedPerTick <= yellowTicksRemaining;
    }

    private long getYellowTicksRemaining(Agent agent, long tickCount) {
        long phase = tickCount % SIGNAL_CYCLE;
        if (getAxis(agent) == TravelAxis.HORIZONTAL) {
            return phase >= HORIZONTAL_GREEN_END && phase < HORIZONTAL_YELLOW_END ? HORIZONTAL_YELLOW_END - phase : 0;
        }
        return phase >= VERTICAL_GREEN_END && phase < VERTICAL_YELLOW_END ? VERTICAL_YELLOW_END - phase : 0;
    }

    private double getTravelSpeedPerTick(Agent agent, boolean allowBoost) {
        double multiplier;
        if (AgentTypes.BUS.equals(agent.getTypeName())) {
            multiplier = allowBoost ? 1.0 : 0.85;
        } else {
            multiplier = allowBoost ? 1.18 : 1.0;
        }
        return Math.hypot(agent.getVelocity().x, agent.getVelocity().y) * multiplier / 30.0;
    }

    private double distanceToClearIntersection(Agent agent) {
        return distanceToCenter(agent) + INTERSECTION_CLEARANCE;
    }

    private boolean shouldHoldForEmergency(Agent agent) {
        return !hasCommittedToIntersection(agent) && isNearStopWindow(agent);
    }

    private boolean hasBlockingVehicleAhead(Agent agent, World world) {
        for (Agent other : world.getAgents()) {
            if (other == agent || !isRoadVehicle(other)) {
                continue;
            }
            if (getAxis(agent) != getAxis(other) || !isSameLane(agent, other)) {
                continue;
            }

            double forwardDistance = getForwardDistance(agent, other);
            if (forwardDistance <= 0) {
                continue;
            }

            double requiredGap = requiredVehicleGap(agent, other, world);
            if (forwardDistance < requiredGap) {
                return true;
            }

            if (AgentTypes.BUS.equals(other.getTypeName()) && isBusStop(other) && forwardDistance < 74) {
                return true;
            }
        }
        return false;
    }

    private boolean isRoadVehicle(Agent agent) {
        return AgentTypes.CAR.equals(agent.getTypeName())
            || AgentTypes.BUS.equals(agent.getTypeName())
            || AgentTypes.EMERGENCY_VEHICLE.equals(agent.getTypeName());
    }

    private boolean isLargeVehicle(Agent agent) {
        return AgentTypes.BUS.equals(agent.getTypeName()) || AgentTypes.EMERGENCY_VEHICLE.equals(agent.getTypeName());
    }

    private boolean isSameLane(Agent first, Agent second) {
        if (getAxis(first) == TravelAxis.HORIZONTAL) {
            return Math.abs(first.getPosition().y - second.getPosition().y) <= 6;
        }
        return Math.abs(first.getPosition().x - second.getPosition().x) <= 6;
    }

    private boolean isVerticalPedestrian(Agent agent) {
        return getAxis(agent) == TravelAxis.VERTICAL;
    }

    private double requiredVehicleGap(Agent source, Agent target, World world) {
        if (AgentTypes.EMERGENCY_VEHICLE.equals(source.getTypeName()) && canEmergencyBypass(source, target, world)) {
            return 6;
        }
        if (AgentTypes.EMERGENCY_VEHICLE.equals(source.getTypeName()) || AgentTypes.EMERGENCY_VEHICLE.equals(target.getTypeName())) {
            return EMERGENCY_BUFFER;
        }
        return isLargeVehicle(source) || isLargeVehicle(target) ? LARGE_VEHICLE_OVERLAP_BUFFER : VEHICLE_OVERLAP_BUFFER;
    }

    private boolean canEmergencyBypass(Agent emergency, Agent blocker, World world) {
        if (!AgentTypes.EMERGENCY_VEHICLE.equals(emergency.getTypeName()) || !isSameLane(emergency, blocker)) {
            return false;
        }
        double gap = getForwardDistance(emergency, blocker);
        return gap > 0 && gap < 96 && shouldClearLaneForEmergency(blocker, world);
    }

    private double getForwardDistance(Agent source, Agent target) {
        Vec2 velocity = source.getVelocity();
        if (Math.abs(velocity.x) >= Math.abs(velocity.y)) {
            return velocity.x >= 0
                ? target.getPosition().x - source.getPosition().x
                : source.getPosition().x - target.getPosition().x;
        }
        return velocity.y >= 0
            ? target.getPosition().y - source.getPosition().y
            : source.getPosition().y - target.getPosition().y;
    }

    private boolean isNearCrosswalkEntry(Vec2 position) {
        return isNearVerticalCrosswalkEntry(position, WEST_CROSSWALK_X)
            || isNearVerticalCrosswalkEntry(position, EAST_CROSSWALK_X)
            || isNearHorizontalCrosswalkEntry(position, NORTH_CROSSWALK_Y)
            || isNearHorizontalCrosswalkEntry(position, SOUTH_CROSSWALK_Y);
    }

    private boolean isInsideCrosswalk(Vec2 position) {
        return isWithinVerticalCrosswalk(position, WEST_CROSSWALK_X)
            || isWithinVerticalCrosswalk(position, EAST_CROSSWALK_X)
            || isWithinHorizontalCrosswalk(position, NORTH_CROSSWALK_Y)
            || isWithinHorizontalCrosswalk(position, SOUTH_CROSSWALK_Y);
    }

    private boolean isNearVerticalCrosswalkEntry(Vec2 position, double crosswalkX) {
        return Math.abs(position.x - crosswalkX) <= CROSSWALK_HALF_WIDTH + 6
            && ((position.y >= -ROAD_HALF_WIDTH - CROSSWALK_ENTRY_MARGIN && position.y <= -INTERSECTION_CLEARANCE)
                || (position.y <= ROAD_HALF_WIDTH + CROSSWALK_ENTRY_MARGIN && position.y >= INTERSECTION_CLEARANCE));
    }

    private boolean isNearHorizontalCrosswalkEntry(Vec2 position, double crosswalkY) {
        return Math.abs(position.y - crosswalkY) <= CROSSWALK_HALF_WIDTH + 6
            && ((position.x >= -ROAD_HALF_WIDTH - CROSSWALK_ENTRY_MARGIN && position.x <= -INTERSECTION_CLEARANCE)
                || (position.x <= ROAD_HALF_WIDTH + CROSSWALK_ENTRY_MARGIN && position.x >= INTERSECTION_CLEARANCE));
    }

    private boolean isWithinVerticalCrosswalk(Vec2 position, double crosswalkX) {
        return Math.abs(position.x - crosswalkX) <= CROSSWALK_HALF_WIDTH + 6
            && Math.abs(position.y) <= CROSSWALK_HALF_LENGTH;
    }

    private boolean isWithinHorizontalCrosswalk(Vec2 position, double crosswalkY) {
        return Math.abs(position.y - crosswalkY) <= CROSSWALK_HALF_WIDTH + 6
            && Math.abs(position.x) <= CROSSWALK_HALF_LENGTH;
    }

    private boolean isEmergencyPreemptionActive(World world) {
        for (Agent agent : world.getAgents()) {
            if (!AgentTypes.EMERGENCY_VEHICLE.equals(agent.getTypeName())) {
                continue;
            }
            if (distanceToCenter(agent) <= EMERGENCY_PREEMPTION_DISTANCE) {
                return true;
            }
        }
        return false;
    }

    private boolean isIntersectionOccupied(World world, Agent emergencyVehicle) {
        for (Agent agent : world.getAgents()) {
            if (agent == emergencyVehicle || !isRoadVehicle(agent)) {
                continue;
            }
            Vec2 position = agent.getPosition();
            if (Math.abs(position.x) <= INTERSECTION_CLEARANCE && Math.abs(position.y) <= INTERSECTION_CLEARANCE) {
                return true;
            }
        }
        return false;
    }

    private double distanceToCenter(Agent agent) {
        Vec2 position = agent.getPosition();
        if (getAxis(agent) == TravelAxis.HORIZONTAL) {
            return Math.abs(position.x);
        }
        return Math.abs(position.y);
    }

    private boolean hasCommittedToIntersection(Agent agent) {
        return distanceToCenter(agent) < STOP_LINE_DISTANCE;
    }


    private TravelAxis getAxis(Agent agent) {
        return LayoutSupport.getAxis(agent);
    }
}
