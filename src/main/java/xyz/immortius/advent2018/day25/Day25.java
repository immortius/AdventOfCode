package xyz.immortius.advent2018.day25;

import com.google.common.io.CharStreams;
import org.joml.Vector4i;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day25 {

    private static final String YEAR = "2018";
    private static final String DAY = "25";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day25().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Vector4i> input = parse(lines);
        part1(input);
    }

    private List<Vector4i> parse(List<String> lines) {
        return lines.stream().map(x -> Arrays.stream(x.split(",")).map(Integer::parseInt).mapToInt(y -> y).toArray()).map(Vector4i::new).toList();
    }

    private void part1(List<Vector4i> input) {
        List<List<Vector4i>> sets = new ArrayList<>();
        for (Vector4i v : input) {
            List<List<Vector4i>> newSets = new ArrayList<>();
            List<Vector4i> newSet = new ArrayList<>();
            newSet.add(v);
            newSets.add(newSet);
            for (List<Vector4i> set : sets) {
                if (inRange(set, v)) {
                    newSet.addAll(set);
                } else {
                    newSets.add(set);
                }
            }
            sets = newSets;
        }
        System.out.println("Part 1: " + sets.size());
    }

    private boolean inRange(List<Vector4i> set, Vector4i v) {
        for (Vector4i existing : set) {
            if (existing.gridDistance(v) <= 3) {
                return true;
            }
        }
        return false;
    }


}

