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

public class SchoolZoneLayout implements MapLayout {
    private static final double MIN_X = -330;
    private static final double MAX_X = 330;
    private static final double MIN_Y = -220;
    private static final double MAX_Y = 220;

    @Override
    public String getName() {
        return "School Zone";
    }

    @Override
    public void draw(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight, long tickCount) {
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, MIN_X, MIN_Y, MAX_X - MIN_X, MAX_Y - MIN_Y, Color.web("#E6E2C8"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -330, -55, 660, 110, Color.web("#383B43"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -65, -220, 130, 440, Color.web("#383B43"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -280, -190, 180, 110, Color.web("#C97A5E"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -90, -185, 180, 105, Color.web("#F3DFA2"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, 120, -185, 150, 105, Color.web("#96C0B7"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -300, 95, 560, 90, Color.web("#A9D18E"));

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3.2);
        for (int i = -75; i <= 75; i += 10) {
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, i, -82, i, -50);
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, i, 50, i, 82);
        }

        gc.setStroke(Color.web("#F1D26A"));
        gc.setLineWidth(2.2);
        for (int x = -310; x <= 290; x += 32) {
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, x, 0, x + 16, 0);
        }
        for (int y = -200; y <= 180; y += 32) {
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, 0, y, 0, y + 16);
        }

        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -250, 58, 75, 16, Color.web("#FFD166"));
        drawSignal(gc, camera, canvasWidth, canvasHeight, tickCount);
    }

    @Override
    public void seed(World world) {
        world.clearAgents();
        world.addAgent(AgentFactory.defaultFactory().create("Car", new Vec2(-280, -16), new Vec2(48, 0)));
        world.addAgent(AgentFactory.defaultFactory().create("Car", new Vec2(280, 16), new Vec2(-48, 0)));
        world.addAgent(AgentFactory.defaultFactory().create("Bus", new Vec2(-320, 32), new Vec2(42, 0)));
        world.addAgent(AgentFactory.defaultFactory().create("Bike", new Vec2(-310, -40), new Vec2(28, 0)));
        world.addAgent(AgentFactory.defaultFactory().create("Pedestrian", new Vec2(-42, -170), new Vec2(0, 22)));
        world.addAgent(AgentFactory.defaultFactory().create("Pedestrian", new Vec2(42, 170), new Vec2(0, -22)));
        world.addAgent(AgentFactory.defaultFactory().create("Pedestrian", new Vec2(-120, 140), new Vec2(24, 0)));
    }

    @Override
    public boolean shouldVehicleYield(Agent agent, long tickCount) {
        return !vehicleSignal(tickCount).allowsMovement() && isNearCenter(agent, 92, 32);
    }

    @Override
    public boolean shouldPedestrianYield(Agent agent, long tickCount) {
        return !pedestrianSignal(tickCount).allowsMovement() && isNearCenter(agent, 105, 55);
    }

    @Override
    public boolean isBusStop(Agent agent) {
        Vec2 position = agent.getPosition();
        return Math.abs(position.y - 32) < 2 && Math.abs(position.x + 215) < 14;
    }

    @Override
    public void keepAgentInBounds(Agent agent) {
        recycleAlongRoute(agent, MIN_X, MAX_X, MIN_Y, MAX_Y);
    }

    @Override
    public Vec2 getSuggestedCameraOffset() {
        return new Vec2(-230, -155);
    }

    private void drawSignal(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight, long tickCount) {
        TrafficSignalState vehicle = vehicleSignal(tickCount);
        TrafficSignalState pedestrian = pedestrianSignal(tickCount);
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, 84, -102, 22, 52, Color.BLACK);
        camera.fillWorldCircle(gc, canvasWidth, canvasHeight, 95, -92, 4, vehicle == TrafficSignalState.RED ? Color.RED : Color.web("#5E1717"));
        camera.fillWorldCircle(gc, canvasWidth, canvasHeight, 95, -77, 4, vehicle == TrafficSignalState.YELLOW ? Color.GOLD : Color.web("#5D4D0A"));
        camera.fillWorldCircle(gc, canvasWidth, canvasHeight, 95, -62, 4, vehicle == TrafficSignalState.GREEN ? Color.LIMEGREEN : Color.web("#17411E"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -102, -14, 18, 28, pedestrian.allowsMovement() ? Color.LIMEGREEN : Color.CRIMSON);
    }

    private TrafficSignalState vehicleSignal(long tickCount) {
        long phase = tickCount % 160;
        if (phase < 45) {
            return TrafficSignalState.GREEN;
        }
        if (phase < 60) {
            return TrafficSignalState.YELLOW;
        }
        return TrafficSignalState.RED;
    }

    private TrafficSignalState pedestrianSignal(long tickCount) {
        long phase = tickCount % 160;
        if (phase >= 60 && phase < 105) {
            return TrafficSignalState.GREEN;
        }
        return TrafficSignalState.RED;
    }

    private boolean isNearCenter(Agent agent, double roadStopDistance, double sidewalkDistance) {
        Vec2 position = agent.getPosition();
        if (Math.abs(agent.getVelocity().x) >= Math.abs(agent.getVelocity().y)) {
            return Math.abs(position.x) < roadStopDistance && Math.abs(position.x) > sidewalkDistance;
        }
        return Math.abs(position.y) < roadStopDistance && Math.abs(position.y) > sidewalkDistance;
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
