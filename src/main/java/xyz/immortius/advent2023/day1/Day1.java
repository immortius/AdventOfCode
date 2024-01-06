package xyz.immortius.advent2023.day1;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class Day1 {

    private static final String YEAR = "2023";
    private static final String DAY = "1";
    private static final boolean REAL_INPUT = true;

    private static final List<String> NUMERALS = Arrays.asList("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    private static final List<Integer> DIGITS = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 1, 2, 3, 4, 5, 6, 7, 8, 9);

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
        int total = 0;
        for (String line : lines) {
            String digits = line.replaceAll("[a-zA-Z]*", "");
            String number = digits.charAt(0) + "" + digits.charAt(digits.length() - 1);
            total += Integer.parseInt(number);
        }
        System.out.println("Part 1: " + total);
    }

    private void part2(List<String> lines) {
        int total = 0;
        for (String line : lines) {
            int value = getFirstDigit(line) * 10 + getLastDigit(line);
            total += value;
        }
        System.out.println("Part 2: " + total);
    }

    private int getFirstDigit(String line) {
        int smallestIndex = Integer.MAX_VALUE;
        int value = -1;
        for (int i = 0; i < NUMERALS.size(); i++) {
            int index = line.indexOf(NUMERALS.get(i));
            if (index != -1 && index < smallestIndex) {
                smallestIndex = index;
                value = DIGITS.get(i);
            }
        }
        return value;
    }

    private int getLastDigit(String line) {
        int largestIndex = Integer.MIN_VALUE;
        int value = -1;
        for (int i = 0; i < NUMERALS.size(); i++) {
            int lastIndex = line.lastIndexOf(NUMERALS.get(i));
            if (lastIndex != -1 && lastIndex > largestIndex) {
                largestIndex = lastIndex;
                value = DIGITS.get(i);
            }
        }
        return value;
    }

}

