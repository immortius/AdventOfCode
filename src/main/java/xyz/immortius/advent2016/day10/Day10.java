package xyz.immortius.advent2016.day10;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Day10 {

    //private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    private static final String YEAR = "2016";
    private static final String DAY = "10";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day10().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        StartingState startingState = parse(lines);
        part1(startingState);
    }

    private StartingState parse(List<String> lines) {
        Set<String> entities = new LinkedHashSet<>();
        ListMultimap<String, Integer> startingValues = ArrayListMultimap.create();
        Map<String, String> lowValueSends = new HashMap<>();
        Map<String, String> highValueSends = new HashMap<>();

        for (String line : lines) {
            if (line.startsWith("value ")) {
                String[] parts = line.substring(6).split(" goes to ");
                entities.add(parts[1]);
                startingValues.put(parts[1], Integer.parseInt(parts[0]));
            } else {
                String[] intermediateParts = line.split(" and high to ");
                String[] parts = intermediateParts[0].split(" gives low to ");
                lowValueSends.put(parts[0], parts[1]);
                highValueSends.put(parts[0], intermediateParts[1]);
                entities.add(parts[0]);
                entities.add(parts[1]);
                entities.add(intermediateParts[1]);
            }
        }
        return new StartingState(entities, startingValues, lowValueSends, highValueSends);
    }

    private void part1(StartingState startingState) {
        ListMultimap<String, Integer> values = ArrayListMultimap.create(startingState.startingValues);
        Map<String, String> lowValueSends = startingState.lowValueSends;
        Map<String, String> highValueSends = startingState.highValueSends;

        Set<String> readyElements = values.asMap().entrySet().stream().filter(x -> x.getValue().size() == 2).map(x -> x.getKey()).collect(Collectors.toSet());
        while (!readyElements.isEmpty()) {
            Set<String> nextElements = new LinkedHashSet<>();
            for (String element : readyElements) {
                ArrayList<Integer> holding = new ArrayList<>(values.get(element));
                holding.sort(Integer::compareTo);

                String sendLowTo = lowValueSends.get(element);
                if (sendLowTo != null) {
                    values.put(sendLowTo, holding.get(0));
                    if (values.get(sendLowTo).size() == 2) {
                        nextElements.add(sendLowTo);
                    }
                }

                String sendHighTo = highValueSends.get(element);
                if (sendHighTo != null) {
                    values.put(sendHighTo, holding.get(1));
                    if (values.get(sendHighTo).size() == 2) {
                        nextElements.add(sendHighTo);
                    }
                }
            }
            readyElements = nextElements;
        }


        System.out.println("Part 1: " + values.asMap().entrySet().stream().filter(x -> x.getValue().contains(17) && x.getValue().contains(61)).map(Map.Entry::getKey).findFirst().get());
        System.out.println("Part 2: " + values.get("output 0").get(0) * values.get("output 1").get(0) * values.get("output 2").get(0));
    }

    private record StartingState(Set<String> entities, ListMultimap<String, Integer> startingValues, Map<String, String> lowValueSends, Map<String, String> highValueSends) {

    }

}

