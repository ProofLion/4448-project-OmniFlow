package sim;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import model.Agent;

/**
 * Shared selection state for controller, renderer, and side panel.
 */
public class SelectionModel {
    private final ObjectProperty<Agent> selectedAgent = new SimpleObjectProperty<>();

    public ObjectProperty<Agent> selectedAgentProperty() {
        return selectedAgent;
    }

    public Agent getSelectedAgent() {
        return selectedAgent.get();
    }

    public void setSelectedAgent(Agent agent) {
        selectedAgent.set(agent);
    }
}
