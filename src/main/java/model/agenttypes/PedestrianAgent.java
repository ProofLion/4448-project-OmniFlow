package model.agenttypes;

import java.util.concurrent.ThreadLocalRandom;
import javafx.scene.paint.Color;
import model.BaseAgent;
import sim.World;
import util.Vec2;

public class PedestrianAgent extends BaseAgent {
    public PedestrianAgent(long id, Vec2 position, Vec2 velocity) {
        super(id, position, velocity);
    }

    @Override
    public String getTypeName() {
        return "Pedestrian";
    }

    @Override
    public Color getColor() {
        return Color.FORESTGREEN;
    }

    @Override
    public double getRenderRadius() {
        return 6.0;
    }

    @Override
    public String getShortLabel() {
        return "P" + getId();
    }

    @Override
    public void update(World world, double dtSeconds) {
        // Wander by gently jittering velocity each tick while staying in sidewalk bounds.
        ThreadLocalRandom random = ThreadLocalRandom.current();
        Vec2 v = getVelocity();
        double newVx = clamp(v.x + (random.nextDouble() - 0.5) * 4.0, -20.0, 20.0);
        double newVy = clamp(v.y + (random.nextDouble() - 0.5) * 4.0, -20.0, 20.0);
        setVelocity(new Vec2(newVx, newVy));
        integrate(dtSeconds);
        bounceWithin(-170, -100, 170, 100);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
