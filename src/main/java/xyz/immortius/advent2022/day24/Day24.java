package xyz.immortius.advent2022.day24;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.util.Direction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day24 {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day24().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day24/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        State state = parse(lines);
        part1(state);
        part2(state);
    }

    private State parse(List<String> lines) {
        int height = lines.size() - 2;
        int width = lines.get(0).length() - 2;

        ListMultimap<Vector2ic, Direction> winds = ArrayListMultimap.create();
        for (int lineIndex = 1; lineIndex < lines.size() - 1; lineIndex++) {
            for (int charPos = 1; charPos < lines.get(lineIndex).length() - 1; charPos++) {
                switch (lines.get(lineIndex).charAt(charPos)) {
                    case '^' -> winds.put(new Vector2i(charPos - 1, lineIndex - 1), Direction.Up);
                    case '<' -> winds.put(new Vector2i(charPos - 1, lineIndex - 1), Direction.Left);
                    case 'v' -> winds.put(new Vector2i(charPos - 1, lineIndex - 1), Direction.Down);
                    case '>' -> winds.put(new Vector2i(charPos - 1, lineIndex - 1), Direction.Right);
                }
            }
        }
        return new State(width, height, winds);
    }

    private void part1(State state) {
        int steps = calculateFastestPathTo(state, new Vector2i(0,-1), new Vector2i(state.width - 1, state.height - 1)).steps + 1;
        System.out.println("Part 1: " + steps);
    }

    private void part2(State state) {
        int totalSteps = 0;
        Result result = calculateFastestPathTo(state, new Vector2i(0,-1), new Vector2i(state.width - 1, state.height - 1));
        totalSteps += result.steps + 1;
        result = calculateFastestPathTo(result.state.updateWinds(), new Vector2i(state.width - 1, state.height), new Vector2i(0, 0));
        totalSteps += result.steps + 1;
        result = calculateFastestPathTo(result.state.updateWinds(), new Vector2i(0,-1), new Vector2i(state.width - 1, state.height - 1));
        totalSteps += result.steps + 1;
        System.out.println("Part 2: " + totalSteps);
    }

    private Result calculateFastestPathTo(State state, Vector2ic initialPosition, Vector2ic target) {
        Set<Vector2ic> possiblePositions = new LinkedHashSet<>();
        possiblePositions.add(initialPosition);
        int step = 0;
        while (!possiblePositions.contains(target)) {
            Set<Vector2ic> nextPossiblePositions = new LinkedHashSet<>();
            state = state.updateWinds();
            for (Vector2ic position : possiblePositions) {
                if (state.winds().get(position).isEmpty()) {
                    nextPossiblePositions.add(position);
                }
                for (Direction dir : Direction.values()) {
                    Vector2i adjPos = position.add(dir.toVector(), new Vector2i());
                    if (state.isInBounds(adjPos) && state.winds().get(adjPos).isEmpty()) {
                        nextPossiblePositions.add(adjPos);
                    }
                }
            }
            possiblePositions = nextPossiblePositions;
            step++;
        }

        return new Result(state, step);
    }
    private record Result(State state, int steps) {}

    private record State(int width, int height, ListMultimap<Vector2ic, Direction> winds) {

        public State updateWinds() {
            ListMultimap<Vector2ic, Direction> next = ArrayListMultimap.create();
            for (Map.Entry<Vector2ic, Direction> entry : winds.entries()) {
                Vector2i newPos = entry.getKey().add(entry.getValue().toVector(), new Vector2i());
                newPos.x = (newPos.x + width) % width;
                newPos.y = (newPos.y + height) % height;
                next.put(newPos, entry.getValue());
            }
            return new State(width, height, next);
        }

        public boolean isInBounds(Vector2ic pos) {
            return pos.x() >= 0 && pos.y() >= 0 && pos.x() < width && pos.y() < height;
        }

        public void print() {
            System.out.print("#.");
            for (int i = 0; i < width; i++) {
                System.out.print('#');
            }
            System.out.println();
            for (int y = 0; y < height; y++) {
                System.out.print('#');
                for (int x = 0; x < width; x++) {
                    List<Direction> winds = this.winds.get(new Vector2i(x, y));
                    if (winds.size() > 1) {
                        System.out.print(winds.size());
                    } else if (winds.size() == 1) {
                        System.out.print(winds.get(0).getId());
                    } else {
                        System.out.print('.');
                    }

                }
                System.out.println('#');
            }
            for (int i = 0; i < width; i++) {
                System.out.print('#');
            }
            System.out.println(".#");
        }
    }

    public enum Direction {
        Down('v', new Vector2i(0, 1)),
        Right('>', new Vector2i(1, 0)),
        Up('^', new Vector2i(0, -1)),
        Left('<', new Vector2i(-1, 0));


        private char id;
        private Vector2ic vector;

        Direction(char c, Vector2ic vector) {
            this.id = c;
            this.vector = vector;
        }

        public Direction left() {
            return Direction.values()[(ordinal() + 1) % Direction.values().length];
        }

        public Direction right() {
            return Direction.values()[(ordinal() + Direction.values().length - 1) % Direction.values().length];
        }

        public char getId() {
            return id;
        }

        public Vector2ic toVector() {
            return vector;
        }

    }

}