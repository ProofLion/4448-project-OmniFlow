package app;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Agent;
import model.AgentTypes;
import model.MapLayout;
import model.layouts.DowntownIntersectionLayout;
import persistence.LayoutStore;
import sim.Camera2D;
import sim.SelectionModel;
import sim.SimulationEngine;
import sim.World;
import ui.ControlPanel;
import ui.MainView;

/**
 * Wires together UI widgets, simulation engine, rendering, and persistence.
 */
public class OmniFlowController {
    private final Map<String, MapLayout> layoutsByName = new LinkedHashMap<>();

    private final Camera2D camera = new Camera2D();
    private final SelectionModel selectionModel = new SelectionModel();

    private World world;
    private SimulationEngine engine;

    private MainView mainView;
    private ControlPanel controlPanel;

    private double fps = 0;

    public void init(Stage stage) {
        registerLayouts();

        world = new World(layoutsByName.get("Downtown Intersection"));
        world.getLayout().seed(world);

        engine = new SimulationEngine(world);

        mainView = new MainView();
        controlPanel = mainView.getControlPanel();
        camera.setState(world.getLayout().getSuggestedCameraOffset().x, world.getLayout().getSuggestedCameraOffset().y, 1.6);

        configureControls(stage);

        mainView.getViewportCanvas().bindInteractions(camera, world, selectionModel);
        mainView.getViewportCanvas().setOnAgentClicked(this::updateSelectedAgentText);

        Scene scene = new Scene(mainView.getRoot(), 1250, 760);
        stage.setScene(scene);
        stage.setTitle("OmniFlow - City Traffic Simulator");
        stage.show();

        startRenderLoop();
        refreshStats();
    }

    private void registerLayouts() {
        MapLayout downtown = new DowntownIntersectionLayout();
        layoutsByName.put(downtown.getName(), downtown);
    }

    private void configureControls(Stage stage) {
        controlPanel.getLayoutSelector().getItems().addAll(layoutsByName.keySet());
        controlPanel.getLayoutSelector().getSelectionModel().select("Downtown Intersection");
        controlPanel.getLayoutSelector().setDisable(true);

        controlPanel.getStartPauseButton().setOnAction(event -> {
            if (engine.isRunning()) {
                engine.pause();
                controlPanel.getStartPauseButton().setText("Start");
            } else {
                engine.start();
                controlPanel.getStartPauseButton().setText("Pause");
            }
        });

        controlPanel.getStepButton().setOnAction(event -> {
            engine.step();
            refreshStats();
        });

        controlPanel.getSpeedSlider().valueProperty().addListener((obs, oldV, newV) -> {
            engine.setSpeedMultiplier(newV.doubleValue());
            refreshStats();
        });

        setupTypeToggle(controlPanel.getCarsToggle());
        setupTypeToggle(controlPanel.getBusesToggle());
        setupTypeToggle(controlPanel.getEmergencyVehiclesToggle());
        setupTypeToggle(controlPanel.getBikesToggle());
        setupTypeToggle(controlPanel.getPedestriansToggle());

        controlPanel.getAddRandomAgentsButton().setOnAction(event -> {
            engine.addRandomAgents(20);
            refreshStats();
        });

        controlPanel.getSpawnEmergencyButton().setOnAction(event -> {
            engine.spawnEmergencyVehicle();
            refreshStats();
        });

        controlPanel.getClearAgentsButton().setOnAction(event -> {
            engine.clearAgents();
            selectionModel.setSelectedAgent(null);
            updateSelectedAgentText(null);
            refreshStats();
        });

        controlPanel.getLayoutSelector().setOnAction(event -> {
            String selected = controlPanel.getLayoutSelector().getValue();
            if (selected != null && layoutsByName.containsKey(selected)) {
                MapLayout layout = layoutsByName.get(selected);
                engine.useLayout(layout);
                camera.setState(layout.getSuggestedCameraOffset().x, layout.getSuggestedCameraOffset().y, 1.6);
                controlPanel.getStartPauseButton().setText("Start");
                selectionModel.setSelectedAgent(null);
                updateSelectedAgentText(null);
                refreshStats();
            }
        });

        controlPanel.getSaveLayoutButton().setOnAction(event -> onSaveLayout(stage));
        controlPanel.getLoadLayoutButton().setOnAction(event -> onLoadLayout(stage));

        selectionModel.selectedAgentProperty().addListener((obs, oldV, newV) -> updateSelectedAgentText(newV));
        applyTypeFilters();
    }

