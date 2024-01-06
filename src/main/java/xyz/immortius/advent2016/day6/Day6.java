package xyz.immortius.advent2016.day6;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Day6 {

    private static final String YEAR = "2016";
    private static final String DAY = "6";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day6().run();
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

        List<Multiset<Character>> characterCounts = new ArrayList<>();
        for (int i = 0; i < lines.get(0).length(); i++) {
            characterCounts.add(HashMultiset.create());
        }
        for (String line : lines) {
            for (int i = 0; i < line.length(); i++) {
                characterCounts.get(i).add(line.charAt(i));
            }
        }
        StringBuilder result = new StringBuilder();
        for (Multiset<Character> characterCount : characterCounts) {
            char mostCharacter = ' ';
            int maxCount = 0;
            for (Multiset.Entry<Character> characterEntry : characterCount.entrySet()) {
                if (characterEntry.getCount() > maxCount) {
                    maxCount = characterEntry.getCount();
                    mostCharacter = characterEntry.getElement();
                }
            }
            result.append(mostCharacter);
        }

        System.out.println("Part 1: " + result);
    }

    private void part2(List<String> lines) {
        List<Multiset<Character>> characterCounts = new ArrayList<>();
        for (int i = 0; i < lines.get(0).length(); i++) {
            characterCounts.add(HashMultiset.create());
        }
        for (String line : lines) {
            for (int i = 0; i < line.length(); i++) {
                characterCounts.get(i).add(line.charAt(i));
            }
        }
        StringBuilder result = new StringBuilder();
        for (Multiset<Character> characterCount : characterCounts) {
            char leastCharacter = ' ';
            int minCount = Integer.MAX_VALUE;
            for (Multiset.Entry<Character> characterEntry : characterCount.entrySet()) {
                if (characterEntry.getCount() < minCount) {
                    minCount = characterEntry.getCount();
                    leastCharacter = characterEntry.getElement();
                }
            }
            result.append(leastCharacter);
        }

        System.out.println("Part 2: " + result);
    }


}

