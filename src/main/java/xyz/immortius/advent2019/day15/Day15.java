package xyz.immortius.advent2019.day15;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.advent2019.day2.IntCodeComputer;
import xyz.immortius.advent2019.day2.IntCodeHelper;
import xyz.immortius.advent2019.day2.IntCodeInputStream;
import xyz.immortius.advent2019.day2.IntCodeOutputStream;
import xyz.immortius.util.AStar;
import xyz.immortius.util.UniqueQueue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day15 {

    private static final String YEAR = "2019";
    private static final String DAY = "15";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day15().run();
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
        Explorer explorer = new Explorer();
        IntCodeComputer computer = new IntCodeComputer(program, explorer, explorer);
        computer.run();
        explorer.printMap();

        System.out.println("Part 1: " + explorer.getPather(new Vector2i(0,0), explorer.oxygenSystem).run());

        Set<Vector2ic> spread = new LinkedHashSet<>();
        Set<Vector2ic> done = new LinkedHashSet<>();
        done.add(explorer.oxygenSystem);
        for (Direction dir : Direction.values()) {
            Vector2i adj = explorer.oxygenSystem.add(dir.toVector(), new Vector2i());
            if (explorer.map.get(adj) != Content.Wall) {
                spread.add(adj);
            }
        }
        int steps = 0;
        while (!spread.isEmpty()) {
            steps++;
            done.addAll(spread);
            Set<Vector2ic> nextSpread = new LinkedHashSet<>();
            for (Vector2ic pos : spread) {
                for (Direction dir : Direction.values()) {
                    Vector2i adj = pos.add(dir.toVector(), new Vector2i());
                    if (!done.contains(adj) && explorer.map.get(adj) != Content.Wall) {
                        nextSpread.add(adj);
                    }
                }
            }
            spread = nextSpread;
        }

        System.out.println("Part 2: " + steps);

    }

    public static class Explorer implements IntCodeInputStream, IntCodeOutputStream {

        private final Vector2i currentLocation = new Vector2i();
        private final Vector2i oxygenSystem = new Vector2i();
        private Direction lastMove;

        private final Map<Vector2ic, Content> map = new HashMap<>();
        private final UniqueQueue<Vector2ic> unexplored = new UniqueQueue<>();

        private final Deque<Direction> movementQueue = new ArrayDeque<>();


        public Explorer() {
            for (Direction dir : Direction.values()) {
                unexplored.add(new Vector2i(dir.vector));
            }
            movementQueue.add(Direction.Up);
        }

        @Override
        public long send() throws InterruptedException {
            Direction dir = movementQueue.pollFirst();
            if (dir == null) {
                throw new InterruptedException();
            }
            lastMove = dir;
            return dir.command;
        }

        @Override
        public void receive(long value) throws InterruptedException {
            switch ((int) value) {
                case 0 -> {
                    Vector2i wallLocation = currentLocation.add(lastMove.vector, new Vector2i());
                    map.put(wallLocation, Content.Wall);
                    unexplored.remove(wallLocation);
                }
                case 1 -> {
                    currentLocation.add(lastMove.vector);
                    map.put(new Vector2i(currentLocation), Content.Empty);
                    updateExplored();
                }
                case 2 -> {
                    currentLocation.add(lastMove.vector);
                    map.put(new Vector2i(currentLocation), Content.OxygenSystem);
                    System.out.println(currentLocation.x + "," + currentLocation.y + " is oxygen system");
                    updateExplored();
                    oxygenSystem.set(currentLocation);
                }
            }
            if (unexplored.isEmpty()) {
                throw new InterruptedException();
            } else if (movementQueue.isEmpty()) {
                Vector2ic target = unexplored.peek();
                AStar<Vector2ic> pather = getPather(currentLocation, target);
                pather.run();
                Vector2i projectedPos = new Vector2i(currentLocation);
                for (Vector2ic pos : pather.getPath()) {
                    Direction dir = Direction.fromVector(pos.sub(projectedPos, new Vector2i()));
                    movementQueue.addLast(dir);
                    projectedPos.set(pos);
                }
            }

        }

        @NotNull
        public AStar<Vector2ic> getPather(Vector2ic from, Vector2ic to) {
            return new AStar<>(from, to, (a) -> to.gridDistance(a.x(), a.y()), (a) -> {
                Map<Vector2ic, Long> adjacent = new LinkedHashMap<>();
                for (Direction dir : Direction.values()) {
                    Vector2i adj = a.add(dir.toVector(), new Vector2i());
                    if (map.get(adj) != Content.Wall) {
                        adjacent.put(adj, 1L);
                    }
                }
                return adjacent;
            });
        }

        private void updateExplored() {
            unexplored.remove(currentLocation);
            for (Direction dir : Direction.values()) {
                Vector2i adjPos = currentLocation.add(dir.vector, new Vector2i());
                if (map.get(adjPos) == null) {
                    unexplored.add(adjPos);
                }
            }
        }

        public void printMap() {
            Vector2i min = new Vector2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
            Vector2i max = new Vector2i(Integer.MIN_VALUE, Integer.MIN_VALUE);
            for (Vector2ic loc : map.keySet()) {
                min.min(loc);
                max.max(loc);
            }

            Vector2i pos = new Vector2i();
            for (pos.y = min.y; pos.y <= max.y; pos.y++) {
                for (pos.x = min.x; pos.x <= max.x; pos.x++) {
                    if (pos.x == 0 && pos.y == 0) {
                        System.out.print("X");
                    } else {
                        System.out.print(switch (map.getOrDefault(pos, Content.Wall)) {
                            case Wall -> '#';
                            case Empty -> '.';
                            case OxygenSystem -> 'O';
                        });
                    }
                }
                System.out.println();
            }
        }
    }

    public enum Content {
        Empty,
        Wall,
        OxygenSystem
    }

    public enum Direction {
        Up(1, new Vector2i(0, 1)),
        Down(2, new Vector2i(0, -1)),
        Left(3, new Vector2i(-1, 0)),
        Right(4, new Vector2i(1, 0));

        private static Map<Vector2ic, Direction> vectorToDirection;

        static {
            ImmutableMap.Builder<Vector2ic, Direction> builder = ImmutableMap.builder();
            for (Direction dir : Direction.values()) {
                builder.put(dir.vector, dir);
            }
            vectorToDirection = builder.build();
        }

        private int command;
        private Vector2ic vector;

        Direction(int command, Vector2ic vector) {
            this.command = command;
            this.vector = vector;
        }

        public Vector2ic toVector() {
            return vector;
        }

        public int getCommand() {
            return command;
        }

        public static Direction fromVector(Vector2ic vector) {
            return vectorToDirection.get(vector);
        }
    }




}

