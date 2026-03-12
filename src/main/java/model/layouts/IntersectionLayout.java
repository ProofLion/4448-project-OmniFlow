package model.layouts;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.AgentFactory;
import model.MapLayout;
import sim.Camera2D;
import sim.World;
import util.Vec2;

public class IntersectionLayout implements MapLayout {
    @Override
    public String getName() {
        return "Intersection";
    }

    @Override
    public void drawStatic(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight) {
        // Major roads crossing in the center.
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -220, -35, 440, 70, Color.web("#2F3137"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -35, -220, 70, 440, Color.web("#2F3137"));

        // Lane markers.
        gc.setStroke(Color.web("#E8E4A8"));
        gc.setLineWidth(2);
        for (int i = -200; i <= 200; i += 30) {
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, i, -2, i + 14, -2);
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, i, 2, i + 14, 2);
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, -2, i, -2, i + 14);
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, 2, i, 2, i + 14);
        }

        // Crosswalks.
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        for (int i = -25; i <= 25; i += 10) {
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, i, -48, i, -34);
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, i, 34, i, 48);
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, -48, i, -34, i);
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, 34, i, 48, i);
        }

        // Visual-only traffic light indicator.
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, 60, -65, 20, 60, Color.BLACK);
        camera.fillWorldCircle(gc, canvasWidth, canvasHeight, 70, -52, 4, Color.RED);
        camera.fillWorldCircle(gc, canvasWidth, canvasHeight, 70, -35, 4, Color.GOLD);
        camera.fillWorldCircle(gc, canvasWidth, canvasHeight, 70, -18, 4, Color.LIMEGREEN);
    }

    @Override
    public void seed(World world) {
        world.clearAgents();
        world.addAgent(AgentFactory.create("Car", new Vec2(-160, -12), new Vec2(58, 0)));
        world.addAgent(AgentFactory.create("Car", new Vec2(160, 12), new Vec2(-56, 0)));
        world.addAgent(AgentFactory.create("Bus", new Vec2(-100, 16), new Vec2(40, 0)));
        world.addAgent(AgentFactory.create("Emergency", new Vec2(12, -160), new Vec2(0, 78)));
        world.addAgent(AgentFactory.create("Pedestrian", new Vec2(-80, -80), new Vec2(10, 8)));
        world.addAgent(AgentFactory.create("Pedestrian", new Vec2(90, 75), new Vec2(-8, -11)));
    }
}
