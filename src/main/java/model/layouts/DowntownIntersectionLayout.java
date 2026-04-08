package model.layouts;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Agent;
import model.AgentFactory;
import model.MapLayout;
import model.TrafficSignalState;
import model.TravelAxis;
import sim.Camera2D;
import sim.World;
import util.Vec2;

public class DowntownIntersectionLayout implements MapLayout {
    private static final double MIN_X = -320;
    private static final double MAX_X = 320;
    private static final double MIN_Y = -220;
    private static final double MAX_Y = 220;

    @Override
    public String getName() {
        return "Downtown Intersection";
    }

    @Override
    public void draw(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight, long tickCount) {
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, MIN_X, MIN_Y, MAX_X - MIN_X, MAX_Y - MIN_Y, Color.web("#DDE5D1"));
        drawBuildings(gc, camera, canvasWidth, canvasHeight);
        drawRoads(gc, camera, canvasWidth, canvasHeight);
        drawSignals(gc, camera, canvasWidth, canvasHeight, tickCount);
    }

    @Override
    public void seed(World world) {
        world.clearAgents();
        world.addAgent(AgentFactory.defaultFactory().create("Car", new Vec2(-250, -18), new Vec2(72, 0)));
        world.addAgent(AgentFactory.defaultFactory().create("Car", new Vec2(250, 18), new Vec2(-68, 0)));
        world.addAgent(AgentFactory.defaultFactory().create("Bus", new Vec2(-290, 42), new Vec2(54, 0)));
        world.addAgent(AgentFactory.defaultFactory().create("EmergencyVehicle", new Vec2(18, -250), new Vec2(0, 84)));
        world.addAgent(AgentFactory.defaultFactory().create("Bike", new Vec2(-300, -52), new Vec2(36, 0)));
        world.addAgent(AgentFactory.defaultFactory().create("Pedestrian", new Vec2(-64, -160), new Vec2(0, 30)));
        world.addAgent(AgentFactory.defaultFactory().create("Pedestrian", new Vec2(64, 160), new Vec2(0, -30)));
    }

    @Override
    public boolean shouldVehicleYield(Agent agent, long tickCount) {
        if (getAxis(agent) == TravelAxis.HORIZONTAL) {
            return !horizontalSignal(tickCount).allowsMovement() && isNearHorizontalStopLine(agent);
        }
        return !verticalSignal(tickCount).allowsMovement() && isNearVerticalStopLine(agent);
    }

    @Override
    public boolean shouldPedestrianYield(Agent agent, long tickCount) {
        return !pedestrianSignal(tickCount).allowsMovement() && isNearCrosswalkEntry(agent.getPosition());
    }

    @Override
    public boolean isBusStop(Agent agent) {
        Vec2 position = agent.getPosition();
        return Math.abs(position.y - 42) < 2 && (Math.abs(position.x + 150) < 12 || Math.abs(position.x - 150) < 12);
    }

    @Override
    public void keepAgentInBounds(Agent agent) {
        recycleAlongRoute(agent, MIN_X, MAX_X, MIN_Y, MAX_Y);
    }

    @Override
    public Vec2 getSuggestedCameraOffset() {
        return new Vec2(-230, -155);
    }

    private void drawBuildings(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight) {
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -300, -200, 110, 90, Color.web("#B7C6D9"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, 190, -200, 100, 90, Color.web("#CFBFA8"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -300, 110, 110, 90, Color.web("#CDB7D8"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, 190, 110, 100, 90, Color.web("#B9D8C2"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -80, -190, 160, 50, Color.web("#8EB69B"));
    }

    private void drawRoads(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight) {
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -320, -70, 640, 140, Color.web("#31343B"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -70, -220, 140, 440, Color.web("#31343B"));

        gc.setStroke(Color.web("#F4E28C"));
        gc.setLineWidth(2.2);
        for (int x = -300; x <= 280; x += 34) {
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, x, -2, x + 16, -2);
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, x, 2, x + 16, 2);
        }
        for (int y = -200; y <= 180; y += 34) {
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, -2, y, -2, y + 16);
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, 2, y, 2, y + 16);
        }

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        for (int i = -48; i <= 48; i += 12) {
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, i, -88, i, -56);
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, i, 56, i, 88);
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, -88, i, -56, i);
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, 56, i, 88, i);
        }

        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -210, 32, 55, 20, Color.web("#8FD3A8"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, 155, 32, 55, 20, Color.web("#8FD3A8"));
    }

    private void drawSignals(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight, long tickCount) {
        drawSignalBox(gc, camera, canvasWidth, canvasHeight, -95, -95, verticalSignal(tickCount));
        drawSignalBox(gc, camera, canvasWidth, canvasHeight, 75, 75, verticalSignal(tickCount));
        drawSignalBox(gc, camera, canvasWidth, canvasHeight, 75, -95, horizontalSignal(tickCount));
        drawSignalBox(gc, camera, canvasWidth, canvasHeight, -95, 75, horizontalSignal(tickCount));

        Color walkColor = pedestrianSignal(tickCount).allowsMovement() ? Color.LIMEGREEN : Color.CRIMSON;
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -112, -12, 18, 24, walkColor);
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, 94, -12, 18, 24, walkColor);
    }

    private void drawSignalBox(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight,
                               double x, double y, TrafficSignalState state) {
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, x, y, 20, 50, Color.BLACK);
        camera.fillWorldCircle(gc, canvasWidth, canvasHeight, x + 10, y + 10, 4, state == TrafficSignalState.RED ? Color.RED : Color.web("#551111"));
        camera.fillWorldCircle(gc, canvasWidth, canvasHeight, x + 10, y + 25, 4, state == TrafficSignalState.YELLOW ? Color.GOLD : Color.web("#5A4A00"));
        camera.fillWorldCircle(gc, canvasWidth, canvasHeight, x + 10, y + 40, 4, state == TrafficSignalState.GREEN ? Color.LIMEGREEN : Color.web("#143E1C"));
    }

    private TrafficSignalState horizontalSignal(long tickCount) {
        long phase = tickCount % 200;
        if (phase < 70) {
            return TrafficSignalState.GREEN;
        }
        if (phase < 80) {
            return TrafficSignalState.YELLOW;
        }
        return TrafficSignalState.RED;
    }

    private TrafficSignalState verticalSignal(long tickCount) {
        long phase = tickCount % 200;
        if (phase >= 100 && phase < 170) {
            return TrafficSignalState.GREEN;
        }
        if (phase >= 170 && phase < 180) {
            return TrafficSignalState.YELLOW;
        }
        return TrafficSignalState.RED;
    }

    private TrafficSignalState pedestrianSignal(long tickCount) {
        long phase = tickCount % 200;
        if ((phase >= 80 && phase < 100) || phase >= 180) {
            return TrafficSignalState.GREEN;
        }
        return TrafficSignalState.RED;
    }

    private boolean isNearHorizontalStopLine(Agent agent) {
        Vec2 position = agent.getPosition();
        Vec2 velocity = agent.getVelocity();
        return (velocity.x > 0 && position.x >= -120 && position.x <= -38)
            || (velocity.x < 0 && position.x <= 120 && position.x >= 38);
    }

    private boolean isNearVerticalStopLine(Agent agent) {
        Vec2 position = agent.getPosition();
        Vec2 velocity = agent.getVelocity();
        return (velocity.y > 0 && position.y >= -120 && position.y <= -38)
            || (velocity.y < 0 && position.y <= 120 && position.y >= 38);
    }

    private boolean isNearCrosswalkEntry(Vec2 position) {
        return (Math.abs(position.x) < 70 && (position.y >= -130 && position.y <= -56 || position.y <= 130 && position.y >= 56))
            || (Math.abs(position.y) < 70 && (position.x >= -130 && position.x <= -56 || position.x <= 130 && position.x >= 56));
    }

    private TravelAxis getAxis(Agent agent) {
        return Math.abs(agent.getVelocity().x) >= Math.abs(agent.getVelocity().y) ? TravelAxis.HORIZONTAL : TravelAxis.VERTICAL;
    }

    private void recycleAlongRoute(Agent agent, double minX, double maxX, double minY, double maxY) {
        Vec2 position = agent.getPosition();
        Vec2 velocity = agent.getVelocity();
        if (Math.abs(velocity.x) >= Math.abs(velocity.y)) {
            if (velocity.x >= 0 && position.x > maxX) {
                agent.setPosition(new Vec2(minX, position.y));
            } else if (velocity.x < 0 && position.x < minX) {
                agent.setPosition(new Vec2(maxX, position.y));
            }
        } else if (velocity.y >= 0 && position.y > maxY) {
            agent.setPosition(new Vec2(position.x, minY));
        } else if (velocity.y < 0 && position.y < minY) {
            agent.setPosition(new Vec2(position.x, maxY));
        }
    }
}
