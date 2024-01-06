package xyz.immortius.advent2015.day6;

import com.google.common.io.CharStreams;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day6 {
    Pattern pattern = Pattern.compile("(?<cmd>(turn on)|(turn off)|(toggle)) (?<startX>[0-9]+),(?<startY>[0-9]+) through (?<endX>[0-9]+),(?<endY>[0-9]+)");

    public static void main(String[] args) throws IOException {
        new Day6().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2015/day6/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        Files.createDirectories(Paths.get("out"));

        part1(lines);
        part2(lines);
    }

    private void part2(List<String> lines) throws IOException {
        int[][] state = new int[1000][1000];

        for (String line : lines) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                String command = matcher.group("cmd");
                int startX = Integer.parseInt(matcher.group("startX"));
                int startY = Integer.parseInt(matcher.group("startY"));
                int endX = Integer.parseInt(matcher.group("endX"));
                int endY = Integer.parseInt(matcher.group("endY"));
                for (int x = startX; x <= endX; x++) {
                    for (int y = startY; y <= endY; y++) {
                        switch (command) {
                            case "turn on" -> state[x][y] = state[x][y] + 1;
                            case "turn off" -> state[x][y] = Math.max(0, state[x][y] - 1);
                            case "toggle" -> state[x][y] = state[x][y] + 2;
                        }
                    }
                }
            } else {
                System.out.println("No match: " + line);
            }
        }

        int countLit = 0;
        for (int[] row : state) {
            for (int value : row) {
                countLit += value;
            }
        }
        System.out.println("Part 2: " + countLit);

        BufferedImage map = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = map.createGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0,0,1000, 1000);

        for (int x = 0; x < state.length; x++) {
            for (int y = 0; y < state[x].length; y++) {
                Color c = new Color(state[x][y], state[x][y], state[x][y]);
                map.setRGB(x, y, c.getRGB());
            }
        }

        ImageIO.write(map, "png", Paths.get("out").resolve("2015day6-2.png").toFile());
    }

    private void part1(List<String> input) throws IOException {
        boolean[][] state = new boolean[1000][1000];

        for (String line : input) {
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                String command = matcher.group("cmd");
                int startX = Integer.parseInt(matcher.group("startX"));
                int startY = Integer.parseInt(matcher.group("startY"));
                int endX = Integer.parseInt(matcher.group("endX"));
                int endY = Integer.parseInt(matcher.group("endY"));
                for (int x = startX; x <= endX; x++) {
                    for (int y = startY; y <= endY; y++) {
                        switch (command) {
                            case "turn on" -> state[x][y] = true;
                            case "turn off" -> state[x][y] = false;
                            case "toggle" -> state[x][y] = !state[x][y];
                        }
                    }
                }
            } else {
                System.out.println("No match: " + line);
            }
        }

        int countLit = 0;
        for (boolean[] row : state) {
            for (boolean value : row) {
                if (value) countLit++;
            }
        }
        System.out.println("Part 1: " + countLit);

        BufferedImage map = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = map.createGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0,0,1000, 1000);

        for (int x = 0; x < state.length; x++) {
            for (int y = 0; y < state[x].length; y++) {
                if (state[x][y]) {
                    map.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }

        ImageIO.write(map, "png", Paths.get("out").resolve("2015day6-1.png").toFile());
    }

}

