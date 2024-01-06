package xyz.immortius.advent2015.day9;

import com.google.common.collect.Collections2;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Day9 {

    private static final String YEAR = "2015";
    private static final String DAY = "9";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        new Day9().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        Table<String, String, Integer> connections = parse(lines);
        process(connections);
    }

    private Table<String, String, Integer> parse(List<String> lines) {
        Table<String, String, Integer> connections = HashBasedTable.create();
        for (String line : lines) {
            String[] tokens = line.split(" ");
            String from = tokens[0];
            String to = tokens[2];
            int distance = Integer.parseInt(tokens[4]);
            connections.put(from, to, distance);
            connections.put(to, from, distance);
        }
        return connections;
    }

    private void process(Table<String, String, Integer> connections) {
        List<String> places = new ArrayList<>(connections.rowKeySet());

        int minDistance = Integer.MAX_VALUE;
        int maxDistance = Integer.MIN_VALUE;
        Collection<List<String>> permutations = Collections2.permutations(places);
        for (List<String> permutation : permutations) {
            int distance = 0;
            for (int i = 0; i < permutation.size() - 1; i++) {
                distance += connections.get(permutation.get(i), permutation.get(i + 1));
            }
            minDistance = Math.min(distance, minDistance);
            maxDistance = Math.max(distance, maxDistance);
        }

        System.out.println("Part 1: " + minDistance);
        System.out.println("Part 2: " + maxDistance);
    }
}

