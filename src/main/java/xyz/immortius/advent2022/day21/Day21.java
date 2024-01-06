package xyz.immortius.advent2022.day21;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class Day21 {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day21().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day21/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        State state = parse(lines);
        part1(new State(state));
        part2(new State(state));
    }

    private State parse(List<String> lines) {
        Set<String> monkeys = new LinkedHashSet<>();
        Map<String, BigInteger> values = new HashMap<>();
        ListMultimap<String, String> dependencies = ArrayListMultimap.create();
        Map<String, BiFunction<BigInteger, BigInteger, BigInteger>> methods = new HashMap<>();

        for (String line : lines) {
            String[] parts = line.split(": ");
            monkeys.add(parts[0]);
            if (parts[1].length() < 5) {
                values.put(parts[0], BigInteger.valueOf(Long.parseLong(parts[1])));
            } else {
                String[] equation = parts[1].split(" ");
                dependencies.put(parts[0], equation[0]);
                dependencies.put(parts[0], equation[2]);
                switch (equation[1]) {
                    case "+" -> methods.put(parts[0], BigInteger::add);
                    case "*" -> methods.put(parts[0], BigInteger::multiply);
                    case "-" -> methods.put(parts[0], BigInteger::subtract);
                    case "/" -> methods.put(parts[0], BigInteger::divide);
                }
            }
        }
        return new State(monkeys, dependencies, values, methods);
    }

    private void part1(State state) {
        BigInteger value = calculate(state, "root");
        System.out.println("Part 1: " + value);
    }

    private BigInteger calculate(State state, String monkey) {
        if (state.values.containsKey(monkey)) {
            return state.values.get(monkey);
        } else {
            BigInteger value = state.methods.get(monkey).apply(calculate(state, state.dependencies.get(monkey).get(0)), calculate(state, state.dependencies.get(monkey).get(1)));
            state.values.put(monkey, value);
            return value;
        }
    }

    private void part2(State state) {
        long l = 0L;
        long r = Long.MAX_VALUE;
        int sign = -calcDiff(state, r).subtract(calcDiff(state, l)).signum();

        while (l <= r) {
            long m = l / 2 + r / 2;
            BigInteger diff = calcDiff(state, m);
            int cmp = diff.compareTo(BigInteger.ZERO) * sign;
            if (cmp > 0) {
                l = m + 1;
            } else if (cmp < 0) {
                r = m - 1;
            }
            else {
                while (BigInteger.ZERO.equals(calcDiff(state, m - 1))) {
                    m = m - 1;
                }
                System.out.println("Part 2: " + m);
                return;
            }
            System.out.println("L: " + l + ", R: " + r + " - " + diff);
        }
    }

    private BigInteger calcDiff(State state, long value) {
        State innerState = new State(state);
        innerState.values.put("humn", BigInteger.valueOf(value));
        BigInteger a = calculate(innerState, innerState.dependencies.get("root").get(0));
        BigInteger b = calculate(innerState, innerState.dependencies.get("root").get(1));
        return b.subtract(a);
    }

    private static final class State {
        private final Set<String> monkeys;
        private final ListMultimap<String, String> dependencies;
        private final Map<String, BigInteger> values;
        private final Map<String, BiFunction<BigInteger, BigInteger, BigInteger>> methods;

        private State(Set<String> monkeys, ListMultimap<String, String> dependencies, Map<String, BigInteger> values, Map<String, BiFunction<BigInteger, BigInteger, BigInteger>> methods) {
            this.monkeys = monkeys;
            this.dependencies = dependencies;
            this.values = values;
            this.methods = methods;
        }

        private State(State other) {
            this.monkeys = new LinkedHashSet<>(other.monkeys);
            this.dependencies = ArrayListMultimap.create(other.dependencies);
            this.values = new HashMap<>(other.values);
            this.methods = new HashMap<>(other.methods);
        }

        public Set<String> monkeys() {
            return monkeys;
        }

        public ListMultimap<String, String> dependencies() {
            return dependencies;
        }

        public Map<String, BigInteger> values() {
            return values;
        }

        public Map<String, BiFunction<BigInteger, BigInteger, BigInteger>> methods() {
            return methods;
        }
    }

}