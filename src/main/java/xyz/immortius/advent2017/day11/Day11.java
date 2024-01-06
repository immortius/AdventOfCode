package xyz.immortius.advent2017.day11;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day11 {

    private static final String YEAR = "2017";
    private static final String DAY = "11";
    private static final boolean REAL_INPUT = false;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day11().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<HexDirection> steps = Arrays.stream(lines.get(0).split(",")).map(HexDirection::parse).toList();
        part1(steps);
        part2(steps);
    }

    private void part1(List<HexDirection> steps) {
        Vector2i pos = new Vector2i(0,0);
        for (HexDirection step : steps) {
            pos.add(step.toVector());
        }
        System.out.println("Part 1: " + hexDistance(pos, new Vector2i()));
    }

    private int hexDistance(Vector2ic pos, Vector2ic dest) {
        Vector2i diff = pos.sub(dest, new Vector2i());
        int dist = Math.abs(diff.x);
        if (diff.x > 0 && diff.y >=0) {
            return Math.max(dist, dist + diff.y - diff.x);
        } else if (diff.x < 0 && diff.y <= 0) {
            return Math.max(dist, dist + diff.x - diff.y);
        }
        dist += Math.abs(diff.y);
        return dist;
    }

    private void part2(List<HexDirection> steps) {
        int max = 0;
        Vector2ic zero = new Vector2i();
        Vector2i pos = new Vector2i(0,0);
        for (HexDirection step : steps) {
            pos.add(step.toVector());
            max = Math.max(max, hexDistance(pos, zero));
        }
        System.out.println("Part 2: " + max);
    }

    public enum HexDirection {
        North("n", new Vector2i(0, 1)),
        NorthEast("ne", new Vector2i(1, 1)),
        SouthEast("se", new Vector2i(1, 0)),
        South("s", new Vector2i(0, -1)),
        SouthWest("sw", new Vector2i(-1, -1)),
        NorthWest("nw", new Vector2i(-1, 0));

        private static final Map<String, HexDirection> lookup;
        private final String id;
        private final Vector2ic vector;

        static {
            lookup = ImmutableMap.copyOf(Arrays.stream(values()).collect(Collectors.toMap(HexDirection::getId, x -> x)));
        }

        HexDirection(String id, Vector2ic vector) {
            this.id = id;
            this.vector = vector;
        }

        public String getId() {
            return id;
        }

        public Vector2ic toVector() {
            return vector;
        }

        public static HexDirection parse(String id) {
            return lookup.get(id);
        }
    }


}

