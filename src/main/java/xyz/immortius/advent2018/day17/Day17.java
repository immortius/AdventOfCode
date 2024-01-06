package xyz.immortius.advent2018.day17;

import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.advent2022.day15.GridMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Day17 {

    private static final String YEAR = "2018";
    private static final String DAY = "17";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day17().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        Input input = parse(lines);
        part1(input);
    }

    private Input parse(List<String> lines) {

        Vector2i min = new Vector2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Vector2i max = new Vector2i(Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (String line : lines) {
            String[] dims = line.split(",\\s+");
            int[] range = Arrays.stream(dims[1].substring(2).split("\\.\\.")).mapToInt(Integer::parseInt).toArray();
            boolean xCentric = line.startsWith("x");
            int minX = (xCentric) ? Integer.parseInt(dims[0].substring(2)) : range[0];
            int maxX = (xCentric) ? minX : range[1];
            int minY = (xCentric) ? range[0] : Integer.parseInt(dims[0].substring(2));
            int maxY = (xCentric) ? range[1] : minY;
            min.min(new Vector2i(minX, minY));
            max.max(new Vector2i(maxX, maxY));
        }
        min.x -= 1;
        max.x += 1;

        GridMap<Content> map = new GridMap<>(min, max, Content.AIR);
        for (String line : lines) {
            String[] dims = line.split(",\\s+");
            int[] range = Arrays.stream(dims[1].substring(2).split("\\.\\.")).mapToInt(Integer::parseInt).toArray();
            boolean xCentric = line.startsWith("x");
            int minX = (xCentric) ? Integer.parseInt(dims[0].substring(2)) : range[0];
            int maxX = (xCentric) ? minX : range[1];
            int minY = (xCentric) ? range[0] : Integer.parseInt(dims[0].substring(2));
            int maxY = (xCentric) ? range[1] : minY;

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    map.set(x, y, Content.CLAY);
                }
            }
        }
        return new Input(min, max, map);
    }

    private void part1(Input input) {
        FlowSimulator simulator = new FlowSimulator(input.map);
        Vector2ic waterStart = new Vector2i(500, Math.max(Math.min(0, input.max.y()), input.min.y()));
        simulator.run(waterStart);

        int water = simulator.map.count(Content.WATER) + simulator.map.count(Content.FLOWING_WATER);
        System.out.println("Part 1: " + water);
        System.out.println("Part 2: " + simulator.map.count(Content.WATER));
    }

    private static class FlowSimulator {
        private GridMap<Content> map;
        private PriorityQueue<Vector2ic> spreadingWater = new PriorityQueue<>(Comparator.comparingInt(Vector2ic::y).reversed());

        public FlowSimulator(GridMap<Content> map) {
            this.map = new GridMap<>(map);
        }

        public void run(Vector2ic waterStart) {
            flowDown(waterStart);
            while (!spreadingWater.isEmpty()) {
                Vector2ic loc = spreadingWater.poll();
                spread(loc);
            }
            System.out.println(map);
        }

        private void spread(Vector2ic pos) {
            if (!map.isInbounds(pos.x(), pos.y() + 1) || !map.get(pos.x(), pos.y() + 1).isBlocking()) {
                // Flow out of the map infinitely, no spread
                return;
            }
            boolean leftBlocked = false;
            boolean rightBlocked = false;
            for (int x = pos.x() - 1; map.isInbounds(x, pos.y()); x--) {
                if (map.get(x, pos.y()).isBlocking()) {
                    leftBlocked = true;
                    break;
                } else if (!map.get(x, pos.y() + 1).isBlocking()) {
                    break;
                }
            }
            for (int x = pos.x() + 1; map.isInbounds(x, pos.y()); x++) {
                if (map.get(x, pos.y()).isBlocking()) {
                    rightBlocked = true;
                    break;
                } else if (!map.get(x, pos.y() + 1).isBlocking()) {
                    break;
                }
            }
            if (leftBlocked && rightBlocked) {
                map.set(pos.x(), pos.y(), Content.WATER);
                for (int x = pos.x() - 1; !map.get(x, pos.y()).isBlocking(); x--) {
                    map.set(x, pos.y(), Content.WATER);
                }
                for (int x = pos.x() + 1; !map.get(x, pos.y()).isBlocking(); x++) {
                    map.set(x, pos.y(), Content.WATER);
                }
            } else {
                for (int x = pos.x() - 1; map.isInbounds(x, pos.y()) && !map.get(x, pos.y()).isBlocking(); x--) {
                    if (!map.get(x, pos.y() + 1).isBlocking()) {
                        flowDown(new Vector2i(x, pos.y()));
                        break;
                    } else {
                        map.set(x, pos.y(), Content.FLOWING_WATER);
                    }
                }
                for (int x = pos.x() + 1; map.isInbounds(x, pos.y()) && !map.get(x, pos.y()).isBlocking(); x++) {
                    if (!map.get(x, pos.y() + 1).isBlocking()) {
                        flowDown(new Vector2i(x, pos.y()));
                        break;
                    } else {
                        map.set(x, pos.y(), Content.FLOWING_WATER);
                    }
                }
            }
        }

        private void flowDown(Vector2ic from) {
            map.set(from.x(), from.y(), Content.FLOWING_WATER);
            spreadingWater.add(new Vector2i(from));
            Vector2i next = new Vector2i(from.x(), from.y() + 1);
            while (map.isInbounds(next.x, next.y) && map.get(next.x, next.y) == Content.AIR) {
                map.set(next.x, next.y, Content.FLOWING_WATER);
                spreadingWater.add(new Vector2i(next));
                next.y++;
            }
        }
    }

    private void part2(List<String> lines) {
        System.out.println("Part 2: ");
    }

    public record Input(Vector2ic min, Vector2ic max, GridMap<Content> map) {}

    public enum Content {
        AIR('.', false),
        FLOWING_WATER('|', false),
        WATER('~', true),
        CLAY('#', true);

        private final char representation;
        private final boolean blocking;

        private Content(char representation, boolean blocking) {
            this.representation = representation;
            this.blocking = blocking;
        }

        public char getRepresentation() {
            return representation;
        }

        public boolean isBlocking() {
            return blocking;
        }

        @Override
        public String toString() {
            return Character.toString(representation);
        }
    }

}

