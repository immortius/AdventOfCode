package xyz.immortius.advent2018.day6;

import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Day6 {

    private static final String YEAR = "2018";
    private static final String DAY = "6";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day6().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Vector2ic> points = parse(lines);
        part1(points);
        part2(points);
    }

    private List<Vector2ic> parse(List<String> lines) {
        List<Vector2ic> points = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(", ");
            points.add(new Vector2i(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
        }
        return points;
    }

    private void part1(List<Vector2ic> points) {
        Vector2i min = new Vector2i(points.get(0));
        Vector2i max = new Vector2i(points.get(0));
        for (Vector2ic point : points) {
            min.min(point);
            max.max(point);
        }
        int[] area = new int[points.size()];
        Set<Integer> excludePoints = new LinkedHashSet<>();
        for (int y = min.y; y <= max.y; y++) {
            for (int x = min.x; x <= max.x; x++) {
                int closestPoint = -1;
                long closestDist = Integer.MAX_VALUE;
                for (int pointIndex = 0; pointIndex < points.size(); pointIndex++) {
                    long dist = points.get(pointIndex).gridDistance(x, y);
                    if (dist < closestDist) {
                        closestDist = dist;
                        closestPoint = pointIndex;
                    } else if (dist == closestDist) {
                        closestPoint = -1;
                    }
                }
                if (closestPoint != -1) {
                    area[closestPoint]++;
                    if (y == min.y || y == max.y || x == min.x || x == max.x) {
                        excludePoints.add(closestPoint);
                    }
                }
            }
        }

        int largestArea = 0;
        for (int i = 0; i < points.size(); i++) {
            if (!excludePoints.contains(i) && area[i] > largestArea) {
                largestArea = area[i];
            }
        }

        System.out.println("Part 1: " + largestArea);
    }

    private void part2(List<Vector2ic> points) {
        int region = 10000;
        Vector2i min = new Vector2i(points.get(0)).sub(region, region);
        Vector2i max = new Vector2i(points.get(0)).add(region, region);
        long count = 0;
        for (int y = min.y; y <= max.y; y++) {
            for (int x = min.x; x <= max.x; x++) {
                int dist = 0;
                for (int i = 0; i < points.size() && dist < region; i++) {
                    dist += points.get(i).gridDistance(x, y);
                }
                if (dist < region) {
                    count++;
                }
            }
        }


        System.out.println("Part 2: " + count);
    }



}

