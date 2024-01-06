package xyz.immortius.advent2016.day9;

import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Day9 {

    private static final String YEAR = "2016";
    private static final String DAY = "9";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day9().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        part1(lines.get(0));
        part2(lines.get(0));
    }

    private void part1(String data) {
        String exploded = decompress(data);
        System.out.println("Part 1: " + exploded.length() + " - " + exploded);
    }

    private void part2(String data) {
        System.out.println("Part 2: " + calculateDecompressedSize(data));
    }

    private long calculateDecompressedSize(String data) {
        long length = 0;
        int index = 0;
        while (index < data.length()) {
            int markerStart = data.indexOf('(', index);
            if (markerStart == -1) {
                length += data.length() - index;
                index = data.length();
            } else {
                length += markerStart - index;

                int markerEnd = data.indexOf(')', markerStart);
                String[] markerParts = data.substring(markerStart + 1, markerEnd).split("x");
                int repeatLength = Integer.parseInt(markerParts[0]);
                int times = Integer.parseInt(markerParts[1]);
                long decompressedSize = calculateDecompressedSize(data.substring(markerEnd + 1, markerEnd + 1 + repeatLength));
                length += decompressedSize * times;
                index = markerEnd + repeatLength + 1;
            }
        }
        return length;
    }

    @NotNull
    private String decompress(String data) {
        StringBuilder exploded = new StringBuilder();
        int index = 0;
        while (index < data.length()) {
            int markerStart = data.indexOf('(', index);
            if (markerStart == -1) {
                exploded.append(data.substring(index));
                index = data.length();
            } else {
                exploded.append(data, index, markerStart);
                int markerEnd = data.indexOf(')', markerStart);
                String[] markerParts = data.substring(markerStart + 1, markerEnd).split("x");
                int repeatLength = Integer.parseInt(markerParts[0]);
                int times = Integer.parseInt(markerParts[1]);
                for (int i = 0; i < times; i++) {
                    exploded.append(data, markerEnd + 1, markerEnd + 1 + repeatLength);
                }
                index = markerEnd + repeatLength + 1;
            }
        }
        return exploded.toString();
    }


}

