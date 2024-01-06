package xyz.immortius.util;

import com.google.common.collect.ImmutableMap;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.Map;

public enum Direction {
    Up('U', new Vector2i(0, 1)),
    Left('L', new Vector2i(-1, 0)),
    Down('D', new Vector2i(0, -1)),
    Right('R', new Vector2i(1, 0));

    private static final Map<Character, Direction> idLookup;

    private char id;
    private Vector2ic vector;

     Direction(char c, Vector2ic vector) {
         this.id = c;
         this.vector = vector;
     }

     static {
         ImmutableMap.Builder<Character, Direction> builder = new ImmutableMap.Builder<>();
         for (Direction direction : Direction.values()) {
             builder.put(direction.id, direction);
         }
         idLookup = builder.build();
     }

     public Vector2ic toVector() {
         return vector;
     }

    public char getId() {
        return id;
    }

    public Direction clockwise() {
         return Direction.values()[(ordinal() + 1) % Direction.values().length];
    }

    public Direction anticlockwise() {
        return Direction.values()[(ordinal() + Direction.values().length - 1) % Direction.values().length];
    }

    public Direction reverse() {
        return Direction.values()[(ordinal() + 2) % Direction.values().length];
    }

    public static Direction parse(char c) {
         return idLookup.get(c);
     }
}
