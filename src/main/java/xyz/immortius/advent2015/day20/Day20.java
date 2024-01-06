package xyz.immortius.advent2015.day20;

import com.google.common.math.LongMath;

import java.io.IOException;
import java.math.RoundingMode;

public class Day20 {

    private final long INPUT = 29000000L;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day20().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() {
        part1();
        part2();
    }

    private void part1() {
        long house = 1;

        while (true) {
            long houseValue = 0;

            long s = LongMath.sqrt(house, RoundingMode.FLOOR);
            for (long i = 1; i < s; i++) {
                if (house % i == 0) {
                    houseValue += i;
                    long pair = house / i;
                    if (pair != i) {
                        houseValue += pair;
                    }
                }
            }
            if (houseValue * 10 >= INPUT) {
                break;
            }
            house++;
        }

        System.out.println("Part 1: " + house);
    }

    private void part2() {
        long house = 1;

        while (true) {
            long houseValue = 0;
            for (long i = 1; i <= 50; i++) {
                if (house % i == 0) {
                    long factor = house / i;
                    houseValue += factor * 11;
                }
            }
            if (houseValue >= INPUT) {
                break;
            }
            house++;
        }


        System.out.println("Part 2: " + house);
    }

}

