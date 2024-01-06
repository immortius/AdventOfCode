package xyz.immortius.advent2023.day16;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.SetMultimap;
import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day16 {

    private static final String YEAR = "2023";
    private static final String DAY = "16";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day16().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            lines = CharStreams.readLines(reader);
        }

        Grid input = parse(lines);
        part1(input);
        part2(input);
    }

    private Grid parse(List<String> lines) {
        Tile[][] tiles = new Tile[lines.get(0).length()][lines.size()];
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                tiles[x][y] = Tile.parse(line.charAt(x));
            }
        }
        return new Grid(tiles);
    }

    private void part1(Grid input) {
        System.out.println("Part 1: " + calculateEnergy(input, new Beam(new Vector2i(-1, 0), new Vector2i(1, 0))));
    }

    private void part2(Grid input) {
        List<Beam> starts = new ArrayList<>();
        for (int x = 0; x < input.size.x(); x++) {
            starts.add(new Beam(new Vector2i(x, -1), new Vector2i(0, 1)));
            starts.add(new Beam(new Vector2i(x, input.size.y()), new Vector2i(0, -1)));
        }
        for (int y = 0; y < input.size.y(); y++) {
            starts.add(new Beam(new Vector2i(-1, y), new Vector2i(1, 0)));
            starts.add(new Beam(new Vector2i(input.size.x(), y), new Vector2i(-1, 0)));
        }

        int bestEnergy = 0;
        for (Beam start : starts) {
            int energy = calculateEnergy(input, start);
            if (energy > bestEnergy) {
                bestEnergy = energy;
            }
        }

        System.out.println("Part 2: " + bestEnergy);
    }

    public int calculateEnergy(Grid grid, Beam start) {
        Deque<Beam> beams = new ArrayDeque<>();
        beams.add(start);
        SetMultimap<Vector2ic, Beam> tileBeams = HashMultimap.create();
        while (!beams.isEmpty()) {
            Beam beam = beams.pop();
            Vector2i newPos = beam.position.add(beam.direction, new Vector2i());
            if (grid.isInBounds(newPos)) {
                List<Beam> newBeams = grid.tileAt(newPos).direct(newPos, beam.direction);
                for (Beam newBeam : newBeams) {
                    if (tileBeams.put(newPos, newBeam)) {
                        beams.add(newBeam);
                    }
                }
            }
        }
        return tileBeams.keySet().size();
    }

    private static class Grid {
        private final Tile[][] tiles;
        private final Vector2ic size;

        public Grid(Tile[][] tiles) {
            this.tiles = tiles;
            size = new Vector2i(tiles[0].length, tiles.length);
        }

        boolean isInBounds(Vector2ic pos) {
            return pos.x() >= 0 && pos.y() >= 0 && pos.x() < size.x() && pos.y() < size.y();
        }

        public Tile tileAt(Vector2ic newPos) {
            return tiles[newPos.x()][newPos.y()];
        }
    }

    private record Beam(Vector2ic position, Vector2ic direction) {}

    private enum Tile {
        EMPTY('.') {
            @Override
            public List<Beam> direct(Vector2i newPos, Vector2ic direction) {
                return List.of(new Beam(newPos, direction));
            }
        },
        TOP_LEFT_MIRROR('\\') {
            @Override
            public List<Beam> direct(Vector2i newPos, Vector2ic direction) {
                return List.of(new Beam(newPos, new Vector2i(direction.y(), direction.x())));
            }
        },
        TOP_RIGHT_MIRROR('/') {
            @Override
            public List<Beam> direct(Vector2i newPos, Vector2ic direction) {
                return List.of(new Beam(newPos, new Vector2i(-direction.y(), -direction.x())));
            }
        },
        HORIZONTAL_SPLITTER('-') {
            @Override
            public List<Beam> direct(Vector2i newPos, Vector2ic direction) {
                if (direction.y() == 0) {
                    return List.of(new Beam(newPos, direction));
                } else {
                    return List.of(new Beam(newPos, new Vector2i(-1, 0)), new Beam(newPos, new Vector2i(1, 0)));
                }
            }
        },
        VERTICAL_SPLITTER('|') {
            @Override
            public List<Beam> direct(Vector2i newPos, Vector2ic direction) {
                if (direction.x() == 0) {
                    return List.of(new Beam(newPos, direction));
                } else {
                    return List.of(new Beam(newPos, new Vector2i(0, -1)), new Beam(newPos, new Vector2i(0, 1)));
                }
            }
        };

        private static final Map<Character, Tile> lookup;
        private final char representation;

        static {
            ImmutableMap.Builder<Character, Tile> builder = ImmutableMap.builder();
            for (Tile tile : Tile.values()) {
                builder.put(tile.representation, tile);
            }
            lookup = builder.build();
        }

        Tile(char representation) {
            this.representation = representation;
        }

        public char getRepresentation() {
            return representation;
        }

        public static Tile parse(char c) {
            return lookup.get(c);
        }

        public abstract List<Beam> direct(Vector2i newPos, Vector2ic direction);
    }
}

