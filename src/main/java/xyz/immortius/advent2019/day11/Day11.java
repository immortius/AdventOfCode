package xyz.immortius.advent2019.day11;

import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.advent2019.day2.IntCodeComputer;
import xyz.immortius.advent2019.day2.IntCodeHelper;
import xyz.immortius.advent2019.day2.IntCodeInputStream;
import xyz.immortius.advent2019.day2.IntCodeOutputStream;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Day11 {

    private static final String YEAR = "2019";
    private static final String DAY = "11";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day11().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        long[] program = IntCodeHelper.parse(lines);
        part1(program);
        part2(program);
    }

    private void part1(long[] program) throws IOException {
        SpaceshipSurface surface = new SpaceshipSurface();
        IntCodeComputer computer = new IntCodeComputer(program, surface, surface);
        computer.run();

        render(surface, "2019day11-1.png");

        System.out.println("Part 1: " + surface.paintedPanels.size());
    }

    public class SpaceshipSurface implements IntCodeInputStream, IntCodeOutputStream {
        private Vector2i currentPosition = new Vector2i(0,0);
        private Set<Vector2ic> whitePanels = new LinkedHashSet<>();
        private Direction currentDir = Direction.Up;

        private List<Long> message = new ArrayList<>();

        private Set<Vector2ic> paintedPanels = new LinkedHashSet<>();

        @Override
        public long send() {
            return whitePanels.contains(currentPosition) ? 1 : 0;
        }

        @Override
        public void receive(long value) {
            message.add(value);
            if (message.size() == 2) {
                switch (message.get(0).intValue()) {
                    case 0 -> whitePanels.remove(currentPosition);
                    case 1 -> {
                        paintedPanels.add(new Vector2i(currentPosition));
                        whitePanels.add(new Vector2i(currentPosition));
                    }
                }
                currentDir = currentDir.turn(message.get(1).intValue());
                currentPosition.add(currentDir.vector);
                message.clear();
            }
        }
    }

    private void part2(long[] program) throws IOException {
        SpaceshipSurface surface = new SpaceshipSurface();
        surface.whitePanels.add(new Vector2i(0,0));
        IntCodeComputer computer = new IntCodeComputer(program, surface, surface);
        computer.run();

        render(surface, "2019day11-2.png");

    }

    private void render(SpaceshipSurface surface, String filename) throws IOException {
        Vector2i min = new Vector2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Vector2i max = new Vector2i(Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (Vector2ic panel : surface.whitePanels) {
            min.min(panel);
            max.max(panel);
        }

        BufferedImage map = new BufferedImage(max.x - min.x + 1, max.y - min.y + 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = map.createGraphics();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0,0,max.x - min.x + 1, max.y - min.y + 1);

        for (Vector2ic panel : surface.whitePanels) {
            Vector2i relPos = panel.sub(min, new Vector2i());
            map.setRGB(relPos.x(), relPos.y(), Color.WHITE.getRGB());
        }

        ImageIO.write(map, "png", Paths.get(filename).toFile());
    }

    public enum Direction {
        Up('U', new Vector2i(0, -1)),
        Right('R', new Vector2i(1, 0)),
        Down('D', new Vector2i(0, 1)),
        Left('L', new Vector2i(-1, 0));

        private char id;
        private Vector2ic vector;

        Direction(char c, Vector2ic vector) {
            this.id = c;
            this.vector = vector;
        }

        public Vector2ic toVector() {
            return vector;
        }

        public char getId() {
            return id;
        }

        private Direction turn(int dir) {
            if (dir == 0) {
                return Direction.values()[(ordinal() + Direction.values().length - 1) % Direction.values().length];
            }
            return Direction.values()[(ordinal() + 1) % Direction.values().length];
        }
    }

}

