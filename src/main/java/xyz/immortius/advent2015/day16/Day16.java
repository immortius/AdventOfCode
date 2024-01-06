package xyz.immortius.advent2015.day16;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day16 {

    private static final String YEAR = "2015";
    private static final String DAY = "16";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day16().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Map<String, Integer>> sueData = parse(lines);
        part1(sueData);
        part2(sueData);
    }

    private List<Map<String, Integer>> parse(List<String> lines) {
        List<Map<String, Integer>> sueData = new ArrayList<>();
        for (String line : lines) {
            Map<String, Integer> sue = new HashMap<>();
            int endHeader = line.indexOf(':');
            line = line.substring(endHeader + 2);
            String[] attributes = line.split(", ");
            for (String attribute : attributes) {
                String[] parts = attribute.split(": ");
                sue.put(parts[0], Integer.parseInt(parts[1]));
            }
            sueData.add(sue);
        }
        return sueData;
    }

    private void part2(List<Map<String, Integer>> sueData) {

        Map<String, Integer> expected = ImmutableMap.of("children", 3, "samoyeds", 2, "akitas", 0, "vizslas", 0, "cars", 2, "perfumes", 1);
        Map<String, Integer> gt = ImmutableMap.of("cats", 7, "trees", 3);
        Map<String, Integer> lt = ImmutableMap.of("pomeranians", 3, "goldfish", 5);

        List<Integer> possibleSues = new ArrayList<>();
        for (int i = 0; i < sueData.size(); i++) {
            Map<String, Integer> sue = sueData.get(i);
            boolean possibleSue = true;
            for (Map.Entry<String, Integer> entry : expected.entrySet()) {
                if (sue.containsKey(entry.getKey())) {
                    if (!sue.get(entry.getKey()).equals(entry.getValue())) {
                        possibleSue = false;
                        break;
                    }
                }
            }
            for (Map.Entry<String, Integer> entry : gt.entrySet()) {
                if (sue.containsKey(entry.getKey())) {
                    if (sue.get(entry.getKey()) <= (entry.getValue())) {
                        possibleSue = false;
                        break;
                    }
                }
            }
            for (Map.Entry<String, Integer> entry : lt.entrySet()) {
                if (sue.containsKey(entry.getKey())) {
                    if (sue.get(entry.getKey()) >= (entry.getValue())) {
                        possibleSue = false;
                        break;
                    }
                }
            }
            if (possibleSue) {
                possibleSues.add(i + 1);
            }
        }

        System.out.println("Part 2: " + possibleSues);
    }

    private void part1(List<Map<String, Integer>> sueData) {
        Map<String, Integer> expected = ImmutableMap.of("children", 3, "cats", 7, "samoyeds", 2, "pomeranians", 3, "akitas", 0, "vizslas", 0, "goldfish", 5, "trees", 3, "cars", 2, "perfumes", 1);

        List<Integer> possibleSues = new ArrayList<>();
        for (int i = 0; i < sueData.size(); i++) {
            Map<String, Integer> sue = sueData.get(i);
            boolean possibleSue = true;
            for (Map.Entry<String, Integer> entry : expected.entrySet()) {
                if (sue.containsKey(entry.getKey())) {
                    if (!sue.get(entry.getKey()).equals(entry.getValue())) {
                        possibleSue = false;
                        break;
                    }
                }
            }
            if (possibleSue) {
                possibleSues.add(i + 1);
            }
        }

        System.out.println("Part 1: " + possibleSues);
    }

}

