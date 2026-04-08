# OmniFlow Design Patterns

This document explains the four design patterns intentionally used in the final city traffic simulator architecture.

## 1. Factory Pattern

- Where: `src/main/java/model/AgentFactory.java`
- How:
  - `AgentFactory` stores constructor mappings for `Car`, `Bus`, `EmergencyVehicle`, `Bike`, and `Pedestrian`.
  - The engine, layouts, and persistence code request agents through the `AgentProvider` abstraction instead of directly using constructors.
- Why it matters:
  - New agent types can be added by registering one new creator.
  - The rest of the code stays focused on the `Agent` abstraction.

## 2. Strategy Pattern

- Where:
  - Strategy contract: `src/main/java/model/MapLayout.java`
  - Concrete strategies:
    - `src/main/java/model/layouts/DowntownIntersectionLayout.java`
    - `src/main/java/model/layouts/SchoolZoneLayout.java`
    - `src/main/java/model/layouts/EmergencyCorridorLayout.java`
- How:
  - Each layout controls drawing, initial seeding, stop rules, bus stops, and map bounds.
  - The controller swaps layouts at runtime through the shared `MapLayout` interface.
- Why it matters:
  - Traffic rules and visual scenes can vary without changing the simulation engine.
  - This makes layout switching easy to explain and demonstrate.

## 3. Observer + MVC

- Where:
  - Controller: `src/main/java/app/OmniFlowController.java`
  - Views: `src/main/java/ui/MainView.java`, `src/main/java/ui/ControlPanel.java`, `src/main/java/ui/ViewportCanvas.java`
  - Model/state: `src/main/java/sim/World.java`, `src/main/java/sim/SimulationEngine.java`, `src/main/java/sim/SelectionModel.java`
- How:
  - The controller connects UI actions to model changes.
  - JavaFX property listeners and action handlers react to button presses, slider changes, checkboxes, and selection updates.
  - The rendering layer reads state from the model instead of owning simulation logic.
- Why it matters:
  - It keeps responsibilities separated.
  - The UI stays easier to change without rewriting core simulation code.

## 4. Template Method

- Where: `src/main/java/model/BaseAgent.java`
- How:
  - `BaseAgent` defines the shared `update(...)` algorithm:
    - run pre-update logic
    - decide whether the agent should pause
    - apply movement
    - lock the agent to its route
    - let the layout recycle the agent if needed
  - Subclasses customize small hooks like:
    - `getSpeedMultiplier(...)`
    - `shouldPause(...)`
    - `beforeUpdate(...)`
    - `afterMove(...)`
- Why it matters:
  - The common movement logic is written once.
  - Agent subclasses stay short, readable, and type-specific.
