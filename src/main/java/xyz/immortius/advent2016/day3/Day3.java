package xyz.immortius.advent2016.day3;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day3 {

    //private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    private static final String YEAR = "2016";
    private static final String DAY = "3";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day3().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        part1(lines);
        part2(lines);
    }

    private void part1(List<String> lines) {
        int valid = 0;
        for (String line : lines) {
            List<Integer> sides = Arrays.stream(line.trim().split(" +")).map(Integer::parseInt).sorted(Integer::compareTo).toList();
            if (sides.get(0) + sides.get(1) > sides.get(2)) {
                valid++;
            }
        }
        System.out.println("Part 1: " + valid);
    }

    private void part2(List<String> lines) {
        int valid = 0;
        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex += 3) {
            List<List<Integer>> triangles = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                triangles.add(new ArrayList<>());
            }
            for (int i = 0; i < 3; i++) {
                List<Integer> sides = Arrays.stream(lines.get(lineIndex + i).trim().split(" +")).map(Integer::parseInt).toList();
                triangles.get(0).add(sides.get(0));
                triangles.get(1).add(sides.get(1));
                triangles.get(2).add(sides.get(2));
            }
            for (int i = 0; i < 3; i++) {
                List<Integer> triangle = triangles.get(i);
                triangle.sort(Integer::compareTo);
                if (triangle.get(0) + triangle.get(1) > triangle.get(2)) {
                    valid++;
                }
            }
        }
        System.out.println("Part 2: " + valid);
    }

}

