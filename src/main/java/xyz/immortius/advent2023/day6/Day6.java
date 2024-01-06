package xyz.immortius.advent2023.day6;

import org.joml.Math;
import xyz.immortius.util.MathUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class Day6 {

    private static final List<Race> EXAMPLE_RACES = List.of(new Race(7, 9), new Race(15, 40), new Race(30, 200));
    private static final List<Race> PART_1_RACES = List.of(new Race(54, 446), new Race(81, 1292), new Race(70, 1035), new Race(88, 1007));
    private static final List<Race> EXAMPLE_RACES_2 = List.of(new Race(71530, 940200));
    private static final List<Race> PART_2_RACES = List.of(new Race(54817088, 446129210351007L));

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day6().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        exampleP1();
        part1();
        exampleP2();
        part2();
    }

    private void exampleP1() {
        int total = getScoreOfWinningRaces(EXAMPLE_RACES);
        System.out.println("Part 1 example: " + total);
    }

    private void part1() {
        int total = getScoreOfWinningRaces(PART_1_RACES);
        System.out.println("Part 1: " + total);
    }

    private void exampleP2() {
        int total = getScoreOfWinningRaces(EXAMPLE_RACES_2);
        System.out.println("Part 2 example: " + total);
    }

    private void part2() {
        int total = getScoreOfWinningRaces(PART_2_RACES);
        System.out.println("Part 2: " + total);
    }

    private int getScoreOfWinningRaces(List<Race> races) {
        int total = 1;
        for (Race race : races) {
            List<Double> result = MathUtil.solveQuadratic(1, -race.time, race.dist + 1);
            Collections.sort(result);
            long max = result.get(1).longValue();
            long min = (long) Math.ceil(result.get(0));
            long count = max - min + 1;
            total *= count;
        }
        return total;
    }

    private record Race(long time, long dist) {};

}

