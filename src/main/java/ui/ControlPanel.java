package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Right-side control cluster. This class owns widgets only;
 * behavior is wired by the controller.
 */
public class ControlPanel extends VBox {
    private final Button startPauseButton = new Button("Start");
    private final Button stepButton = new Button("Step");
    private final Slider speedSlider = new Slider(0.25, 4.0, 1.0);

    private final CheckBox carsToggle = new CheckBox("Cars");
    private final CheckBox busesToggle = new CheckBox("Buses");
    private final CheckBox emergencyVehiclesToggle = new CheckBox("Emergency Vehicles");
    private final CheckBox bikesToggle = new CheckBox("Bikes");
    private final CheckBox pedestriansToggle = new CheckBox("Pedestrians");

    private final Button addRandomAgentsButton = new Button("Add Random Agents");
    private final Button spawnEmergencyButton = new Button("Spawn Emergency");
    private final Button clearAgentsButton = new Button("Clear Agents");

    private final ComboBox<String> layoutSelector = new ComboBox<>();

    private final Button saveLayoutButton = new Button("Save Layout");
    private final Button loadLayoutButton = new Button("Load Layout");

    private final TextArea statsArea = new TextArea();
    private final TextArea selectedAgentArea = new TextArea();

    public ControlPanel() {
        setPadding(new Insets(14));
        setSpacing(9);
        setPrefWidth(300);
        setStyle("-fx-background-color: linear-gradient(to bottom, #F8FBFF, #E6EDF5); -fx-border-color: #C7D2E0; -fx-border-width: 0 0 0 1;");

        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(0.75);
        speedSlider.setMinorTickCount(2);
        speedSlider.setBlockIncrement(0.25);

        carsToggle.setSelected(true);
        busesToggle.setSelected(true);
        emergencyVehiclesToggle.setSelected(true);
        bikesToggle.setSelected(true);
        pedestriansToggle.setSelected(true);

        statsArea.setEditable(false);
        statsArea.setWrapText(true);
        statsArea.setPrefRowCount(8);

        selectedAgentArea.setEditable(false);
        selectedAgentArea.setWrapText(true);
        selectedAgentArea.setPrefRowCount(4);
        selectedAgentArea.setPromptText("Select an agent in the viewport.");

        getChildren().addAll(
            new Label("OmniFlow Controls"),
            startPauseButton,
            stepButton,
            new Label("Simulation Speed"),
            speedSlider,
            new Label("Active Agent Types"),
            carsToggle,
            busesToggle,
            emergencyVehiclesToggle,
            bikesToggle,
            pedestriansToggle,
            addRandomAgentsButton,
            spawnEmergencyButton,
            clearAgentsButton,
            new Label("Layout"),
            layoutSelector,
            saveLayoutButton,
            loadLayoutButton,
            new Label("Simulation Stats"),
            statsArea,
            new Label("Selected Agent"),
            selectedAgentArea
        );

        VBox.setVgrow(statsArea, Priority.ALWAYS);
        VBox.setVgrow(selectedAgentArea, Priority.ALWAYS);
    }

    public Button getStartPauseButton() {
        return startPauseButton;
    }

    public Button getStepButton() {
        return stepButton;
    }

    public Slider getSpeedSlider() {
        return speedSlider;
    }

    public CheckBox getCarsToggle() {
        return carsToggle;
    }

    public CheckBox getBusesToggle() {
        return busesToggle;
    }

    public CheckBox getEmergencyVehiclesToggle() {
        return emergencyVehiclesToggle;
    }

    public CheckBox getBikesToggle() {
        return bikesToggle;
    }

    public CheckBox getPedestriansToggle() {
        return pedestriansToggle;
    }

    public Button getAddRandomAgentsButton() {
        return addRandomAgentsButton;
    }

    public Button getClearAgentsButton() {
        return clearAgentsButton;
    }

    public Button getSpawnEmergencyButton() {
        return spawnEmergencyButton;
    }

    public ComboBox<String> getLayoutSelector() {
        return layoutSelector;
    }

    public Button getSaveLayoutButton() {
        return saveLayoutButton;
    }

    public Button getLoadLayoutButton() {
        return loadLayoutButton;
    }

    public TextArea getStatsArea() {
        return statsArea;
    }

    public TextArea getSelectedAgentArea() {
        return selectedAgentArea;
    }
}
