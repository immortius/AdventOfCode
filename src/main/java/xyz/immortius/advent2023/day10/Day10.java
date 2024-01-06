package xyz.immortius.advent2023.day10;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day10 {

    private static final String YEAR = "2023";
    private static final String DAY = "10";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day10().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            lines = CharStreams.readLines(reader);
        }

        Grid grid = parse(lines);
        Set<Vector2ic> loop = part1(grid);
        part2(grid, loop);
    }

    private void part2b(Grid grid, Set<Vector2ic> loop) {
        
    }

    private void part2(Grid grid, Set<Vector2ic> loop) {
        boolean[][] expandedGrid = new boolean[grid.size.x() * 3][grid.size.y() * 3];
        for (int y = 0; y < grid.size.y(); y++) {
            for (int x = 0; x < grid.size.x(); x++) {
                if (loop.contains(new Vector2i(x, y))) {
                    expandedGrid[x * 3 + 1][y * 3 + 1] = true;
                    for (Vector2ic adjDir : grid.map[x][y].adjacentDirections) {
                        expandedGrid[x * 3 + 1 + adjDir.x()][y * 3 + 1 + adjDir.y()] = true;
                    }
                }
            }
        }

        Deque<Vector2ic> open = new ArrayDeque<>();
        open.add(new Vector2i(0,0));
        Set<Vector2ic> closed = new LinkedHashSet<>();

        while (!open.isEmpty()) {
            Vector2ic pos = open.pop();
            for (Vector2ic adjDir : PipePart.START.adjacentDirections) {
                Vector2i adj = pos.add(adjDir, new Vector2i());
                if (adj.x >= 0 && adj.y >= 0 && adj.x < grid.size.x() * 3 && adj.y < grid.size.y() * 3 && !expandedGrid[adj.x][adj.y] && closed.add(adj)) {
                    open.add(adj);
                }
            }
        }

        int count = 0;
        Vector2i pos = new Vector2i(0,0);
        for (pos.y = 0; pos.y < grid.size.y(); pos.y++) {
            for (pos.x = 0; pos.x < grid.size.x(); pos.x++) {
                if (!loop.contains(pos) && !closed.contains(new Vector2i(pos.x * 3 + 1, pos.y * 3 + 1))) {
                    count++;
                }
            }
        }
        System.out.println("Part 2: " + count);
    }

    private Grid parse(List<String> lines) {
        Vector2i start = new Vector2i();
        PipePart[][] map = new PipePart[lines.get(0).length()][lines.size()];
        Vector2ic size = new Vector2i(lines.get(0).length(), lines.size());

        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                map[x][y] = PipePart.parse(line.charAt(x));
                if (map[x][y] == PipePart.START) {
                    start.set(x, y);
                }
            }
        }

        Set<Vector2ic> startAdjacency = new LinkedHashSet<>();
        for (Vector2ic dir : PipePart.START.adjacentDirections) {
            Vector2i adj = start.add(dir, new Vector2i());
            if (adj.x >= 0 && adj.x < size.x() && adj.y >= 0 && adj.y < size.y() && map[adj.x][adj.y].adjacentDirections.contains(dir.mul(-1, new Vector2i()))) {
                startAdjacency.add(dir);
            }
        }
        for (PipePart part : PipePart.values()) {
            if (part.adjacentDirections.equals(startAdjacency)) {
                System.out.println("Start is " + part);
                map[start.x][start.y] = part;
            }
        }

        return new Grid(start, size, map);

    }

    private Set<Vector2ic> part1(Grid grid) {
        Set<Vector2ic> loop = new LinkedHashSet<>();
        Vector2i pos = new Vector2i(grid.start);
        for (Vector2ic dir : PipePart.START.adjacentDirections) {
            Vector2i adjPos = pos.add(dir, new Vector2i());
            PipePart adjPipe = grid.map[adjPos.x][adjPos.y];
            if (adjPipe.adjacentDirections.contains(dir.mul(-1, new Vector2i()))) {
                pos.set(adjPos);
                break;
            }
        }
        Vector2i lastPos = new Vector2i(grid.start);
        long length = 1;
        loop.add(new Vector2i(grid.start));

        while (!grid.start.equals(pos)) {
            loop.add(new Vector2i(pos));
            System.out.println(pos.x + "," + pos.y + ": " + grid.map[pos.x][pos.y].representation);
            length++;
            for (Vector2ic adjDir : grid.map[pos.x][pos.y].adjacentDirections) {
                Vector2i adjPos = pos.add(adjDir, new Vector2i());
                if (!lastPos.equals(adjPos)) {
                    lastPos.set(pos);
                    pos.set(adjPos);
                    break;
                }
            }
        }
        System.out.println("Part 1: " + (length / 2));
        return loop;
    }

    private record Grid(Vector2ic start, Vector2ic size, PipePart[][] map) {}

    private enum PipePart {
        VERTICAL('|', new Vector2i(0, 1), new Vector2i(0, -1)),
        HORIZONTAL('-', new Vector2i(1, 0), new Vector2i(-1, 0)),
        NORTH_WEST('J', new Vector2i(0, -1), new Vector2i(-1, 0)),
        NORTH_EAST('L', new Vector2i(0, -1), new Vector2i(1, 0)),
        SOUTH_WEST('7', new Vector2i(0, 1), new Vector2i(-1, 0)),
        SOUTH_EAST('F', new Vector2i(0, 1), new Vector2i(1, 0)),
        EMPTY('.'),
        START('S', new Vector2i(0, 1), new Vector2i(0, -1), new Vector2i(1, 0), new Vector2i(-1, 0));

        private static final Map<Character, PipePart> lookup;

        private final char representation;
        private final Set<Vector2ic> adjacentDirections;

        static {
            ImmutableMap.Builder<Character, PipePart> builder = ImmutableMap.builder();
            for (PipePart part : PipePart.values()) {
                builder.put(part.representation, part);
            }
            lookup = builder.build();
        }

        PipePart(char representation, Vector2ic ... adjacency) {
            this.representation = representation;
            this.adjacentDirections = new LinkedHashSet<>(List.of(adjacency));
        }

        public static PipePart parse(char c) {
            return lookup.get(c);
        }

        public Collection<Vector2ic> getAdjacency() {
            return adjacentDirections;
        }
    }

}

