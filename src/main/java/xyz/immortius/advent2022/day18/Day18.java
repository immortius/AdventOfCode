package xyz.immortius.advent2022.day18;

import com.google.common.io.CharStreams;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day18 {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day18().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day18/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        List<Vector3i> cubes = parse(lines);
        part1(cubes);
        part2(cubes);
    }

    private void part1(List<Vector3i> cubes) {
        Vector3i min = cubes.stream().reduce(new Vector3i(cubes.get(0)), (a, b) -> new Vector3i(a).min(b));
        Vector3i max = cubes.stream().reduce(new Vector3i(cubes.get(0)), (a, b) -> new Vector3i(a).max(b));
        System.out.println("Min: " + min.x + "," + min.y + "," + min.z + ", Max: " + max.x + "," + max.y + "," + max.z);

        Grid3D<Boolean> grid = new Grid3D<>(max.sub(min, new Vector3i()).add(3,3,3), min.sub(1, 1, 1, new Vector3i()), false, false);
        for (Vector3i cube : cubes) {
            grid.set(cube, true);
        }

        int exposedSides = 0;
        Vector3i pos = new Vector3i();
        Vector3i adjPos = new Vector3i();
        for (pos.z = min.z; pos.z <= max.z; pos.z++) {
            for (pos.y = min.y; pos.y <= max.y; pos.y++) {
                for (pos.x = min.x; pos.x <= max.x; pos.x++) {
                    if (grid.get(pos)) {
                        for (Side side : Side.values()) {
                            pos.add(side.toVector(), adjPos);
                            if (!grid.get(adjPos)) {
                                exposedSides++;
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Part 1: " + exposedSides);
    }

    private void part2(List<Vector3i> cubes) {
        Vector3i min = cubes.stream().reduce(new Vector3i(cubes.get(0)), (a, b) -> new Vector3i(a).min(b));
        Vector3i max = cubes.stream().reduce(new Vector3i(cubes.get(0)), (a, b) -> new Vector3i(a).max(b));
        System.out.println("Min: " + min.x + "," + min.y + "," + min.z + ", Max: " + max.x + "," + max.y + "," + max.z);

        Grid3D<Content> grid = new Grid3D<>(max.sub(min, new Vector3i()).add(3,3,3), min.sub(1, 1, 1, new Vector3i()), Content.AIR, Content.WATER);
        for (Vector3i cube : cubes) {
            grid.set(cube, Content.ROCK);
        }

        Deque<Vector3ic> spreadTo = new ArrayDeque<>();
        spreadTo.push(min.sub(1, 1, 1, new Vector3i()));
        while (!spreadTo.isEmpty()) {
            Vector3ic pos = spreadTo.pop();
            grid.set(pos, Content.WATER);
            for (Side side : Side.values()) {
                Vector3i adjPos = pos.add(side.toVector(), new Vector3i());
                if (grid.get(adjPos) == Content.AIR) {
                    spreadTo.push(adjPos);
                }
            }
        }

        int exposedSides = 0;
        Vector3i pos = new Vector3i();
        Vector3i adjPos = new Vector3i();
        for (pos.z = min.z; pos.z <= max.z; pos.z++) {
            for (pos.y = min.y; pos.y <= max.y; pos.y++) {
                for (pos.x = min.x; pos.x <= max.x; pos.x++) {
                    if (grid.get(pos) == Content.ROCK) {
                        for (Side side : Side.values()) {
                            pos.add(side.toVector(), adjPos);
                            if (grid.get(adjPos) == Content.WATER) {
                                exposedSides++;
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Part 2: " + exposedSides);
    }

    private List<Vector3i> parse(List<String> lines) {
        List<Vector3i> cubes = new ArrayList<>();
        for (String line : lines) {
            String[] dims = line.split(",");
            cubes.add(new Vector3i(Integer.parseInt(dims[0]), Integer.parseInt(dims[1]), Integer.parseInt(dims[2])));
        }
        return cubes;
    }

    private static class Grid3D<T> {
        private final T[] values;
        private final Vector3i dimensions;
        private final Vector3i offset;
        private final T outOfBounds;

        public Grid3D(Vector3i dimensions, Vector3i offset, T defaultValue, T outOfBounds) {
            this.dimensions = new Vector3i(dimensions);
            this.offset = new Vector3i(offset);
            this.outOfBounds = outOfBounds;
            values = (T[]) new Object[dimensions.x * dimensions.y * dimensions.z];
            Arrays.fill(values, defaultValue);
        }

        public boolean isInBounds(Vector3ic v) {
            Vector3i adjusted = adjust(v);
            return adjusted.x >= 0 && adjusted.y >= 0 && adjusted.z >= 0 && adjusted.x < dimensions.x && adjusted.y < dimensions.y && adjusted.z < dimensions.z;
        }

        public void set(Vector3ic pos, T value) {
            int index = indexFor(pos);
            values[index] = value;
        }

        public T get(Vector3ic pos) {
            if (!isInBounds(pos)) {
                return outOfBounds;
            }
            return values[indexFor(pos)];
        }

        private Vector3i adjust(Vector3ic v) {
            return v.sub(offset, new Vector3i());
        }

        private int indexFor(Vector3ic v) {
            Vector3i adjusted = adjust(v);
            return adjusted.x + dimensions.x * (adjusted.y + dimensions.y * adjusted.z);
        }
    }

    private enum Content {
        AIR,
        WATER,
        ROCK
    }

    private enum Side {
        TOP(new Vector3i(0, 1, 0)),
        BOTTOM(new Vector3i(0, -1, 0)),
        FRONT(new Vector3i(0, 0, 1)),
        BACK(new Vector3i(0, 0, -1)),
        LEFT(new Vector3i(-1, 0, 0)),
        RIGHT(new Vector3i(1, 0, 0));

        private Vector3ic vector;

        private Side(Vector3ic vec) {
            this.vector = vec;
        }

        public Vector3ic toVector() {
            return vector;
        }
    }

}
