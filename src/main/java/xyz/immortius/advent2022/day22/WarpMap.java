package xyz.immortius.advent2022.day22;

import org.joml.Vector2i;
import org.joml.Vector2ic;

public class WarpMap {
    private final int dimX;
    private final int dimY;
    private final Vector2i offset;
    private final Boolean[][] map;
    private final Vector2i upperBound;

    public WarpMap(Vector2ic min, Vector2ic max) {
        this.offset = new Vector2i(min);
        this.map = new Boolean[max.y() - min.y() + 1][max.x() - min.x() + 1];
        upperBound = new Vector2i(max);
        dimX = map[0].length;
        dimY = map.length;
    }

    public boolean isInbounds(int x, int y) {
        return x - offset.x >= 0 && y - offset.y >= 0 && x <= upperBound.x && y <= upperBound.y;
    }

    public Boolean get(int x, int y) {
        if (isInbounds(x, y)) {
            return map[y - offset.y][x - offset.x];
        }
        return null;
    }

    public Vector2i nextPos(Vector2ic pos, Vector2ic dir) {
        if (get(pos.x(), pos.y())) {
            int x = pos.x() - offset.x;
            int y = pos.y() - offset.y;
            x = (x + dir.x() + dimX) % dimX;
            y = (y + dir.y() + dimY) % dimY;
            while (map[y][x] == null) {
                x = (x + dir.x() + dimX) % dimX;
                y = (y + dir.y() + dimY) % dimY;
            }
            return new Vector2i(x + offset.x, y + offset.y);
        }
        return new Vector2i(pos);
    }

    public void set(Vector2ic pos, Boolean value) {
        set(pos.x(), pos.y(), value);
    }

    public void set(int x, int y, Boolean value) {
        if (isInbounds(x, y)) {
            map[y - offset.y][x - offset.x] = value;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Boolean[] row : map) {
            for (Boolean c : row) {
                if (c == null) {
                    builder.append(' ');
                } else if (c) {
                    builder.append('.');
                } else {
                    builder.append('#');
                }
            }
            builder.append('\n');
        }
        return builder.toString();
    }
}
