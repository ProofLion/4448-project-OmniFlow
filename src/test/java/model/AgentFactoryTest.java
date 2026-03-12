package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import model.layouts.IntersectionLayout;
import org.junit.jupiter.api.Test;
import sim.World;
import util.Vec2;

class AgentFactoryTest {
    @Test
    void createKnownTypeReturnsRequestedAgentKind() {
        AgentProvider provider = AgentFactory.defaultFactory();
        Agent agent = provider.create("Car", new Vec2(0, 0), new Vec2(5, 0));

        assertEquals("Car", agent.getTypeName());
        assertTrue(agent.getId() > 0);
    }

    @Test
    void polymorphicUpdateWorksThroughAgentAbstraction() {
        AgentProvider provider = AgentFactory.defaultFactory();
        World world = new World(new IntersectionLayout());
        world.clearAgents();

        List<Agent> agents = List.of(
            provider.createWithId("Car", 200, new Vec2(0, 0), new Vec2(30, 0)),
            provider.createWithId("Bus", 201, new Vec2(0, 0), new Vec2(30, 0))
        );

        for (Agent agent : agents) {
            agent.update(world, 1.0 / 30.0);
        }

        assertEquals(1.0, agents.get(0).getPosition().x, 0.0001);
        assertEquals(1.0, agents.get(1).getPosition().x, 0.0001);
        assertEquals(8.0, agents.get(0).getRenderRadius());
        assertEquals(11.0, agents.get(1).getRenderRadius());
    }

    @Test
    void unknownTypeThrowsHelpfulError() {
        AgentProvider provider = AgentFactory.defaultFactory();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> provider.create("NotAType", new Vec2(0, 0), new Vec2(0, 0)));

        assertTrue(ex.getMessage().contains("Unknown agent type"));
    }
}
