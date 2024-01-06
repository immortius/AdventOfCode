package xyz.immortius.advent2019.day6;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day6 {

    private static final String YEAR = "2019";
    private static final String DAY = "6";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day6().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        Map<String, String> orbitMap = parse(lines);
        part1(orbitMap);
        part2(orbitMap);
    }

    private Map<String, String> parse(List<String> lines) {
        Map<String, String> input = new LinkedHashMap<>();
        for (String line : lines) {
            String[] orbitMap = line.split("\\)");
            input.put(orbitMap[1], orbitMap[0]);
        }
        return input;
    }

    private void part1(Map<String, String> orbitMap) {
        int orbits = 0;
        for (String item : orbitMap.keySet()) {
            String parent = orbitMap.get(item);
            while (parent != null) {
                orbits++;
                parent = orbitMap.get(parent);
            }
        }

        System.out.println("Part 1: " + orbits);
    }

    private void part2(Map<String, String> orbitMap) {
        Deque<String> youPath = pathTo("YOU", orbitMap);
        Deque<String> santaPath = pathTo("SAN", orbitMap);
        while (Objects.equals(youPath.peek(), santaPath.peek())) {
            youPath.pop();
            santaPath.pop();
        }
        System.out.println("Part 2: " + (youPath.size() + santaPath.size()));
    }

    private Deque<String> pathTo(String you, Map<String, String> orbitMap) {
        Deque<String> path = new ArrayDeque<>();
        String parent = orbitMap.get(you);
        while (parent != null) {
            path.push(parent);
            parent = orbitMap.get(parent);
        }
        return path;
    }


}

