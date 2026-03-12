# Assignment Requirements Trace

This trace maps each rubric requirement to concrete code, tests, and documentation in this repository.

## 1. Identify 3+ design patterns and explain usage

- Satisfied by: `DESIGN.md`
- Evidence:
  - Factory: `src/main/java/model/AgentFactory.java`
  - Strategy: `src/main/java/model/MapLayout.java` and `src/main/java/model/layouts/*`
  - MVC/Observer: `src/main/java/app/OmniFlowController.java`, `src/main/java/sim/SelectionModel.java`, `src/main/java/ui/*`

## 2. Foundational classes/interfaces have real logic and clear responsibilities

- Satisfied by:
  - `src/main/java/model/Agent.java`: core simulation contract and hit-test default behavior.
  - `src/main/java/model/BaseAgent.java`: shared integration/bounds logic and common state.
  - `src/main/java/model/AgentFactory.java`: registry-backed creation logic, id handling integration.
  - `src/main/java/sim/SimulationEngine.java`: tick loop, speed control, type filtering, layout switching.
  - `src/main/java/sim/World.java`: active-agent storage, selection hit-test, world wrapping helper.

## 3. Demonstrate OO principles

- Coding to abstractions:
  - `Agent` interface used throughout `World`/`SimulationEngine` (`List<Agent>`, `Agent.update(...)`).
  - `MapLayout` interface used by controller and engine without depending on concrete layouts.
  - `AgentProvider` abstraction introduced in `src/main/java/model/AgentProvider.java`.
- Polymorphism:
  - Different subclasses (`CarAgent`, `BusAgent`, `BoatAgent`, etc.) are used through `Agent`.
  - `SimulationEngine.tickOnce()` calls `agent.update(world, dt)` polymorphically.
  - Test evidence: `src/test/java/model/AgentFactoryTest.java` (`polymorphicUpdateWorksThroughAgentAbstraction`).
- Explicit dependency injection:
  - `SimulationEngine` constructor accepts injected `AgentProvider` and random-source `Supplier<RandomGenerator>`:
    - `src/main/java/sim/SimulationEngine.java`
  - Default constructor delegates to production dependencies, preserving existing behavior.

## 4. Add at least 5 meaningful unit tests

- Existing tests:
  - `src/test/java/sim/Camera2DTest.java` (2 tests)
- Added tests:
  - `src/test/java/sim/SimulationEngineTest.java` (5 tests)
  - `src/test/java/model/AgentFactoryTest.java` (3 tests)
  - `src/test/java/persistence/LayoutStoreTest.java` (2 tests)
- Total tests: 12

## 5. Add a requirements trace section/file

- Satisfied by: this file (`REQUIREMENTS.md`)
