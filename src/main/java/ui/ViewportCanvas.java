package ui;

import java.util.Set;
import java.util.function.Consumer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import model.Agent;
import sim.Camera2D;
import sim.SelectionModel;
import sim.World;
import util.Vec2;

/**
 * Canvas wrapper that owns mouse interactions (pan/zoom/select) and drawing.
 * Controller calls renderFrame() from AnimationTimer.
 */
public class ViewportCanvas extends StackPane {
    private final Canvas canvas = new Canvas(900, 700);

    private double lastMouseX;
    private double lastMouseY;
    private boolean dragging;
    private Consumer<Agent> onAgentClicked;

    public ViewportCanvas() {
        getChildren().add(canvas);
        setStyle("-fx-background-color: #D7DBE2;");

        // Keep Canvas size matched to parent region so we can use layout panes naturally.
        widthProperty().addListener((obs, oldV, newV) -> canvas.setWidth(newV.doubleValue()));
        heightProperty().addListener((obs, oldV, newV) -> canvas.setHeight(newV.doubleValue()));

        canvas.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                lastMouseX = event.getX();
                lastMouseY = event.getY();
                dragging = false;
            }
        });

        canvas.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                double dx = event.getX() - lastMouseX;
                double dy = event.getY() - lastMouseY;
                if (Math.abs(dx) > 0.5 || Math.abs(dy) > 0.5) {
                    dragging = true;
                }
                lastMouseX = event.getX();
                lastMouseY = event.getY();
            }
        });

        addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaY() == 0) {
                return;
            }
            event.consume();
        });
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void bindInteractions(Camera2D camera, World world, SelectionModel selectionModel) {
        canvas.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                double dx = event.getX() - lastMouseX;
                double dy = event.getY() - lastMouseY;
                if (Math.abs(dx) > 0.5 || Math.abs(dy) > 0.5) {
                    dragging = true;
                }
                camera.panByScreenDelta(dx, dy);
                lastMouseX = event.getX();
                lastMouseY = event.getY();
            }
        });

        canvas.setOnScroll(event -> {
            double zoomFactor = event.getDeltaY() > 0 ? 1.1 : 0.9;
            camera.zoomAroundScreenPoint(zoomFactor, event.getX(), event.getY());
        });

        canvas.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.PRIMARY || dragging) {
                return;
            }
            Vec2 worldPoint = camera.screenToWorld(event.getX(), event.getY());
            Agent picked = world.pickAgentAt(worldPoint);
            selectionModel.setSelectedAgent(picked);
            if (onAgentClicked != null) {
                onAgentClicked.accept(picked);
            }
        });
    }

    public void setOnAgentClicked(Consumer<Agent> onAgentClicked) {
        this.onAgentClicked = onAgentClicked;
    }

    public void renderFrame(
        World world,
        Camera2D camera,
        SelectionModel selectionModel,
        Set<String> visibleTypes,
        long tickCount,
        double fps
    ) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        gc.setFill(Color.web("#C7CCD5"));
        gc.fillRect(0, 0, w, h);

        drawGrid(gc, camera, w, h);
        world.getLayout().drawStatic(gc, camera, w, h);

        gc.setFont(Font.font("Consolas", 10));
        for (Agent agent : world.getAgents()) {
            if (!visibleTypes.contains(agent.getTypeName())) {
                continue;
            }

            Vec2 screen = camera.worldToScreen(agent.getPosition());
            double radius = agent.getRenderRadius() * camera.getZoom();

            gc.setFill(agent.getColor());
            gc.fillOval(screen.x - radius, screen.y - radius, radius * 2, radius * 2);

            gc.setStroke(Color.color(0, 0, 0, 0.35));
            gc.strokeOval(screen.x - radius, screen.y - radius, radius * 2, radius * 2);

            gc.setFill(Color.BLACK);
            gc.fillText(agent.getShortLabel(), screen.x + radius + 2, screen.y - radius - 2);

            if (selectionModel.getSelectedAgent() == agent) {
                gc.setStroke(Color.GOLD);
                gc.setLineWidth(2.4);
                gc.strokeOval(screen.x - (radius + 4), screen.y - (radius + 4), (radius + 4) * 2, (radius + 4) * 2);
                gc.setLineWidth(1.0);
            }
        }

        HudOverlay.draw(gc, fps, tickCount);
    }

    private void drawGrid(GraphicsContext gc, Camera2D camera, double w, double h) {
        gc.setStroke(Color.color(0, 0, 0, 0.12));
        gc.setLineWidth(1);

        // Convert screen corners to world so we draw only visible grid lines.
        Vec2 worldTopLeft = camera.screenToWorld(0, 0);
        Vec2 worldBottomRight = camera.screenToWorld(w, h);

        int startX = (int) Math.floor(worldTopLeft.x / 20.0) * 20;
        int endX = (int) Math.ceil(worldBottomRight.x / 20.0) * 20;
        int startY = (int) Math.floor(worldTopLeft.y / 20.0) * 20;
        int endY = (int) Math.ceil(worldBottomRight.y / 20.0) * 20;

        for (int x = startX; x <= endX; x += 20) {
            Vec2 a = camera.worldToScreen(new Vec2(x, worldTopLeft.y));
            Vec2 b = camera.worldToScreen(new Vec2(x, worldBottomRight.y));
            gc.strokeLine(a.x, a.y, b.x, b.y);
        }

        for (int y = startY; y <= endY; y += 20) {
            Vec2 a = camera.worldToScreen(new Vec2(worldTopLeft.x, y));
            Vec2 b = camera.worldToScreen(new Vec2(worldBottomRight.x, y));
            gc.strokeLine(a.x, a.y, b.x, b.y);
        }
    }
}
