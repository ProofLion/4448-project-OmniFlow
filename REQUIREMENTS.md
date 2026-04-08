# Assignment Requirements Trace

This file maps the class rubric to concrete code and documents where each requirement is satisfied in the final city traffic simulator.

## 1. Identify at least 4 design patterns

- Satisfied by: `DESIGN.md`
- Evidence:
  - Factory: `src/main/java/model/AgentFactory.java`
  - Strategy: `src/main/java/model/MapLayout.java` and `src/main/java/model/layouts/*`
  - Observer / MVC: `src/main/java/app/OmniFlowController.java`, `src/main/java/sim/SelectionModel.java`, `src/main/java/ui/*`
  - Template Method: `src/main/java/model/BaseAgent.java`

## 2. Foundational classes and interfaces have real logic

- `src/main/java/model/Agent.java`
  - defines the core simulation contract used throughout the program
- `src/main/java/model/BaseAgent.java`
  - contains the shared movement/update algorithm and route handling
- `src/main/java/model/AgentFactory.java`
  - handles factory-based agent creation
- `src/main/java/model/MapLayout.java`
  - defines the layout strategy contract for drawing and traffic rules
- `src/main/java/sim/SimulationEngine.java`
  - advances the simulation, applies filters, changes layouts, and counts agents
- `src/main/java/sim/World.java`
  - stores the active layout, active agents, and current tick state

## 3. OO principles are demonstrated

### Coding to abstractions

- `SimulationEngine` works with `AgentProvider` and `MapLayout` abstractions.
- `World` stores `List<Agent>` rather than concrete subclasses.
- The controller switches layouts through `MapLayout` rather than direct concrete coupling.

### Polymorphism

- `SimulationEngine` updates every agent through `Agent.update(world, dt)`.
- Different subclasses provide different movement behavior:
  - `CarAgent`
  - `BusAgent`
  - `EmergencyVehicleAgent`
  - `BikeAgent`
  - `PedestrianAgent`
- Test reference: `src/test/java/model/AgentFactoryTest.java`

### Dependency injection

- `SimulationEngine` supports constructor injection for:
  - `World`
  - `AgentProvider`
  - `Supplier<RandomGenerator>`
- This keeps the engine easier to test and avoids hard-coding all dependencies inside the class.

## 4. UI or persisted state requirement

- UI requirement is satisfied through the JavaFX application:
  - `src/main/java/app/*`
  - `src/main/java/ui/*`
- Persisted state requirement is also satisfied through:
  - `src/main/java/persistence/LayoutStore.java`

## 5. Meaningful test cases

- `src/test/java/model/AgentFactoryTest.java`
  - verifies factory creation
  - verifies polymorphic agent updates
  - verifies helpful errors for unknown types
- `src/test/java/sim/SimulationEngineTest.java`
  - verifies stepping
  - verifies type filtering
  - verifies vehicle stopping at red lights
  - verifies emergency vehicle behavior
  - verifies layout switching
- `src/test/java/persistence/LayoutStoreTest.java`
  - verifies save/load round-trip
  - verifies fallback behavior for unknown layout names
- `src/test/java/sim/Camera2DTest.java`
  - verifies camera coordinate conversions and zoom clamping

## 6. This requirements trace file exists

- Satisfied by: `REQUIREMENTS.md`
