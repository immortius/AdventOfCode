package xyz.immortius.advent2015.day7;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Function;

public class Day7 {
    public static void main(String[] args) throws IOException {
        new Day7().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2015/day7/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        Map<String, Wire> wires = parse(lines);
        int result = process(wires);
        System.out.println("Part 1: " + result);
        wires = parse(lines);
        wires.remove("b");
        wires.put("b", new Wire("b", (values) -> result));
        System.out.println("Part 2: " + process(wires));
    }

    private Function<Map<String, Integer>, Integer> processSource(String source, Wire wire) {
        if (source.charAt(0) >= '0' && source.charAt(0) <= '9') {
            int value = Integer.parseInt(source);
            return (values) -> value;
        } else {
            wire.dependencies.add(source);
            return (values) -> values.get(source);
        }
    }

    private int process(Map<String, Wire> wires) {
        Map<String, Integer> values = new HashMap<>();
        List<Wire> sortedWires = wires.values().stream().sorted(Comparator.comparingInt(a -> a.dependencies.size())).toList();
        while (!sortedWires.isEmpty()) {
            Wire wire = sortedWires.get(0);
            wires.remove(wire.name);
            values.put(wire.name, wire.valueFunc.apply(values));
            for (Wire otherWire : sortedWires) {
                otherWire.dependencies.remove(wire.name);
            }
            sortedWires = wires.values().stream().sorted(Comparator.comparingInt(a -> a.dependencies.size())).toList();
        }

        return values.get("a");
    }

    private Map<String, Wire> parse(List<String> lines) {
        Map<String, Wire> wires = new HashMap<>();
        for (String line : lines) {
            String[] parts = line.split(" -> ");
            String target = parts[1];
            String input = parts[0];

            Wire wire = new Wire();
            wire.name = target;
            if (input.startsWith("NOT ")) {
                Function<Map<String, Integer>, Integer> source = processSource(input.substring(4), wire);
                wire.valueFunc = (values) -> ~source.apply(values);
            } else if (input.contains(" AND ")) {
                String[] sources = input.split(" AND ");
                Function<Map<String, Integer>, Integer> a = processSource(sources[0], wire);
                Function<Map<String, Integer>, Integer> b = processSource(sources[1], wire);
                wire.valueFunc = (values) -> a.apply(values) & b.apply(values);
            } else if (input.contains(" OR ")) {
                String[] sources = input.split(" OR ");
                Function<Map<String, Integer>, Integer> a = processSource(sources[0], wire);
                Function<Map<String, Integer>, Integer> b = processSource(sources[1], wire);
                wire.valueFunc = (values) -> a.apply(values) | b.apply(values);
            } else if (input.contains(" LSHIFT ")) {
                String[] sources = input.split(" LSHIFT ");
                Function<Map<String, Integer>, Integer> a = processSource(sources[0], wire);
                int amount = Integer.parseInt(sources[1]);
                wire.valueFunc = (values) -> a.apply(values) << amount;
            } else if (input.contains(" RSHIFT ")) {
                String[] sources = input.split(" RSHIFT ");
                Function<Map<String, Integer>, Integer> a = processSource(sources[0], wire);
                int amount = Integer.parseInt(sources[1]);
                wire.valueFunc = (values) -> a.apply(values) >> amount;
            } else {
                wire.valueFunc = processSource(input, wire);
            }
            wires.put(target, wire);
        }
        return wires;
    }

    private static class Wire {
        String name;
        Function<Map<String, Integer>, Integer> valueFunc;
        List<String> dependencies = new ArrayList<>();

        Wire() {}

        Wire(String name, Function<Map<String, Integer>, Integer> valueFunc) {
            this.name = name;
            this.valueFunc = valueFunc;
        }
    }

}

