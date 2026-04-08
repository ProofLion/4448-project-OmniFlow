package model;

import util.Vec2;

/**
 * Small helper used by agents and layouts to reason about horizontal vs vertical routes.
 */
public enum TravelAxis {
    HORIZONTAL,
    VERTICAL;

    public static TravelAxis fromVelocity(Vec2 velocity) {
        return Math.abs(velocity.x) >= Math.abs(velocity.y) ? HORIZONTAL : VERTICAL;
    }
}
