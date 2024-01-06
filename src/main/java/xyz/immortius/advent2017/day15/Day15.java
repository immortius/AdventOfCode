package xyz.immortius.advent2017.day15;

import java.io.IOException;

public class Day15 {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day15().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        part1();
        part2();
    }

    private void part1() {
        long[] genFactors = new long[] {16807, 48271};
        long div = 2147483647;
        long[] generators = new long[] {591, 393};

        int count = 0;
        for (int i = 0; i < 40_000_000; i++) {
            for (int gen = 0; gen < generators.length; gen++) {
                generators[gen] = (generators[gen] * genFactors[gen]) % div;
            }
            if (compareLower(generators[0], generators[1])) {
                count++;
            }
        }

        System.out.println("Part 1: " + count);
    }

    private boolean compareLower(long a, long b) {
        long compA = a & 0xffff;
        long compB = b & 0xffff;
        return compA == compB;
    }

    private void part2() {
        long[] genFactors = new long[] {16807, 48271};
        long div = 2147483647;
        long[] generators = new long[] {591, 393};
        long[] genDesiredDiv = new long[] {4, 8};

        int count = 0;
        int pairs = 0;
        while (pairs < 5_000_000) {
            for (int gen = 0; gen < generators.length; gen++) {
                do {
                    generators[gen] = (generators[gen] * genFactors[gen]) % div;
                } while (generators[gen] % genDesiredDiv[gen] != 0);
            }
            if (compareLower(generators[0], generators[1])) {
                count++;
            }
            pairs++;
        }

        System.out.println("Part 2: " + count);
    }



}

