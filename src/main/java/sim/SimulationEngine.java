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
import model.AgentTypes;
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
        updatingEnabledTypes.addAll(AgentTypes.ALL_CITY_TYPES);
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
        for (int i = 0; i < count; i++) {
            String type = randomDowntownType(random);
            Vec2 pos;
            Vec2 vel;
            if (AgentTypes.PEDESTRIAN.equals(type)) {
                if (random.nextBoolean()) {
                    double crosswalkX = random.nextBoolean() ? -102 : 102;
                    pos = new Vec2(crosswalkX, random.nextBoolean() ? -240 : 240);
                    vel = new Vec2(0, pos.y < 0 ? random.nextDouble(22, 32) : -random.nextDouble(22, 32));
                } else {
                    double crosswalkY = random.nextBoolean() ? -102 : 102;
                    pos = new Vec2(random.nextBoolean() ? -260 : 260, crosswalkY);
                    vel = new Vec2(pos.x < 0 ? random.nextDouble(20, 30) : -random.nextDouble(20, 30), 0);
                }
            } else if (AgentTypes.BIKE.equals(type)) {
                if (random.nextBoolean()) {
                    double sidewalkX = random.nextBoolean() ? -130 : 130;
                    pos = new Vec2(sidewalkX, random.nextBoolean() ? -260 : 260);
                    vel = new Vec2(0, pos.y < 0 ? random.nextDouble(28, 38) : -random.nextDouble(28, 38));
                } else {
                    double sidewalkY = random.nextBoolean() ? -130 : 130;
                    pos = new Vec2(random.nextBoolean() ? -280 : 280, sidewalkY);
                    vel = new Vec2(pos.x < 0 ? random.nextDouble(26, 36) : -random.nextDouble(26, 36), 0);
                }
            } else if (AgentTypes.BUS.equals(type)) {
                pos = new Vec2(-360, 54);
                vel = new Vec2(random.nextDouble(46, 56), 0);
            } else if (random.nextBoolean()) {
                boolean eastbound = random.nextBoolean();
                double laneY = eastbound
                    ? (random.nextBoolean() ? 18 : 54)
                    : (random.nextBoolean() ? -18 : -54);
                pos = new Vec2(eastbound ? -360 : 360, laneY);
                vel = new Vec2((eastbound ? 1 : -1) * random.nextDouble(36, 78), 0);
            } else {
                boolean southbound = random.nextBoolean();
                double laneX = southbound
                    ? (random.nextBoolean() ? -54 : -18)
                    : (random.nextBoolean() ? 18 : 54);
                pos = new Vec2(laneX, southbound ? -280 : 280);
                vel = new Vec2(0, (southbound ? 1 : -1) * random.nextDouble(28, 70));
            }
            world.addAgent(agentProvider.create(type, pos, vel));
        }
    }

    public void spawnEmergencyVehicle() {
        boolean alreadyPresent = world.getAgents().stream()
            .anyMatch(agent -> AgentTypes.EMERGENCY_VEHICLE.equals(agent.getTypeName()));
        if (alreadyPresent) {
            return;
        }

        RandomGenerator random = randomSupplier.get();
        int route = random.nextInt(4);
        Vec2 position;
        Vec2 velocity;
        switch (route) {
            case 0 -> {
                position = new Vec2(-18, -280);
                velocity = new Vec2(0, 78);
            }
            case 1 -> {
                position = new Vec2(18, 280);
                velocity = new Vec2(0, -78);
            }
            case 2 -> {
                position = new Vec2(-360, 18);
                velocity = new Vec2(78, 0);
            }
            default -> {
                position = new Vec2(360, -18);
                velocity = new Vec2(-78, 0);
            }
        }
        world.addAgent(agentProvider.create(AgentTypes.EMERGENCY_VEHICLE, position, velocity));
    }

    private String randomDowntownType(RandomGenerator random) {
        int roll = random.nextInt(29);
        if (roll < 12) {
            return AgentTypes.CAR;
        }
        if (roll < 15) {
            return AgentTypes.BUS;
        }
        if (roll < 23) {
            return AgentTypes.PEDESTRIAN;
        }
        if (roll < 29) {
            return AgentTypes.BIKE;
        }
        return AgentTypes.BIKE;
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
