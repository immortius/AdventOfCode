package xyz.immortius.advent2022.day22;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Day22P2 {

    // Magic number for side dimensions
    private static final int SIDE_SIZE = 50;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day22P2().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day22/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        Map<Vector2i, Side> sides = parseSides(lines);
        State startingState = determineStartingState(sides);
        List<Command> commands = parseCommands(lines.get(lines.size() - 1));
        connectSides(sides);
        processCommands(startingState, commands);
    }

    private State determineStartingState(Map<Vector2i, Side> sides) {
        Vector2i pos = new Vector2i(0,0);
        for (pos.y = 0; pos.y < 4; pos.y++) {
            for (pos.x = 0; pos.x < 4; pos.x++) {
                Side side = sides.get(pos);
                if (side != null) {
                    Vector2i startPos = new Vector2i();
                    for (startPos.x = 0; startPos.x < SIDE_SIZE; startPos.x++) {
                        if (side.get(startPos)) {
                            return new State(side, Facing.RIGHT, startPos);
                        }
                    }
                }
            }
        }
        throw new IllegalStateException();
    }

    private Map<Vector2i, Side> parseSides(List<String> lines) {
        Map<Vector2i, Side> sides = new HashMap<>();

        Vector2i sidePos = new Vector2i(0,0);
        for (sidePos.y = 0; sidePos.y * SIDE_SIZE < lines.size(); sidePos.y++) {
            for (sidePos.x = 0; sidePos.x * SIDE_SIZE < lines.get(sidePos.y * SIDE_SIZE).length(); sidePos.x++) {
                if (lines.get(sidePos.y * SIDE_SIZE).charAt(sidePos.x * SIDE_SIZE) != ' ') {
                    Side side = new Side(new Vector2i(sidePos.x * SIDE_SIZE + 1, sidePos.y * SIDE_SIZE + 1), new Vector2i(sidePos), SIDE_SIZE);
                    for (int y = 0; y < SIDE_SIZE; y++) {
                        for (int x = 0; x < SIDE_SIZE; x++) {
                            side.set(new Vector2i(x, y), lines.get(sidePos.y * SIDE_SIZE + y).charAt(sidePos.x * SIDE_SIZE + x) == '.');
                        }
                    }
                    sides.put(new Vector2i(sidePos), side);
                }
            }
        }
        return sides;
    }

    private void connectSides(Map<Vector2i, Side> sides) {

        // Setup initial connections from adjacency
        Table<Side, Facing, Connection> connectionTable = HashBasedTable.create();
        for (Map.Entry<Vector2i, Side> entry : sides.entrySet()) {
            for (Facing facing : Facing.values()) {
                Vector2i adjacent = entry.getKey().add(facing.toVector(), new Vector2i());
                Side adjSide = sides.get(adjacent);
                if (adjSide != null) {
                    connectionTable.put(entry.getValue(), facing, new Connection(adjSide, 0));
                }
            }
        }

        // Connect sides, based on repeatedly checking if a connected side has a left or right leaf that could be folded to connect
        // 3 here is just from eyeballing the maximum folds it takes to connect all sides
        for (int i = 0; i < 3; i ++) {
            for (Side side : sides.values()) {
                for (Facing facing : Facing.values()) {
                    Connection connection = connectionTable.get(side, facing);
                    if (connection != null && !connectionTable.contains(side, facing.turn(-1)) && connectionTable.contains(connection.side(), facing.turn(connection.rotations - 1))) {
                        Connection adjConnection = connectionTable.get(connection.side, facing.turn(connection.rotations - 1));
                        connectionTable.put(side, facing.turn(-1), new Connection(adjConnection.side, adjConnection.rotations + connection.rotations + 1));
                    } else if (connection != null && !connectionTable.contains(side, facing.turn(1)) && connectionTable.contains(connection.side(), facing.turn(connection.rotations + 1))) {
                        Connection adjConnection = connectionTable.get(connection.side, facing.turn(connection.rotations + 1));
                        connectionTable.put(side, facing.turnRight(), new Connection(adjConnection.side, adjConnection.rotations + connection.rotations - 1));
                    }
                }
            }
        }

        // Now we add in the connection logic, applying rotation and position transformation moving between sides
        for (Side side : sides.values()) {
            for (Facing facing: Facing.values()) {
                Connection connection = connectionTable.get(side, facing);
                side.setConnectedSide(facing, (s) -> {
                    Facing newFacing = facing.turn(connection.rotations);
                    Vector2i position = new Vector2i(s.position);
                    for (int i = 0; i < (connection.rotations() % 4 + 4) % 4; i++) {
                        position = new Vector2i(SIDE_SIZE - position.y - 1, position.x);
                    }
                    position.add(newFacing.toVector());
                    position.x = (position.x + SIDE_SIZE) % SIDE_SIZE;
                    position.y = (position.y + SIDE_SIZE) % SIDE_SIZE;
                    return new State(connection.side, newFacing, position);
                });
            }
        }
    }

    private List<Command> parseCommands(String line) {
        List<Command> commands = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (char c : line.toCharArray()) {
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

    private record Connection(Side side, int rotations) {}

    private void processCommands(State startingState, List<Command> commands) {
        State state = startingState;
        System.out.println(state.truePos().x() + ", " + state.truePos().y());
        for (Command command : commands) {
            state = command.apply(state);
            System.out.println(state.truePos().x() + ", " + state.truePos().y());
        }
        Vector2ic result = state.truePos();
        System.out.println(result.x() + ", " + result.y() + " facing " + state.facing);
        System.out.println("Answer: " + ((result.y()) * 1000 + (result.x()) * 4 + state.facing.ordinal()));
    }

    private interface Command {
        State apply(State state);
    }

    private static class TurnLeft implements Command {

        @Override
        public State apply(State state) {
            return new State(state.side, state.facing.turnLeft(), state.position);
        }
    }

    private static class TurnRight implements Command {
        @Override
        public State apply(State state) {
            return new State(state.side, state.facing.turnRight(), state.position);
        }
    }

    private static class Forwards implements Command {

        private final int distance;

        public Forwards(int distance) {
            this.distance = distance;
        }

        @Override
        public State apply(State state) {
            State newState = state;
            for (int i = 0; i < distance; i++) {
                State proposedState = newState.side.move(newState);
                if (!proposedState.side.get(proposedState.position)) {
                    return newState;
                }
                newState = proposedState;
            }
            return newState;
        }
    }

    private record State(Side side, Facing facing, Vector2ic position) {
        Vector2ic truePos() {
            return side.getFromOrigin(position);
        }
    }

    private static class Side {
        private final Vector2i position;
        private final Vector2i origin;
        private final boolean[][] values;
        private final Map<Facing, Function<State, State>> connectionTransform = new HashMap<>();

        public Side(Vector2i origin, Vector2i position, int size) {
            this.position = position;
            this.origin = new Vector2i(origin);
            values = new boolean[size][size];
        }

        public void set(Vector2ic pos, boolean value) {
            values[pos.y()][pos.x()] = value;
        }

        public boolean get(Vector2ic pos) {
            return values[pos.y()][pos.x()];
        }

        public void setConnectedSide(Facing facing, Function<State, State> transform) {
            connectionTransform.put(facing, transform);
        }

        public State move(State state) {
            Vector2i newPos = state.position.add(state.facing.toVector(), new Vector2i());
            if (newPos.x < 0 || newPos.y < 0 || newPos.x >= SIDE_SIZE || newPos.y >= SIDE_SIZE) {
                return connectionTransform.get(state.facing).apply(state);
            }
            return new State(this, state.facing, newPos);
        }

        public Vector2ic getFromOrigin(Vector2ic pos) {
            return pos.add(origin, new Vector2i());
        }

        @Override
        public String toString() {
            return "Side{" +
                    "x=" + position.x +
                    ",y=" + position.y +
                    '}';
        }
    }

    private enum Facing {
        RIGHT(new Vector2i(1, 0)),
        DOWN(new Vector2i(0, 1)),
        LEFT(new Vector2i(-1, 0)),
        UP(new Vector2i(0, -1));

        private final Vector2ic vector;

        private Facing(Vector2ic vec) {
            this.vector = vec;
        }

        public Facing turnLeft() {
            return turn(-1);
        }

        public Facing turnRight() {
            return turn(1);
        }

        public Facing turn(int amount) {
            return Facing.values()[(ordinal() + amount + Facing.values().length) % Facing.values().length];
        }

        public Vector2ic toVector() {
            return vector;
        }

    }


}