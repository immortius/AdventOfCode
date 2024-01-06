package xyz.immortius.advent2023.day18;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import xyz.immortius.util.Vector2l;
import xyz.immortius.util.Vector2lc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day18 {

    //private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    private static final String YEAR = "2023";
    private static final String DAY = "18";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day18().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Instruction> input = parse(lines);
        part1(input);
        shoelace(input);
        List<Instruction> input2 = parse2(lines);
        part1(input2);
        shoelace(input2);
    }

    private List<Instruction> parse(List<String> lines) {
        return lines.stream().map(line -> line.split(" ")).map(parts -> new Instruction(Direction.parse(parts[0]), Long.parseLong(parts[1]))).toList();
    }

    private List<Instruction> parse2(List<String> lines) {
        return lines.stream().map(line -> line.split(" ")).map(parts -> new Instruction(Direction.values()[3 - Integer.parseInt(parts[2].substring(7, 8))], Long.parseLong(parts[2].substring(2, parts[2].length() - 2), 16))).toList();
    }

    private void part1(List<Instruction> input) {
        List<Side> sides = new ArrayList<>();
        Vector2l pos = new Vector2l();
        for (Instruction current : input) {
            Side side = new Side(new Vector2l(pos), current.dir.toVector().mul(current.length, new Vector2l()).add(pos));
            sides.add(side);
            pos.add(current.dir.toVector().mul(current.length, new Vector2l()));
        }

        List<Long> heights = sides.stream().map(x -> x.startPos.y()).distinct().sorted().toList();
        sides.sort(Comparator.<Side>comparingLong(x -> x.startPos().x()).thenComparingLong(x -> x.endPos().x()));
        long total = 0;
        for (int i = 0; i < heights.size() - 1; i++) {
            total += sumLineArea(sides, heights.get(i));
            long space = heights.get(i + 1) - heights.get(i);
            if (space > 1) {
                total += sumLineArea(sides, heights.get(i) + 1) * (space - 1);
            }
        }

        total += sumLineArea(sides, heights.get(heights.size() - 1));

        System.out.println("Part 1: " + total);
    }

    private void shoelace(List<Instruction> input) {
        long perimeter = 1;
        List<Vector2lc> points = new ArrayList<>();
        Vector2l pos = new Vector2l();
        for (Instruction current : input) {
            pos.add(current.dir.toVector().mul(current.length, new Vector2l()));
            points.add(new Vector2l(pos));
            perimeter += current.length;
        }

        long total = 0;
        for (int i = 0; i < points.size() - 1; i++) {
            total += points.get(i).x() * points.get(i + 1).y();
            total -= points.get(i).y() * points.get(i + 1).x();
        }
        total += points.get(points.size() - 1).x() * points.get(0).y();
        total -= points.get(points.size() - 1).y() * points.get(0).x();

        total = Math.abs(total) / 2;
        total += perimeter / 2;

        System.out.println("Shoelace: " + total);
    }

    private long sumLineArea(List<Side> sides, long y) {
        long total = 0;
        boolean inside = false;
        long nextX = Long.MIN_VALUE;
        for (Side side : sides) {
            if (side.intersectsHeight(y)) {
                if (inside) {
                    total += Math.max(0, side.startPos.x() - nextX);
                }

                if (side.isHorizontal()) {
                    total += side.endPos.x() - Math.max(side.startPos.x(), nextX) + 1;
                } else {
                    if (nextX <= side.startPos.x()) {
                        total += 1;
                    }
                    if (side.startPos.y() != y) {
                        inside = !inside;
                    }
                }
                nextX = side.endPos().x() + 1;
            }
        }
        return total;
    }

    private static final class Side {
        private final Vector2lc startPos;
        private final Vector2lc endPos;

        private Side(Vector2lc startPos, Vector2l endPos) {
            this.startPos = new Vector2l(startPos).min(endPos);
            this.endPos = new Vector2l(startPos).max(endPos);
        }

        public long length() {
            return endPos.gridDistance(startPos);
        }

        public boolean intersectsHeight(long height) {
            return height >= startPos().y() && height <= endPos().y();
        }

        public Vector2lc startPos() {
            return startPos;
        }

        public Vector2lc endPos() {
            return endPos;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Side) obj;
            return Objects.equals(this.startPos, that.startPos) &&
                    Objects.equals(this.endPos, that.endPos);
        }

        @Override
        public int hashCode() {
            return Objects.hash(startPos, endPos);
        }

        @Override
        public String toString() {
            return "Side[" +
                    "startPos=" + startPos + ", " +
                    "endPos=" + endPos + ']';
        }

        public boolean isHorizontal() {
            return startPos.y() == endPos.y();
        }
    }


    private record Instruction(Direction dir, long length) {
    }

    public enum Direction {
        Up('U', new Vector2l(0, -1)),
        Left('L', new Vector2l(-1, 0)),
        Down('D', new Vector2l(0, 1)),
        Right('R', new Vector2l(1, 0));

        private static final Map<Character, Direction> idLookup;

        private char id;
        private Vector2lc vector;

        Direction(char c, Vector2lc vector) {
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

        public Vector2lc toVector() {
            return vector;
        }

        public char getId() {
            return id;
        }

        public boolean isVertical() {
            return vector.y() != 0;
        }

        public boolean isHorizontal() {
            return vector.y() == 0;
        }

        public Direction clockwise() {
            return Direction.values()[(ordinal() + 1) % Direction.values().length];
        }

        public Direction anticlockwise() {
            return Direction.values()[(ordinal() + Direction.values().length - 1) % Direction.values().length];
        }

        public Direction reverse() {
            return Direction.values()[(ordinal() + 2) % Direction.values().length];
        }

        public static Direction parse(char c) {
            return idLookup.get(c);
        }

        public static Direction parse(String c) {
            return idLookup.get(c.charAt(0));
        }
    }
}

