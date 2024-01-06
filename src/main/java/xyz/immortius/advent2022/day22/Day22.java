package xyz.immortius.advent2022.day22;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.advent2022.day15.GridMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day22 {

    private WarpMap map;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day22().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day22/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        List<Command> commands = parse(lines);
        part1(commands);
        part2();
    }

    private List<Command> parse(List<String> lines) {
        map = new WarpMap(new Vector2i(1, 1), new Vector2i(151, lines.size() - 1));
        for (int y = 0; y < lines.size() - 2; y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) != ' ') {
                    map.set(x + 1, y + 1, line.charAt(x) == '.');
                }
            }
        }

        List<Command> commands = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        for (char c : lines.get(lines.size() - 1).toCharArray()) {
            switch (c) {
                case 'L' -> {
                    if (builder.length() > 0) {
                        commands.add(new Forwards(Integer.parseInt(builder.toString())));
                        builder.setLength(0);
                    }
                    commands.add(new TurnLeft());
                }
                case 'R' -> {
                    if (builder.length() > 0) {
                        commands.add(new Forwards(Integer.parseInt(builder.toString())));
                        builder.setLength(0);
                    }
                    commands.add(new TurnRight());
                }
                default -> builder.append(c);
            }
        }
        if (builder.length() > 0) {
            commands.add(new Forwards(Integer.parseInt(builder.toString())));
        }
        return commands;
    }

    private void part1(List<Command> commands) {
        Vector2i position = new Vector2i(1,1);
        position.x = 1;
        while (!Boolean.TRUE.equals(map.get(position.x, 1))) {
            position.x++;
        }
        State state = new State(Facing.RIGHT, position);
        for (Command command : commands) {
            state = command.apply(state, map);
        }
        System.out.println(state.position.x() + ", " + state.position.y() + " facing " + state.facing);
        System.out.println(state.position.y() * 1000 + state.position.x() * 4 + state.facing.ordinal());
    }

    private void part2() {

    }

    private interface Command {
        State apply(State state, WarpMap map);
    }

    private static class TurnLeft implements Command {

        @Override
        public State apply(State state, WarpMap map) {
            return new State(state.facing.turnLeft(), state.position);
        }
    }

    private static class TurnRight implements Command {

        @Override
        public State apply(State state, WarpMap map) {
            return new State(state.facing.turnRight(), state.position);
        }
    }

    private static class Forwards implements Command {

        private final int distance;

        public Forwards(int distance) {
            this.distance = distance;
        }

        @Override
        public State apply(State state, WarpMap map) {
            Vector2ic moveDir = state.facing.toVector();
            Vector2i pos = new Vector2i(state.position);
            for (int i = 0; i < distance; i++) {
                Vector2i newPos = map.nextPos(pos, moveDir);
                if (map.get(newPos.x, newPos.y)) {
                    pos.set(newPos);
                } else {
                    break;
                }
            }
            return new State(state.facing, pos);
        }
    }

    private record State(Facing facing, Vector2ic position) {}

    private enum Facing {
        RIGHT(new Vector2i(1, 0)),
        DOWN(new Vector2i(0, 1)),
        LEFT(new Vector2i(-1, 0)),
        UP(new Vector2i(0, -1));

        private final Vector2ic vector;

        private static Map<Facing, Facing> turnLeft;
        private static Map<Facing, Facing> turnRight;

        private Facing(Vector2ic vec) {
            this.vector = vec;
        }

        public Facing turnLeft() {
            return Facing.values()[(Facing.values().length + ordinal() - 1) % Facing.values().length];
        }

        public Facing turnRight() {
            return Facing.values()[(ordinal() + 1) % Facing.values().length];
        }

        public Vector2ic toVector() {
            return vector;
        }
    }


    private enum Content {
        Wall('#', false, false),
        Path('.', true, false),
        Nothing(' ', false, true);

        private static Map<Character, Content> PARSE_MAP;

        static {
            ImmutableMap.Builder<Character, Content> builder = ImmutableMap.builder();
            for (Content content : Content.values()) {
                builder.put(content.representation, content);
            }
            PARSE_MAP = builder.build();
        }

        private char representation;
        private boolean traversable;
        private boolean warp;

        private Content(char representation, boolean traversable, boolean warp) {
            this.representation = representation;
            this.traversable = traversable;
            this.warp = warp;
        }

        public char getRepresentation() {
            return representation;
        }

        public boolean isTraversable() {
            return traversable;
        }

        public boolean isWarp() {
            return warp;
        }

        public static Content parse(char c) {
            return PARSE_MAP.get(c);
        }

        @Override
        public String toString() {
            return Character.toString(representation);
        }
    }

}