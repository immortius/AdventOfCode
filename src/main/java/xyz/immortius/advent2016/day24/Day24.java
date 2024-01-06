package xyz.immortius.advent2016.day24;

import com.google.common.collect.*;
import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.util.Direction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day24 {

    private static final String YEAR = "2016";
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

        Data data = parse(lines);
        part1(data);
        part2(data);
    }

    private Data parse(List<String> lines) {
        boolean[][] maze = new boolean[lines.size()][lines.get(0).length()];
        BiMap<Integer, Vector2ic> positions = HashBiMap.create();

        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                maze[y][x] = line.charAt(x) != '#';
                if (line.charAt(x) >= '0' && line.charAt(x) <= '9') {
                    positions.put(Integer.parseInt(line.substring(x, x + 1)), new Vector2i(x, y));
                }
            }
        }

        return new Data(maze, positions);
    }

    private void part1(Data data) {
        Table<Integer, Integer, Integer> distanceMap = processMaze(data);
        System.out.println(distanceMap);

        ArrayList<Integer> ids = new ArrayList<>(data.positions.keySet());
        ids.remove(Integer.valueOf(0));

        int smallest = Integer.MAX_VALUE;
        List<Integer> perm = null;
        for (List<Integer> permutation : Collections2.permutations(ids)) {
            int distance = distanceMap.get(0, permutation.get(0));
            for (int i = 1; i < permutation.size(); i++) {
                distance += distanceMap.get(permutation.get(i - 1), permutation.get(i));
            }
            if (distance < smallest) {
                smallest = Math.min(smallest, distance);
                perm = permutation;
            }
        }


        System.out.println("Part 1: " + smallest + " " + perm);
    }

    private void completeMap(Data data, Table<Integer, Integer, Integer> distanceMap) {
        for (int id : data.positions.keySet()) {
            Deque<Integer> open = new ArrayDeque<>(distanceMap.row(id).keySet());
            while (!open.isEmpty()) {
                int adj = open.pop();
                int dist = distanceMap.get(id, adj);
                for (Map.Entry<Integer, Integer> entry : distanceMap.row(adj).entrySet()) {
                    int fullDist = dist + entry.getValue();
                    if (!distanceMap.contains(id, entry.getKey()) || distanceMap.get(id, entry.getKey()) > fullDist) {
                        System.out.println("Connecting " + id + ", " + entry.getKey());
                        distanceMap.put(id, entry.getKey(), fullDist);
                        open.add(entry.getKey());
                    }
                }
            }
        }
    }

    @NotNull
    private Table<Integer, Integer, Integer> processMaze(Data data) {
        List<Vector2ic> open = new ArrayList<>();
        Set<Vector2ic> reached = Sets.newLinkedHashSet();

        Table<Integer, Integer, Integer> distanceMap = HashBasedTable.create();
        for (int id : data.positions.keySet()) {
            distanceMap.put(id, id, 0);
        }

        for (int id : data.positions.keySet()) {
            int steps = 0;
            reached.clear();
            reached.add(data.positions.get(id));
            open.add(data.positions.get(id));
            while (!open.isEmpty()) {
                steps++;
                List<Vector2ic> newOpen = new ArrayList<>();
                for (Vector2ic pos : open) {
                    for (Direction dir : Direction.values()) {
                        Vector2i adj = pos.add(dir.toVector(), new Vector2i());
                        if (!data.isOpen(adj) || !reached.add(adj)) {
                            continue;
                        }
                        newOpen.add(adj);
                        if (data.positions().inverse().containsKey(adj)) {
                            int loc = data.positions().inverse().get(adj);
                            if (!distanceMap.contains(id, loc)) {
                                distanceMap.put(id, loc, steps);
                            }
                        }
                    }
                }
                open = newOpen;
            }
        }
        return distanceMap;
    }

    private void part2(Data data) {
        Table<Integer, Integer, Integer> distanceMap = processMaze(data);
        System.out.println(distanceMap);

        ArrayList<Integer> ids = new ArrayList<>(data.positions.keySet());
        ids.remove(Integer.valueOf(0));

        int smallest = Integer.MAX_VALUE;
        List<Integer> perm = null;
        for (List<Integer> permutation : Collections2.permutations(ids)) {
            int distance = distanceMap.get(0, permutation.get(0));
            for (int i = 1; i < permutation.size(); i++) {
                distance += distanceMap.get(permutation.get(i - 1), permutation.get(i));
            }
            distance += distanceMap.get(permutation.get(permutation.size() - 1), 0);
            if (distance < smallest) {
                smallest = Math.min(smallest, distance);
                perm = permutation;
            }
        }


        System.out.println("Part 1: " + smallest + " " + perm);
    }

    private record Data(boolean[][] maze, BiMap<Integer, Vector2ic> positions) {
        public boolean isOpen(Vector2ic adj) {
            if (adj.x() < 0 || adj.x() >= maze[0].length || adj.y() < 0 || adj.y() >= maze.length) {
                return false;
            }
            return maze[adj.y()][adj.x()];
        }
    }


}

