package xyz.immortius.advent2023.day2;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Day2 {

    private static final String YEAR = "2023";
    private static final String DAY = "2";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day2().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Game> games = parse(lines);
        part1(games);
        part2(games);
    }

    private List<Game> parse(List<String> lines) {
        List<Game> games = new ArrayList<>();
        for (String line : lines) {
            String setPart = line.split(": ")[1];
            String[] sets = setPart.split("; ");
            games.add(new Game(Arrays.stream(sets).map(set -> {
                int r = 0;
                int g = 0;
                int b = 0;
                String[] colorCounts = set.split(", ");
                for (String colorCount : colorCounts) {
                    String[] parts = colorCount.split(" ");
                    int amount = Integer.parseInt(parts[0]);
                    switch (parts[1]) {
                        case "blue" -> b = amount;
                        case "red" -> r = amount;
                        case "green" -> g = amount;
                        default -> System.out.println("Error, unknown color " + parts[1]);
                    }
                }
                return new Content(r, g, b);
            }).toList()));
        }
        return games;
    }

    private void part2(List<Game> games) {
        int total = 0;
        for (Game game : games) {
            total += game.power();
        }
        System.out.println("Part 2: " + total);
    }

    private void part1(List<Game> games) {
        int total = 0;
        Content content = new Content(12, 13, 14);
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);
            if (game.isValidFor(content)) {
                total += i + 1;
            }
        }
        System.out.println("Part 1: " + total);
    }

    public record Content(int red, int green, int blue) {

        boolean isSubset(Content other) {
            return other.red <= red && other.green <= green && other.blue <= blue;
        }

        @Override
        public String toString() {
            return "{" +
                    "r=" + red +
                    ", g=" + green +
                    ", b=" + blue +
                    '}';
        }
    }

    public record Game(List<Content> sets) {

        boolean isValidFor(Content content) {
            for (Content set : sets) {
                if (!content.isSubset(set)) {

                    return false;
                }
            }
            return true;
        }

        int power() {
            int minR = 0;
            int minG = 0;
            int minB = 0;
            for (Content set : sets) {
                minR = Math.max(minR, set.red);
                minG = Math.max(minG, set.green);
                minB = Math.max(minB, set.blue);
            }
            return minR * minG * minB;
        }

        @Override
        public String toString() {
            return "Game{" +
                    "sets=" + sets +
                    '}';
        }
    }

}


