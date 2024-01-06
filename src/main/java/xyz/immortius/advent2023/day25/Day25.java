package xyz.immortius.advent2023.day25;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Day25 {

    //private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    private static final String YEAR = "2023";
    private static final String DAY = "25";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day25().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            lines = CharStreams.readLines(reader);
        }

        Set<Wire> input = parse(lines);
        part1(input);
    }

    private Set<Wire> parse(List<String> lines) {
        Set<Wire> wires = new HashSet<>();
        for (String line : lines) {
            String[] components = line.split(":* ");
            for (int i = 1; i < components.length; i++) {
                if (!wires.contains(new Wire(components[i], components[0]))) {
                    wires.add(new Wire(components[0], components[i]));
                }
            }
        }
        return wires;
    }

    private record Wire(String a, String b) {}

    private void part1(Set<Wire> input) {
        Set<String> componentSet = new LinkedHashSet<>(input.stream().map(x -> x.a).toList());
        componentSet.addAll(input.stream().map(x -> x.b).toList());
        List<String> components = new ArrayList<>(componentSet);

        List<Wire> wires = new ArrayList<>(input);
        Multimap<String, String> fullConnectivity = HashMultimap.create();
        for (Wire wire : wires) {
            fullConnectivity.put(wire.a, wire.b);
            fullConnectivity.put(wire.b, wire.a);
        }


        Set<String> group = new LinkedHashSet<>();
        group.add(components.get(0));
        while (group.size() < components.size()) {
            int connections = 0;
            String bestAddition = null;
            long additionConnections = Integer.MAX_VALUE;
            long totalConnections = Integer.MAX_VALUE;
            for (String component : group) {
                for (String connected : fullConnectivity.get(component)) {
                    if (!group.contains(connected)) {
                        connections += 1;
                        long furtherConnections = fullConnectivity.get(connected).stream().filter(x -> !group.contains(x)).count();
                        if (furtherConnections < additionConnections) {
                            bestAddition = connected;
                            additionConnections = furtherConnections;
                            totalConnections = fullConnectivity.get(connected).size();
                        } else if (furtherConnections == additionConnections && totalConnections > fullConnectivity.get(connected).size()) {
                            bestAddition = connected;
                            totalConnections = fullConnectivity.get(connected).size();
                        }
                    }
                }
            }
            if (connections == 3) {
                break;
            }
            group.add(bestAddition);
        }

        System.out.println(group.size() + " * " + (components.size() - group.size()) + " = " + (group.size() * (components.size() - group.size())));

        System.out.println("Part 1: ");
    }

    record CacheResult(Set<String> group) {}
    Map<Set<String>, CacheResult> cache = new HashMap<>();

    private Set<String> findThreeConnections(Set<String> group, Multimap<String, String> fullConnectivity) {
        CacheResult result = cache.get(group);
        if (result == null) {
            result = new CacheResult(inner(group, fullConnectivity));
            cache.put(group, result);
        }
        return result.group;
    }

    @Nullable
    private Set<String> inner(Set<String> group, Multimap<String, String> fullConnectivity) {
        Set<String> outgoing = new LinkedHashSet<>();
        int outgoingCount = 0;
        for (String component : group) {
            for (String connected : fullConnectivity.get(component)) {
                if (!group.contains(connected)) {
                    outgoing.add(connected);
                    outgoingCount++;
                }
            }
        }
        if (outgoingCount == 3) {
            return group;
        }
        if (group.size() + 1 == fullConnectivity.keySet().size() / 2) {
            return null;
        }
        for (String out : outgoing) {
            Set<String> newGroup = new HashSet<>(group);
            newGroup.add(out);
            Set<String> result = findThreeConnections(newGroup, fullConnectivity);
            if (result != null) {
                return result;
            }
        }
        return null;
    }


}

