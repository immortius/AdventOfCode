package xyz.immortius.advent2017.day4;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day4 {

    private static final String YEAR = "2017";
    private static final String DAY = "4";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day4().run();
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
        int valid = 0;
        for (String line : lines) {
            String[] words = line.split("\s+");
            Set<String> uniqueWords = Arrays.stream(words).collect(Collectors.toSet());
            if (words.length == uniqueWords.size()) {
                valid++;
            }
        }
        System.out.println("Part 1: " + valid);
    }

    private void part2(List<String> lines) {
        int valid = 0;
        for (String line : lines) {
            String[] words = line.split("\s+");
            Set<String> uniqueWords = Arrays.stream(words).map(x -> {
                char[] word = x.toCharArray();
                Arrays.sort(word);
                return new String(word);
            }).collect(Collectors.toSet());
            if (words.length == uniqueWords.size()) {
                valid++;
            }
        }
        System.out.println("Part 2: " + valid);
    }



}

