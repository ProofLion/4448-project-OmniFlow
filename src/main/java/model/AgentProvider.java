package model;

import util.Vec2;

/**
 * Abstraction for creating agents, used to decouple engine/persistence code
 * from one concrete factory implementation.
 */
public interface AgentProvider {
    Agent create(String typeName, Vec2 position, Vec2 velocity);

    Agent createWithId(String typeName, long id, Vec2 position, Vec2 velocity);
}
