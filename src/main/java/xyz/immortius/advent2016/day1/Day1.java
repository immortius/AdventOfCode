package xyz.immortius.advent2016.day1;

import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day1 {

    private static final String YEAR = "2016";
    private static final String DAY = "1";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day1().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Step> steps = parse(lines.get(0));
        part1(steps);
        part2(steps);
    }

    private List<Step> parse(String line) {
        String[] directions = line.split(", ");
        List<Step> result = new ArrayList<>();
        for (String dir : directions) {
            boolean clockwise = dir.charAt(0) == 'R';
            int distance = Integer.parseInt(dir.substring(1));
            result.add(new Step(clockwise, distance));
        }
        return result;
    }

    private void part1(List<Step> steps) {

        CardinalDirection dir = CardinalDirection.North;
        Vector2i pos = new Vector2i();
        for (Step step : steps) {
            dir = (step.clockwise) ? dir.turnClockwise() : dir.turnAnticlockwise();
            pos.add(dir.toVector().mul(step.distance, new Vector2i()));
        }

        System.out.println("Part 1: " + pos.gridDistance(0,0));
    }

    private void part2(List<Step> steps) {

        CardinalDirection dir = CardinalDirection.North;
        Vector2i pos = new Vector2i();
        Set<Vector2ic> visited = new HashSet<>();
        visited.add(new Vector2i(pos));
        for (Step step : steps) {
            dir = (step.clockwise) ? dir.turnClockwise() : dir.turnAnticlockwise();
            boolean found = false;
            for (int i = 0; i < step.distance; i++) {
                pos.add(dir.toVector());
                if (!visited.add(new Vector2i(pos))) {
                    found = true;
                    break;
                }
            }
            if (found) {
                break;
            }
        }

        System.out.println("Part 2: " + pos.gridDistance(0,0));
    }


    private record Step(boolean clockwise, int distance) {}

}

