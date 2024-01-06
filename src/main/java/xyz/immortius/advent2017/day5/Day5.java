package xyz.immortius.advent2017.day5;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class Day5 {

    private static final String YEAR = "2017";
    private static final String DAY = "5";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day5().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        int[] jumps = parse(lines);
        part1(Arrays.copyOf(jumps, jumps.length));
        part2(Arrays.copyOf(jumps, jumps.length));
    }

    private int[] parse(List<String> lines) {
        return lines.stream().map(Integer::parseInt).mapToInt(x -> x).toArray();
    }

    private void part1(int[] jumps) {
        int index = 0;
        int steps = 0;
        while (index >= 0 && index < jumps.length) {
            steps++;
            index += jumps[index]++;
        }
        System.out.println("Part 1: " + steps);
    }

    private void part2(int[] jumps) {
        int index = 0;
        int steps = 0;
        while (index >= 0 && index < jumps.length) {
            steps++;
            int offset = jumps[index];
            if (offset >= 3) {
                jumps[index]--;
            } else {
                jumps[index]++;
            }
            index += offset;
        }
        System.out.println("Part 2: " + steps);
    }



}

