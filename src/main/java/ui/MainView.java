package ui;

import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

/**
 * Composes the overall scene graph:
 * center = viewport canvas, right = controls.
 */
public class MainView {
    private final BorderPane root = new BorderPane();
    private final ViewportCanvas viewportCanvas = new ViewportCanvas();
    private final ControlPanel controlPanel = new ControlPanel();

    public MainView() {
        root.setStyle("-fx-background-color: #DCE5EF;");
        root.setCenter(viewportCanvas);
        root.setRight(controlPanel);
    }

    public Parent getRoot() {
        return root;
    }

    public ViewportCanvas getViewportCanvas() {
        return viewportCanvas;
    }

    public ControlPanel getControlPanel() {
        return controlPanel;
    }
}
