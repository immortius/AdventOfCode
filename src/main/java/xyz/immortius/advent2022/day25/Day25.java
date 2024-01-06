package xyz.immortius.advent2022.day25;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Day25 {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day25().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day25/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        part1(lines);
    }

    private long toDecimal(String snafu) {
        long value = 0;
        long significance = 1;
        for (int i = 0; i < snafu.length(); i++) {
            char digit = snafu.charAt(snafu.length() - 1 - i);
            value += switch (digit) {
                case '2' -> significance * 2;
                case '1' -> significance;
                case '-' -> significance * -1;
                case '=' -> significance * -2;
                default -> 0;
            };
            significance *= 5;
        }
        return value;
    }

    private String toSnafu(long decimal) {
        long value = decimal;
        StringBuilder builder = new StringBuilder();
        while (value > 0) {
            int remainder = (int) (value % 5);
            switch (remainder) {
                case 0 -> builder.insert(0, '0');
                case 1 -> builder.insert(0, '1');
                case 2 -> builder.insert(0, '2');
                case 3 -> {
                    builder.insert(0, '=');
                    value += 2;
                }
                case 4 -> {
                    builder.insert(0, '-');
                    value += 1;
                }
            }
            value = value / 5;
        }
        return builder.toString();

    }

    private void part1(List<String> values) {
        long total = values.stream().map(this::toDecimal).reduce(0L, Long::sum);
        System.out.println("Part 1: " + toSnafu(total));
    }

}