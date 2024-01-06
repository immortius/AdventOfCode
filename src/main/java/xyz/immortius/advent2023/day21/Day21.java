package xyz.immortius.advent2023.day21;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;
import xyz.immortius.util.Vector2l;
import xyz.immortius.util.Vector2lc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day21 {

    private static final String YEAR = "2023";
    private static final String DAY = "21";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day21().run();
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
        part2(input);
    }

    private Input parse(List<String> lines) {
        Set<Vector2lc> rocks = new LinkedHashSet<>();
        Vector2l start = new Vector2l();

        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                switch (line.charAt(x)) {
                    case '#' -> rocks.add(new Vector2l(x, y));
                    case 'S' -> start.set(x, y);
                }
            }
        }
        return new Input(start, rocks, new Vector2l(lines.get(0).length(), lines.size()));
    }

    private void part1(Input input) {
        int steps = 3 * 131 + 65;

        Set<Vector2lc> oddClosed = new HashSet<>();
        Set<Vector2lc> evenClosed = new HashSet<>();
        Deque<Vector2lc> open = new ArrayDeque<>();
        open.add(input.start);
        for (int i = 0; i < steps; i++) {
            open = step(input, open, i % 2 == 0 ? oddClosed : evenClosed);
        }


        System.out.println("Part 1: " + (((steps % 2) == 1) ? oddClosed.size() : evenClosed.size()));
    }

    private void printState(Set<Vector2lc> at, Input input) {
        Vector2l pos = new Vector2l();
        for (pos.y = -2 * input.size.y(); pos.y < 3 * input.size.y(); pos.y++) {
            for (pos.x = - 2 * input.size.x(); pos.x < 3 * input.size().x(); pos.x++) {
                if (input.rocks.contains(wrap(pos, input.size))) {
                    System.out.print('#');
                } else if (at.contains(pos)) {
                    System.out.print('O');
                } else {
                    System.out.print('.');
                }
            }
            System.out.println();
        }
    }

    private void part2(Input input) {

        long targetSteps = 26501365L;
        //long targetSteps = 4 * 131 + 65;

        long maxX = (targetSteps) % input.size().x();

        long steps = 4 * input.size.x() + maxX;

        Set<Vector2lc> oddClosed = new HashSet<>();
        Set<Vector2lc> evenClosed = new HashSet<>();
        Deque<Vector2lc> open = new ArrayDeque<>();
        open.add(input.start);
        for (int i = 0; i < steps; i++) {
            open = step(input, open, i % 2 == 0 ? oddClosed : evenClosed);
        }

        Set<Vector2lc> closed = (targetSteps % 2) == 1 ? oddClosed : evenClosed;

        long tilesBase = (targetSteps - maxX) / input.size.x();

        long offCenterSquares =  tilesBase * tilesBase;
        long centerSquares = (tilesBase - 1) * (tilesBase - 1);
        long centerDiagonals = (tilesBase - 1);
        long outerDiagonals = tilesBase;

        long totalCount = count(closed, new Vector2l(0,0), input.size) * centerSquares
                + count(closed, new Vector2l(input.size.x(),0), input.size) * offCenterSquares
                + count(closed, new Vector2l(2 * input.size.x(), 2 * input.size.y()), input.size) * centerDiagonals
                + count(closed, new Vector2l(2 * input.size.x(), -2 * input.size.y()), input.size) * centerDiagonals
                + count(closed, new Vector2l(-2 * input.size.x(), 2 * input.size.y()), input.size) * centerDiagonals
                + count(closed, new Vector2l(-2 * input.size.x(), -2 * input.size.y()), input.size) * centerDiagonals
                + count(closed, new Vector2l(3 * input.size.x(), 2 * input.size.y()), input.size) * outerDiagonals
                + count(closed, new Vector2l(3 * input.size.x(), -2 * input.size.y()), input.size) * outerDiagonals
                + count(closed, new Vector2l(-3 * input.size.x(), 2 * input.size.y()), input.size) * outerDiagonals
                + count(closed, new Vector2l(-3 * input.size.x(), -2 * input.size.y()), input.size) * outerDiagonals
                + count(closed, new Vector2l(4 * input.size.x(), 0), input.size)
                + count(closed, new Vector2l(-4 * input.size.x(), 0), input.size)
                + count(closed, new Vector2l(0, 4 * input.size.y()), input.size)
                + count(closed, new Vector2l(0, -4 * input.size.y()), input.size);

        Vector2l pos = new Vector2l();
        for (pos.y = -5; pos.y <= 5; pos.y++) {
            for (pos.x = -5; pos.x <= 5; pos.x++) {
                long val = count(closed, new Vector2l(input.size.x() * pos.x, input.size.y() * pos.y), input.size);
                String valString = Long.toString(val);
                for (int i = valString.length(); i < 5; i++) {
                    System.out.print(" ");
                }
                System.out.print(val);
            }
            System.out.println();
        }

        System.out.println("Part 2: " + totalCount + " vs " + closed.size());

    }

    private long count(Set<Vector2lc> closed, Vector2lc start, Vector2lc size) {
        long total = 0;
        Vector2l pos = new Vector2l();
        for (pos.y = start.y(); pos.y < start.y() + size.y(); pos.y++) {
            for (pos.x = start.x(); pos.x < start.x() + size.x(); pos.x++) {
                if (closed.contains(pos)) total++;
            }
        }
        return total;
    }

    @NotNull
    private Map<Vector2lc, Long> getDists(Input input) {
        Map<Vector2lc, Long> dist = new LinkedHashMap<>();
        Deque<Vector2lc> open = new ArrayDeque<>();
        dist.put(input.start, 0L);
        open.add(input.start);
        while (!open.isEmpty()) {
            Vector2lc pos = open.removeFirst();
            long newDist = dist.get(pos) + 1;
            for (Direction dir : Direction.values()) {
                Vector2l adjPos = dir.toVector().add(pos, new Vector2l());
                if (adjPos.x >= 0 && adjPos.y >= 0 && adjPos.x < input.size.x() && adjPos.y < input.size.y() && !input.rocks.contains(adjPos)) {
                    if (!dist.containsKey(adjPos)) {
                        dist.put(adjPos, newDist);
                        open.add(adjPos);
                    }
                }
            }
        }
        return dist;
    }

    @NotNull
    private Deque<Vector2lc> step(Input input, Deque<Vector2lc> open, Set<Vector2lc> closed) {
        Deque<Vector2lc> newOpen = new ArrayDeque<>();
        for (Vector2lc pos : open) {
            for (Direction dir : Direction.values()) {
                Vector2l adjPos = dir.toVector().add(pos, new Vector2l());
                if (!input.rocks.contains(wrap(adjPos, input.size)) && closed.add(adjPos)) {
                    newOpen.add(adjPos);
                }
            }
        }
        return newOpen;
    }

    private Vector2lc wrap(Vector2lc pos, Vector2lc size) {
        Vector2l result = new Vector2l(pos);
        result.x = ((result.x % size.x()) + size.x()) % size.x();
        result.y = ((result.y % size.x()) + size.y()) % size.y();
        return result;
    }

    private record Input(Vector2lc start, Set<Vector2lc> rocks, Vector2lc size) {
    }

    public enum Direction {
        Up('U', new Vector2l(0, 1)),
        Left('L', new Vector2l(-1, 0)),
        Down('D', new Vector2l(0, -1)),
        Right('R', new Vector2l(1, 0));

        private static final Map<Character, Direction> idLookup;

        private char id;
        private Vector2lc vector;

        Direction(char c, Vector2lc vector) {
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

        public Vector2lc toVector() {
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

