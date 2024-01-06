package xyz.immortius.advent2015.day20;

import java.io.IOException;

public class Day20 {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day20().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() {
        //part1();
        part2();
    }

    private void part1() {
        long house = 1;

        while (true) {
            long houseValue = 0;
            long factor = house;
            for (long i = 1; i < factor; i++) {
                if (house % i == 0) {
                    factor = house / i;
                    houseValue += factor * 10;
                    if (factor != i) {
                        houseValue += i * 10;
                    }
                }
            }
            if (houseValue >= 29000000L) {
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
            if (houseValue >= 29000000L) {
                break;
            }
            house++;
        }


        System.out.println("Part 2: " + house);
    }

    public class Elf {
        long id;
        int deliveriesRemaining;

        public Elf(long id) {
            this.id = id;
            deliveriesRemaining = 10;
        }
    }


}

