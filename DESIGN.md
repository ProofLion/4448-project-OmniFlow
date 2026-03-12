# OmniFlow Design Patterns

This document identifies the primary design patterns used in the OmniFlow architecture.

## 1. Factory Pattern

- Where: `src/main/java/model/AgentFactory.java`
- How:
  - `AgentFactory` keeps a registry of creators (`Map<String, AgentCreator>`) instead of hard-coded `switch` logic.
  - Agent instances are created through `create(...)` / `createWithId(...)` using type keys (`Car`, `Bus`, `Boat`, etc.).
  - Layouts and engine code request agents from the factory instead of constructing concrete classes directly.
- Why it matters:
  - New agent types can be added by registering one constructor mapping.
  - Client code depends on the `Agent` abstraction, not concrete implementations.

## 2. Strategy Pattern (Layout Behavior)

- Where:
  - Strategy contract: `src/main/java/model/MapLayout.java`
  - Concrete strategies: `src/main/java/model/layouts/IntersectionLayout.java`, `HarborLayout.java`, `AirportLayout.java`
- How:
  - `MapLayout` defines the behaviors `drawStatic(...)` and `seed(World world)`.
  - Each concrete layout encapsulates different drawing and initial spawn logic.
  - `SimulationEngine.useLayout(...)` and controller selection swap layouts at runtime.
- Why it matters:
  - Scene rules vary independently from engine tick logic.
  - Runtime layout switching happens without changing engine internals.

## 3. MVC + Observer Pattern

- Where:
  - Controller: `src/main/java/app/OmniFlowController.java`
  - Views: `src/main/java/ui/MainView.java`, `ControlPanel.java`, `ViewportCanvas.java`
  - Model/state: `src/main/java/sim/World.java`, `SimulationEngine.java`, `SelectionModel.java`
- How:
  - Controller wires model state, view widgets, and actions.
  - JavaFX listeners (`setOnAction`, property listeners) react to UI and model changes.
  - Selection updates use observable property callbacks (`selectionModel.selectedAgentProperty().addListener(...)`).
- Why it matters:
  - UI components remain focused on presentation.
  - State transitions propagate through observer callbacks instead of tight coupling.
