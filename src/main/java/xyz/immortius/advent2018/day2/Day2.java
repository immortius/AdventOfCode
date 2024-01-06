package xyz.immortius.advent2018.day2;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Day2 {

    private static final String YEAR = "2018";
    private static final String DAY = "2";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day2().run();
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
        int doubleCount = 0;
        int tripleCount = 0;
        for (String line : lines) {
            if (containsMultiple(line, 2)) {
                doubleCount++;
            }
            if (containsMultiple(line, 3)) {
                tripleCount++;
            }

        }
        System.out.println("Part 1: " + (doubleCount * tripleCount));
    }

    private void part2(List<String> lines) {

        String answer = findCommonString(lines);
        System.out.println("Part 2: " + answer);
    }

    private String findCommonString(List<String> lines) {
        for (int aIndex = 0; aIndex < lines.size() - 1; aIndex++) {
            for (int bIndex = aIndex + 1; bIndex < lines.size(); bIndex++) {
                if (differenceCount(lines.get(aIndex), lines.get(bIndex)) == 1) {
                    StringBuilder answer = new StringBuilder();
                    String a = lines.get(aIndex);
                    String b = lines.get(bIndex);
                    for (int i = 0; i < a.length(); i++) {
                        if (a.charAt(i) == b.charAt(i)) {
                            answer.append(a.charAt(i));
                        }
                    }
                    return answer.toString();
                }
            }
        }
        return "";
    }

    private boolean containsMultiple(String line, int count) {
        Multiset<Character> charSet = HashMultiset.create();
        for (char c : line.toCharArray()) {
            charSet.add(c);
        }
        for (char c : charSet.elementSet()) {
            if (charSet.count(c) == count) {
                return true;
            }
        }
        return false;
    }

    private int differenceCount(String a, String b) {
        int diff = 0;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) {
                diff++;
            }
        }
        return diff;
    }





}

