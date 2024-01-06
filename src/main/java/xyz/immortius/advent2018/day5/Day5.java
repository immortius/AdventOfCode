package xyz.immortius.advent2018.day5;

import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day5 {

    private static final String YEAR = "2018";
    private static final String DAY = "5";
    private static final boolean REAL_INPUT = true;
    private static final int CAPTIALISATION_OFFSET = 'a' - 'A';

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day5().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        part1(lines.get(0));
        part2(lines.get(0));
    }

    private void part1(String input) {
        String processed = depolymerize(input);
        System.out.println("Part 1: " + processed + " (" + processed.length() + ")");
    }

    @NotNull
    private String depolymerize(String input) {
        StringBuilder builder = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (builder.length() > 0 && Math.abs(c - builder.charAt(builder.length() - 1)) == CAPTIALISATION_OFFSET) {
                builder.setLength(builder.length() - 1);
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    private void part2(String input) {
        String processed = depolymerize(input);

        Set<Character> units = new HashSet<>();
        for (char c : processed.toCharArray()) {
            if (c > 'Z') {
                units.add((char)(c - CAPTIALISATION_OFFSET));
            } else {
                units.add(c);
            }
        }

        char bestUnit = 'a';
        int bestLength = processed.length();

        for (char c : units) {
            String result = depolymerize(processed.replaceAll("" + c + "|" + (char)(c + CAPTIALISATION_OFFSET), ""));
            if (result.length() < bestLength) {
                bestLength = result.length();
                bestUnit = c;
            }
        }

        System.out.println("Part 2: " + bestUnit + " (" + bestLength + ")");
    }



}

