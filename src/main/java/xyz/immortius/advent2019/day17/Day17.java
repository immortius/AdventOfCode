package xyz.immortius.advent2019.day17;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.advent2019.day2.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day17 {

    private static final String YEAR = "2019";
    private static final String DAY = "17";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day17().run();
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
    }

    private void part1(long[] program) {
        MapCapturer mapper = new MapCapturer();
        IntCodeComputer mappingComputer = new IntCodeComputer(program, new ConsoleInputStream(), mapper);
        mappingComputer.run();
        GridMap map = new GridMap(mapper.readMap());

        int sum = 0;
        for (int x = 1; x < map.getDimensions().x() - 1; x++) {
            for (int y = 1; y < map.getDimensions().y() - 1; y++) {
                if (map.isScaffold(x, y) && map.isScaffold(x + 1, y) && map.isScaffold(x - 1, y) && map.isScaffold(x, y + 1) && map.isScaffold(x, y - 1)) {
                    sum += x * y;
                }
            }
        }
        System.out.println("Part 1: " + sum);

        List<String> path = calculatePath(map);
        Path splitPath = optimisePath(path);

        program[0] = 2;

        ConnectingStream inputStream = new ConnectingStream();
        for (char c : splitPath.main.toCharArray()) {
            inputStream.receive(c);
        }
        inputStream.receive('\n');
        for (char c : splitPath.a.toCharArray()) {
            inputStream.receive(c);
        }
        inputStream.receive('\n');
        for (char c : splitPath.b.toCharArray()) {
            inputStream.receive(c);
        }
        inputStream.receive('\n');
        for (char c : splitPath.c.toCharArray()) {
            inputStream.receive(c);
        }
        inputStream.receive('\n');
        inputStream.receive('n');
        inputStream.receive('\n');

        IntCodeComputer computer = new IntCodeComputer(program, inputStream, value -> { System.out.println("Dust: " + value); });
        computer.run();

    }

    private static final List<String> FUNCTIONS = Arrays.asList("A", "B", "C");

    private Path optimisePath(List<String> path) {
        List<String> mainPath = new ArrayList<>(path);
        Map<String, List<String>> subPaths = new LinkedHashMap<>();

        while (subPaths.size() < 3) {
            int startIndex = 0;
            while (FUNCTIONS.contains(mainPath.get(startIndex))) {
                startIndex++;
            }
            int endIndex = startIndex + 2;
            while (!FUNCTIONS.contains(path.get(endIndex + 1)) && countOccurances(mainPath.subList(startIndex, endIndex + 2), mainPath) > 1) {
                endIndex += 2;
            }

            String subFunction = FUNCTIONS.get(subPaths.size());
            List<String> subPath = new ArrayList<>(mainPath.subList(startIndex, endIndex));
            subPaths.put(subFunction, subPath);
            replaceAll(subFunction, subPath, mainPath);
        }

        Joiner commaJoiner = Joiner.on(",");
        return new Path(commaJoiner.join(mainPath), commaJoiner.join(subPaths.get("A")), commaJoiner.join(subPaths.get("B")), commaJoiner.join(subPaths.get("C")));
    }

    private void replaceAll(String toValue, List<String> subPath, List<String> mainPath) {
        List<String> result = new ArrayList<>();
        int startIndex = 0;
        while (startIndex < mainPath.size()) {
            boolean match = true;
            for (int i = 0; i < subPath.size(); i++) {
                if (startIndex + i >= mainPath.size() || !subPath.get(i).equals(mainPath.get(startIndex + i))) {
                    match = false;
                    break;
                }
            }
            if (match) {
                result.add(toValue);
                startIndex += subPath.size();
            } else {
                result.add(mainPath.get(startIndex));
                startIndex++;
            }
        }
        mainPath.clear();
        mainPath.addAll(result);
    }

    private int countOccurances(List<String> subList, List<String> inList) {
        int result = 0;
        int startIndex = 0;
        while (startIndex < inList.size() - subList.size() + 1) {
            boolean match = true;
            for (int i = 0; i < subList.size(); i++) {
                if (!subList.get(i).equals(inList.get(startIndex + i))) {
                    match = false;
                    break;
                }
            }
            if (match) {
                result++;
                startIndex += subList.size();
            } else {
                startIndex++;
            }
        }
        return result;
    }

    private record Path(String main, String a, String b, String c) {

    }

    private List<String> calculatePath(GridMap map) {
        Vector2i pos = new Vector2i(map.botPos);
        Direction dir = map.botFacing;

        List<String> commands = new ArrayList<>();
        int dist = 0;
        while (true) {
            if (map.isScaffold(pos.add(dir.toVector(), new Vector2i()))) {
                dist++;
                pos.add(dir.toVector());
            } else {
                if (dist > 0) {
                    commands.add(Integer.toString(dist));
                }
                dist = 0;

                if (map.isScaffold(pos.add(dir.turnLeft().toVector(), new Vector2i()))) {
                    commands.add("L");
                    dir = dir.turnLeft();
                } else if (map.isScaffold(pos.add(dir.turnRight().toVector(), new Vector2i()))) {
                    commands.add("R");
                    dir = dir.turnRight();
                } else {
                    return commands;
                }
            }
        }
    }

    public static class MapCapturer implements IntCodeInputStream, IntCodeOutputStream {

        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();

        @Override
        public long send() throws InterruptedException {
            return 0;
        }

        @Override
        public void receive(long value) throws InterruptedException {
            if (value == 10) {
                if (!currentLine.isEmpty()) {
                    lines.add(currentLine.toString());
                    currentLine.setLength(0);
                }
            } else {
                currentLine.append((char) value);
            }
        }

        public List<String> readMap() {
            if (!currentLine.isEmpty()) {
                lines.add(currentLine.toString());
                currentLine.setLength(0);
            }
            return lines;
        }
    }

    public class GridMap {
        private boolean[][] map;
        private Vector2i botPos = new Vector2i();
        private Direction botFacing;
        private final Vector2ic dimensions;

        public GridMap(List<String> lines) {
            dimensions = new Vector2i(lines.get(0).length(), lines.size());
            map = new boolean[dimensions.x()][dimensions.y()];
            for (int x = 0; x < dimensions.x(); x++) {
                for (int y = 0; y < dimensions.y(); y++) {
                    char c = lines.get(y).charAt(x);
                    map[x][y] = c != '.';
                    if (c != '.' && c != '#') {
                        botPos.set(x, y);
                        botFacing = Direction.parse(c);
                    }
                }
            }
        }

        public Vector2ic getDimensions() {
            return dimensions;
        }

        public boolean isScaffold(Vector2ic pos) {
            return isScaffold(pos.x(), pos.y());
        }

        public boolean isScaffold(int x, int y) {
            if (x < 0 || y < 0 || x >= dimensions.x() || y >= dimensions.y()) {
                return false;
            }
            return map[x][y];
        }

        public Direction getBotFacing() {
            return botFacing;
        }

        public Vector2i getBotPos() {
            return botPos;
        }
    }

    public enum Direction {
        Up('^', new Vector2i(0, -1)),
        Right('>', new Vector2i(1, 0)),
        Down('v', new Vector2i(0, 1)),
        Left('<', new Vector2i(-1, 0))
        ;

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

        public char getId() {
            return id;
        }

        public static Direction parse(char c) {
            return idLookup.get(c);
        }

        public Direction turnRight() {
            return Direction.values()[(ordinal() + 1) % Direction.values().length];
        }

        public Direction turnLeft() {
            return Direction.values()[(ordinal() + 3) % Direction.values().length];
        }

        public Direction reverse() {
            return Direction.values()[(ordinal() + 2) % Direction.values().length];
        }
    }

}

