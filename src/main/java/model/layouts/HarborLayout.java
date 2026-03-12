package model.layouts;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.AgentFactory;
import model.MapLayout;
import sim.Camera2D;
import sim.World;
import util.Vec2;

public class HarborLayout implements MapLayout {
    @Override
    public String getName() {
        return "Harbor";
    }

    @Override
    public void drawStatic(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight) {
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -320, -240, 640, 480, Color.web("#8FC0E8"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -320, -240, 640, 140, Color.web("#BBAA8E"));

        gc.setStroke(Color.web("#644B30"));
        gc.setLineWidth(3);
        for (int x = -280; x <= 280; x += 80) {
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, x, -100, x, -20);
        }

        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -300, 20, 600, 10, Color.web("#DDEEFF"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -300, 90, 600, 10, Color.web("#DDEEFF"));
    }

    @Override
    public void seed(World world) {
        world.clearAgents();
        world.addAgent(AgentFactory.create("Boat", new Vec2(-220, 120), new Vec2(35, 0)));
        world.addAgent(AgentFactory.create("Boat", new Vec2(200, 180), new Vec2(-28, 0)));
        world.addAgent(AgentFactory.create("Pedestrian", new Vec2(-140, -140), new Vec2(7, 5)));
        world.addAgent(AgentFactory.create("Car", new Vec2(-180, -130), new Vec2(45, 0)));
    }
}
