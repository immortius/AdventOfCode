package xyz.immortius.advent2023.day17;

import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.util.AStar;
import xyz.immortius.util.Direction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day17 {

    //private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    private static final String YEAR = "2023";
    private static final String DAY = "17";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day17().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            lines = CharStreams.readLines(reader);
        }

        int[][] input = parse(lines);
        part1(input);
        part2(input);
    }

    private int[][] parse(List<String> lines) {
        int[][] grid = new int[lines.get(0).length()][lines.size()];
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                grid[x][y] = line.charAt(x) - '0';
            }
        }
        return grid;
    }

    private void part1(int[][] input) {

        State initial = new State(new Vector2i(0,0), null);
        Vector2i size = new Vector2i(input.length, input[0].length);
        Vector2i target = size.sub(1, 1, new Vector2i());


        AStar<State> aStar = new AStar<State>(initial, (x) -> target.equals(x.pos), (x) -> target.gridDistance(x.pos), x -> {
            Map<State, Long> moves = new LinkedHashMap<>();
            List<Direction> directions;
            if (x.lastDir == null) {
                directions = Arrays.asList(Direction.values());
            } else {
                directions = List.of(x.lastDir.clockwise(), x.lastDir.anticlockwise());
            }
            for (Direction dir : directions) {
                Vector2i newPos = new Vector2i(x.pos).add(dir.toVector());
                long cost = 0;
                for (int i = 1; i <= 3 && isInBounds(newPos, size); i++) {
                    cost += input[newPos.x][newPos.y];
                    moves.put(new State(new Vector2i(newPos), dir), cost);
                    newPos.add(dir.toVector());
                }
            }

            return moves;
        });

        System.out.println("Part 1: " + aStar.run());
    }

    private void part2(int[][] input) {

        State initial = new State(new Vector2i(0,0), null);
        Vector2i size = new Vector2i(input.length, input[0].length);
        Vector2i target = size.sub(1, 1, new Vector2i());


        AStar<State> aStar = new AStar<State>(initial, (x) -> target.equals(x.pos), (x) -> 0L, x -> {
            Map<State, Long> moves = new LinkedHashMap<>();
            List<Direction> directions;
            if (x.lastDir == null) {
                directions = Arrays.asList(Direction.values());
            } else {
                directions = List.of(x.lastDir.clockwise(), x.lastDir.anticlockwise());
            }
            for (Direction dir : directions) {
                Vector2i newPos = new Vector2i(x.pos).add(dir.toVector());
                long cost = 0;
                for (int i = 1; i <= 10 && isInBounds(newPos, size); i++) {
                    cost += input[newPos.x][newPos.y];
                    if (i >= 4) {
                        moves.put(new State(new Vector2i(newPos), dir), cost);
                    }
                    newPos.add(dir.toVector());
                }
            }

            return moves;
        });

        long cost = aStar.run();
        System.out.println("Part 2: " + cost);
    }

    private boolean isInBounds(Vector2ic pos, Vector2ic size) {
        return pos.x() >= 0 && pos.y() >= 0 && pos.x() < size.x() && pos.y() < size.y();
    }

    private record State(Vector2ic pos, Direction lastDir) {}


}

