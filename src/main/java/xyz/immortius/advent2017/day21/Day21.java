package xyz.immortius.advent2017.day21;

import com.google.common.base.Objects;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day21 {

    private static final String YEAR = "2017";
    private static final String DAY = "21";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day21().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        Map<Square, Square> rules = parse(lines);
        part1(rules);
        part2(lines);
    }

    private Map<Square, Square> parse(List<String> lines) {
        Map<Square, Square> rules = new HashMap<>();
        for (String line : lines) {
            String[] inOut = line.split(" => ");
            Square in = parseSquareData(inOut[0]);
            Square out = parseSquareData(inOut[1]);
            rules.put(in, out);
            for (int i = 0; i < 3; i++) {
                in  = in.rotate();
                rules.put(in, out);
            }
            in = in.hFlip();
            rules.put(in, out);
            for (int i = 0; i < 3; i++) {
                in  = in.rotate();
                rules.put(in, out);
            }
        }
        return rules;
    }

    private Square parseSquareData(String string) {
        String[] lines = string.split("/");
        boolean[] data = new boolean[lines.length * lines.length];
        int index = 0;
        for (String line : lines) {
            for (char c : line.toCharArray()) {
                data[index++] = c == '#';
            }
        }
        return new Square(lines.length, data);
    }

    private void part1(Map<Square, Square> rules) {
        Square square = new Square(3, new boolean[] {false, true, false, false, false, true, true, true, true});

        System.out.println(square);
        for (int i = 0; i < 18; i++) {
            square = square.iterate(rules);
            System.out.println(square);
        }

        System.out.println("Part 1: " + square.onCount());
    }

    private void part2(List<String> lines) {
        System.out.println("Part 2: ");
    }

    private static class Square {
        private final int size;
        private final boolean[] content;

        public Square(int size) {
            this.size = size;
            this.content = new boolean[size * size];
        }

        public Square(int size, boolean[] data) {
            this.size = size;
            this.content = new boolean[size * size];
            System.arraycopy(data, 0, content, 0, size * size);
        }

        public Square(Square other) {
            this.size = other.size;
            this.content = new boolean[size * size];
            System.arraycopy(other.content, 0, content, 0, size * size);
        }

        public Square(int size, List<Square> squares) {
            this.size = size;
            this.content = new boolean[size * size];
            int x = 0;
            int y = 0;
            for (Square square : squares) {
                for (int subY = 0; subY < square.size; subY++) {
                    for (int subX = 0; subX < square.size; subX++) {
                        set(x + subX, y + subY, square.get(subX, subY));
                    }
                }
                x += square.size;
                if (x >= size) {
                    x = 0;
                    y += square.size;
                }
            }
        }

        public boolean get(int x, int y) {
            return content[x + y * size];
        }

        public void set(int x, int y, boolean value) {
            content[x + y * size] = value;
        }

        public Square rotate() {
            Square rotated = new Square(size);
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    rotated.set(x, y, get(y, size - x - 1));
                }
            }
            return rotated;
        }

        public Square hFlip() {
            Square flipped = new Square(size);
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    flipped.set(x, y, get(size - x - 1, y));
                }
            }
            return flipped;
        }

        public Square vFlip() {
            Square flipped = new Square(size);
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    flipped.set(x, y, get(x, size - y - 1));
                }
            }
            return flipped;
        }

        public int onCount() {
            int count = 0;
            for (boolean b : content) {
                if (b) {
                    count++;
                }
            }
            return count;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Square square = (Square) o;
            return size == square.size && Arrays.equals(content, square.content);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(size, Arrays.hashCode(content));
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    builder.append(get(x, y) ? '#' : '.');
                }
                builder.append('\n');
            }
            return builder.toString();
        }

        public Square iterate(Map<Square, Square> rules) {
            List<Square> squares = new ArrayList<>();
            int newSize;
            if (size % 2 == 0) {
                newSize = 3 * (size / 2);
                for (int y = 0; y < size; y += 2) {
                    for (int x = 0; x < size; x += 2) {
                        Square subSquare = getSubsquare(x, y, 2);
                        squares.add(rules.get(subSquare));
                    }
                }
            } else {
                newSize = 4 * (size / 3);
                for (int y = 0; y < size; y += 3) {
                    for (int x = 0; x < size; x += 3) {
                        Square subSquare = getSubsquare(x, y, 3);
                        squares.add(rules.get(subSquare));
                    }
                }
            }
            return new Square(newSize, squares);

        }

        private Square getSubsquare(int x, int y, int size) {
            Square square = new Square(size); {
                for (int suby = 0; suby < size; suby++) {
                    for (int subx = 0; subx < size; subx++) {
                        square.set(subx, suby, get(x + subx, y + suby));
                    }
                }
            }
            return square;
        }
    }

}

