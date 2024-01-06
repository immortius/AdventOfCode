package xyz.immortius.advent2015.day19;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;
import xyz.immortius.util.AStar;

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
                String result = input.substring(0, index) +
                        conversion.getValue() +
                        input.substring(index + conversion.getKey().length());
                outputs.add(result);
                if (index + 1 < input.length()) {
                    index = input.indexOf(conversion.getKey(), index + 1);
                } else {
                    index = -1;
                }
            }
        }
        return outputs;
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

        int steps = build(input, reversed, "e");
        System.out.println("Part 2: " + steps);
    }

    private Integer build(String input, ListMultimap<String, String> conversions, String target) {
        if (target.equals(input)) {
            return 0;
        }
        Integer result = null;
        for (String option : getConversionOptions(conversions, input).stream().sorted(Comparator.comparingInt(String::length)).toList()) {
            Integer steps = build(option, conversions, target);
            if (steps != null) {
                // We can assume that the path that shrinks the string the most is the shortest path
                result = steps + 1;
                break;
            }
        }
        return result;
    }

}

