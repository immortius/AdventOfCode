package xyz.immortius.advent2017.day2;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day2 {

    private static final String YEAR = "2017";
    private static final String DAY = "2";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day2().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<List<Integer>> input = parse(lines);
        part1(input);
        part2(input);
    }

    private List<List<Integer>> parse(List<String> lines) {
        List<List<Integer>> result = new ArrayList<>();
        for (String line : lines) {
            result.add(Arrays.stream(line.split("\\s+")).map(Integer::parseInt).toList());
        }
        return result;
    }

    private void part1(List<List<Integer>> spreadsheet) {
        int total = 0;
        for (List<Integer> row : spreadsheet) {
            int max = row.stream().max(Integer::compareTo).orElse(0);
            int min = row.stream().min(Integer::compareTo).orElse(0);
            total += max - min;
        }
        System.out.println("Part 1: " + total);
    }

    private void part2(List<List<Integer>> spreadsheet) {
        int total = 0;
        for (List<Integer> row : spreadsheet) {
            for (int a = 0; a < row.size(); a++) {
                for (int b = a + 1; b < row.size(); b++) {
                    int valA = row.get(a);
                    int valB = row.get(b);
                    if (valB % valA == 0) {
                        total += valB / valA;
                    } else if (valA % valB == 0) {
                        total += valA / valB;
                    }
                }
            }
        }
        System.out.println("Part 2: " + total);
    }



}

