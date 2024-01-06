package xyz.immortius.advent2022.day12;

import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.util.Direction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day12 {
    public static void main(String[] args) throws IOException {
        new Day12().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day12/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        process(lines);
    }

    private void process(List<String> lines) {
        HeightMap map = parseMap(lines);

        List<Vector2ic> path = path(map.raw, map.end, 0);
        System.out.println(path.size());
    }

    Map<Vector2ic, Long> fScore = new HashMap<>();
    Map<Vector2ic, Long> gScore = new HashMap<>();
    Map<Vector2ic, Vector2ic> cameFrom = new HashMap<>();
    PriorityQueue<Vector2ic> open = new PriorityQueue<>(Comparator.comparingLong((Vector2ic a) -> fScore.get(a)));

    private List<Vector2ic> path(int[][] heightmap, Vector2ic start, Vector2ic end) {
        gScore.put(start, 0L);
        fScore.put(start, gScore.get(start) + end.gridDistance(start));
        open.add(start);

        while (!open.isEmpty()) {
            Vector2ic current = open.remove();
            if (current.equals(end)) {
                List<Vector2ic> path = new ArrayList<>();
                while (!current.equals(start)) {
                    path.add(current);
                    current = cameFrom.get(current);
                }
                return path;
            }

            for (Direction d : Direction.values()) {
                Vector2ic neighbour = current.add(d.toVector(), new Vector2i());
                if (neighbour.x() < 0 || neighbour.y() < 0 || neighbour.x() >= heightmap.length || neighbour.y() >= heightmap[0].length ||  heightmap[neighbour.x()][neighbour.y()] - heightmap[current.x()][current.y()] > 1) {
                    continue;
                }
                long neighbourGScore = gScore.get(current) + 1;
                if (neighbourGScore < gScore.getOrDefault(neighbour, Long.MAX_VALUE)) {
                    cameFrom.put(neighbour, current);
                    gScore.put(neighbour, neighbourGScore);
                    fScore.put(neighbour, neighbourGScore + end.gridDistance(neighbour));
                    if (!open.contains(neighbour)) {
                        open.add(neighbour);
                    }
                }
            }


        }
        return null;
    }

    private List<Vector2ic> path(int[][] heightmap, Vector2ic start, int targetHeight) {
        gScore.put(start, 0L);
        fScore.put(start, gScore.get(start) + heightmap[start.x()][start.y()]);
        open.add(start);

        while (!open.isEmpty()) {
            Vector2ic current = open.remove();
            if (heightmap[current.x()][current.y()] == targetHeight) {
                List<Vector2ic> path = new ArrayList<>();
                while (!current.equals(start)) {
                    path.add(current);
                    current = cameFrom.get(current);
                }
                return path;
            }

            for (Direction d : Direction.values()) {
                Vector2ic neighbour = current.add(d.toVector(), new Vector2i());
                if (neighbour.x() < 0 || neighbour.y() < 0 || neighbour.x() >= heightmap.length || neighbour.y() >= heightmap[0].length ||  heightmap[neighbour.x()][neighbour.y()] - heightmap[current.x()][current.y()] < -1) {
                    continue;
                }
                long neighbourGScore = gScore.get(current) + 1;
                if (neighbourGScore < gScore.getOrDefault(neighbour, Long.MAX_VALUE)) {
                    cameFrom.put(neighbour, current);
                    gScore.put(neighbour, neighbourGScore);
                    fScore.put(neighbour, neighbourGScore + heightmap[neighbour.x()][neighbour.y()]);
                    if (!open.contains(neighbour)) {
                        open.add(neighbour);
                    }
                }
            }
        }
        return null;
    }

    private HeightMap parseMap(List<String> lines) {
        int[][] map = new int[lines.get(0).length()][lines.size()];
        Vector2i start = new Vector2i();
        Vector2i end = new Vector2i();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c == 'S') {
                    map[x][y] = 0;
                    start.set(x, y);
                } else if (c == 'E') {
                    map[x][y] = 25;
                    end.set(x, y);
                } else {
                    map[x][y] = c - 'a';
                }
            }
        }
        return new HeightMap(map, start, end);
    }

    private record HeightMap(int[][] raw, Vector2ic start, Vector2ic end) {}

}

