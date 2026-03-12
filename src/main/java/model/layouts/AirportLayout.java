package model.layouts;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.AgentFactory;
import model.MapLayout;
import sim.Camera2D;
import sim.World;
import util.Vec2;

public class AirportLayout implements MapLayout {
    @Override
    public String getName() {
        return "Airport Corridor";
    }

    @Override
    public void drawStatic(GraphicsContext gc, Camera2D camera, double canvasWidth, double canvasHeight) {
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -320, -240, 640, 480, Color.web("#9FB0BF"));
        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -320, -190, 640, 70, Color.web("#2C3B46"));

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2.5);
        for (int x = -300; x <= 300; x += 35) {
            camera.strokeWorldLine(gc, canvasWidth, canvasHeight, x, -155, x + 15, -155);
        }

        camera.fillWorldRect(gc, canvasWidth, canvasHeight, -300, 20, 600, 90, Color.web("#D7DEE6"));
        gc.setStroke(Color.web("#6B747D"));
        gc.setLineWidth(2);
        camera.strokeWorldLine(gc, canvasWidth, canvasHeight, -280, 65, 280, 65);
    }

    @Override
    public void seed(World world) {
        world.clearAgents();
        world.addAgent(AgentFactory.create("Aircraft", new Vec2(-260, -155), new Vec2(95, 0)));
        world.addAgent(AgentFactory.create("Aircraft", new Vec2(250, -140), new Vec2(-85, 0)));
        world.addAgent(AgentFactory.create("Bus", new Vec2(-180, 70), new Vec2(36, 0)));
        world.addAgent(AgentFactory.create("Pedestrian", new Vec2(100, 80), new Vec2(-9, 6)));
    }
}
