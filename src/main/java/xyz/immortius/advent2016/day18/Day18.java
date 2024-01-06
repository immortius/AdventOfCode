package xyz.immortius.advent2016.day18;

import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

public class Day18 {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day18().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private Map<List<Boolean>, Boolean> rules = ImmutableMap.<List<Boolean>, Boolean>builder()
            .put(List.of(false, false, false), false)
            .put(List.of(false, false, true), true)
            .put(List.of(false, true, false), false)
            .put(List.of(false, true, true), true)
            .put(List.of(true, false, false), true)
            .put(List.of(true, false, true), false)
            .put(List.of(true, true, false), true)
            .put(List.of(true, true, true), false)
            .build();

    private boolean[] rules2 = new boolean[] {false, true, false, true, true, false, true, false};

    private void run() throws IOException {
        example();
        part1();
        part2();
    }

    private void example() {
        boolean[] initialInput = toRow(".^^.^.^^^^");

        List<boolean[]> floor = generateFloor(initialInput, 10);
        int totalSafe = 0;
        for (boolean[] row : floor) {
            System.out.println(toString(row));
            for (boolean b : row) {
                if (!b) totalSafe++;
            }
        }

        System.out.println("Example: " + totalSafe);
    }

    private void part1() {
        boolean[] initialInput = toRow("...^^^^^..^...^...^^^^^^...^.^^^.^.^.^^.^^^.....^.^^^...^^^^^^.....^.^^...^^^^^...^.^^^.^^......^^^^");

        List<boolean[]> floor = generateFloor(initialInput, 40);
        int totalSafe = 0;
        for (boolean[] row : floor) {
            for (boolean b : row) {
                if (!b) totalSafe++;
            }
        }

        System.out.println("Part 1: " + totalSafe);
    }

    private void part2() {
        boolean[] initialInput = toRow("...^^^^^..^...^...^^^^^^...^.^^^.^.^.^^.^^^.....^.^^^...^^^^^^.....^.^^...^^^^^...^.^^^.^^......^^^^");

        List<boolean[]> floor = generateFloor(initialInput, 400000);
        int totalSafe = 0;
        for (boolean[] row : floor) {
            for (boolean b : row) {
                if (!b) totalSafe++;
            }
        }

        System.out.println("Part 1: " + totalSafe);
    }

    private List<boolean[]> generateFloor(boolean[] initialInput, int rows) {
        List<boolean[]> floor = new ArrayList<>();
        floor.add(initialInput);
        while (floor.size() < rows) {
            boolean[] last = new boolean[initialInput.length + 2];
            System.arraycopy(floor.get(floor.size() - 1), 0, last, 1, initialInput.length);

            boolean[] row = new boolean[initialInput.length];

            for (int tile = 0; tile < row.length; tile++) {
                int ruleIndex = 0;
                for (int i = 0; i < 3; i++) {
                    ruleIndex = ruleIndex << 1;
                    if (last[tile + i]) {
                        ruleIndex += 1;
                    }
                }
                row[tile] = rules2[ruleIndex];
            }

            floor.add(row);
        }
        return floor;
    }

    private String toString(boolean[] row) {
        StringBuilder builder = new StringBuilder();
        for (boolean b : row) {
            builder.append(b ? '^' : '.');
        }
        return builder.toString();
    }

    private boolean[] toRow(String s) {
        boolean[] result = new boolean[s.length()];
        for (int i = 0; i < s.length(); i++) {
            result[i] = s.charAt(i) == '^';
        }
        return result;
    }


}

