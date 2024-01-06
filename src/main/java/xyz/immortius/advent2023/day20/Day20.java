package xyz.immortius.advent2023.day20;

import com.google.common.io.CharStreams;
import xyz.immortius.util.MathUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day20 {

    private static final String YEAR = "2023";
    private static final String DAY = "20";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day20().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            lines = CharStreams.readLines(reader);
        }

        Map<String, Node> nodes = parse(lines);
        part1(copy(nodes));
        part2(copy(nodes));
    }

    public Map<String, Node> copy(Map<String, Node> nodes) {
        Map<String, Node> result = new LinkedHashMap<>();
        for (Map.Entry<String, Node> entry : nodes.entrySet()) {
            result.put(entry.getKey(), entry.getValue().copy());
        }
        return result;
    }

    private Map<String, Node> parse(List<String> lines) {
        Map<String, Node> nodes = new LinkedHashMap<>();
        Set<String> unknown = new LinkedHashSet<>();
        lines.forEach(line -> {
            String type = "";
            if (line.startsWith("%") || line.startsWith("&")) {
                type = line.substring(0, 1);
                line = line.substring(1);
            }
            String[] fromTo = line.split(" -> ");
            Node result = switch (type) {
                case "&" -> new Conjunction(fromTo[0]);
                case "%" -> new FlipFlop(fromTo[0]);
                default -> new Broadcast(fromTo[0]);
            };
            String[] targets = fromTo[1].split(", ");
            nodes.put(fromTo[0], result);
            for (String target : targets) {
                result.attachOutput(target);
                if (!nodes.containsKey(target)) {
                    unknown.add(target);
                }
            }
            unknown.remove(fromTo[0]);
        });

        for (String node : unknown) {
            nodes.put(node, new Terminator(node));
        }

        for (Node node : nodes.values()) {
            for (String output : node.outputNodes) {
                nodes.get(output).attachInput(node.name);
            }
        }
        return nodes;

    }

    private void part1(Map<String, Node> nodes) {
        long low = 0;
        long high = 0;
        for (int i = 0; i < 1; i++) {
            Deque<Signal> signals = new ArrayDeque<>();
            signals.push(new Signal("button", "broadcaster", false));
            while (!signals.isEmpty()) {
                Signal signal = signals.removeFirst();
                System.out.println(signal);
                if (signal.high) {
                    high++;
                } else {
                    low++;
                }

                signals.addAll(nodes.get(signal.to).pulse(signal.from, signal.high));
            }
        }

        System.out.println("Part 1: " + (low * high));
    }

    private void part2(Map<String, Node> nodes) {
        long index = 0;
        boolean done = false;

//        Conjunction dh = (Conjunction) nodes.get("dh");
//        //dh.log = true;
//        while (!done) {
//            index++;
//            Deque<Signal> signals = new ArrayDeque<>();
//            signals.push(new Signal("button", "broadcaster", false));
//            while (!signals.isEmpty() && !done) {
//                Signal signal = signals.removeFirst();
//
//                if (signal.to.equals("rx") && !signal.high) {
//                    done = true;
//                } else if (signal.to.equals("dh") && signal.high) {
//                    System.out.println(signal.from + " active " + index);
//                }
//
//                signals.addAll(nodes.get(signal.to).pulse(signal.from, signal.high));
//            }
//            for (Map.Entry<String, Boolean> stringBooleanEntry : dh.lastSignal.entrySet()) {
//                if (stringBooleanEntry.getValue()) {
//                   System.out.println(stringBooleanEntry.getKey() + " on " + index);
//                }
//            }
//        }

        index = MathUtil.lcm(Arrays.asList(3739L, 3761L, 3797L, 3889L));

        System.out.println("Part 2: " + index);
    }


    public static abstract class Node {
        protected List<String> outputNodes = new ArrayList<>();
        protected List<String> inputNodes = new ArrayList<>();
        protected String name;

        public Node(String name) {
            this.name = name;
        }

        public void attachInput(String other) {
            inputNodes.add(other);
        }

        public void attachOutput(String other) {
            outputNodes.add(other);
        }

        public abstract List<Signal> pulse(String from, boolean high);

        public abstract Node copy();

    }

    public static class FlipFlop extends Node {
        private boolean on;

        public FlipFlop(String name) {
            super(name);
        }

        @Override
        public List<Signal> pulse(String from, boolean high) {
            if (!high) {
                on = !on;
                return outputNodes.stream().map(x -> new Signal(name, x, on)).toList();
            }
            return Collections.emptyList();
        }

        @Override
        public Node copy() {
            FlipFlop result = new FlipFlop(name);
            result.on = on;
            result.outputNodes = outputNodes;
            result.inputNodes = inputNodes;
            return result;
        }
    }

    public static class Conjunction extends Node {
        Map<String, Boolean> lastSignal = new LinkedHashMap<>();
        boolean log;

        public Conjunction(String name) {
            super(name);
        }

        public void attachInput(String other) {
            super.attachInput(other);
            lastSignal.put(other, false);
        }

        @Override
        public List<Signal> pulse(String from, boolean high) {
            lastSignal.put(from, high);
            boolean signal = lastSignal.containsValue(false);
            if (log && high) {
                System.out.println("Active: " + from);
            }
            return outputNodes.stream().map(x -> new Signal(name, x, signal)).toList();
        }

        @Override
        public Node copy() {
            Conjunction result = new Conjunction(name);
            result.outputNodes = outputNodes;
            result.inputNodes = inputNodes;
            result.lastSignal.putAll(lastSignal);
            return result;
        }
    }

    public static class Broadcast extends Node {
        public Broadcast(String name) {
            super(name);
        }

        @Override
        public List<Signal> pulse(String from, boolean high) {
            return outputNodes.stream().map(x -> new Signal(name, x, high)).toList();
        }

        @Override
        public Node copy() {
            return this;
        }
    }

    public static class Terminator extends Node {
        public Terminator(String name) {
            super(name);
        }

        @Override
        public List<Signal> pulse(String from, boolean high) {
            return Collections.emptyList();
        }

        @Override
        public Node copy() {
            return this;
        }


    }

    private record Signal(String from, String to, boolean high) {
        @Override
        public String toString() {
            return from + " -" + ((high) ? "high" : "low") + "-> " + to;
        }
    }

}

