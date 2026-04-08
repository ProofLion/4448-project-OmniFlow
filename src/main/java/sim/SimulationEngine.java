package sim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import model.Agent;
import model.AgentFactory;
import model.AgentProvider;
import model.MapLayout;
import util.Vec2;

/**
 * Owns simulation ticks and all mutable runtime state.
 * Render loop reads from this world, while tick loop advances agent motion.
 */
public class SimulationEngine {
    private final World world;
    private final AgentProvider agentProvider;
    private final Supplier<RandomGenerator> randomSupplier;
    private long tickCount;
    private boolean running;
    private double speedMultiplier = 1.0;
    private Timeline tickTimeline;
    private final Set<String> updatingEnabledTypes = new HashSet<>();

    public SimulationEngine(World world) {
        this(world, AgentFactory.defaultFactory(), ThreadLocalRandom::current);
    }

    public SimulationEngine(World world, AgentProvider agentProvider, Supplier<RandomGenerator> randomSupplier) {
        this.world = Objects.requireNonNull(world, "world");
        this.agentProvider = Objects.requireNonNull(agentProvider, "agentProvider");
        this.randomSupplier = Objects.requireNonNull(randomSupplier, "randomSupplier");
        updatingEnabledTypes.addAll(List.of("Car", "Bus", "EmergencyVehicle", "Bike", "Pedestrian"));
        world.setTickCount(0);
        rebuildTimeline();
    }

    public World getWorld() {
        return world;
    }

    public long getTickCount() {
        return tickCount;
    }

    public boolean isRunning() {
        return running;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = Math.max(0.25, Math.min(4.0, speedMultiplier));
        rebuildTimeline();
        if (running) {
            tickTimeline.play();
        }
    }

    public void setUpdatingEnabledTypes(Set<String> enabledTypes) {
        updatingEnabledTypes.clear();
        updatingEnabledTypes.addAll(enabledTypes);
    }

    public Set<String> getUpdatingEnabledTypes() {
        return Set.copyOf(updatingEnabledTypes);
    }

    public void start() {
        running = true;
        tickTimeline.play();
    }

    public void pause() {
        running = false;
        tickTimeline.pause();
    }

    public void step() {
        tickOnce();
    }

    public void clearAgents() {
        world.clearAgents();
    }

    public void useLayout(MapLayout layout) {
        pause();
        world.setLayout(layout);
        layout.seed(world);
        tickCount = 0;
        world.setTickCount(0);
    }

    public void addRandomAgents(int count) {
        RandomGenerator random = randomSupplier.get();
        String[] types = {"Car", "Bus", "EmergencyVehicle", "Bike", "Pedestrian"};
        for (int i = 0; i < count; i++) {
            String type = types[random.nextInt(types.length)];
            Vec2 pos;
            Vec2 vel;
            if (random.nextBoolean()) {
                double laneY = switch (random.nextInt(4)) {
                    case 0 -> -52;
                    case 1 -> -18;
                    case 2 -> 18;
                    default -> 42;
                };
                pos = new Vec2(random.nextBoolean() ? -320 : 320, laneY);
                double direction = pos.x < 0 ? 1 : -1;
                vel = new Vec2(direction * random.nextDouble(32, 76), 0);
            } else {
                double laneX = switch (random.nextInt(3)) {
                    case 0 -> -42;
                    case 1 -> 0;
                    default -> 42;
                };
                pos = new Vec2(laneX, random.nextBoolean() ? -220 : 220);
                double direction = pos.y < 0 ? 1 : -1;
                vel = new Vec2(0, direction * random.nextDouble(20, 64));
            }
            world.addAgent(agentProvider.create(type, pos, vel));
        }
    }

    public Map<String, Long> countByType() {
        Map<String, Long> counts = new HashMap<>();
        for (Agent agent : world.getAgents()) {
            counts.merge(agent.getTypeName(), 1L, Long::sum);
        }
        return counts;
    }

    private void tickOnce() {
        double dt = 1.0 / 30.0;
        world.setTickCount(tickCount);
        List<Agent> agents = new ArrayList<>(world.getAgents());
        for (Agent agent : agents) {
            if (updatingEnabledTypes.contains(agent.getTypeName())) {
                agent.update(world, dt);
            }
        }
        tickCount++;
        world.setTickCount(tickCount);
    }

    private void rebuildTimeline() {
        if (tickTimeline != null) {
            tickTimeline.stop();
        }
        double baseIntervalMs = 33.333;
        double interval = baseIntervalMs / speedMultiplier;
        tickTimeline = new Timeline(new KeyFrame(Duration.millis(interval), event -> tickOnce()));
        tickTimeline.setCycleCount(Timeline.INDEFINITE);
    }
}
