package xyz.immortius.advent2016.day8;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Day8 {


    private static final String YEAR = "2016";
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

        List<Consumer<Screen>> commands = parse(lines);
        part1(commands);
        part2(lines);
    }

    private List<Consumer<Screen>> parse(List<String> lines) {
        List<Consumer<Screen>> commands = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith("rect ")) {
                String[] parts = line.substring(5).split("x");
                commands.add((screen) -> {screen.setArea(0, 0, Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), true);});
            } else if (line.startsWith("rotate row y=")) {
                String[] parts = line.substring(13).split(" by ");
                commands.add((screen) -> {screen.rotateRow(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));});
            } else if (line.startsWith("rotate column x=")) {
                String[] parts = line.substring(16).split(" by ");
                commands.add((screen) -> {screen.rotateColumn(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));});
            }
        }
        return commands;
    }

    private void part1(List<Consumer<Screen>> commands) {
        Screen screen = new Screen(50, 6);
        for (Consumer<Screen> command : commands) {
            command.accept(screen);
        }
        int lit = 0;
        for (int y = 0; y < screen.height(); y++) {
            for (int x = 0; x < screen.width(); x++) {
                if (screen.get(x, y)) {
                    lit++;
                }
            }
        }
        System.out.println("Part 1: " + lit);

        screen.print();
    }

    private void part2(List<String> lines) {
        System.out.println("Part 2: ");
    }

    public interface Command {
        void apply(Screen screen);
    }

    private static class Screen {
        private boolean[][] pixels;

        public Screen(int width, int height) {
            this.pixels = new boolean[height][width];
        }

        public int width() {
            return pixels[0].length;
        }

        public int height() {
            return pixels.length;
        }

        public void set(int x, int y, boolean value) {
            pixels[y][x] = value;
        }

        public boolean get(int x, int y) {
            return pixels[y][x];
        }

        public void setArea(int originX, int originY, int width, int height, boolean value) {
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    set(x + originX, y + originY, value);
                }
            }
        }

        public void rotateRow(int row, int shift) {
            boolean[] rowValues = pixels[row];

            shift = ((shift % width()) + width()) % width();
            if (shift != 0) {
                boolean[] newRow = new boolean[width()];
                System.arraycopy(rowValues, 0, newRow, shift, width() - shift);
                System.arraycopy(rowValues, width() - shift, newRow, 0, shift);
                pixels[row] = newRow;
            }
        }

        public void rotateColumn(int column, int shift) {
            boolean[] columnValues = new boolean[height()];
            for (int i = 0; i < height(); i++) {
                columnValues[i] = get(column, i);
            }
            shift = ((shift % height()) + height()) % height();
            for (int i = 0; i < height(); i++) {
                set(column, (i + shift) % height(), columnValues[i]);
            }
        }

        public void print() {
            for (int y = 0; y < height(); y++) {
                for (int x = 0; x < width(); x++) {
                    System.out.print(get(x, y) ? "█" : " ");
                }
                System.out.println();
            }
        }

    }



}

