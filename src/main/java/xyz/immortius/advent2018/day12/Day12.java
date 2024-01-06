package xyz.immortius.advent2018.day12;

import com.google.common.base.Objects;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Day12 {

    //private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    private static final String YEAR = "2018";
    private static final String DAY = "12";
    private static final boolean REAL_INPUT = true;

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

        Input input = parse(lines);
        part1(input);
        part2(input);
    }

    private Input parse(List<String> lines) {
        Set<Long> initialState = new HashSet<>();
        char[] rawState = lines.get(0).substring("initial state: ".length()).toCharArray();
        for (int i = 0; i < rawState.length; i++) {
            if (rawState[i] == '#') {
                initialState.add((long)i);
            }
        }
        
        boolean[] rules = new boolean[32];
        for (String line : lines.subList(2, lines.size())) {
            int from = 0;
            for (char c : line.substring(0, 5).toCharArray()) {
                from = (from << 1) + ((c == '#') ? 1 : 0);
            }
            rules[from] = line.charAt(9) == '#';
        }
        return new Input(initialState, rules);
    }

    private void part1(Input input) {
        State state = new State(input.initialState);
        state.print();
        for (int i = 0; i < 200; i++) {
            state = state.apply(input.rules);
            System.out.print("" + i + " (" + state.minPlant + "): ");
            state.print();

        }

        System.out.println("Part 1: " + state.count());
    }

    private void part2(Input input) {
        State state = new State(input.initialState);
        Map<String, StepInfo> pastStates = new HashMap<>();
        pastStates.put(state.toString(), new StepInfo(0, state.minPlant));
        for (long i = 0; i < 50000000000L; i++) {
            state = state.apply(input.rules);
            StepInfo last = pastStates.put(state.toString(), new StepInfo(i, state.minPlant));
            if (last != null) {
                System.out.println("Cycle detected");
                long time = i - last.step;
                long dist = state.minPlant - last.minPlant;
                long cycles = (50000000000L - i - 1) / time;
                i = i + cycles * time;
                state = state.shift(dist * cycles);
                pastStates.clear();
            }
        }

        System.out.println("Part 2: " + state.count());
    }

    public record Input(Set<Long> initialState, boolean[] rules) {}

    public static class State {
        private final Set<Long> plants = new HashSet<>();
        private final long minPlant;
        private final long maxPlant;

        public State(Set<Long> plants) {
            this(plants, plants.stream().reduce(Long.MAX_VALUE, Math::min), plants.stream().reduce(Long.MIN_VALUE, Math::max));
        }

        private State(Set<Long> plants, long minPlant, long maxPlant) {
            this.plants.addAll(plants);
            this.minPlant = minPlant;
            this.maxPlant = maxPlant;
        }

        public boolean plantAt(long value) {
            return plants.contains(value);
        }

        public State apply(boolean[] rules) {
            Set<Long> newPlants = new HashSet<>();
            int key = 0;
            for (long i = minPlant - 2; i <= maxPlant + 2; i++) {
                key = ((key << 1) & 0b11111) + (plantAt(i + 2) ? 1 : 0) ;
                if (rules[key]) {
                    newPlants.add(i);
                }
            }
            return new State(newPlants);
        }


        public long count() {
            return plants.stream().reduce(0L, Long::sum);
        }

        public void print() {
            for (long i = minPlant - 2; i <= maxPlant + 2; i++) {
                if (plants.contains(i)) {
                    System.out.print('#');
                } else {
                    System.out.print('.');
                }
            }
            System.out.println();
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (long i = minPlant - 2; i <= maxPlant + 2; i++) {
                if (plants.contains(i)) {
                    builder.append('#');
                } else {
                    builder.append('.');
                }
            }
            return builder.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return Objects.equal(plants, state.plants);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(plants);
        }

        public State shift(long dist) {
            return new State(plants.stream().map(x -> x + dist).collect(Collectors.toSet()), minPlant + dist, maxPlant + dist);
        }
    }

    public record StepInfo(long step, long minPlant) {}


}

