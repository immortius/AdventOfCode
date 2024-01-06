package xyz.immortius.advent2019.day3;

import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.util.Direction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day3 {

    private static final String YEAR = "2019";
    private static final String DAY = "3";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day3().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<List<Segment>> input = parse(lines);
        part1(input);
        part2(input);
    }

    private List<List<Segment>> parse(List<String> lines) {
        List<List<Segment>> result = new ArrayList<>();
        for (String line : lines) {
            result.add(Arrays.stream(line.split(",")).map(x -> new Segment(Direction.parse(x.charAt(0)), Integer.parseInt(x.substring(1)))).toList());
        }
        return result;
    }

    private void part1(List<List<Segment>> input) {
        List<Set<Vector2ic>> wires = new ArrayList<>();
        for (List<Segment> wire : input) {
            Set<Vector2ic> positions = new HashSet<>();
            Vector2i current = new Vector2i();
            positions.add(new Vector2i(current));
            for (Segment segment : wire) {
                for (int i = 0; i < segment.distance; i++) {
                    current.add(segment.direction.toVector());
                    positions.add(new Vector2i(current));
                }
            }
            wires.add(positions);
        }

        Vector2ic bestIntersect = null;
        long bestDistance = Integer.MAX_VALUE;
        for (Vector2ic pos : Sets.intersection(wires.get(0), wires.get(1))) {
            long dist = pos.gridDistance(0,0);
            if (dist > 0 && dist < bestDistance) {
                bestDistance = dist;
                bestIntersect = pos;
            }
        }

        System.out.println("Part 1: " + bestDistance + " at " + bestIntersect.x() + "," + bestIntersect.y());
    }

    private void part2(List<List<Segment>> input) {
        List<Set<Vector2ic>> wires = new ArrayList<>();
        List<Map<Vector2ic, Integer>> wireDists = new ArrayList<>();
        for (List<Segment> wire : input) {
            Set<Vector2ic> positions = new HashSet<>();
            Map<Vector2ic, Integer> dists = new HashMap<>();
            Vector2i current = new Vector2i();
            int dist = 0;
            positions.add(new Vector2i(current));
            dists.put(new Vector2i(current), 0);
            for (Segment segment : wire) {
                for (int i = 0; i < segment.distance; i++) {
                    Vector2ic pos = current.add(segment.direction.toVector(), new Vector2i());
                    dists.putIfAbsent(pos, ++dist);
                    positions.add(pos);
                    current.set(pos);
                }
            }
            wires.add(positions);
            wireDists.add(dists);
        }

        Vector2ic bestIntersect = null;
        long bestDistance = Integer.MAX_VALUE;
        for (Vector2ic pos : Sets.intersection(wires.get(0), wires.get(1))) {
            long dist = wireDists.get(0).get(pos) + wireDists.get(1).get(pos);
            if (dist > 0 && dist < bestDistance) {
                bestDistance = dist;
                bestIntersect = pos;
            }
        }

        System.out.println("Part 2: " + bestDistance + " at " + bestIntersect.x() + "," + bestIntersect.y());
    }

    record Segment(Direction direction, int distance) {}

}

