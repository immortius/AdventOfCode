package xyz.immortius.advent2016.day22;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.util.Direction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day22 {

    //private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    private static final String YEAR = "2016";
    private static final String DAY = "22";

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day22().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + "input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        List<FileSystem> fileSystems = parse(lines);
        part1(fileSystems);
        part2(fileSystems);
    }

    private void part2(List<FileSystem> fileSystems) {
        State initialState = new State(fileSystems);

        //int steps = stepsToComplete(initialState);
        int steps = stepsToComplete2(initialState);

//
//        Set<State> possibleStates = new LinkedHashSet<>();
//        possibleStates.add(initialState);
//        Set<State> pastStates = new LinkedHashSet<>();
//
//        int steps = 0;
//        while (possibleStates.stream().noneMatch(x -> x.dataPos.equals(0,0))) {
//            System.out.println("Step: " + steps + ", states: " + possibleStates.size());
//            steps++;
//
//            pastStates.addAll(possibleStates);
//            Set<State> nextStates = new LinkedHashSet<>();
//            for (State state : possibleStates) {
//                nextStates.addAll(state.getPossibleStates());
//            }
//            nextStates.removeAll(pastStates);
//            possibleStates = nextStates;
//        }

        System.out.println("Part 2: " + steps);
    }

    private int stepsToComplete2(State initialState) {
        Set<Vector2ic> possibleEmpty = new LinkedHashSet<>();
        Vector2i dataPos = initialState.dataPos;
        int[] used = initialState.used;
        int[] size = initialState.size;

        possibleEmpty.add(initialState.emptyPos);

        int step = 0;
        while (!dataPos.equals(0,0 )) {
            step++;
            if (possibleEmpty.contains(new Vector2i(dataPos.x - 1, dataPos.y))) {
                possibleEmpty.clear();
                possibleEmpty.add(new Vector2i(dataPos));
                dataPos.x--;
            } else {
                Set<Vector2ic> newEmpty = Sets.newLinkedHashSet();
                for (Vector2ic pos : possibleEmpty) {
                    for (Vector2ic dir : DIRECTIONS) {
                        Vector2i adjPos = pos.add(dir, new Vector2i());
                        if (adjPos.x >= 0 && adjPos.y >= 0 && adjPos.x < initialState.dims.x && adjPos.y < initialState.dims.y) {
                            if (!adjPos.equals(dataPos) && used[initialState.indexOf(adjPos.x, adjPos.y)] <= size[initialState.indexOf(pos.x(), pos.y())]) {
                                newEmpty.add(adjPos);
                            }
                        }
                    }
                }
                possibleEmpty = newEmpty;
            }
        }
        return step;
    }

    private int stepsToComplete(State initialState) {
        PriorityQueue<State> queue = new PriorityQueue<>();
        queue.add(initialState);
        Set<State> pastStates = new HashSet<>();
        int bestSteps = Integer.MAX_VALUE;
        while (!queue.isEmpty()) {
            State next = queue.poll();
            if (next.dataPos.equals(0, 0)) {
                if (next.steps < bestSteps) {
                    System.out.println(next.steps);
                    bestSteps = next.steps;
                }
            } else {
                Set<State> states = next.getPossibleStates();
                states.removeAll(pastStates);
                queue.addAll(states);
                pastStates.addAll(states);
            }
        }
        return bestSteps;
    }

    private List<FileSystem> parse(List<String> lines) {
        List<FileSystem> fileSystems = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(" +");
            String[] coords = parts[0].split("-");
            int x = Integer.parseInt(coords[1].substring(1));
            int y = Integer.parseInt(coords[2].substring(1));
            fileSystems.add(new FileSystem(parts[0], x, y, Integer.parseInt(parts[1].substring(0, parts[1].length() - 1)), Integer.parseInt(parts[2].substring(0, parts[2].length() - 1))));
        }
        return fileSystems;
    }

    private void part1(List<FileSystem> fileSystems) {
        int pairs = 0;
        for (int i = 0; i < fileSystems.size(); i++) {
            for (int j = 0; j < fileSystems.size(); j++) {
                if (i == j) continue;

                if (fileSystems.get(i).used() > 0 && fileSystems.get(i).used() <= fileSystems.get(j).free()) {
                    pairs++;
                }

            }
        }
        System.out.println("Part 1: " + pairs);
    }

    public record FileSystem(String name, int x, int y, int size, int used) {
        Vector2ic position() {
            return new Vector2i(x, y);
        }
        int free() {
            return size - used;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FileSystem that = (FileSystem) o;
            return size == that.size && used == that.used && Objects.equal(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, size, used);
        }
    }

    private static final List<Vector2ic> DIRECTIONS = new ArrayList<>(Arrays.asList(new Vector2i(0, 1), new Vector2i(1, 0), new Vector2i(-1,0), new Vector2i(0, -1)));

    public static class State implements Comparable<State> {
        private final int steps;
        private final int[] used;
        private final int[] size;
        private final Vector2i dataPos;
        private final Vector2i emptyPos;
        private final Vector2i dims;

        public State(List<FileSystem> fileSystems) {
            steps = 0;
            int maxX = fileSystems.stream().map(x -> x.x).max(Comparator.naturalOrder()).get();
            int maxY = fileSystems.stream().map(x -> x.y).max(Comparator.naturalOrder()).get();
            dims = new Vector2i(maxX + 1, maxY + 1);
            used = new int[dims.y * dims.x];
            size = new int[dims.y * dims.x];
            for (FileSystem fileSystem : fileSystems) {
                used[indexOf(fileSystem.x, fileSystem.y)] = fileSystem.used;
                size[indexOf(fileSystem.x, fileSystem.y)] = fileSystem.size;
            }
            this.dataPos = new Vector2i(maxX, 0);
            for (int y = 0; y < dims.y; y++) {
                for (int x = 0; x < dims.x; x++) {
                    int index = indexOf(x, y);
                    if (used[index] == 0) {
                        emptyPos = new Vector2i(x, y);
                        return;
                    }
                }
            }
            emptyPos = new Vector2i();
        }

        private int indexOf(int x, int y) {
            return x + dims.x * y;
        }

        State(State other) {
            steps = other.steps + 1;
            used = Arrays.copyOf(other.used, other.used.length);
            size = Arrays.copyOf(other.size, other.size.length);
            dims = other.dims;
            dataPos = new Vector2i(other.dataPos);
            emptyPos = new Vector2i(other.emptyPos);
        }

        public Set<State> getPossibleStates() {
            Set<State> nextStates = new HashSet<>();
            int emptyIndex = indexOf(emptyPos.x, emptyPos.y);
            for (Vector2ic dir : DIRECTIONS) {
                Vector2i adjPos = emptyPos.add(dir, new Vector2i());
                if (adjPos.x >= 0 && adjPos.y >= 0 && adjPos.x < dims.x && adjPos.y < dims.y) {
                    int adjIndex = indexOf(adjPos.x, adjPos.y);
                    if (used[adjIndex] < size[emptyIndex]) {
                        State newState = new State(this);
                        newState.emptyPos.set(adjPos);
                        if (adjPos.equals(dataPos)) {
                            if (emptyPos.y != 0) {
                                continue;
                            }
                            newState.dataPos.set(emptyPos);
                        }
                        nextStates.add(newState);
                    }
                }
            }
            return nextStates;
        }

        public long estimate() {
            long emptyDist = Long.MAX_VALUE;
            Vector2i emptyPos = new Vector2i();
            for (int y = 0; y < dims.y; y++) {
                for (int x = 0; x < dims.x; x++) {
                    int index = indexOf(x, y);
                    if (used[index] == 0 && emptyDist > dataPos.gridDistance(x, y)) {
                        emptyPos.x = x;
                        emptyPos.y = y;
                        emptyDist = dataPos.gridDistance(x, y);
                    }
                }
            }
            long est = 2 * dataPos.x + 2 * dataPos.y;
            if (est > 0) {
                est += emptyPos.gridDistance(dataPos.x - 1, dataPos.y);
            }
            return est;
        }

        public void print() {
            int dataSize = used[indexOf(dataPos.x, dataPos.y)];
            for (int y = 0; y < dims.y; y++) {
                for (int x = 0; x < dims.x; x++) {
                    if (dataPos.equals(x, y)) {
                        System.out.print('D');
                    } else if (emptyPos.equals(x,y)) {
                        System.out.print('_');
                    } else {
                        System.out.print('#');
                    }
                }
                System.out.println();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            State state = (State) o;

            if (dataPos != null ? !dataPos.equals(state.dataPos) : state.dataPos != null) return false;
            return emptyPos != null ? emptyPos.equals(state.emptyPos) : state.emptyPos == null;
        }

        @Override
        public int hashCode() {
            int result = dataPos != null ? dataPos.hashCode() : 0;
            result = 31 * result + (emptyPos != null ? emptyPos.hashCode() : 0);
            return result;
        }

        @Override
        public int compareTo(@NotNull State o) {
            return Long.compare(estimate(), o.estimate());
        }
    }


}

