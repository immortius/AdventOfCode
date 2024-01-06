package xyz.immortius.advent2015.day13;

import com.google.common.collect.Collections2;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day13 {

    private static final String YEAR = "2015";
    private static final String DAY = "13";
    private static final boolean REAL_INPUT = true;

    private final Pattern linePattern = Pattern.compile("(?<PersonA>[A-Za-z]+) would (?<Negative>(gain)|(lose)) (?<Amount>[0-9]+) happiness units by sitting next to (?<PersonB>[A-Za-z]+).");

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day13().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        Table<String, String, Integer> connections = parse(lines);
        part1(connections);
        part2(connections);
    }

    private Table<String, String, Integer> parse(List<String> lines) {
        Table<String, String, Integer> connections = HashBasedTable.create();
        for (String line : lines) {
            Matcher matcher = linePattern.matcher(line);
            if (!matcher.matches()) {
                System.out.println("Failed to match for: " + line);
            } else {
                String a = matcher.group("PersonA");
                String b = matcher.group("PersonB");
                int value = Integer.parseInt(matcher.group("Amount"));
                if ("lose".equals(matcher.group("Negative"))) {
                    value *= -1;
                }
                connections.put(a, b, value);
            }
        }
        return connections;
    }

    private void part1(Table<String, String, Integer> connections) {
        int max = findMaxHappiness(connections);

        System.out.println("Part 1: " + max);
    }

    private int findMaxHappiness(Table<String, String, Integer> connections) {
        List<String> people = new ArrayList<>(connections.rowKeySet());
        String initialPerson = people.remove(0);

        int max = Integer.MIN_VALUE;
        for (List<String> permutation : Collections2.permutations(people)) {
            int value = connections.get(initialPerson, permutation.get(0));
            value += connections.get(permutation.get(0), initialPerson);
            value += connections.get(initialPerson, permutation.get(permutation.size() - 1));
            value += connections.get(permutation.get(permutation.size() - 1), initialPerson);
            for (int i = 0; i < permutation.size() - 1; i++) {
                value += connections.get(permutation.get(i), permutation.get(i + 1));
                value += connections.get(permutation.get(i + 1), permutation.get(i));
            }
            max = Math.max(max, value);
        }
        return max;
    }

    private void part2(Table<String, String, Integer> connections) {
        List<String> people = new ArrayList<>(connections.rowKeySet());
        for (String person : people) {
            connections.put("me", person, 0);
            connections.put(person, "me", 0);
        }

        int max = findMaxHappiness(connections);

        System.out.println("Part 2: " + max);
    }



}

