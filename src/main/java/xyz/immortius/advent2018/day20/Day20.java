package xyz.immortius.advent2018.day20;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day20 {

    private static final String YEAR = "2018";
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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        part1(lines.get(0));
        part2(lines);
    }

    private void part1(String line) {
        Multimap<Vector2ic, Vector2ic> network = HashMultimap.create();
        Set<Vector2ic> positions = new LinkedHashSet<>();
        positions.add(new Vector2i(0,0));
        Path path = extractPath(line.substring(1, line.length() - 1));

        path.explore(network, positions);

        Set<Vector2ic> open = new LinkedHashSet<>();
        open.add(new Vector2i(0,0));
        Set<Vector2ic> closed = new HashSet<>();
        closed.add(new Vector2i(0,0));
        int dist = 0;
        int count = 0;
        while (!open.isEmpty()) {
            dist++;
            Set<Vector2ic> newOpen = new LinkedHashSet<>();
            for (Vector2ic pos : open) {
                for (Vector2ic adjPos : network.get(pos)) {
                    if (closed.add(adjPos)) {
                        if (dist >= 1000) {
                            count++;
                        }
                        newOpen.add(adjPos);
                    }
                }
            }
            open = newOpen;
        }


        System.out.println("Part 1: " + (dist - 1));
        System.out.println("Part 2: " + count);
    }

    private Path extractPath(String line) {
        List<String> splitLine = choiceSplit(line);
        if (splitLine.size() > 1) {
            return new PathChoice(splitLine.stream().map(this::extractPath).toList());
        }

        List<String> segments = segmentSplit(line);
        if (segments.size() > 1) {
            return new PathSequence(segments.stream().map(this::extractPath).toList());
        }

        return new PathSegment(line);
    }

    private List<String> segmentSplit(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        int depth = 0;
        for (char c : line.toCharArray()) {
            switch (c) {
                case '(' -> {
                    if (depth == 0) {
                        result.add(builder.toString());
                        builder.setLength(0);
                        depth++;
                    } else {
                        depth++;
                        builder.append(c);
                    }
                }
                case ')' -> {
                    depth--;
                    if (depth == 0) {
                        result.add(builder.toString());
                        builder.setLength(0);
                    } else {
                        builder.append(c);
                    }
                }
                default -> builder.append(c);
            }
        }
        result.add(builder.toString());
        return result;
    }

    private List<String> choiceSplit(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        int depth = 0;
        for (char c : line.toCharArray()) {
            switch (c) {
                case '|' -> {
                    if (depth == 0) {
                        result.add(builder.toString());
                        builder.setLength(0);
                    } else {
                        builder.append(c);
                    }
                }
                case '(' -> {
                    depth++;
                    builder.append(c);
                }
                case ')' -> {
                    depth--;
                    builder.append(c);
                }
                default -> builder.append(c);
            }
        }
        result.add(builder.toString());
        return result;
    }

    private void part2(List<String> lines) {
        System.out.println("Part 2: ");
    }

    private interface Path {
        Set<Vector2ic> explore(Multimap<Vector2ic, Vector2ic> network, Set<Vector2ic> positions);
    }

    private static class PathSegment implements Path {
        private List<Direction> segment;

        public PathSegment(String segment) {
            this.segment = new ArrayList<>();
            for (char c : segment.toCharArray()) {
                this.segment.add(Direction.parse(c));
            }
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (Direction dir : segment) {
                builder.append(dir.id);
            }
            return builder.toString();
        }

        @Override
        public Set<Vector2ic> explore(Multimap<Vector2ic, Vector2ic> network, Set<Vector2ic> positions) {
            Set<Vector2ic> currentPositions = positions;
            for (Direction dir : segment) {
                Set<Vector2ic> nextPositions = new LinkedHashSet<>();
                for (Vector2ic pos : currentPositions) {
                    Vector2i next = pos.add(dir.toVector(), new Vector2i());
                    network.put(pos, next);
                    network.put(next, pos);
                    nextPositions.add(next);
                }
                currentPositions = nextPositions;
            }
            return currentPositions;
        }
    }

    private static class PathSequence implements Path {
        private final List<Path> paths;

        public PathSequence(List<Path> paths) {
            this.paths = new ArrayList<>(paths);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (Path path : paths) {
                builder.append(path);
            }
            return builder.toString();
        }

        @Override
        public Set<Vector2ic> explore(Multimap<Vector2ic, Vector2ic> network, Set<Vector2ic> positions) {
            Set<Vector2ic> current = positions;
            for (Path path : paths) {
                current = path.explore(network, current);
            }
            return current;
        }
    }

    private static class PathChoice implements Path {
        private static final Joiner CHOICE_JOINER = Joiner.on('|');
        private final List<Path> paths;

        public PathChoice(List<Path> paths) {
            this.paths = new ArrayList<>(paths);
        }

        @Override
        public String toString() {
            return "(" + CHOICE_JOINER.join(paths) + ")";
        }

        @Override
        public Set<Vector2ic> explore(Multimap<Vector2ic, Vector2ic> network, Set<Vector2ic> positions) {
            Set<Vector2ic> next = new LinkedHashSet<>();
            for (Path path : paths) {
                next.addAll(path.explore(network, positions));
            }
            return next;
        }
    }

    public enum Direction {
        Up('N', new Vector2i(0, 1)),
        Down('S', new Vector2i(0, -1)),
        Left('W', new Vector2i(-1, 0)),
        Right('E', new Vector2i(1, 0));

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

        public static Direction parse(char c) {
            return idLookup.get(c);
        }
    }

}

