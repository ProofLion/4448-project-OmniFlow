package persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import model.Agent;
import model.AgentFactory;
import model.MapLayout;
import sim.Camera2D;
import util.Vec2;

/**
 * Manual text serializer for scene configuration.
 * Format is intentionally human-readable and diff-friendly.
 */
public final class LayoutStore {
    public record LoadedLayout(String layoutName, double cameraX, double cameraY, double zoom, List<Agent> agents) {
    }

    private LayoutStore() {
    }

    public static void save(Path path, String layoutName, Camera2D camera, List<Agent> agents) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("layout=" + layoutName);
        lines.add(String.format(Locale.US, "camera=%.4f,%.4f,%.4f", camera.getOffsetX(), camera.getOffsetY(), camera.getZoom()));

        for (Agent agent : agents) {
            Vec2 p = agent.getPosition();
            Vec2 v = agent.getVelocity();
            lines.add(String.format(Locale.US,
                "agent=%s,%d,%.4f,%.4f,%.4f,%.4f",
                agent.getTypeName(),
                agent.getId(),
                p.x,
                p.y,
                v.x,
                v.y
            ));
        }

        Files.write(path, lines);
    }

    public static LoadedLayout load(Path path, Map<String, MapLayout> layoutsByName) throws IOException {
        List<String> lines = Files.readAllLines(path);

        String layoutName = "Downtown Intersection";
        double cameraX = -230;
        double cameraY = -155;
        double zoom = 1.6;
        List<Agent> agents = new ArrayList<>();

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            if (trimmed.startsWith("layout=")) {
                layoutName = trimmed.substring("layout=".length());
            } else if (trimmed.startsWith("camera=")) {
                String[] parts = trimmed.substring("camera=".length()).split(",");
                if (parts.length >= 3) {
                    cameraX = Double.parseDouble(parts[0]);
                    cameraY = Double.parseDouble(parts[1]);
                    zoom = Double.parseDouble(parts[2]);
                }
            } else if (trimmed.startsWith("agent=")) {
                String[] parts = trimmed.substring("agent=".length()).split(",");
                if (parts.length >= 6) {
                    String type = parts[0];
                    long id = Long.parseLong(parts[1]);
                    double x = Double.parseDouble(parts[2]);
                    double y = Double.parseDouble(parts[3]);
                    double vx = Double.parseDouble(parts[4]);
                    double vy = Double.parseDouble(parts[5]);
                    try {
                        agents.add(AgentFactory.defaultFactory().createWithId(type, id, new Vec2(x, y), new Vec2(vx, vy)));
                    } catch (IllegalArgumentException ignored) {
                        // Ignore unknown legacy agent types so old save files still partially load.
                    }
                }
            }
        }

        if (!layoutsByName.containsKey(layoutName)) {
            layoutName = layoutsByName.keySet().stream().findFirst().orElse("Downtown Intersection");
        }

        return new LoadedLayout(layoutName, cameraX, cameraY, zoom, agents);
    }
}
