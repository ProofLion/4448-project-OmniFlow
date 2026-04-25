package model.layouts;

import model.Agent;
import model.TravelAxis;
import util.Vec2;

final class LayoutSupport {
    private LayoutSupport() {
    }

    static TravelAxis getAxis(Agent agent) {
        return TravelAxis.fromVelocity(agent.getVelocity());
    }

    static boolean isWithinDirectionalWindow(Agent agent, double minCoordinate, double maxCoordinate) {
        Vec2 position = agent.getPosition();
        Vec2 velocity = agent.getVelocity();

        if (Math.abs(velocity.x) >= Math.abs(velocity.y)) {
            double x = position.x;
            return velocity.x > 0
                ? x >= -maxCoordinate && x <= -minCoordinate
                : x <= maxCoordinate && x >= minCoordinate;
        }

        double y = position.y;
        return velocity.y > 0
            ? y >= -maxCoordinate && y <= -minCoordinate
            : y <= maxCoordinate && y >= minCoordinate;
    }

    static void recycleAlongRoute(Agent agent, double minX, double maxX, double minY, double maxY) {
        Vec2 position = agent.getPosition();
        Vec2 velocity = agent.getVelocity();
        if (Math.abs(velocity.x) >= Math.abs(velocity.y)) {
            if (velocity.x >= 0 && position.x > maxX) {
                agent.setPosition(new Vec2(minX, position.y));
            } else if (velocity.x < 0 && position.x < minX) {
                agent.setPosition(new Vec2(maxX, position.y));
            }
        } else if (velocity.y >= 0 && position.y > maxY) {
            agent.setPosition(new Vec2(position.x, minY));
        } else if (velocity.y < 0 && position.y < minY) {
            agent.setPosition(new Vec2(position.x, maxY));
        }
    }
}
