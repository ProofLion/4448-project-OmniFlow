package persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import model.Agent;
import model.AgentFactory;
import model.MapLayout;
import model.layouts.HarborLayout;
import model.layouts.IntersectionLayout;
import org.junit.jupiter.api.Test;
import sim.Camera2D;
import util.Vec2;

class LayoutStoreTest {
    @Test
    void saveThenLoadRoundTripPreservesSceneData() throws IOException {
        Path tempFile = Files.createTempFile("omniflow-layout", ".txt");
        try {
            Camera2D camera = new Camera2D();
            camera.setState(-10.5, 45.25, 2.2);

            Agent first = AgentFactory.defaultFactory().createWithId("Car", 310, new Vec2(1, 2), new Vec2(3, 4));
            Agent second = AgentFactory.defaultFactory().createWithId("Boat", 311, new Vec2(-5, 6), new Vec2(7, -8));

            LayoutStore.save(tempFile, "Harbor", camera, java.util.List.of(first, second));

            Map<String, MapLayout> layouts = new LinkedHashMap<>();
            layouts.put("Intersection", new IntersectionLayout());
            layouts.put("Harbor", new HarborLayout());

            LayoutStore.LoadedLayout loaded = LayoutStore.load(tempFile, layouts);

            assertEquals("Harbor", loaded.layoutName());
            assertEquals(-10.5, loaded.cameraX(), 0.0001);
            assertEquals(45.25, loaded.cameraY(), 0.0001);
            assertEquals(2.2, loaded.zoom(), 0.0001);
            assertEquals(2, loaded.agents().size());
            assertEquals("Car", loaded.agents().get(0).getTypeName());
            assertEquals(310, loaded.agents().get(0).getId());
            assertEquals("Boat", loaded.agents().get(1).getTypeName());
            assertEquals(311, loaded.agents().get(1).getId());
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void loadFallsBackToFirstKnownLayoutWhenNameMissing() throws IOException {
        Path tempFile = Files.createTempFile("omniflow-layout-fallback", ".txt");
        try {
            Files.writeString(tempFile, "layout=DoesNotExist\n");
            Map<String, MapLayout> layouts = new LinkedHashMap<>();
            layouts.put("Intersection", new IntersectionLayout());
            layouts.put("Harbor", new HarborLayout());

            LayoutStore.LoadedLayout loaded = LayoutStore.load(tempFile, layouts);
            assertEquals("Intersection", loaded.layoutName());
            assertTrue(loaded.agents().isEmpty());
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
