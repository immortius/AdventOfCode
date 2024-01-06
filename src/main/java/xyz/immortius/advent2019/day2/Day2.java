package xyz.immortius.advent2019.day2;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class Day2 {

    private static final String YEAR = "2019";
    private static final String DAY = "2";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        new Day2().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws Exception {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        long[] input = IntCodeHelper.parse(lines);
        part1(input);
        part2(input);
    }

    private void part1(long[] input) throws Exception {
        IntCodeComputer computer = new IntCodeComputer(input);
        computer.set(1, 12);
        computer.set(2, 2);
        computer.run();
        System.out.println("Part 1: " + computer.get(0));
    }

    private void part2(long[] input) throws Exception {
        for (int noun = 0; noun <= 99; noun++) {
            for (int verb = 0; verb <= 99; verb++) {
                IntCodeComputer computer = new IntCodeComputer(input);
                computer.set(1, noun);
                computer.set(2, verb);
                computer.run();
                if (computer.get(0) == 19690720) {
                    System.out.println("Part 2: " + computer.get(0) + " - " + noun + ", " + verb + " = " + (100 * noun + verb));
                }
            }
        }
    }


}

