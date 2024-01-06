package xyz.immortius.advent2023.day11;

import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.util.Vector2l;
import xyz.immortius.util.Vector2lc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Day11 {

    //private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    private static final String YEAR = "2023";
    private static final String DAY = "11";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day11().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Vector2lc> input = parse(lines);
        part1(input);
        part2(input);
    }

    private List<Vector2lc> parse(List<String> lines) {
        List<Vector2lc> galaxies = new ArrayList<>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == '#') {
                    galaxies.add(new Vector2l(x, y));
                }
            }
        }
        return galaxies;
    }

    private void part1(List<Vector2lc> input) {
        List<Vector2lc> galaxies = expand(input, 2);

        long dist = totalDistance(galaxies);

        System.out.println("Part 1: " + dist);
    }

    private void part2(List<Vector2lc> input) {
        List<Vector2lc> galaxies = expand(input, 1_000_000);

        long dist = totalDistance(galaxies);

        System.out.println("Part 2: " + dist);
    }

    private long totalDistance(List<Vector2lc> galaxies) {
        long dist = 0;
        for (int i = 0; i < galaxies.size() - 1; i++) {
            for (int j = i + 1; j < galaxies.size(); j++) {
                dist += galaxies.get(i).gridDistance(galaxies.get(j));
            }
        }
        return dist;
    }

    private List<Vector2lc> expand(List<Vector2lc> input, int factor) {
        Vector2i max = size(input);

        boolean[] filledX = new boolean[max.x() + 1];
        boolean[] filledY = new boolean[max.y() + 1];

        for (Vector2lc pos : input) {
            filledX[(int) pos.x()] = true;
            filledY[(int) pos.y()] = true;
        }

        long[] xOffsets = calcExpansionOffsets(filledX, factor);
        long[] yOffsets = calcExpansionOffsets(filledY, factor);
        return input.stream().<Vector2lc>map(vec -> new Vector2l(vec.x() + xOffsets[(int) vec.x()], vec.y() + yOffsets[(int) vec.y()])).toList();
    }

    @NotNull
    private Vector2i size(List<Vector2lc> input) {
        Vector2l max = new Vector2l(Integer.MIN_VALUE);
        for (Vector2lc pos : input) {
            max.max(pos);
        }
        return new Vector2i((int)max.x, (int)max.y);
    }

    private long[] calcExpansionOffsets(boolean[] filled, long factor) {
        long[] offsets = new long[filled.length];
        long rollingOffset = 0;
        for (int i = 0; i < filled.length; i++) {
            offsets[i] = rollingOffset;
            if (!filled[i]) {
                rollingOffset += factor - 1;
            }
        }
        return offsets;
    }


}

