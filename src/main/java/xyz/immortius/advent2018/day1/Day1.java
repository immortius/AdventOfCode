package xyz.immortius.advent2018.day1;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day1 {

    private static final String YEAR = "2018";
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

        part1(lines);
        part2(lines);
    }

    private void part1(List<String> lines) {
        System.out.println("Part 1: " + lines.stream().map(Integer::parseInt).reduce(0, Integer::sum).longValue());
    }

    private void part2(List<String> lines) {
        List<Integer> values = lines.stream().map(Integer::parseInt).toList();

        Set<Integer> frequencies = new HashSet<>();
        int i = 0;
        int total = values.get(0);
        System.out.println(total);
        while (frequencies.add(total)) {
            i = (i + 1) % values.size();
            total += values.get(i);
            System.out.println(total);
        }
        System.out.println("Part 2: " + total);
    }



}

