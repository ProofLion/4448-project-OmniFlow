package sim;

import static org.junit.jupiter.api.Assertions.assertEquals;

import model.layouts.IntersectionLayout;
import org.junit.jupiter.api.Test;

class SimulationEngineTest {
    @Test
    void stepAdvancesTickCount() {
        World world = new World(new IntersectionLayout());
        SimulationEngine engine = new SimulationEngine(world);

        assertEquals(0, engine.getTickCount());
        engine.step();
        assertEquals(1, engine.getTickCount());
    }
}
