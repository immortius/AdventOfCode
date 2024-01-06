package xyz.immortius.advent2016.day1;

import com.google.common.collect.ImmutableMap;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.Map;

public enum CardinalDirection {
    North('N', new Vector2i(0, 1)),
    East('E', new Vector2i(1, 0)),
    South('S', new Vector2i(0, -1)),
    West('W', new Vector2i(-1, 0));

    private static final Map<Character, CardinalDirection> idLookup;

    private char id;
    private Vector2ic vector;

    CardinalDirection(char c, Vector2ic vector) {
        this.id = c;
        this.vector = vector;
    }

    public CardinalDirection turnClockwise() {
        return CardinalDirection.values()[(this.ordinal() + 1) % CardinalDirection.values().length];
    }

    public CardinalDirection turnAnticlockwise() {
        return CardinalDirection.values()[(this.ordinal() - 1 + CardinalDirection.values().length) % CardinalDirection.values().length];
    }

    static {
        ImmutableMap.Builder<Character, CardinalDirection> builder = new ImmutableMap.Builder<>();
        for (CardinalDirection direction : CardinalDirection.values()) {
            builder.put(direction.id, direction);
        }
        idLookup = builder.build();
    }

    public Vector2ic toVector() {
        return vector;
    }
}
