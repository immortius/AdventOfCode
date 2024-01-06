package xyz.immortius.advent2015.day7;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Supplier;

public class Day7 {
    public static void main(String[] args) throws IOException {
        new Day7().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2015/day7/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        process(lines);
    }

    private Supplier<Integer> processSource(String source, Map<String, Integer> values, Wire wire) {
        if (source.charAt(0) >= '0' && source.charAt(0) <= '9') {
            int value = Integer.parseInt(source);
            return () -> value;
        } else {
            wire.dependencies.add(source);
            return () -> values.get(source);
        }
    }

    private void process(List<String> lines) throws IOException {
        Map<String, Integer> values = new HashMap<>();
        Map<String, Wire> wires = new HashMap<>();
        for (String line : lines) {
            String[] parts = line.split(" -> ");
            String target = parts[1];
            String input = parts[0];

            Wire wire = new Wire();
            wire.name = target;
            if (input.startsWith("NOT ")) {
                Supplier<Integer> source = processSource(input.substring(4), values, wire);
                wire.value = () -> ~source.get();
            } else if (input.contains(" AND ")) {
                String[] sources = input.split(" AND ");
                Supplier<Integer> a = processSource(sources[0], values, wire);
                Supplier<Integer> b = processSource(sources[1], values, wire);
                wire.value = () -> a.get() & b.get();
            } else if (input.contains(" OR ")) {
                String[] sources = input.split(" OR ");
                Supplier<Integer> a = processSource(sources[0], values, wire);
                Supplier<Integer> b = processSource(sources[1], values, wire);
                wire.value = () -> a.get() | b.get();
            } else if (input.contains(" LSHIFT ")) {
                String[] sources = input.split(" LSHIFT ");
                Supplier<Integer> a = processSource(sources[0], values, wire);
                int amount = Integer.parseInt(sources[1]);
                wire.value = () -> a.get() << amount;
            } else if (input.contains(" RSHIFT ")) {
                String[] sources = input.split(" RSHIFT ");
                Supplier<Integer> a = processSource(sources[0], values, wire);
                int amount = Integer.parseInt(sources[1]);
                wire.value = () -> a.get() >> amount;
            } else {
                wire.value = processSource(input, values, wire);
            }
            wires.put(target, wire);
        }

//        wires.remove("b");
//        values.put("b", 16076);
//        for (Wire wire : wires.values()) {
//            wire.dependencies.remove("b");
//        }

        List<Wire> sortedWires = wires.values().stream().sorted(Comparator.comparingInt(a -> a.dependencies.size())).toList();
        while (!sortedWires.isEmpty()) {
            Wire wire = sortedWires.get(0);
            wires.remove(wire.name);
            values.put(wire.name, wire.value.get());
            for (Wire otherWire : sortedWires) {
                otherWire.dependencies.remove(wire.name);
            }
            sortedWires = wires.values().stream().sorted(Comparator.comparingInt(a -> a.dependencies.size())).toList();
        }

        System.out.println(values.get("a"));
    }

    private static class Wire {
        String name;
        Supplier<Integer> value;
        List<String> dependencies = new ArrayList<>();
    }



}

