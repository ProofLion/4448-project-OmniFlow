# OmniFlow Design Patterns

This document explains the four design patterns intentionally used in the final OmniFlow architecture.

## 1. Factory Pattern
- Where:
  - `src/main/java/model/AgentFactory.java`
  - `src/main/java/model/AgentProvider.java`
- How:
  - `AgentFactory` stores constructor mappings for the supported city agent types.
  - Other parts of the program request `Agent` objects through the `AgentProvider` abstraction instead of directly calling constructors.
- Why it helps:
  - agent creation logic stays centralized
  - new supported types can be added without introducing a large switch statement

## 2. Strategy Pattern
- Where:
  - `src/main/java/model/MapLayout.java`
  - `src/main/java/model/layouts/DowntownIntersectionLayout.java`
- How:
  - the layout contract still defines drawing, seeding, stop rules, and bounds behavior behind one shared interface
  - the current demo uses the downtown implementation to package signal timing, bus-stop queueing, bike/pedestrian crossing rules, and emergency-light preemption behind that contract
- Why it helps:
  - downtown-specific behavior changes without changing the simulation engine
  - the renderer and agents still code to the same abstraction instead of hard-coding scene logic everywhere

## 3. Template Method Pattern
- Where:
  - `src/main/java/model/BaseAgent.java`
- How:
  - `BaseAgent` defines the shared `update(...)` algorithm
  - subclasses customize only the behavior hooks they need, such as:
    - `beforeUpdate(...)`
    - `shouldPause(...)`
    - `getSpeedMultiplier(...)`
    - `afterMove(...)`
- Why it helps:
  - common movement behavior is written once
  - agent subclasses stay short and readable

## 4. Observer / MVC-style UI
- Where:
  - `src/main/java/app/OmniFlowController.java`
  - `src/main/java/sim/SelectionModel.java`
  - `src/main/java/ui/ControlPanel.java`
  - `src/main/java/ui/ViewportCanvas.java`
- How:
  - the controller wires UI actions to simulation state changes
  - JavaFX listeners react to button presses, slider changes, filter toggles, and selection updates
  - view classes render model state without owning the simulation rules
- Why it helps:
  - responsibilities stay separated
  - UI changes do not require rewriting core engine logic

## Requirement Note
Piazza clarified that the project needs **four** design patterns. This document focuses on those four required patterns. If we mention a fifth pattern during the demo, it should be treated as bonus context rather than a requirement.
