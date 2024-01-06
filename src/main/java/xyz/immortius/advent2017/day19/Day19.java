package xyz.immortius.advent2017.day19;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class Day19 {


    private static final String YEAR = "2017";
    private static final String DAY = "19";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day19().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        part1(lines);
    }

    private void part1(List<String> lines) {
        Vector2i position = new Vector2i();
        position.x = lines.get(0).indexOf('|');
        Direction dir = Direction.Down;
        StringBuilder letters = new StringBuilder();

        int steps = 1;
        boolean atEnd = false;
        while (!atEnd) {
            position.add(dir.toVector());
            char c = lines.get(position.y).charAt(position.x());
            if (c == '+') {
                Vector2i leftPos = position.add(dir.toLeft().toVector(), new Vector2i());
                if (leftPos.y >= 0 && leftPos.y < lines.size() && leftPos.x >= 0 && leftPos.x < lines.get(leftPos.y).length() && lines.get(leftPos.y).charAt(leftPos.x) != ' ') {
                    dir = dir.toLeft();
                } else {
                    dir = dir.toRight();
                }
                steps++;
            } else if (Character.isAlphabetic(c)) {
                letters.append(c);
                steps++;
            } else if (c == ' ') {
                atEnd = true;
            } else {
                steps++;
            }
        }


        System.out.println("Part 1: " + letters.toString());
        System.out.println("Part 2: " + steps);
    }

    public enum Direction {
        Up('U', new Vector2i(0, -1)),
        Right('R', new Vector2i(1, 0)),
        Down('D', new Vector2i(0, 1)),
        Left('L', new Vector2i(-1, 0));

        private static final Map<Character, Direction> idLookup;

        private char id;
        private Vector2ic vector;

        Direction(char c, Vector2ic vector) {
            this.id = c;
            this.vector = vector;
        }

        static {
            ImmutableMap.Builder<Character, Direction> builder = new ImmutableMap.Builder<>();
            for (Direction direction : Direction.values()) {
                builder.put(direction.id, direction);
            }
            idLookup = builder.build();
        }

        public Vector2ic toVector() {
            return vector;
        }

        public Direction toLeft() {
            return Direction.values()[(ordinal() + Direction.values().length - 1) % Direction.values().length];
        }

        public Direction toRight() {
            return Direction.values()[(ordinal() + 1) % Direction.values().length];
        }

        public char getId() {
            return id;
        }

        public static Direction parse(char c) {
            return idLookup.get(c);
        }
    }


}

