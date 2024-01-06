package xyz.immortius.advent2015.day17;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Day17 {

    private static final String YEAR = "2015";
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

        List<Integer> containers = parse(lines);
        part1(containers);
        part2(containers);
    }

    private List<Integer> parse(List<String> lines) {
        return lines.stream().map(Integer::parseInt).toList();
    }

    private void part1(List<Integer> originalContainers) {
        List<Integer> containers = new ArrayList<>(originalContainers);
        containers.sort(Comparator.reverseOrder());
        int arrangements = combinationsOf(containers, 150);

        System.out.println("Part 1: " + arrangements);
    }

    private int combinationsOf(List<Integer> containers, int amount) {
        int count = 0;
        for (int i = 0; i < containers.size(); i++) {
            int containerSize = containers.get(i);
            if (containerSize < amount) {
                int residual = amount - containerSize;
                count += combinationsOf(containers.subList(i + 1, containers.size()), residual);
            }
            else if (containerSize == amount) {
                count++;
            }
        }
        return count;
    }

    private void part2(List<Integer> originalContainers) {
        List<Integer> containers = new ArrayList<>(originalContainers);
        containers.sort(Comparator.reverseOrder());
        List<List<Integer>> combinations = detailedCombinationsOf(containers, 150);
        int shortest = combinations.stream().map(List::size).min(Integer::compareTo).get();
        long count = combinations.stream().filter(x -> x.size() == shortest).count();

        System.out.println("Part 2: " + count);
    }

    private List<List<Integer>> detailedCombinationsOf(List<Integer> containers, int amount) {
        List<List<Integer>> combinations = new ArrayList<>();
        for (int i = 0; i < containers.size(); i++) {
            int containerSize = containers.get(i);
            if (containerSize < amount) {
                int residual = amount - containerSize;
                List<List<Integer>> residualCombinations = detailedCombinationsOf(containers.subList(i + 1, containers.size()), residual);
                for (List<Integer> combo : residualCombinations) {
                    List<Integer> fullCombo = new ArrayList<>(combo);
                    fullCombo.add(containerSize);
                    combinations.add(fullCombo);
                }
            }
            else if (containerSize == amount) {
                combinations.add(Collections.singletonList(containerSize));
            }
        }
        return combinations;
    }

}

