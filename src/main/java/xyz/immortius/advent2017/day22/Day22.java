
package xyz.immortius.advent2017.day22;

import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.advent2022.day24.Day24;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day22 {

    private static final String YEAR = "2017";
    private static final String DAY = "22";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day22().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        Set<Vector2ic> grid = parse(lines);
        part1(new HashSet<>(grid));
        part2(new HashSet<>(grid));
    }

    private Set<Vector2ic> parse(List<String> lines) {
        Vector2i offset = new Vector2i(-lines.get(0).length() / 2, -lines.size() / 2);
        Set<Vector2ic> grid = new HashSet<>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == '#') {
                    grid.add(new Vector2i(x, y).add(offset));
                }
            }
        }
        return grid;
    }

    private void part1(Set<Vector2ic> grid) {
        Vector2i position = new Vector2i();
        Day24.Direction dir = Day24.Direction.Up;

        int infections = 0;
        for (int i = 0; i < 10000; i++) {
            boolean currentInfection = grid.contains(position);
            if (currentInfection) {
                dir = dir.right();
                grid.remove(position);
                position.add(dir.toVector());
            } else {
                dir = dir.left();
                grid.add(new Vector2i(position));
                infections++;
                position.add(dir.toVector());
            }
        }
        System.out.println("Part 1: " + infections);
    }

    private void part2(Set<Vector2ic> infected) {
        Vector2i position = new Vector2i();
        Day24.Direction dir = Day24.Direction.Up;
        Set<Vector2ic> weakened = new HashSet<>();
        Set<Vector2ic> flagged = new HashSet<>();

        int infections = 0;
        for (int i = 0; i < 10000000; i++) {
            if (infected.contains(position)) {
                dir = dir.right();
                infected.remove(position);
                flagged.add(new Vector2i(position));
            } else if (weakened.contains(position)) {
                weakened.remove(position);
                infected.add(new Vector2i(position));
                infections++;
            } else if (flagged.contains(position)) {
                dir = dir.right().right();
                flagged.remove(position);
            } else {
                dir = dir.left();
                weakened.add(new Vector2i(position));
            }
            position.add(dir.toVector());
//            draw(weakened, flagged, infected);
//            System.out.println();
        }
        System.out.println("Part 2: " + infections);
    }

    private void draw(Set<Vector2ic> weakened, Set<Vector2ic> flagged, Set<Vector2ic> infections) {
        Vector2i min = new Vector2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Vector2i max = new Vector2i(Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (Vector2ic v : weakened) {
            min.min(v);
            max.max(v);
        }
        for (Vector2ic v : flagged) {
            min.min(v);
            max.max(v);
        }
        for (Vector2ic v : infections) {
            min.min(v);
            max.max(v);
        }

        Vector2i cursor = new Vector2i();
        for (cursor.y = min.y; cursor.y <= max.y; cursor.y++) {
            for (cursor.x = min.x; cursor.x <= max.x; cursor.x++) {
                if (infections.contains(cursor)) {
                    System.out.print('#');
                } else if (flagged.contains(cursor)) {
                    System.out.print('F');
                } else if (weakened.contains(cursor)) {
                    System.out.print('W');
                } else {
                    System.out.print('.');
                }
            }
            System.out.print('\n');
        }
    }


}

