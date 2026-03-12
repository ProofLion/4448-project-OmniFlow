# OmniFlow

OmniFlow is a Java 17 + JavaFX scaffold for a 2D simulation UI. It is intentionally UI-first so you can plug in simulation and backend logic later.

## Run

- Windows: `./gradlew.bat run`
- macOS/Linux: `./gradlew run`

## Test

- `./gradlew test`

## Project Structure

- `src/main/java/app`
- `src/main/java/ui`
- `src/main/java/sim`
- `src/main/java/model`
- `src/main/java/persistence`
- `src/main/java/util`
- `src/test/java`

## What Is Included

- Canvas-based viewport with pan/zoom and agent selection.
- Separate render loop (`AnimationTimer`) and simulation tick loop (`Timeline`).
- Control panel with speed, start/pause, step, filters, map selection, add/clear agents, save/load.
- Text-based persistence format (no external JSON libs).
- Placeholder simulation behaviors for multiple agent types and map layouts.

## Backend Integration Points

- Implement richer movement, routing, and policy logic in `sim/SimulationEngine.java` and model classes.
- Integrate traffic signals and interaction rules by extending `model/MapLayout.java` and concrete layouts.
- Replace text persistence if needed in `persistence/LayoutStore.java` while keeping DTO boundaries.

## Pan / Zoom / Selection Notes

- Pan: drag with primary mouse button.
- Zoom: mouse wheel zooms around cursor using camera transform math.
- Selection: click an agent to select; details appear in the right panel.

## Add a New Agent Type

1. Add class in `model/agenttypes` extending `BaseAgent`.
2. Register in `model/AgentFactory.java`.
3. Add a checkbox in `ui/ControlPanel.java` and default visibility in controller.

## Add a New Layout

1. Add class in `model/layouts` implementing `MapLayout`.
2. Register in `app/OmniFlowController.java` map registry.
3. Add drawing/spawn conventions in that layout implementation.
