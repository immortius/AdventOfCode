package xyz.immortius.advent2017.day12;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.SetMultimap;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day12 {

    private static final String YEAR = "2017";
    private static final String DAY = "12";
    private static final boolean REAL_INPUT = false;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day12().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        SetMultimap<Integer, Integer> connections = parse(lines);
        part1(connections);
        part2(connections);
    }

    private SetMultimap<Integer, Integer> parse(List<String> lines) {
        SetMultimap<Integer, Integer> connections = HashMultimap.create();
        for (String line : lines) {
            String[] connected = line.split(" <-> ");
            int start = Integer.parseInt(connected[0]);
            List<Integer> ends = Arrays.stream(connected[1].split(", ")).map(Integer::parseInt).toList();
            connections.putAll(start, ends);
            for (int end : ends) {
                connections.put(end, start);
            }
        }
        return connections;
    }

    private void part1(SetMultimap<Integer, Integer> connections) {
        Set<Integer> open = new LinkedHashSet<>(connections.get(0));

        while (!open.isEmpty()) {
            Set<Integer> newOpen = new LinkedHashSet<>();
            for (int node : open) {
                for (int next : connections.get(node)) {
                    if (connections.put(0, next)) {
                        newOpen.add(next);
                    }
                }
            }
            open = newOpen;
        }

        System.out.println("Part 1: " + connections.get(0).size());
    }

    private void part2(SetMultimap<Integer, Integer> connections) {
        List<Integer> nodes = ImmutableList.copyOf(connections.keySet());
        for (int id : nodes) {
            Set<Integer> open = new LinkedHashSet<>(connections.get(id));

            while (!open.isEmpty()) {
                Set<Integer> newOpen = new LinkedHashSet<>();
                for (int node : open) {
                    for (int next : connections.get(node)) {
                        if (connections.put(id, next)) {
                            newOpen.add(next);
                        }
                    }
                }
                open = newOpen;
            }
        }

        HashMultimap<Integer, Integer> groupConnections = HashMultimap.create(connections);
        int groups = 0;
        while (groupConnections.size() > 0) {
            int node = groupConnections.keySet().stream().findAny().get();
            for (int connected : new ArrayList<>(groupConnections.get(node))) {
                groupConnections.removeAll(connected);
            }
            groups++;
        }

        System.out.println("Part 2: " + groups);
    }



}

