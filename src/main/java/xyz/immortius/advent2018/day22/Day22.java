package xyz.immortius.advent2018.day22;

import com.google.common.base.Objects;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.util.Direction;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Day22 {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day22().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        example1();
        part1();
        example2();
        part2();
    }

    private void example1() {
        Cave cave = new Cave(10, 10, 510);
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                System.out.print(switch (cave.getErosionAt(x, y) % 3) {
                    case 0 -> '.';
                    case 1 -> '=';
                    default -> '|';
                });
            }
            System.out.println();
        }

        int risk = 0;
        for (int y = 0; y <= 10; y++) {
            for (int x = 0; x <= 10; x++) {
                risk += cave.getErosionAt(x, y) % 3;
            }
        }
        System.out.println("Example risk: " + risk);
    }

    private void part1() {
        Vector2i targetPos = new Vector2i(6, 797);
        int depth = 11991;
        Cave cave = new Cave(targetPos.x, targetPos.y, depth);
        int risk = 0;
        for (int y = 0; y <= targetPos.y; y++) {
            for (int x = 0; x <= targetPos.x; x++) {
                risk += cave.getErosionAt(x, y) % 3;
            }
        }
        System.out.println("Part 1: " + risk);
    }

    private void example2() {
        Vector2i targetPos = new Vector2i(10, 10);
        Cave cave = new Cave(targetPos.x, targetPos.y, 510);

        AStar aStar = new AStar(new State(new Vector2i(0, 0), Tool.Torch), new State(targetPos, Tool.Torch), (state) -> targetPos.gridDistance(state.pos) + ((state.equipped == Tool.Torch) ? 0 : 7), (state) -> {
            List<State> results = new ArrayList<>();
            AreaType type = cave.getTypeAt(state.pos.x(), state.pos.y());
            for (Tool tool : Tool.values()) {
                if (tool != state.equipped && tool.isUsableIn(type)) {
                    results.add(new State(state.pos, tool));
                }
            }
            for (Direction dir : Direction.values()) {
                Vector2i adjPos = state.pos.add(dir.toVector(), new Vector2i());
                if (adjPos.x >= 0 && adjPos.y >= 0 && state.equipped.isUsableIn(cave.getTypeAt(adjPos.x, adjPos.y))) {
                    results.add(new State(adjPos, state.equipped));
                }
            }
            return results;
        }, (from, to) -> (from.equipped == to.equipped) ? 1 : 7);


        long time = aStar.run();
        System.out.println("Example 2: " + time);

    }

    private void part2() {
        Vector2i targetPos = new Vector2i(6, 797);
        int depth = 11991;
        Cave cave = new Cave(targetPos.x, targetPos.y, depth);

        AStar aStar = new AStar(new State(new Vector2i(0, 0), Tool.Torch), new State(targetPos, Tool.Torch), (state) -> targetPos.gridDistance(state.pos) + ((state.equipped == Tool.Torch) ? 0 : 7), (state) -> {
            List<State> results = new ArrayList<>();
            AreaType type = cave.getTypeAt(state.pos.x(), state.pos.y());
            for (Tool tool : Tool.values()) {
                if (tool != state.equipped && tool.isUsableIn(type)) {
                    results.add(new State(state.pos, tool));
                }
            }
            for (Direction dir : Direction.values()) {
                Vector2i adjPos = state.pos.add(dir.toVector(), new Vector2i());
                if (adjPos.x >= 0 && adjPos.y >= 0 && state.equipped.isUsableIn(cave.getTypeAt(adjPos.x, adjPos.y))) {
                    results.add(new State(adjPos, state.equipped));
                }
            }
            return results;
        }, (from, to) -> (from.equipped == to.equipped) ? 1 : 7);


        long time = aStar.run();
        System.out.println("Part2 2: " + time);
    }

    private static class Cave {
        private static final int EROSION_MODULO = 20183;
        private Map<Vector2ic, Integer> erosionIndex = new LinkedHashMap<>();
        private final int depth;

        public Cave(int targetX, int targetY, int depth) {
            erosionIndex.put(new Vector2i(0, 0), depth % EROSION_MODULO);
            erosionIndex.put(new Vector2i(targetX, targetY), depth % EROSION_MODULO);
            this.depth = depth % 20183;
        }

        public AreaType getTypeAt(int x, int y) {
            return AreaType.values()[getErosionAt(x, y) % 3];
        }

        public int getErosionAt(int x, int y) {
            Vector2i pos = new Vector2i(x, y);
            Integer result = erosionIndex.get(pos);
            if (result == null) {
                int erosion = calcErosionFor(pos);
                erosionIndex.put(pos, erosion);
                return erosion;
            }
            return result;
        }

        private int calcErosionFor(Vector2ic pos) {
            return (int) ((calcGeologicFor(pos) + depth) % EROSION_MODULO);
        }

        private long calcGeologicFor(Vector2ic pos) {
            if (pos.y() == 0) {
                return pos.x() * 16807L;
            } else if (pos.x() == 0) {
                return pos.y() * 48271L;
            } else {
                return (long) getErosionAt(pos.x() - 1, pos.y()) * getErosionAt(pos.x(), pos.y() - 1);
            }
        }
    }

    public enum AreaType {
        Rocky('.'),
        Wet('='),
        Narrow('|');

        private final char display;

        private AreaType(char display) {
            this.display = display;
        }
    }

    public enum Tool {
        Nothing(AreaType.Wet, AreaType.Narrow),
        ClimbingGear(AreaType.Rocky, AreaType.Wet),
        Torch(AreaType.Narrow, AreaType.Rocky);

        private final Set<AreaType> usableIn;

        private Tool(AreaType... usableIn) {
            this.usableIn = new HashSet<>();
            this.usableIn.addAll(Arrays.asList(usableIn));
        }

        boolean isUsableIn(AreaType type) {
            return usableIn.contains(type);
        }
    }

    public record State(Vector2ic pos, Tool equipped) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return Objects.equal(pos, state.pos) && equipped == state.equipped;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(pos, equipped);
        }
    }

    public static class AStar {

        private Map<State, Long> fScore = new HashMap<>();
        private Map<State, Long> gScore = new HashMap<>();
        private Map<State, State> cameFrom = new HashMap<>();
        private PriorityQueue<State> open = new PriorityQueue<>(Comparator.comparingLong((State a) -> fScore.get(a)));

        private Function<State, Long> estimateFunc;
        private Function<State, List<State>> connectionFunc;
        private BiFunction<State, State, Integer> costFunc;
        private State end;

        private long cutoff = Long.MAX_VALUE;

        public AStar(State start, State end, Function<State, Long> estimate, Function<State, List<State>> connectionFunc, BiFunction<State, State, Integer> costFunc) {
            this.estimateFunc = estimate;
            gScore.put(start, 0L);
            fScore.put(start, gScore.get(start) + estimateFunc.apply(start));
            open.add(start);
            this.costFunc = costFunc;
            this.end = end;
            this.connectionFunc = connectionFunc;
        }

        public long run() {
            while (!open.isEmpty()) {
                State current = open.remove();
                if (current.equals(end)) {
                    return gScore.get(current);
                }

                if (fScore.get(current) >= cutoff) {
                    break;
                }

                for (State neighbour : connectionFunc.apply(current)) {
                    long neighbourGScore = gScore.get(current) + costFunc.apply(current, neighbour);
                    if (neighbourGScore <= cutoff && neighbourGScore < gScore.getOrDefault(neighbour, Long.MAX_VALUE)) {
                        cameFrom.put(neighbour, current);
                        gScore.put(neighbour, neighbourGScore);
                        fScore.put(neighbour, neighbourGScore + estimateFunc.apply(neighbour));
                        open.remove(neighbour);
                        open.add(neighbour);
                    }
                }
            }
            if (gScore.get(end) != null) {
                return gScore.get(end);
            }
            return -1;
        }

        public void setCutoff(int cutoff) {
            this.cutoff = cutoff;
        }

        public int getReachable() {
            return gScore.size();
        }
    }


}