    private void setupTypeToggle(CheckBox checkBox) {
        checkBox.selectedProperty().addListener((obs, oldV, newV) -> applyTypeFilters());
    }

    private void applyTypeFilters() {
        Set<String> enabled = new HashSet<>();
        if (controlPanel.getCarsToggle().isSelected()) {
            enabled.add(AgentTypes.CAR);
        }
        if (controlPanel.getBusesToggle().isSelected()) {
            enabled.add(AgentTypes.BUS);
        }
        if (controlPanel.getEmergencyVehiclesToggle().isSelected()) {
            enabled.add(AgentTypes.EMERGENCY_VEHICLE);
        }
        if (controlPanel.getBikesToggle().isSelected()) {
            enabled.add(AgentTypes.BIKE);
        }
        if (controlPanel.getPedestriansToggle().isSelected()) {
            enabled.add(AgentTypes.PEDESTRIAN);
        }

        engine.setUpdatingEnabledTypes(enabled);
        refreshStats();
    }

    private void onSaveLayout(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save OmniFlow Layout");
        chooser.setInitialFileName("omniflow-layout.txt");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        var file = chooser.showSaveDialog(stage);
        if (file == null) {
            return;
        }

        try {
            LayoutStore.save(file.toPath(), world.getLayout().getName(), camera, world.getAgents());
        } catch (IOException ex) {
            controlPanel.getStatsArea().appendText("\nSave failed: " + ex.getMessage());
        }
    }

    private void onLoadLayout(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load OmniFlow Layout");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        var file = chooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }

        try {
            Path path = file.toPath();
            LayoutStore.LoadedLayout loaded = LayoutStore.load(path, layoutsByName);
            MapLayout layout = layoutsByName.get(loaded.layoutName());

            engine.pause();
            controlPanel.getStartPauseButton().setText("Start");

            world.setLayout(layout);
            world.replaceAgents(loaded.agents());
            camera.setState(loaded.cameraX(), loaded.cameraY(), loaded.zoom());

            controlPanel.getLayoutSelector().getSelectionModel().select(loaded.layoutName());
            selectionModel.setSelectedAgent(null);
            updateSelectedAgentText(null);
            refreshStats();
        } catch (IOException | RuntimeException ex) {
            controlPanel.getStatsArea().appendText("\nLoad failed: " + ex.getMessage());
        }
    }

    private void startRenderLoop() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastFrameNanos = -1;

            @Override
            public void handle(long now) {
                if (lastFrameNanos > 0) {
                    double dt = (now - lastFrameNanos) / 1_000_000_000.0;
                    if (dt > 0) {
                        fps = 1.0 / dt;
                    }
                }
                lastFrameNanos = now;

                mainView.getViewportCanvas().renderFrame(
                    world,
                    camera,
                    selectionModel,
                    engine.getUpdatingEnabledTypes(),
                    engine.getTickCount(),
                    fps
                );

                refreshStats();
            }
        };
        timer.start();
    }

    private void refreshStats() {
        Map<String, Long> counts = engine.countByType();
        String text = String.format(Locale.US,
            "Layout: %s\nTick: %d\nFPS (est): %.1f\nSpeed: %.2fx\nRunning: %s\nCars: %d\nBuses: %d\nEmergency Vehicles: %d\nBikes: %d\nPedestrians: %d",
            world.getLayout().getName(),
            engine.getTickCount(),
            fps,
            engine.getSpeedMultiplier(),
            engine.isRunning(),
            counts.getOrDefault(AgentTypes.CAR, 0L),
            counts.getOrDefault(AgentTypes.BUS, 0L),
            counts.getOrDefault(AgentTypes.EMERGENCY_VEHICLE, 0L),
            counts.getOrDefault(AgentTypes.BIKE, 0L),
            counts.getOrDefault(AgentTypes.PEDESTRIAN, 0L)
        );
        controlPanel.getStatsArea().setText(text);

        updateSelectedAgentText(selectionModel.getSelectedAgent());
    }

    private void updateSelectedAgentText(Agent agent) {
        if (agent == null) {
            controlPanel.getSelectedAgentArea().setText("No agent selected.");
            return;
        }

        controlPanel.getSelectedAgentArea().setText(String.format(Locale.US,
            "Id: %d\nType: %s\nPosition: (%.1f, %.1f)\nVelocity: (%.1f, %.1f)",
            agent.getId(),
            agent.getTypeName(),
            agent.getPosition().x,
            agent.getPosition().y,
            agent.getVelocity().x,
            agent.getVelocity().y
        ));
    }
}
