package xyz.immortius.advent2018.day3;

import com.google.common.base.Objects;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Day3 {

    private static final String YEAR = "2018";
    private static final String DAY = "3";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day3().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Rectangle> rectangles = parse(lines);
        part1(rectangles);
        part2(rectangles);
    }

    private List<Rectangle> parse(List<String> lines) {
        List<Rectangle> rectangles = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split("\\s");
            int id = Integer.parseInt(parts[0].substring(1));
            Vector2ic pos = parseVector(parts[2].substring(0, parts[2].length() - 1), ",");
            Vector2ic offset = parseVector(parts[3], "x");
            rectangles.add(new Rectangle(id, pos, offset));
        }
        return rectangles;
    }

    private Vector2ic parseVector(String s, String separator) {
        String[] parts = s.split(separator);
        return new Vector2i(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    private void part1(List<Rectangle> rectangles) {
        Multiset<Vector2ic> positions = HashMultiset.create();
        for (Rectangle rect : rectangles) {
            for (int y = 0; y < rect.size.y(); y++) {
                for (int x = 0; x < rect.size.x(); x++) {
                    positions.add(new Vector2i(x + rect.offset.x(), y + rect.offset.y()));
                }
            }
        }

        int count = 0;
        for (Multiset.Entry<Vector2ic> entry : positions.entrySet()) {
            if (entry.getCount() > 1) {
                count++;
            }
        }


        System.out.println("Part 1: " + count);
    }

    private void part2(List<Rectangle> rectangles) {
        for (Rectangle a : rectangles) {
            boolean overlap = false;
            for (Rectangle b : rectangles) {
                if (!a.equals(b) && Rectangle.overlap(a, b)) {
                    overlap = true;
                    break;
                }
            }
            if (!overlap) {
                System.out.println("Part 2: " + a.id);
            }
        }
    }

    private static class Rectangle {
        private final Vector2ic offset;
        private final Vector2ic size;
        private final int id;

        public Rectangle(int id, Vector2ic offset, Vector2ic size) {
            this.id = id;
            this.offset = new Vector2i(offset);
            this.size = new Vector2i(size);
        }

        public static boolean overlap(Rectangle a, Rectangle b) {
            Vector2ic minA = a.min();
            Vector2ic maxA = a.max();
            Vector2ic minB = b.min();
            Vector2ic maxB = b.max();

            return maxA.x() >= minB.x() && minA.x() <= maxB.x() && maxA.y() >= minB.y() && minA.y() <= maxB.y();
        }

        public Vector2ic min() {
            return offset;
        }

        public Vector2ic max() {
            return offset.add(size, new Vector2i());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Rectangle rectangle = (Rectangle) o;
            return id == rectangle.id;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }

}

