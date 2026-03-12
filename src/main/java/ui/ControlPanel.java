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
    private final CheckBox emergencyToggle = new CheckBox("Emergency Vehicles");
    private final CheckBox pedestriansToggle = new CheckBox("Pedestrians");
    private final CheckBox boatsToggle = new CheckBox("Boats");
    private final CheckBox aircraftToggle = new CheckBox("Aircraft");

    private final Button addRandomAgentsButton = new Button("Add Random Agents");
    private final Button clearAgentsButton = new Button("Clear Agents");

    private final ComboBox<String> layoutSelector = new ComboBox<>();

    private final Button saveLayoutButton = new Button("Save Layout");
    private final Button loadLayoutButton = new Button("Load Layout");

    private final TextArea statsArea = new TextArea();
    private final TextArea selectedAgentArea = new TextArea();

    public ControlPanel() {
        setPadding(new Insets(12));
        setSpacing(8);
        setPrefWidth(300);
        setStyle("-fx-background-color: #F4F5F8;");

        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(0.75);
        speedSlider.setMinorTickCount(2);
        speedSlider.setBlockIncrement(0.25);

        carsToggle.setSelected(true);
        busesToggle.setSelected(true);
        emergencyToggle.setSelected(true);
        pedestriansToggle.setSelected(true);
        boatsToggle.setSelected(true);
        aircraftToggle.setSelected(true);

        statsArea.setEditable(false);
        statsArea.setWrapText(true);
        statsArea.setPrefRowCount(7);

        selectedAgentArea.setEditable(false);
        selectedAgentArea.setWrapText(true);
        selectedAgentArea.setPrefRowCount(4);

        getChildren().addAll(
            new Label("Simulation"),
            startPauseButton,
            stepButton,
            new Label("Speed"),
            speedSlider,
            new Label("Visible/Active Agent Types"),
            carsToggle,
            busesToggle,
            emergencyToggle,
            pedestriansToggle,
            boatsToggle,
            aircraftToggle,
            addRandomAgentsButton,
            clearAgentsButton,
            new Label("Demo Layout"),
            layoutSelector,
            saveLayoutButton,
            loadLayoutButton,
            new Label("Stats"),
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

    public CheckBox getEmergencyToggle() {
        return emergencyToggle;
    }

    public CheckBox getPedestriansToggle() {
        return pedestriansToggle;
    }

    public CheckBox getBoatsToggle() {
        return boatsToggle;
    }

    public CheckBox getAircraftToggle() {
        return aircraftToggle;
    }

    public Button getAddRandomAgentsButton() {
        return addRandomAgentsButton;
    }

    public Button getClearAgentsButton() {
        return clearAgentsButton;
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
