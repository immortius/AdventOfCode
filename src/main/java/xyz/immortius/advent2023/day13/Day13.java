package xyz.immortius.advent2023.day13;

import com.google.common.io.CharStreams;
import org.joml.Vector2i;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Day13 {

    private static final String YEAR = "2023";
    private static final String DAY = "13";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day13().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        String lines = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            lines = CharStreams.toString(reader);
        }

        List<Grid> blocks = parse(lines);
        part1(blocks);
        part2(blocks);
    }

    private List<Grid> parse(String input) {
        return Arrays.stream(input.split("\r\n\r\n")).map(block -> {
            String[] lines = block.split("\r\n");
            boolean[][] content = new boolean[lines[0].length()][lines.length];
            for (int y = 0; y < lines.length; y++) {
                String line = lines[y];
                for (int x = 0; x < line.length(); x++) {
                    content[x][y] = line.charAt(x) == '#';
                }
            }
            return new Grid(content);
        }).toList();

    }

    private void part1(List<Grid> blocks) {
        int columns = 0;
        int rows = 0;
        for (Grid block : blocks) {
            int column = block.findMirrorColumn();
            if (column == -1) {
                rows += block.rotate().findMirrorColumn();
            } else {
                columns += column;
            }
        }

        System.out.println("Part 1: " + ((100 * rows) + columns));
    }

    private void part2(List<Grid> blocks) {
        int columns = 0;
        int rows = 0;
        for (Grid block : blocks) {
            int column = block.findSmudgeColumn();
            if (column == -1) {
                rows += block.rotate().findSmudgeColumn();
            } else {
                columns += column;
            }
        }

        System.out.println("Part 2: " + ((100 * rows) + columns));
    }

    private record Grid(boolean[][] grid) {

        public Grid rotate() {
            Vector2i size = size();
            boolean[][] result = new boolean[size.y][size.x];
            for (int y = 0; y < size.y; y++) {
                for (int x = 0; x < size.x; x++) {
                    result[y][x] = grid[x][y];
                }
            }
            return new Grid(result);
        }

        Vector2i size() {
            return new Vector2i(grid.length, grid[0].length);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            Vector2i size = size();
            for (int y = 0; y < size.y; y++) {
                for (int x = 0; x < size.x; x++) {
                    builder.append(grid[x][y] ? '#' : '.');
                }
                builder.append('\n');
            }
            return builder.toString();
        }

        public int findMirrorColumn() {
            for (int col = 1; col < size().x; col++) {
                int offset = 0;
                boolean match = true;
                while (col + offset < size().x && col - offset - 1 >= 0) {
                    if (!columnMatch(col - offset - 1, col + offset)) {
                        match = false;
                        break;
                    }
                    offset++;
                }
                if (match) {
                    return col;
                }
            }
            return -1;
        }

        public boolean columnMatch(int colA, int colB) {
            for (int y = 0; y < size().y; y++) {
                if (grid[colA][y] != grid[colB][y]) {
                    return false;
                }
            }
            return true;
        }

        public int findSmudgeColumn() {
            for (int col = 1; col < size().x; col++) {
                int offset = 0;
                int smudges = 0;
                while (col + offset < size().x && col - offset - 1 >= 0) {
                    smudges += columnSmudges(col - offset - 1, col + offset);
                    if (smudges > 1) {
                        break;
                    }
                    offset++;
                }
                if (smudges == 1) {
                    return col;
                }
            }
            return -1;
        }

        public int columnSmudges(int colA, int colB) {
            int result = 0;
            for (int y = 0; y < size().y; y++) {
                if (grid[colA][y] != grid[colB][y]) {
                    result++;
                }
            }
            return result;
        }
    }

}

