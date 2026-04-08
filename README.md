# OmniFlow

OmniFlow is a Java 17 city traffic simulator built for our OOAD class final project. The simulator models street traffic in a small city environment using cars, buses, emergency vehicles, bikes, and pedestrians. Users can switch between city layouts, start or pause the simulation, adjust speed, add random agents, and save or load a scene.

The Java side is intentionally student-level and readable. The goal is to show OO design, polymorphism, dependency injection, and design patterns clearly rather than build a highly realistic traffic engine.

## Run

- Windows: `./gradlew.bat run`
- macOS/Linux: `./gradlew run`

## Test

- Windows: `./gradlew.bat test --gradle-user-home .gradle-local`
- macOS/Linux: `./gradlew test --gradle-user-home .gradle-local`

Using a local Gradle home avoids permission issues on some machines.

## Project Structure

- `src/main/java/app`: JavaFX application startup and controller wiring
- `src/main/java/ui`: JavaFX view classes and canvas rendering
- `src/main/java/sim`: world state, camera, selection model, simulation engine
- `src/main/java/model`: agent abstractions, factory, layout strategy contract
- `src/main/java/model/agenttypes`: city traffic agent implementations
- `src/main/java/model/layouts`: city layout strategies
- `src/main/java/persistence`: text-based save/load support
- `src/test/java`: unit tests
- `DESIGN.md`: design pattern explanation
- `REQUIREMENTS.md`: rubric trace

## Design Patterns Used

### 1. Factory Pattern

- `AgentFactory` creates agents from type names like `Car`, `Bus`, `Bike`, and `EmergencyVehicle`.
- The rest of the code requests an `Agent` through the factory instead of directly constructing concrete classes.
- This keeps creation logic in one place and avoids a large switch statement.

### 2. Strategy Pattern

- `MapLayout` is the strategy interface for city environments.
- The active strategies are:
  - `DowntownIntersectionLayout`
  - `SchoolZoneLayout`
  - `EmergencyCorridorLayout`
- Each layout decides how the map is drawn, which agents are seeded, and how simple traffic rules work.

### 3. Observer / MVC

- The JavaFX UI follows an MVC-style split:
  - model/sim classes hold state
  - `OmniFlowController` wires user actions to the model
  - UI classes render and display information
- JavaFX listeners update the simulation when controls change and update the side panel when selection changes.

### 4. Template Method

- `BaseAgent` owns the shared update flow for agents.
- Subclasses override only the small behavior hooks they need, such as speed multipliers, red-light stopping, and bus-stop pauses.
- This keeps the common movement code in one place while still allowing type-specific behavior.

## JavaFX Implementation

The JavaFX presentation layer in this project was AI-generated / heavily AI-assisted. That includes the overall JavaFX layout, canvas rendering approach, control panel wiring patterns, and most of the visual polish in the city scenes.

That JavaFX code is mainly responsible for:

- rendering the city layouts and agents
- handling pan, zoom, and selection
- providing buttons, sliders, filters, and layout selection
- showing simulation stats and selected-agent details

For the class project, the main OO design work is in the Java model and simulation architecture, especially:

- `Agent` and `BaseAgent`
- `AgentFactory`
- `MapLayout` and the concrete city layouts
- `SimulationEngine`

## Current Features

- City-only traffic simulation with:
  - cars
  - buses
  - emergency vehicles
  - bikes
  - pedestrians
- Three city layouts with different visual style and traffic behavior
- Simple traffic-light and crosswalk rules
- Bus-stop pauses
- Start, pause, step, and speed controls
- Type filters
- Agent selection with details panel
- Save/load through a simple text file format

## Controls

- Pan: drag with the primary mouse button
- Zoom: use the mouse wheel
- Select agent: click an agent in the viewport
- Start/Pause: toggle the simulation loop
- Step: advance one simulation tick
- Speed slider: change the tick rate
- Add Random Agents: populate the map with more city agents
- Save / Load: persist the current scene to a text file

## What Is Left / Teammate Handoff

These are the main follow-up tasks that still make sense for my teammate:

- Tweak movement constants so the behavior better matches common sense and any tests they want to write.
- Downtown itersection map, Fix stop light and pedestrian walking timing, ensure cars, bikes and busses stop at red lights, and if light is orange then they slow down to stop, ensuring they are not running reds
- On school map vehicles stop in middle of intersection if light turned red
- Make clear lines for intersections to better see where vehicles should stop
- Fix emergency corridor map in general or remove
- Adjust stop-line distances, pedestrian timing, and bus-stop pause lengths if some layouts feel awkward.
- Add or refine tests for edge cases, especially around layout-specific traffic behavior.
- Decide whether to fully delete the unused legacy boat/aircraft files or just leave them as legacy code.
- Polish demo notes for the final presentation.
- If needed, make small balancing changes so agent speeds feel more believable on screen.

## Notes

- The project was intentionally simplified from a broader multimodal idea into a clearer city traffic simulator.
- The Java logic favors readability over realism so it stays explainable at class-project level.
