package xyz.immortius.advent2023.day23;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day23 {

    private static final String YEAR = "2023";
    private static final String DAY = "23";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day23().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            lines = CharStreams.readLines(reader);
        }

        Input input = parse(lines);
        part1(input);
    }

    private Input parse(List<String> lines) {
        Map<Vector2ic, Direction> directions = new LinkedHashMap<>();
        Set<Vector2ic> paths = new LinkedHashSet<>();

        Vector2i pos = new Vector2i();
        for (pos.y = 0; pos.y < lines.size(); pos.y++) {
            String line = lines.get(pos.y);
            for (pos.x = 0; pos.x < line.length(); pos.x++) {
                switch (line.charAt(pos.x)) {
                    case '.' -> paths.add(new Vector2i(pos));
                    case '<' -> directions.put(new Vector2i(pos), Direction.Left);
                    case '^' -> directions.put(new Vector2i(pos), Direction.Up);
                    case '>' -> directions.put(new Vector2i(pos), Direction.Right);
                    case 'v' -> directions.put(new Vector2i(pos), Direction.Down);
                }
            }
        }
        return new Input(paths, directions, new Vector2i(lines.get(0).length() - 2, lines.size() - 1));
    }

    private void part1(Input input) {

        Map<Vector2ic, Path> paths = new LinkedHashMap<>();
        Set<Vector2ic> pathStarts = new LinkedHashSet<>();
        pathStarts.add(new Vector2i(1, 0));
        for (Map.Entry<Vector2ic, Direction> directionalTiles : input.directions.entrySet()) {
            pathStarts.add(directionalTiles.getValue().toVector().add(directionalTiles.getKey(), new Vector2i()));
        }
        for (Vector2ic pathStart : pathStarts) {
            Vector2i pos = new Vector2i(pathStart);
            Direction backDir = null;
            int length = 1;
            boolean end = false;
            while (!end) {
                Vector2ic nextPos = null;
                Direction nextDir = null;
                for (Direction dir : Direction.values()) {
                    if (dir != backDir) {
                        Vector2i adjPos = dir.vector.add(pos, new Vector2i());
                        if (input.paths.contains(adjPos)) {
                            nextPos = adjPos;
                            nextDir = dir;
                            break;
                        }
                    }
                }
                if (nextPos != null) {
                    pos.set(nextPos);
                    backDir = nextDir.reverse();
                    length++;
                } else {
                    end = true;
                }
            }

            Path path = new Path(pathStart, pos, length);
            paths.put(pathStart, path);
            paths.put(path.end, path);
        }

        List<Path> path = findMaxPath(new Vector2i(1, 0), input.end, paths, input.directions, new LinkedHashSet<>());

        System.out.println("Part 1: " + (sumPath(path) - 1));

        List<Path> path2 = findMaxPathUndirectional(new Vector2i(1, 0), input.end, paths, input.directions, new LinkedHashSet<>());

        System.out.println("Part 2: " + (sumPath(path2) - 1));
    }


    private List<Path> findMaxPath(Vector2i start, Vector2ic end, Map<Vector2ic, Path> paths, Map<Vector2ic, Direction> directions, Set<Vector2ic> visited) {
        Path current = paths.get(start);
        if (end.equals(current.end)) {
            return List.of(current);
        }
        Set<Vector2ic> newVisited = new LinkedHashSet<>(visited);
        newVisited.add(start);

        int max = 0;
        List<Path> maxPath = null;
        for (Direction dir : Direction.values()) {
            Vector2i adjPos = dir.toVector().add(current.end, new Vector2i());
            if (directions.get(adjPos) == dir && !newVisited.contains(adjPos)) {
                adjPos.add(dir.toVector());
                List<Path> path = findMaxPath(adjPos, end, paths, directions, newVisited);
                if (path != null && sumPath(path) > max) {
                    max = sumPath(path);
                    maxPath = path;
                }
            }
        }
        if (maxPath == null) {
            return null;
        }
        List<Path> result = new ArrayList<>();
        result.add(current);
        result.addAll(maxPath);
        return result;
    }

    private List<Path> findMaxPathUndirectional(Vector2ic start, Vector2ic end, Map<Vector2ic, Path> paths, Map<Vector2ic, Direction> directions, Set<Path> visited) {
        Path current = paths.get(start);
        Vector2ic pathEnd = (current.start.equals(start)) ? current.end : current.start;
        if (end.equals(pathEnd)) {
            return List.of(current);
        }
        Set<Path> newVisited = new LinkedHashSet<>(visited);
        newVisited.add(current);

        int max = 0;
        List<Path> maxPath = null;
        for (Direction dir : Direction.values()) {
            Vector2i adjPos = dir.toVector().add(pathEnd, new Vector2i());
            if (directions.get(adjPos) != null) {
                adjPos.add(dir.toVector());
                Path adjPath = paths.get(adjPos);
                if (!newVisited.contains(adjPath)) {
                    List<Path> path = findMaxPathUndirectional(adjPos, end, paths, directions, newVisited);
                    if (path != null && sumPath(path) > max) {
                        max = sumPath(path);
                        maxPath = path;
                    }
                }
            }
        }
        if (maxPath == null) {
            return null;
        }
        List<Path> result = new ArrayList<>();
        result.add(current);
        result.addAll(maxPath);
        return result;
    }

    public int sumPath(List<Path> paths) {
        return paths.stream().mapToInt(x -> x.length).sum() + paths.size() - 1;
    }

    private record Input(Set<Vector2ic> paths, Map<Vector2ic, Direction> directions, Vector2ic end) {
    }

    private record Path(Vector2ic start, Vector2ic end, int length) {
    }

    public enum Direction {
        Up('U', new Vector2i(0, -1)),
        Left('L', new Vector2i(-1, 0)),
        Down('D', new Vector2i(0, 1)),
        Right('R', new Vector2i(1, 0));

        private static final Map<Character, Direction> idLookup;

        private char id;
        private Vector2ic vector;

        Direction(char c, Vector2ic vector) {
            this.id = c;
            this.vector = vector;
        }

        static {
            ImmutableMap.Builder<Character, Direction> builder = new ImmutableMap.Builder<>();
            for (Direction direction : Direction.values()) {
                builder.put(direction.id, direction);
            }
            idLookup = builder.build();
        }

        public Vector2ic toVector() {
            return vector;
        }

        public char getId() {
            return id;
        }

        public Direction clockwise() {
            return Direction.values()[(ordinal() + 1) % Direction.values().length];
        }

        public Direction anticlockwise() {
            return Direction.values()[(ordinal() + Direction.values().length - 1) % Direction.values().length];
        }

        public Direction reverse() {
            return Direction.values()[(ordinal() + 2) % Direction.values().length];
        }

        public static Direction parse(char c) {
            return idLookup.get(c);
        }
    }


}

