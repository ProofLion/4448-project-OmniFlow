# Assignment Requirements Trace

This file maps the class rubric to the current OmniFlow codebase.

## 1. Identify at least 4 design patterns
- Satisfied by:
  - `DESIGN.md`
- Evidence:
  - Factory: `src/main/java/model/AgentFactory.java`
  - Strategy: `src/main/java/model/MapLayout.java` and `src/main/java/model/layouts/*`
  - Template Method: `src/main/java/model/BaseAgent.java`
  - Observer / MVC-style UI: `src/main/java/app/OmniFlowController.java`, `src/main/java/sim/SelectionModel.java`, `src/main/java/ui/*`

## 2. Foundational classes and interfaces contain real logic
- `src/main/java/model/Agent.java`
  - core simulation contract
- `src/main/java/model/BaseAgent.java`
  - shared movement/update algorithm
- `src/main/java/model/AgentFactory.java`
  - registry-based creation logic
- `src/main/java/model/MapLayout.java`
  - shared contract for layouts
- `src/main/java/sim/SimulationEngine.java`
  - tick loop, downtown-focused random seeding, filters, counters
- `src/main/java/sim/World.java`
  - active layout, active agents, current tick state

## 3. OO principles are demonstrated
### Coding to abstractions
- `SimulationEngine` works with `AgentProvider` and `MapLayout`.
- `World` stores `List<Agent>`.
- The controller still uses `MapLayout` rather than coupling itself to downtown-specific logic.

### Polymorphism
- All agents update through `Agent.update(world, dt)`.
- Different subclasses vary movement and stop behavior:
  - `CarAgent`
  - `BusAgent`
  - `EmergencyVehicleAgent`
  - `BikeAgent`
  - `PedestrianAgent`
- Recent downtown-specific examples:
  - buses dwell at the curb stop and can drop off pedestrians
  - bikes follow pedestrian crossing rules instead of vehicle light rules
  - emergency vehicles use a different speed policy during intersection preemption

### Dependency injection
- `SimulationEngine` supports constructor injection for:
  - `World`
  - `AgentProvider`
  - `Supplier<RandomGenerator>`

## 4. UI or persisted state requirement
- UI requirement:
  - `src/main/java/app/*`
  - `src/main/java/ui/*`
- Persisted state requirement:
  - `src/main/java/persistence/LayoutStore.java`

## 5. Meaningful tests exist
- `src/test/java/model/AgentFactoryTest.java`
  - factory creation
  - polymorphic update behavior
  - unknown type error handling
- `src/test/java/sim/SimulationEngineTest.java`
  - ticking
  - enabled/disabled type updates
  - emergency vehicle behavior
  - red-light stopping
  - layout switching
  - manual emergency spawning without random emergency inserts
- `src/test/java/model/layouts/LayoutBehaviorTest.java`
  - downtown stop-before-intersection behavior
  - yellow-light handling near stop bars
  - pedestrians finishing a crossing after the signal turns red
  - directional pedestrian signal timing and flashing-start prevention
  - vehicles queueing behind a bus at the stop
  - emergency preemption holding conflicting traffic
- `src/test/java/persistence/LayoutStoreTest.java`
  - save/load round-trip
  - fallback behavior for unknown layouts
- `src/test/java/sim/Camera2DTest.java`
  - screen/world conversion and zoom clamping

## 6. Project direction matches Piazza clarification
- Piazza clarified that the project needs **four** required design patterns.
- The docs and demo materials in this repo align to that clarification.
