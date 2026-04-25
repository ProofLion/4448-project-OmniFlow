package sim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import util.Vec2;

class Camera2DTest {
    @Test
    void worldToScreenRoundTripIsConsistent() {
        Camera2D camera = new Camera2D();
        Vec2 world = new Vec2(25, -30);

        Vec2 screen = camera.worldToScreen(world);
        Vec2 recovered = camera.screenToWorld(screen.x, screen.y);

        assertTrue(Math.abs(world.x - recovered.x) < 0.0001);
        assertTrue(Math.abs(world.y - recovered.y) < 0.0001);
    }

    @Test
    void zoomIsClamped() {
        Camera2D camera = new Camera2D();
        camera.setState(0, 0, 100);
        assertEquals(6.0, camera.getZoom());
    }
}
