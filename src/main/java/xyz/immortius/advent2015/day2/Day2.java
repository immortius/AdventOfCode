package xyz.immortius.advent2015.day2;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day2 {
    public static void main(String[] args) throws IOException {
        new Day2().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2015/day2/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        process(lines);
    }

    private void process(List<String> input) {

        List<int[]> dimensions = new ArrayList<>();
        for (String line : input) {
            String[] sides = line.split("x");
            int[] dims = new int[] {Integer.parseInt(sides[0]), Integer.parseInt(sides[1]), Integer.parseInt(sides[2])};
            Arrays.sort(dims);
            dimensions.add(dims);
        }
        int totalPaper = 0;
        for (int[] dims : dimensions) {
            int area = 2 * dims[0] * dims[1] + 2 * dims[1] * dims[2] + 2 * dims[0] * dims[2] + dims[0] * dims[1];
            totalPaper += area;
        }
        int totalRibbon = 0;
        for (int[] dims : dimensions) {
            int ribbon = 2 * dims[0] + 2 * dims[1] + dims[0] * dims[1] * dims[2];
            totalRibbon += ribbon;
        }
        System.out.println("Total Paper: " + totalPaper);
        System.out.println("Total Ribbon: " + totalRibbon);
    }

}

