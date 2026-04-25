# OmniFlow

OmniFlow is a Java 17 + JavaFX city traffic simulator built for our OOAD final project. The finished version now focuses on a single polished `Downtown Intersection` scene with cars, buses, emergency vehicles, bikes, and pedestrians moving through a shared 2D traffic model.

The goal is not hyper-realistic traffic modeling. The goal is to demonstrate clean OO design, polymorphism, dependency injection, design patterns, JavaFX UI work, and basic persistence in a way that is easy to explain during the final code/demo interview.

## Team
- Evan Mohan
- Jake Huebner

## Tech Stack
- Java 17
- JavaFX
- Gradle
- JUnit 5

## Current Project Scope
- One polished demo layout:
  - `Downtown Intersection`
- Agent types:
  - cars
  - buses
  - emergency vehicles
  - bikes
  - pedestrians
- Features:
  - start, pause, and single-step simulation controls
  - speed control
  - agent type filtering
  - downtown-only layout focus
  - agent selection/details
  - save/load of layout state
  - longer pedestrian walk windows with "finish crossing" behavior
  - four-lane downtown roads on both axes
  - bus-stop dwell behavior with trailing vehicles queueing behind the bus
  - less frequent emergency vehicles with temporary all-red preemption and intersection-clearing slowdown
  - bikes staying on sidewalk routes and stopping short of pedestrian crosswalks

## Run
- Windows: `.\gradlew.bat run`
- macOS/Linux: `./gradlew run`

## Test
- Windows: set `GRADLE_USER_HOME` to a non-OneDrive folder, then run `.\gradlew.bat test`
- macOS/Linux: set `GRADLE_USER_HOME` to a local writable folder, then run `./gradlew test`

This repo currently lives in a OneDrive-backed path on our machine, and Gradle lock files can fail there. If that happens, use a local temp directory outside OneDrive for `GRADLE_USER_HOME`.

## Project Structure
- `src/main/java/app`: JavaFX startup and controller wiring
- `src/main/java/ui`: JavaFX view and canvas rendering
- `src/main/java/sim`: simulation engine, world state, camera, selection model
- `src/main/java/model`: agent abstractions, factory, shared enums/constants
- `src/main/java/model/agenttypes`: concrete traffic agents
- `src/main/java/model/layouts`: layout strategies and layout helpers
- `src/main/java/persistence`: save/load support
- `src/test/java`: unit tests and starter scaffolding

## Design Patterns Used
### Factory
- `AgentFactory` creates agents from shared type names.
- The engine and persistence layer code to the `AgentProvider` abstraction.

### Strategy
- `MapLayout` is the layout strategy interface.
- `DowntownIntersectionLayout` is the active demo layout and owns downtown-specific drawing, signal timing, queueing, pedestrian crossing rules, and emergency preemption.

### Template Method
- `BaseAgent` owns the shared `update(...)` algorithm.
- Agent subclasses customize behavior through small override hooks.

### Observer / MVC-style UI
- `OmniFlowController` wires UI actions to the model.
- JavaFX listeners react to control changes and selection updates.
- Rendering reads model state rather than owning simulation logic.

## OO Principles
### Coding to Abstractions
- `SimulationEngine` depends on `AgentProvider` and `MapLayout`.
- `World` stores `Agent` instances polymorphically.

### Dependency Injection
- `SimulationEngine` accepts `World`, `AgentProvider`, and a random supplier through its constructor.

### Polymorphism
- Each agent is updated through the shared `Agent` interface.
- Agent subclasses vary behavior without large if/else trees or switch statements.

## Tests
Main tests already in the repo:
- `AgentFactoryTest`
- `SimulationEngineTest`
- `LayoutBehaviorTest`
- `LayoutStoreTest`
- `Camera2DTest`

Starter TODO-style scaffold:
- `LayoutBehaviorTodoTest`

For this class project, we are aiming for meaningful coverage rather than perfect coverage. The model/simulation code matters more than exhaustive UI testing.

## Demo Prep Files
- `DESIGN.md`: where the four required patterns are documented
- `REQUIREMENTS.md`: rubric trace to concrete code
- `DEMO_INTERVIEW.md`: quick guide for what to show during the final demo/interview
- `TODO.md`: finish-line checklist

## Notes
- Earlier project ideas included broader multimodal scope. The final submission is intentionally narrowed to a cleaner downtown traffic simulator because it is easier to finish well and explain clearly.
- The JavaFX UI was AI-assisted, which our instructors explicitly allowed for the project UI.
