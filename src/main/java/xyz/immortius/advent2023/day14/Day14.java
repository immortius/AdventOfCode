package xyz.immortius.advent2023.day14;

import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day14 {

    //private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    private static final String YEAR = "2023";
    private static final String DAY = "14";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day14().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            lines = CharStreams.readLines(reader);
        }

        Grid board = parse(lines);
        part1(board);
        part2(board);
    }

    private Grid parse(List<String> lines) {
        boolean[][] map = new boolean[lines.get(0).length()][lines.size()];
        List<Vector2ic> rocks = new ArrayList<>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                switch (line.charAt(x)) {
                    case 'O' -> rocks.add(new Vector2i(x, y));
                    case '#' -> map[x][y] = true;
                }
            }
        }
        return new Grid(map, rocks);
    }

    private void part1(Grid board) {
        System.out.println("Part 1: " + board.tilt(Direction.NORTH).score());
    }

    private void part2(Grid board) {
        int LOOPS = 1000000000;
        List<Grid> pastBoards = new ArrayList<>();
        for (int i = 0; i < LOOPS; i++) {
            for (Direction dir : Direction.values()) {
                board = board.tilt(dir);
            }
            if (pastBoards.contains(board)) {
                System.out.println("Repeat at " + i);
                int loopSize = i - pastBoards.indexOf(board);
                int repetitions = (LOOPS - i) / loopSize;
                i += loopSize * repetitions;
                pastBoards.clear();
            }
            pastBoards.add(board);
        }
        System.out.println("Part 2: " + board.score());
    }

    private static final class Grid {
        private static boolean[][] map;
        private Set<Vector2ic> rocks;
        private Vector2ic size;

        public Grid(boolean[][] map, Collection<Vector2ic> rocks) {
            this.map = map;
            this.rocks = new LinkedHashSet<>(rocks);
            size = new Vector2i(map.length, map[0].length);
        }

        public Grid tilt(Direction dir) {
            List<Vector2ic> newRocks = new ArrayList<>();
            Vector2i pos = new Vector2i();

            Direction iteration = dir.reverse();

            for (pos.setComponent(dir.component, iteration.start(size)); pos.get(dir.component) != iteration.end(size); pos.add(iteration.getVector())) {
                for (pos.setComponent(dir.getTangentComponent(), 0); pos.get(dir.getTangentComponent()) != size.get(dir.getTangentComponent()); pos.setComponent(dir.getTangentComponent(), pos.get(dir.getTangentComponent()) + 1)) {
                    if (rocks.contains(pos)) {
                        Vector2i newRock = new Vector2i(pos);
                        newRock.add(dir.getVector());
                        while (newRock.get(dir.component) != dir.end(size) && !map[newRock.x][newRock.y] && !newRocks.contains(newRock)) {
                            newRock.add(dir.getVector());
                        }
                        newRock.sub(dir.getVector());
                        newRocks.add(newRock);
                    }
                }
            }

            return new Grid(map, newRocks);
        }

        public Vector2ic size() {
            return size;
        }

        public long score() {
            int score = 0;
            for (Vector2ic rock : rocks) {
                score += size.y() - rock.y();
            }
            return score;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            Vector2i pos = new Vector2i();
            for (pos.y = 0; pos.y < map.length; pos.y++) {
                for (pos.x = 0; pos.x < map.length; pos.x++) {
                    if (map[pos.x][pos.y]) {
                        builder.append('#');
                    } else if (rocks.contains(pos)) {
                        builder.append('O');
                    } else {
                        builder.append('.');
                    }
                }
                builder.append('\n');
            }
            return builder.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Grid grid = (Grid) o;
            return com.google.common.base.Objects.equal(rocks, grid.rocks);
        }

        @Override
        public int hashCode() {
            return com.google.common.base.Objects.hashCode(rocks);
        }
    }

    enum Direction {
        NORTH(new Vector2i(0, -1), 1),
        WEST(new Vector2i(-1, 0), 0),
        SOUTH(new Vector2i(0, 1), 1),
        EAST(new Vector2i(1, 0), 0);

        private Vector2ic dir;
        private int component;

        private Direction(Vector2ic dir, int component) {
            this.dir = dir;
            this.component = component;
        }

        public Direction reverse() {
            return Direction.values()[(ordinal() + 2) % Direction.values().length];
        }

        public int getComponent() {
            return component;
        }

        public int getTangentComponent() {
            return (component + 1) % 2;
        }

        public Vector2ic getVector() {
            return dir;
        }

        public int start(Vector2ic size) {
            return dir.get(component) == -1 ? size.get(component) -1 : 0;
        }

        public int end(Vector2ic size) {
            return dir.get(component) == -1 ? -1 : size.get(component);
        }
    }

}

