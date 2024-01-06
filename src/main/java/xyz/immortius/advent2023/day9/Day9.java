package xyz.immortius.advent2023.day9;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day9 {

    private static final String YEAR = "2023";
    private static final String DAY = "9";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day9().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            lines = CharStreams.readLines(reader);
        }

        List<long[]> input = parse(lines);
        long start = System.currentTimeMillis();


        part1(input);
        part2(input);
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private List<long[]> parse(List<String> lines) {
        return lines.stream().map(x -> x.split(" ")).map(x -> Arrays.stream(x).mapToLong(Long::parseLong).toArray()).toList();
    }

    private void part1(List<long[]> input) {
        long total = 0;
        for (long[] sequence : input) {
            long nextValue = getNextValue(sequence);
            total += nextValue;
        }

        System.out.println("Part 1: " + total);
    }

    private void part2(List<long[]> input) {
        long total = 0;
        for (long[] sequence : input) {
            long nextValue = getPrevValue(sequence);
            total += nextValue;
        }

        System.out.println("Part 2: " + total);
    }

    public static long getNextValue(long[] initialSequence) {
        Deque<Long> lastValues = new ArrayDeque<>();
        int length = initialSequence.length;
        long[] sequence = Arrays.copyOf(initialSequence, length);
        boolean allZero = false;
        while (!allZero) {
            lastValues.addLast(sequence[length - 1]);
            length--;
            allZero = true;
            for (int i = 0; i < length; i++) {
                sequence[i] = sequence[i + 1] - sequence[i];
                if (sequence[i] != 0) {
                    allZero = false;
                }
            }
        }
        long result = 0;
        while (!lastValues.isEmpty()) {
            result = result + lastValues.removeLast();
        }
        return result;
    }

    private long getPrevValue(long[] sequence) {
        long[] reverseSequence = new long[sequence.length];
        for (int i = 0; i < sequence.length; i++) {
            reverseSequence[i] = sequence[sequence.length - i - 1];
        }
        return getNextValue(reverseSequence);
    }

}

