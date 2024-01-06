package xyz.immortius.advent2019.day8;

import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day8 {

    private static final String YEAR = "2019";
    private static final String DAY = "8";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day8().run();
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
        int width = 25;
        int height = 6;

        int[][][] layers = assembleLayers(data, width, height);

        int bestLayer = 0;
        int bestZeros = Integer.MAX_VALUE;
        for (int l = 0; l < layers.length; l++) {
            int zeroes = count(layers[l], 0);
            if (zeroes < bestZeros) {
                bestLayer = l;
                bestZeros = zeroes;
            }
        }

        int result = count(layers[bestLayer], 1) * count(layers[bestLayer], 2);
        System.out.println("Part 1: " + result);
    }

    private void part2(String data) throws IOException {
        int width = 25;
        int height = 6;

        int[][][] layers = assembleLayers(data, width, height);

        int[][] image = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int layer = 0;
                while (layers[layer][y][x] == 2) {
                    layer++;
                }
                image[y][x] = layers[layer][y][x];
            }
        }

        BufferedImage map = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = map.createGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0,0,width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = image[y][x] == 1 ? Color.WHITE : Color.BLACK;
                map.setRGB(x, y, c.getRGB());
            }
        }

        ImageIO.write(map, "png", Paths.get("2019day8-2.png").toFile());
    }

    @NotNull
    private int[][][] assembleLayers(String data, int width, int height) {
        int[][][] layers = new int[data.length() / width / height][height][width];
        for (int l = 0; l < layers.length; l++) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int offset = l * width * height + y * width + x;
                    layers[l][y][x] = (data.charAt(offset) - '0');
                }
            }
        }
        return layers;
    }

    private int count(int[][] layer, int value) {
        int count = 0;
        for (int[] row : layer) {
            for (int pixel : row) {
                if (pixel == value) {
                    count++;
                }
            }
        }
        return count;
    }

}

