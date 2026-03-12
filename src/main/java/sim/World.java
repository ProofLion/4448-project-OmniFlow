package sim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.Agent;
import model.MapLayout;
import util.Vec2;

/**
 * World stores layout + active agents. Engine mutates world over time.
 */
public class World {
    private MapLayout layout;
    private final List<Agent> agents = new ArrayList<>();

    public World(MapLayout layout) {
        this.layout = layout;
    }

    public MapLayout getLayout() {
        return layout;
    }

    public void setLayout(MapLayout layout) {
        this.layout = layout;
    }

    public List<Agent> getAgents() {
        return Collections.unmodifiableList(agents);
    }

    public void addAgent(Agent agent) {
        agents.add(agent);
    }

    public void clearAgents() {
        agents.clear();
    }

    public void replaceAgents(List<Agent> replacement) {
        agents.clear();
        agents.addAll(replacement);
    }

    public Agent pickAgentAt(Vec2 worldPoint) {
        for (int i = agents.size() - 1; i >= 0; i--) {
            Agent agent = agents.get(i);
            if (agent.containsPoint(worldPoint)) {
                return agent;
            }
        }
        return null;
    }

    public void wrap(Vec2 position, double minX, double minY, double maxX, double maxY) {
        if (position.x < minX) {
            position.x = maxX;
        } else if (position.x > maxX) {
            position.x = minX;
        }

        if (position.y < minY) {
            position.y = maxY;
        } else if (position.y > maxY) {
            position.y = minY;
        }
    }
}
