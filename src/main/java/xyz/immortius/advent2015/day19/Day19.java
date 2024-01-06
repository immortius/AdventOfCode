package xyz.immortius.advent2015.day19;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Day19 {

    private static final String YEAR = "2015";
    private static final String DAY = "19";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day19().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        ListMultimap<String, String> conversions = parse(lines.subList(0, lines.size() - 2));
        System.out.println(conversions);
        part1(conversions, lines.get(lines.size() - 1));
        part2(conversions, lines.get(lines.size() - 1));
    }

    private void part1(ListMultimap<String, String> conversions, String input) {
        Set<String> outputs = getConversionOptions(conversions, input);

        System.out.println("Part 1: " + outputs.size());
    }

    @NotNull
    private Set<String> getConversionOptions(ListMultimap<String, String> conversions, String input) {
        Set<String> outputs = new HashSet<>();
        for (Map.Entry<String, String> conversion : conversions.entries()) {
            int index = input.indexOf(conversion.getKey());
            while (index != -1) {
                StringBuilder builder = new StringBuilder();
                builder.append(input, 0, index);
                builder.append(conversion.getValue());
                builder.append(input, index + conversion.getKey().length(), input.length());
                //System.out.println(input + ": " + conversion.getKey() + " => " + conversion.getValue() + " = " + builder);
                outputs.add(builder.toString());
                if (index + 1 < input.length()) {
                    index = input.indexOf(conversion.getKey(), index + 1);
                } else {
                    index = -1;
                }
            }
        }
        return outputs;
    }

    private int occurrences(String input, String target) {
        int index = input.indexOf(target);
        int result = 0;
        while (index != -1) {
            result++;
            index = input.indexOf(target, index + 1);
        }
        return result;
    }

    private ListMultimap<String, String> parse(List<String> lines) {
        ListMultimap<String, String> conversions = ArrayListMultimap.create();
        for (String line : lines) {
            String[] parts = line.split(" => ");
            conversions.put(parts[0], parts[1]);
        }
        return conversions;
    }

    private void part2(ListMultimap<String, String> conversions, String input) {
        ListMultimap<String, String> reversed = ArrayListMultimap.create();
        for (Map.Entry<String, String> entry : conversions.entries()) {
            reversed.put(entry.getValue(), entry.getKey());
        }

        int steps = build(input, reversed, "e", Integer.MAX_VALUE,  1);

        //int steps = build("e", conversions, input);
        System.out.println("Part 2: " + steps);
    }

    private int build(String input, ListMultimap<String, String> conversions, String target, int minSteps, int depth) {
//        int step = 0;
//        Set<String> visited = new LinkedHashSet<>();
//        visited.add(input);
//        Set<String> options = Collections.singleton(input);
//        while (!options.contains(target)) {
//            step++;
//            Set<String> nextSet = new LinkedHashSet<>();
//            for (String option : options) {
//                nextSet.addAll(getConversionOptions(conversions, option));
//            }
//
//            options = nextSet.stream().filter(x -> !visited.contains(x))
//                    .collect(Collectors.toSet());
//            visited.addAll(options);
//            System.out.println("Step " + step + " - " + options.size());
//        }
//        return step;

        if (minSteps == 1) {
            return minSteps;
        }
        List<String> options = getConversionOptions(conversions, input).stream()
                .sorted(Comparator.comparingInt(String::length)).toList();
        if (options.isEmpty()) {
            return minSteps;
        }
        if (options.contains(target)) {
            return 1;
        }
        for (String option : options) {
            int result = build(option, conversions, target, minSteps - 1, depth + 1);
            if (result + 1 < minSteps) {
                System.out.println("Reducing min steps to " + (result + depth) + " (" + depth + ")");
                minSteps = result + 1;
            }
        }
        return minSteps;
    }

}

