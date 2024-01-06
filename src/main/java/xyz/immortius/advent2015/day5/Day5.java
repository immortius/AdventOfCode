package xyz.immortius.advent2015.day5;

import com.google.common.io.CharStreams;
import xyz.immortius.util.CircularBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Day5 {
    public static void main(String[] args) throws IOException {
        new Day5().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2015/day5/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        System.out.println("Part 1: " + part1(lines));
        System.out.println("Part 2: " + part2(lines));

    }

    private int part1(List<String> input) {
        int nice = 0;
        for (String name : input) {
            if (noBannedParts(name) && hasDouble(name) && correctVowels(name)) {
                nice++;
            }
        }
        return nice;
    }

    private int part2(List<String> input) {
        int nice = 0;
        for (String name : input) {
            if (hasReveb(name) && hasDuplicatePair(name)) {
                nice++;
            }
        }
        return nice;
    }

    private boolean hasReveb(String name) {
        CircularBuffer<Character> buffer = CircularBuffer.create(2);
        for (char c : name.toCharArray()) {
            if (buffer.size() == 2 && buffer.get(0) == c) {
                return true;
            }
            buffer.add(c);
        }
        return false;
    }

    private boolean hasDuplicatePair(String name) {
        for (int pairIndex = 0; pairIndex < name.length() - 1; pairIndex++) {
            for (int checkIndex = pairIndex + 2; checkIndex < name.length() - 1; checkIndex++) {
                if (name.charAt(pairIndex) == name.charAt(checkIndex) && name.charAt(pairIndex + 1) == name.charAt(checkIndex + 1)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean noBannedParts(String name) {
        return !name.contains("ab") && !name.contains("cd") && !name.contains("pq") && !name.contains("xy");
    }


    private boolean hasDouble(String name) {
        char last = ' ';
        for (char c : name.toCharArray()) {
            if (c == last) {
                return true;
            }
            last = c;
        }
        return false;
    }

    private boolean correctVowels(String name) {
        int vowels = 0;
        for (char c : name.toCharArray()) {
            if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
                vowels++;
            }
        }
        return vowels >= 3;
    }
}

