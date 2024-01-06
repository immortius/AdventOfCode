package xyz.immortius.advent2022.day15;

import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.Arrays;

public class GridMap<T> {
    private final Vector2i offset;
    private final T[][] map;
    private final Vector2i upperBound;
    private final T defaultValue;

    public GridMap(Vector2ic min, Vector2ic max, T defaultValue) {
        this.defaultValue = defaultValue;
        this.offset = new Vector2i(min);
        this.map = (T[][]) new Object[max.y() - min.y() + 1][max.x() - min.x() + 1];
        upperBound = new Vector2i(max);
        for (T[] contents : map) {
            Arrays.fill(contents, defaultValue);
        }
    }

    public GridMap(GridMap<T> other) {
        this.offset = new Vector2i(other.offset);
        this.upperBound = new Vector2i(other.upperBound);
        this.defaultValue = other.defaultValue;
        this.map = (T[][]) new Object[other.map.length][];
        for (int i = 0; i < map.length; i++) {
            map[i] = Arrays.copyOf(other.map[i], other.map[i].length);
        }
    }

    public boolean isInbounds(int x, int y) {
        return x - offset.x >= 0 && y - offset.y >= 0 && x <= upperBound.x && y <= upperBound.y;
    }

    public T get(int x, int y) {
        if (isInbounds(x, y)) {
            return map[y - offset.y][x - offset.x];
        }
        return defaultValue;
    }

    public void set(Vector2ic pos, T value) {
        set(pos.x(), pos.y(), value);
    }

    public void set(int x, int y, T value) {
        if (isInbounds(x, y)) {
            map[y - offset.y][x - offset.x] = value;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (T[] row : map) {
            for (T c : row) {
                builder.append(c.toString());
            }
            builder.append('\n');
        }
        return builder.toString();
    }

    public int count(T value) {
        int count = 0;
        for (T[] row : map) {
            for (T v : row) {
                if (v == value) {
                    count++;
                }
            }
        }
        return count;
    }
}
