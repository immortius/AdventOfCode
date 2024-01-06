package xyz.immortius.advent2019.day4;

import com.google.common.io.CharStreams;
import org.checkerframework.checker.units.qual.A;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Day4 {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day4().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        part1();
        part2();
    }

    private void part1() {
        int count = 0;
        for (int n = 183564; n <= 657474; n++) {
            if (check(n)) {
                count++;
            }
        }

        System.out.println("Part 1: " + count);
    }

    private boolean check(int n) {
        int lastDigit = n % 10;
        int remainder = n / 10;
        boolean doubleFound = false;
        while (remainder > 0) {
            int nextDigit = remainder % 10;
            if (nextDigit > lastDigit) {
                return false;
            }
            if (nextDigit == lastDigit) {
                doubleFound = true;
            }
            lastDigit = nextDigit;
            remainder = remainder / 10;
        }
        return doubleFound;
    }

    private void part2() {
        int count = 0;
        for (int n = 183564; n <= 657474; n++) {
            if (check2(n)) {
                count++;
            }
        }

        System.out.println("Part 2: " + count);
    }

    private boolean check2(int n) {
        int lastDigit = n % 10;
        int remainder = n / 10;
        int repeatLength = 1;
        boolean doubleFound = false;
        while (remainder > 0) {
            int nextDigit = remainder % 10;
            if (nextDigit > lastDigit) {
                return false;
            }
            if (nextDigit == lastDigit) {
                repeatLength++;
            } else if (repeatLength == 2) {
                doubleFound = true;
                repeatLength = 1;
            } else {
                repeatLength = 1;
            }
            lastDigit = nextDigit;
            remainder = remainder / 10;
        }
        if (repeatLength == 2) {
            doubleFound = true;
        }
        return doubleFound;
    }





}

