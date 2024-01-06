package xyz.immortius.advent2022.day14;

import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Day14 {
    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day14().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day14/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        process(lines);
    }

    private void process(List<String> lines) throws IOException {
        Vector2i min = new Vector2i(500, 0);
        Vector2i max = new Vector2i(500, 0);

        for (String line : lines) {
            String[] coords = line.split(" -> ");
            for (String coord : coords) {
                Vector2i pos = parse(coord);
                max.max(pos);
                min.min(pos);
            }
        }

        min.x -= 150;
        max.x += 150;
        max.y += 2;

        Map map = new Map(min, max);

        for (String line : lines) {
            String[] coords = line.split(" -> ");
            Vector2i start = parse(coords[0]);
            for (int i = 1; i < coords.length; i++) {
                Vector2i next = parse(coords[i]);
                if (next.x == start.x) {
                    for (int y = Math.min(start.y, next.y); y <= Math.max(start.y, next.y); y++) {
                        map.set(start.x, y, Content.Wall);
                    }
                } else {
                    for (int x = Math.min(start.x, next.x); x <= Math.max(start.x, next.x); x++) {
                        map.set(x, start.y, Content.Wall);
                    }
                }
                start = next;
            }
        }

        for (int x = min.x; x <= max.x; x++) {
            map.set(x, max.y, Content.Wall);
        }

        boolean finished = false;
        int steps = 0;
        while (!finished && map.get(500, 0) != Content.Sand) {
            Vector2i sandPos = new Vector2i(500, 0);

            boolean rested = false;
            while (!rested && sandPos.y < max.y + 1) {
                if (Content.Air == map.get(sandPos.x, sandPos.y + 1)) {
                    sandPos.y += 1;
                } else if (Content.Air == map.get(sandPos.x - 1, sandPos.y + 1)) {
                    sandPos.x -= 1;
                    sandPos.y += 1;
                } else if (Content.Air == map.get(sandPos.x + 1, sandPos.y + 1)) {
                    sandPos.x += 1;
                    sandPos.y += 1;
                } else {
                    map.set(sandPos, Content.Sand);
                    rested = true;
                    steps++;
                }
            }

            if (!rested) {
                finished = true;
            }
        }

        System.out.println(map);
        System.out.println(steps);

        BufferedImage image = new BufferedImage(map.map[0].length, map.map.length, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);

        graphics.fillRect(0,0,map.map[0].length, map.map.length);

        int rowIndex = 0;
        for (Content[] row : map.map) {
            for (int i = 0; i < row.length; i++) {
                image.setRGB(i, rowIndex, row[i].color.getRGB());
            }
            rowIndex++;
        }
        ImageIO.write(image, "png", Paths.get("2022day14-2.png").toFile());
    }

    private Vector2i parse(String coord) {
        String[] values = coord.split(",");
        return new Vector2i(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
    }

    enum Content {
        Air('.', Color.WHITE),
        Sand('o', Color.YELLOW),
        Wall('#', Color.BLACK);

        private Color color;
        private char representation;

        private Content(char rep, Color c) {
            this.color = c;
            this.representation = rep;
        }

        public Color getColor() {
            return color;
        }

        public char getRepresentation() {
            return representation;
        }
    }

    private static class Map {
        private final Vector2i offset;
        private final Content[][] map;
        private final Vector2i upperBound;

        public Map(Vector2ic min, Vector2ic max) {
            offset = new Vector2i(min);
            map = new Content[max.y() - min.y() + 1][max.x() - min.x() + 1];
            upperBound = new Vector2i(max);
            for (Content[] contents : map) {
                Arrays.fill(contents, Content.Air);
            }
        }

        public boolean isInbounds(int x, int y) {
            return x - offset.x >= 0 && y - offset.y >= 0 && x <= upperBound.x && y <= upperBound.y;
        }

        public Content get(int x, int y) {
            if (isInbounds(x, y)) {
                return map[y - offset.y][x - offset.x];
            }
            return Content.Air;
        }

        public void set(Vector2ic pos, Content value) {
            set(pos.x(), pos.y(), value);
        }

        public void set(int x, int y, Content value) {
            map[y - offset.y][x - offset.x] = value;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (Content[] row : map) {
                for (Content c : row) {
                    builder.append(c.representation);
                }
                builder.append('\n');
            }
            return builder.toString();
        }
    }




}

