package xyz.immortius.advent2018.day10;

import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.util.NewtonEstimation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Day10 {

    private static final String YEAR = "2018";
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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Star> stars = parse(lines);
        part1(stars);
    }

    private List<Star> parse(List<String> lines) {
        List<Star> result = new ArrayList<>();
        for (String line : lines) {
            String rawPos = line.substring(line.indexOf('<') + 1, line.indexOf('>'));
            String rawVel = line.substring(line.lastIndexOf('<') + 1, line.lastIndexOf('>'));
            String[] posNums = rawPos.trim().split(",\\s+");
            String[] velNums = rawVel.trim().split(",\\s+");
            Vector2i pos = new Vector2i(Integer.parseInt(posNums[0]), Integer.parseInt(posNums[1]));
            Vector2i vel = new Vector2i(Integer.parseInt(velNums[0]), Integer.parseInt(velNums[1]));
            result.add(new Star(pos, vel));
        }
        return result;
    }

    private void part1(List<Star> stars) {
        NewtonEstimation newtonEstimation = new NewtonEstimation((time) -> score(stars, time));
        long time = newtonEstimation.minimise(-1000000, 100000);

        System.out.println("Part 1:");
        printStars(stars, time);

        System.out.println("Part 2: " + time);
    }

    private void printStars(List<Star> stars, long time) {
        Set<Vector2i> positions = new LinkedHashSet<>();
        for (Star s : stars) {
            positions.add(s.atTime(time));
        }

        Vector2i min = new Vector2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Vector2i max = new Vector2i(Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (Vector2i pos : positions) {
            min.min(pos);
            max.max(pos);
        }

        Vector2i current = new Vector2i();
        for (current.y = min.y; current.y <= max.y; current.y++) {
            for (current.x = min.x; current.x <= max.x; current.x++) {
                if (positions.contains(current)) {
                    System.out.print("█");
                } else {
                    System.out.print('.');
                }
            }
            System.out.println();
        }
    }

    private long score(List<Star> stars, long time) {
        Vector2i min = new Vector2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Vector2i max = new Vector2i(Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (Star star : stars) {
            min.min(star.atTime(time));
            max.max(star.atTime(time));
        }
        return (long) Math.abs(max.y - min.y);
    }

    private record Star(Vector2ic position, Vector2ic velocity) {
        public Vector2i atTime(long time) {
            return velocity.mul((int)time, new Vector2i()).add(position);
        }
    }



}

