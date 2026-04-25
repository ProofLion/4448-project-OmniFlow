# OmniFlow Demo and Interview Guide

## Short Demo Flow
1. Launch the app on `Downtown Intersection`.
2. Start the simulation and point out the shared update loop and live stats.
3. Pause, step once or twice, and show how the directional walk phases, flashing pedestrian countdown, longer yellow timing, bus-stop queueing, and emergency all-red preemption affect cars, buses, bikes, and pedestrians.
4. Select an agent to show the detail panel and explain how the UI reads model state.
5. Zoom in on the four-lane downtown road markings, crosswalks, pedestrian signals, and bus stop to show the visual polish.
6. Save a layout, then load it back to demonstrate persistence.

## Four Required Patterns
### 1. Factory
- File: `src/main/java/model/AgentFactory.java`
- Show:
  - creator registry
  - creation through `AgentProvider`
  - no large switch statement for agent construction

### 2. Strategy
- Files:
  - `src/main/java/model/MapLayout.java`
  - `src/main/java/model/layouts/DowntownIntersectionLayout.java`
- Show:
  - the downtown layout owns drawing, seeding, stop rules, queueing, and emergency preemption through one interface
  - world-aware layout hooks let rendering and movement rules stay aligned

### 3. Template Method
- File: `src/main/java/model/BaseAgent.java`
- Show:
  - final `update(...)` flow
  - hooks like `beforeUpdate`, `shouldPause`, `getSpeedMultiplier`, `afterMove`
  - subclasses only override the pieces they need

### 4. Observer / MVC-style UI wiring
- Files:
  - `src/main/java/app/OmniFlowController.java`
  - `src/main/java/sim/SelectionModel.java`
  - `src/main/java/ui/ControlPanel.java`
  - `src/main/java/ui/ViewportCanvas.java`
- Show:
  - controller wires UI actions to model updates
  - JavaFX listeners update engine state
  - renderer reads world state instead of owning simulation logic

## OO Principles Talking Points
### Dependency Injection
- `SimulationEngine` constructor accepts `World`, `AgentProvider`, and `Supplier<RandomGenerator>`.
- This makes the engine easier to test and keeps dependencies explicit.

### Coding to Abstractions
- `SimulationEngine` depends on `AgentProvider` and `MapLayout`.
- `World` stores `List<Agent>` instead of concrete subclasses.

### Polymorphism
- Agents are updated through `Agent.update(world, dt)`.
- Different agent subclasses change behavior through overrides instead of large conditionals.

## Persistence and UI Talking Points
- Persistence: `src/main/java/persistence/LayoutStore.java`
  - saves the selected layout, camera state, and agents
  - loads older files while ignoring unknown legacy agent types
- UI:
  - JavaFX canvas renders the simulation
  - right-side panel controls speed, filters, layout changes, and save/load
  - selected agent data is displayed without mixing UI code into the model

## Useful Tests to Mention
- `src/test/java/model/AgentFactoryTest.java`
- `src/test/java/sim/SimulationEngineTest.java`
- `src/test/java/model/layouts/LayoutBehaviorTest.java`
- `src/test/java/persistence/LayoutStoreTest.java`

## Known Limitations
- The simulation is intentionally simplified for clarity and class-project explainability.
- Vehicle behavior is rule-based, not realistic pathfinding or full turning/pathfinding logic.
- The final submission focuses on one polished downtown layout instead of the older broader multimodal scope.
- UI test coverage is intentionally lighter than model/simulation coverage.
