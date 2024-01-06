package xyz.immortius.advent2022.day16;

import com.google.common.collect.*;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day16 {
    private final Pattern linePattern = Pattern.compile("Valve (?<valve>[A-Z]+) has flow rate=(?<flow>[0-9]+); tunnels? leads? to valves? (?<connected>[A-Z]+(, [A-Z]+)*)");

    private long steps = 0;
    private long earlyStops = 0;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day16().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day16/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        process(lines);
        System.out.println(steps);
        System.out.println(earlyStops);
    }

    private void process(List<String> lines) {

        Map<String, Integer> valveFlows = new HashMap<>();
        SetMultimap<String, String> connections = HashMultimap.create();

        for (String line : lines) {
            Matcher match = linePattern.matcher(line);
            if (match.matches()) {
                String valve = match.group("valve");
                valveFlows.put(valve, Integer.parseInt(match.group("flow")));
                for (String connected : match.group("connected").split(", ")) {
                    connections.put(valve, connected);
                }
            } else {
                System.out.println("Failed to match: '" + line + "'");
            }
        }

        part1(valveFlows, connections);
    }

    public Table<String, String, Integer> calculateAllDistances(SetMultimap<String, String> connections) {
        Table<String, String, Integer> table = HashBasedTable.create();
        for (String keyA : connections.keys()) {
            table.put(keyA, keyA, 0);
        }
        for (String keyA : connections.keys()) {
            int dist = 1;
            Set<String> wave = connections.get(keyA);
            while (!wave.isEmpty()) {
                Set<String> nextWave = new LinkedHashSet<>();
                for (String keyB : wave) {
                    Integer existing = table.get(keyA, keyB);
                    if (existing == null || existing > dist) {
                        table.put(keyA, keyB, dist);
                        nextWave.addAll(connections.get(keyB));
                    }
                }
                wave = nextWave;
                dist++;
            }
        }
        return table;
    }

    private void part1(Map<String, Integer> valveFlows, SetMultimap<String, String> connections) {
        Table<String, String, Integer> distances = calculateAllDistances(connections);
        List<String> remainingValves = valveFlows.keySet().stream().filter(x -> valveFlows.get(x) > 0).toList();
        System.out.println(remainingValves);

        int maxValue = optimisePath(distances, valveFlows, remainingValves, 30, "AA");

        System.out.println(maxValue);
    }

    private void part2(Map<String, Integer> valveFlows, SetMultimap<String, String> connections) {
        Table<String, String, Integer> distances = calculateAllDistances(connections);
        List<String> remainingValves = valveFlows.keySet().stream().filter(x -> valveFlows.get(x) > 0).toList();

        int maxValue = splitPath(distances, remainingValves, 26, "AA", valveFlows);

        System.out.println(maxValue);
    }

    private int splitPath(Table<String, String, Integer> distances, Collection<String> remainingValves, int timeRemaining, String startPos, Map<String, Integer> valveFlows) {
        int maxValue = 0;
        Set<String> values = new LinkedHashSet<>(remainingValves);
        for (int size = 1; size <= remainingValves.size() / 2; size++) {
            for (Set<String> combination : Sets.combinations(values, size)) {
                List<String> remainder = new ArrayList<>(remainingValves);
                remainder.removeAll(combination);

                int score = optimisePath(distances, valveFlows, combination, timeRemaining, startPos);
                score += optimisePath(distances, valveFlows, remainder, timeRemaining, startPos);
                maxValue = Math.max(score, maxValue);
            }
        }

        return maxValue;
    }

    private int optimisePath(Table<String, String, Integer> distances, Map<String, Integer> valveFlows, Collection<String> remainingValves, int minutesRemaining, String position) {
        steps++;
        int max = 0;
        for (String valve : remainingValves) {
            List<String> nextRemaining = remainingValves.stream().filter(x -> !x.equals(valve)).toList();
            int time = distances.get(position, valve) + 1;
            if (time < minutesRemaining) {
                int nextMinutesRemaining = minutesRemaining - time;
                int score = valveFlows.get(valve) * nextMinutesRemaining + optimisePath(distances, valveFlows, nextRemaining, nextMinutesRemaining, valve);
                max = Math.max(score, max);
            } else {
                earlyStops++;
            }
        }
        return max;
    }
}

