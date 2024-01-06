package xyz.immortius.advent2016.day15;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day15 {

    private final Pattern linePattern = Pattern.compile("Disc #(?<id>[0-9]+) has (?<positions>[0-9]+) positions; at time=0, it is at position (?<initialPosition>[0-9]+)\\.");

    private static final String YEAR = "2016";
    private static final String DAY = "15";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day15().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Disc> discs = parse(lines);
        part1(discs);
        part2(discs);
    }

    private List<Disc> parse(List<String> lines) {
        List<Disc> discs = new ArrayList<>();
        for (String line : lines) {
            Matcher matcher = linePattern.matcher(line);
            if (matcher.matches()) {
                int disc = Integer.parseInt(matcher.group("id"));
                int positions = Integer.parseInt(matcher.group("positions"));
                int initialPosition = Integer.parseInt(matcher.group("initialPosition"));
                discs.add(new Disc(disc, positions, initialPosition));
            } else {
                System.out.println("Failed to match: " + line);
            }
        }
        return discs;
    }

    private void part1(List<Disc> discs) {
        int time = findAlignedTime(discs);

        System.out.println("Part 1: " + time);
    }

    private void part2(List<Disc> discs) {
        ArrayList<Disc> enhancedDiscs = new ArrayList<>(discs);
        enhancedDiscs.add(new Disc(discs.size() + 1, 11, 0));
        int time = findAlignedTime(enhancedDiscs);

        System.out.println("Part 2: " + time);
    }

    private int findAlignedTime(List<Disc> discs) {
        int[] positions = new int[discs.size()];
        int[] offsets = new int[discs.size()];
        for (Disc disc : discs) {
            positions[disc.id - 1] = disc.positions;
            offsets[disc.id - 1] = (disc.initialPositions + disc.id) % disc.positions;
        }

        int largest = 0;
        int value = positions[0];
        for (int i = 1; i < positions.length; i++) {
            if (positions[i] > value) {
                largest = i;
                value = positions[i];
            }
        }

        int time = (positions[largest] - offsets[largest]) % positions[largest];
        while (!aligned(positions, offsets, time)) {
            time += positions[largest];
        }
        return time;
    }

    private boolean aligned(int[] positions, int[] offsets, int time) {
        for (int i = 0; i < positions.length; i++) {
            if (((offsets[i] + time) % positions[i] != 0)) {
                return false;
            }
        }
        return true;
    }



    public record Disc(int id, int positions, int initialPositions) {

    }

}

