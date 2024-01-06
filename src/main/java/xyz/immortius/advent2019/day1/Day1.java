package xyz.immortius.advent2019.day1;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Day1 {

    private static final String YEAR = "2019";
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

        List<Integer> input = parse(lines);
        part1(input);
        part2(input);
    }

    private List<Integer> parse(List<String> lines) {
        return lines.stream().map(Integer::parseInt).toList();
    }

    private void part1(List<Integer> input) {
        Integer result = input.stream().map(x -> x / 3 - 2).reduce(0, Integer::sum);
        System.out.println("Part 1: " + result);
    }

    private void part2(List<Integer> input) {
        int total = 0;
        for (int module : input) {
            int subtotal = 0;
            int fuelRequired = module / 3 - 2;
            while (fuelRequired > 0) {
                subtotal += fuelRequired;
                fuelRequired = fuelRequired / 3 - 2;
            }
            total += subtotal;
        }
        System.out.println("Part 2: " + total);
    }



}

