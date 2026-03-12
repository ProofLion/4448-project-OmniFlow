package app;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX entry point.
 */
public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        OmniFlowController controller = new OmniFlowController();
        controller.init(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
