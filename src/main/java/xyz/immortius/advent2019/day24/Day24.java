package xyz.immortius.advent2019.day24;

import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day24 {

    private static final String YEAR = "2019";
    private static final String DAY = "24";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day24().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        boolean[][] state = parse(lines);
        part1(state);
        part2(state);
    }


    private boolean[][] parse(List<String> lines) {
        boolean[][] grid = new boolean[5][5];

        for (int y = 0; y < 5; y ++) {
            for (int x = 0; x < 5; x++) {
                grid[x][y] = lines.get(y).charAt(x) == '#';
            }
        }
        return grid;
    }

    public record SimpleState(boolean[][] state) {
        SimpleState increment() {
            boolean[][] newState = new boolean[5][5];
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 5; x++) {
                    int count = getValue(x - 1, y) + getValue(x + 1, y) + getValue(x, y - 1) + getValue(x, y + 1);
                    newState[x][y] = (count == 1) || (count == 2 && !state[x][y]);
                }
            }
            return new SimpleState(newState);
        }

        int getValue(int x, int y) {
            if (x < 0 || y < 0 || x >= 5 || y >= 5) {
                return 0;
            }
            return (state[x][y]) ? 1 : 0;
        }

        void printState() {
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 5; x++) {
                    System.out.print((state[x][y]) ? '#' : '.');
                }
                System.out.println();
            }
            System.out.println();
        }

        private int rating() {
            int result = 0;
            for (int y = 4; y >= 0; y--) {
                for (int x = 4; x >= 0; x--) {
                    result = result << 1;
                    if (state[x][y]) {
                        result += 1;
                    }
                }
            }
            return result;
        }
    }

    private void part1(boolean[][] initialState) {

        Set<Integer> pastRatings = new HashSet<>();
        SimpleState state = new SimpleState(initialState);
        while (pastRatings.add(state.rating())) {
            state = state.increment();
        }
        System.out.println("Part 1: " + state.rating());
    }

    private void part2(boolean[][] initialState) {
        RecursiveState state = new RecursiveState(initialState);

        for (int i = 0; i < 200; i++) {
            state = state.step();
        }

        System.out.println("Part 2: " + state.count());
    }

    private static class RecursiveState {
        private final int[][] values = new int[5][5];
        private RecursiveState parent;
        private RecursiveState child;

        public RecursiveState() {
        }

        public RecursiveState(boolean[][] state) {
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 5; x++) {
                    values[x][y] = state[x][y] ? 1 : 0;
                }
            }
        }

        public RecursiveState step() {
            RecursiveState newState = new RecursiveState();
            spread(newState);
            if (newState.parent != null) {
                parent = new RecursiveState();
                parent.child = this;
                parent.reduce(newState.parent);
                return newState.parent;
            } else {
                reduce(newState);
                return newState;
            }
        }

        private void reduce(RecursiveState newState) {
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 5; x++) {
                    newState.values[x][y] = switch (newState.values[x][y]) {
                        case 1 -> 1;
                        case 2 -> values[x][y] == 1 ? 0 : 1;
                        default -> 0;
                    };
                }
            }
            if (newState.child != null) {
                if (child != null) {
                    child.reduce(newState.child);
                } else {
                    new RecursiveState().reduce(newState.child);
                }
            }
        }

        private void spread(RecursiveState newState) {
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 5; x++) {
                    if (values[x][y] != 0) {
                        for (Direction dir : Direction.values()) {
                            Vector2i adj = dir.toVector().add(x, y, new Vector2i());
                            if (adj.x < 0 || adj.x > 4 || adj.y < 0 || adj.y > 4) {
                                newState.getParent().values[2 + dir.toVector().x()][2 + dir.toVector().y()]++;
                            } else if (adj.x == 2 && adj.y == 2) {
                                newState.getChild().increment(dir);
                            } else {
                                newState.values[adj.x][adj.y]++;
                            }
                        }
                    }
                }
            }
            if (child != null) {
                child.spread(newState.getChild());
            }
        }

        private RecursiveState getChild() {
            if (child == null) {
                child = new RecursiveState();
                child.parent = this;
            }
            return child;
        }

        private RecursiveState getParent() {
            if (parent == null) {
                parent = new RecursiveState();
                parent.child = this;
            }
            return parent;
        }

        private void increment(Direction dir) {
            switch (dir) {
                case Up -> {
                    for (int x = 0; x < 5; x++) {
                        values[x][4]++;
                    }
                }
                case Down -> {
                    for (int x = 0; x < 5; x++) {
                        values[x][0]++;
                    }
                }
                case Left -> {
                    for (int y = 0; y < 5; y++) {
                        values[4][y]++;
                    }
                }
                case Right -> {
                    for (int y = 0; y < 5; y++) {
                        values[0][y]++;
                    }
                }
            }
        }

        public int count() {
            int count = 0;
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 5; x++) {
                    count += values[x][y];
                }
            }
            if (child != null) {
                return count + child.count();
            }
            return count;
        }

        public void print() {
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 5; x++) {
                    System.out.print(values[x][y]);
                }
                System.out.println();
            }
            System.out.println();
            if (child != null) {
                child.print();
            }
        }
    }

    public enum Direction {
        Up('U', new Vector2i(0, -1)),
        Down('D', new Vector2i(0, 1)),
        Left('L', new Vector2i(-1, 0)),
        Right('R', new Vector2i(1, 0));

        private char id;
        private Vector2ic vector;

        Direction(char c, Vector2ic vector) {
            this.id = c;
            this.vector = vector;
        }

        public Vector2ic toVector() {
            return vector;
        }

        public char getId() {
            return id;
        }
    }

}

