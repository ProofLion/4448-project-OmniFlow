package model.layouts;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.Agent;
import model.AgentFactory;
import model.MapLayout;
import model.TrafficSignalState;
import sim.Camera2D;
import sim.World;
import util.Vec2;

public class EmergencyCorridorLayout implements MapLayout {
    private static final double MIN_X = -360;
    private static final double MAX_X = 360;
    private static final double MIN_Y = -220;
    private static final double MAX_Y = 220;

    @Override
    public String getName() {
        return "Emergency Corridor";
    }

    @Override
    public void draw(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight, long tickCount) {
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, MIN_X, MIN_Y, MAX_X - MIN_X, MAX_Y - MIN_Y, Color.web("#D5DEE8"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -360, -90, 720, 180, Color.web("#2F333A"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -55, -220, 110, 440, Color.web("#3A3E45"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -310, -180, 140, 75, Color.web("#C9B79C"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, 180, -180, 120, 75, Color.web("#9CC5A1"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -320, 110, 620, 75, Color.web("#BFD7EA"));

        gc.setStroke(Color.web("#F0E08C"));
        gc.setLineWidth(2.5);
        for (int x = -340; x <= 320; x += 28) {
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, x, -18, x + 14, -18);
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, x, 18, x + 14, 18);
        }
        for (int y = -200; y <= 180; y += 32) {
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, -2, y, -2, y + 16);
        }

        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -250, 34, 70, 18, Color.web("#A1D99B"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, 180, -52, 70, 18, Color.web("#A1D99B"));
        drawSignals(gc, camera, canvasWidth, canvasHeight, tickCount);
    }

    @Override
    public void seed(World world) {
        world.clearAgents();
        world.addAgent(AgentFactory.defaultFactory().create("EmergencyVehicle", new Vec2(-330, -18), new Vec2(96, 0)));
        world.addAgent(AgentFactory.defaultFactory().create("Car", new Vec2(-280, 18), new Vec2(68, 0)));
        world.addAgent(AgentFactory.defaultFactory().create("Car", new Vec2(310, -18), new Vec2(-64, 0)));
        world.addAgent(AgentFactory.defaultFactory().create("Bus", new Vec2(-340, 34), new Vec2(52, 0)));
        world.addAgent(AgentFactory.defaultFactory().create("Bike", new Vec2(320, -52), new Vec2(-30, 0)));
        world.addAgent(AgentFactory.defaultFactory().create("Pedestrian", new Vec2(28, -180), new Vec2(0, 24)));
    }

    @Override
    public boolean shouldVehicleYield(Agent agent, long tickCount) {
        return !sideStreetSignal(tickCount).allowsMovement() && isNearVerticalCrossing(agent);
    }

    @Override
    public boolean shouldPedestrianYield(Agent agent, long tickCount) {
        return !pedestrianSignal(tickCount).allowsMovement() && Math.abs(agent.getPosition().y) < 110 && Math.abs(agent.getPosition().y) > 58;
    }

    @Override
    public boolean isBusStop(Agent agent) {
        Vec2 position = agent.getPosition();
        return (Math.abs(position.y - 34) < 2 && Math.abs(position.x + 215) < 14)
            || (Math.abs(position.y + 52) < 2 && Math.abs(position.x - 215) < 14);
    }

    @Override
    public void keepAgentInBounds(Agent agent) {
        recycleAlongRoute(agent, MIN_X, MAX_X, MIN_Y, MAX_Y);
    }

    @Override
    public Vec2 getSuggestedCameraOffset() {
        return new Vec2(-245, -155);
    }

    private void drawSignals(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight, long tickCount) {
        TrafficSignalState main = mainRoadSignal(tickCount);
        TrafficSignalState side = sideStreetSignal(tickCount);
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, 72, -108, 20, 52, Color.BLACK);
        camera.fillWorldCircle(gc, canvasWidth, canvasHeight, 82, -97, 4, side == TrafficSignalState.RED ? Color.RED : Color.web("#5B1717"));
        camera.fillWorldCircle(gc, canvasWidth, canvasHeight, 82, -81, 4, side == TrafficSignalState.YELLOW ? Color.GOLD : Color.web("#5B4A08"));
        camera.fillWorldCircle(gc, canvasWidth, canvasHeight, 82, -65, 4, side == TrafficSignalState.GREEN ? Color.LIMEGREEN : Color.web("#163E19"));

        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -94, 58, 20, 52, Color.BLACK);
        camera.fillWorldCircle(gc, canvasWidth, canvasHeight, -84, 69, 4, main == TrafficSignalState.RED ? Color.RED : Color.web("#5B1717"));
        camera.fillWorldCircle(gc, canvasWidth, canvasHeight, -84, 85, 4, main == TrafficSignalState.YELLOW ? Color.GOLD : Color.web("#5B4A08"));
        camera.fillWorldCircle(gc, canvasWidth, canvasHeight, -84, 101, 4, main == TrafficSignalState.GREEN ? Color.LIMEGREEN : Color.web("#163E19"));
    }

    private TrafficSignalState mainRoadSignal(long tickCount) {
        long phase = tickCount % 180;
        if (phase < 115) {
            return TrafficSignalState.GREEN;
        }
        if (phase < 125) {
            return TrafficSignalState.YELLOW;
        }
        return TrafficSignalState.RED;
    }

    private TrafficSignalState sideStreetSignal(long tickCount) {
        long phase = tickCount % 180;
        if (phase >= 135 && phase < 170) {
            return TrafficSignalState.GREEN;
        }
        if (phase >= 170) {
            return TrafficSignalState.YELLOW;
        }
        return TrafficSignalState.RED;
    }

    private TrafficSignalState pedestrianSignal(long tickCount) {
        long phase = tickCount % 180;
        if (phase >= 125 && phase < 135) {
            return TrafficSignalState.GREEN;
        }
        return TrafficSignalState.RED;
    }

    private boolean isNearVerticalCrossing(Agent agent) {
        if (Math.abs(agent.getVelocity().x) >= Math.abs(agent.getVelocity().y)) {
            return false;
        }
        double y = agent.getPosition().y;
        double vy = agent.getVelocity().y;
        return (vy > 0 && y >= -118 && y <= -42) || (vy < 0 && y <= 118 && y >= 42);
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
