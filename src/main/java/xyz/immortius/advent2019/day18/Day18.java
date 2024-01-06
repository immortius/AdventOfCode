package xyz.immortius.advent2019.day18;

import com.google.common.collect.*;
import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.util.AStar;
import xyz.immortius.util.Direction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Day18 {

    private static final String YEAR = "2019";
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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        WorldMap input = parse(lines);
        part1(input.copy());
    }

    private WorldMap parse(List<String> lines) {
        boolean[][] world = new boolean[lines.get(0).length()][lines.size()];
        BiMap<Character, Vector2ic> doors = HashBiMap.create();
        BiMap<Vector2ic, Character> keys = HashBiMap.create();
        List<Vector2ic> startPositions = new ArrayList<>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                world[x][y] = c != '#';
                if (c == '@') {
                    startPositions.add(new Vector2i(x, y));
                }
                if (c >= 'A' && c <= 'Z') {
                    doors.put(Character.toLowerCase(c), new Vector2i(x, y));
                }
                if (c >= 'a' && c <= 'z') {
                    keys.put(new Vector2i(x, y), c);
                }
            }
        }
        return new WorldMap(world, startPositions, doors, keys);
    }

    private void part1(WorldMap input) {

        Set<Vector2ic> closed = new LinkedHashSet<>();

        cleanUnusedDoors(input, closed);
        Map<Character, Integer> robotPerKey = determineKeyAccess(input);

        AStar<State> solver = createSolver(input, robotPerKey);
        long dist = solver.run();
        System.out.println("Critical dist: " + dist);

        List<State> path = new ArrayList<>(solver.getPath());
        path.add(0, new State(input.startPositions, Collections.emptySet(), input));
        {
            List<Character> keyOrder = new ArrayList<>();
            for (int i = 1; i < path.size(); i++) {
                keyOrder.addAll(Sets.difference(path.get(i).keys, path.get(i - 1).keys));
            }
            System.out.println("Order: " + keyOrder);
        }

        List<Character> optionalKey = new ArrayList<>(Sets.difference(input.keyLocations.values(), input.doorLocations.keySet()));
        Set<Character> freeKeys = new LinkedHashSet<>();
        for (Character key : input.keyLocations.values()) {
            int robot = robotPerKey.get(key);
            AStar<Vector2ic> basicPath = createPathing(input, path.get(path.size() - 1).keys(), input.startPositions.get(robot), input.keyLocations.inverse().get(key));
            basicPath.run();
            for (Vector2ic pos : basicPath.getPath()) {
                Character passedKey = input.keyLocations.get(pos);
                if (passedKey != null && key != passedKey.charValue() && optionalKey.contains(passedKey)) {
                    freeKeys.add(passedKey);
                }
            }
        }
        optionalKey.removeAll(freeKeys);

        System.out.println(path);
        for (int index = optionalKey.size() - 1; index >= 0; index--) {
            char key = optionalKey.get(index);
            Vector2ic keyPos = input.keyLocations.inverse().get(key);
            int robot = robotPerKey.get(key);

            AStar<Vector2ic> endPath = createPathing(input, path.get(path.size() - 1).keys(), path.get(path.size() - 1).positions().get(robot), keyPos);
            long additionalDist = endPath.run();
            int bestInsertPoint = path.size();
            for (int i = path.size() - 2; i >= 0; i--) {
                AStar<Vector2ic> initialSection = createPathing(input, path.get(i).keys(), path.get(i).positions.get(robot), keyPos);
                long distA = initialSection.run();
                if (distA == -1) {
                    break;
                }
                AStar<Vector2ic> endSection = createPathing(input, path.get(i).keys(), keyPos, path.get(i + 1).positions.get(robot));
                long distB = endSection.run();
                AStar<Vector2ic> straightPath = createPathing(input, path.get(i).keys(), path.get(i).positions.get(robot), path.get(i + 1).positions.get(robot));
                long baseDist = straightPath.run();

                long diff = distA + distB - baseDist;
                if (diff < additionalDist) {
                    additionalDist = diff;
                    bestInsertPoint = i + 1;
                }
            }
            dist += additionalDist;
            List<Vector2ic> positions = new ArrayList<>(path.get(bestInsertPoint - 1).positions);
            positions.set(robot, keyPos);
            path.add(bestInsertPoint, new State(positions, new LinkedHashSet<>(path.get(bestInsertPoint - 1).keys()), input));
            for (int i = bestInsertPoint; i < path.size(); i++) {
                path.get(i).keys.add(key);
            }
        }


        System.out.println("Part 1: " + dist);
        List<Character> keyOrder = new ArrayList<>();
        for (int i = 1; i < path.size(); i++) {
            keyOrder.addAll(Sets.difference(path.get(i).keys, path.get(i - 1).keys));
        }
        System.out.println("Order: " + keyOrder);
    }

    private Map<Character, Integer> determineKeyAccess(WorldMap input) {
        Map<Character, Integer> robotMap = new LinkedHashMap<>();
        for (int i = 0; i < input.startPositions.size(); i++) {
            for (char key : findAllPotentialKeys(input, input.startPositions.get(i))) {
                robotMap.put(key, i);
            }
        }
        return robotMap;
    }

    @NotNull
    private void cleanUnusedDoors(WorldMap input, Set<Vector2ic> closed) {
        List<Section> sections = new ArrayList<>();
        for (int y = 0; y < input.world[0].length; y++) {
            for (int x = 0; x < input.world.length; x++) {
                if (input.world[x][y] && !closed.contains(new Vector2i(x, y)) && !input.doorLocations.containsValue(new Vector2i(x, y))) {
                    BiMap<Character, Vector2ic> doors = HashBiMap.create();
                    BiMap<Character, Vector2ic> keys = HashBiMap.create();
                    Set<Vector2ic> sectionLocations = new LinkedHashSet<>();

                    Vector2ic start = new Vector2i(x, y);
                    closed.add(start);
                    sectionLocations.add(start);
                    Deque<Vector2ic> open = new ArrayDeque<>();
                    open.add(start);

                    while (!open.isEmpty()) {
                        Vector2ic pos = open.pop();
                        if (input.keyLocations.containsKey(pos)) {
                            keys.put(input.keyLocations.get(pos), pos);
                        }
                        for (Direction dir : Direction.values()) {
                            Vector2ic next = pos.add(dir.toVector(), new Vector2i());
                            if (input.doorLocations.inverse().containsKey(next)) {
                                doors.put(input.doorLocations.inverse().get(next), next);
                            } else if (input.world[next.x()][next.y()] && !closed.contains(next)) {
                                closed.add(next);
                                open.add(next);
                                sectionLocations.add(next);
                            }
                        }
                    }
                    boolean containsRobot = false;
                    for (Vector2ic pos : input.startPositions) {
                        if (sectionLocations.contains(pos)) {
                            containsRobot = true;
                            break;
                        }
                    }
                    sections.add(new Section(sectionLocations, doors, keys, containsRobot));
                }
            }
        }
        System.out.println(sections);

        int sectionCount;
        do {
            sectionCount = sections.size();
            List<Section> removeSections = sections.stream().filter(x -> x.keys.isEmpty() && x.doors.size() == 1 && !x.containsRobot).toList();
            sections.removeAll(removeSections);
            List<Character> orphanedDoors = removeSections.stream().map(x -> x.doors.keySet().iterator().next()).toList();
            if (!orphanedDoors.isEmpty()) {
                System.out.println("Removing unused doors: " + orphanedDoors);
            }
            for (Section section : sections) {
                section.doors.keySet().removeAll(orphanedDoors);
            }
            for (char c : orphanedDoors) {
                input.doorLocations.remove(c);
            }
        } while (sectionCount != sections.size());
    }

    record WorldMap(boolean[][] world, List<Vector2ic> startPositions, BiMap<Character, Vector2ic> doorLocations, BiMap<Vector2ic, Character> keyLocations) {

        public WorldMap copy() {
            boolean[][] worldCopy = new boolean[world.length][];
            for (int i = 0; i < worldCopy.length; i++) {
                worldCopy[i] = Arrays.copyOf(world[i], world[i].length);
            }
            return new WorldMap(worldCopy, new ArrayList<>(startPositions), HashBiMap.create(doorLocations), HashBiMap.create(keyLocations));
        }
    }

    public AStar<Vector2ic> createPathing(WorldMap input, Set<Character> keys, Vector2ic start, Vector2ic end) {
        Set<Vector2ic> blockedDoors = input.doorLocations.keySet().stream().filter(x -> !keys.contains(x)).map(input.doorLocations::get).collect(Collectors.toSet());
        return new AStar<>(
                start,
                end,
                end::gridDistance,
                (x) -> Arrays.stream(Direction.values()).<Vector2ic>map(dir -> dir.toVector().add(x, new Vector2i())).filter(newPos -> input.world[newPos.x()][newPos.y()] && !blockedDoors.contains(newPos)).collect(Collectors.toMap((a) -> a, (a) -> 1L)));
    }

    public AStar<State> createSolver(WorldMap worldMap, Map<Character, Integer> robotPerKey) {
        System.out.println("All possible keys: " + robotPerKey.keySet());
        SetMultimap<Integer, Character> robotKeys = HashMultimap.create();
        for (Map.Entry<Character, Integer> entry : robotPerKey.entrySet()) {
            robotKeys.put(entry.getValue(), entry.getKey());
        }
        return new AStar<>(
            new State(worldMap.startPositions, Collections.emptySet(), worldMap),
            new State(new ArrayList<>(), new LinkedHashSet<>(worldMap.keyLocations.values()), worldMap),
                (state) -> {
                long estimate = 0;
                for (int i = 0; i < state.positions.size(); i++) {
                    long robotEstimate = 0;
                    for (char key : robotKeys.get(i)) {
                        if (!state.keys.contains(key) && state.map.doorLocations.keySet().contains(key)) {
                            robotEstimate = Math.max(robotEstimate, state.map.keyLocations.inverse().get(key).gridDistance(state.positions.get(i)));
                        }
                    }
                    estimate += robotEstimate;
                }
                return estimate;
            },
            (state) -> {
                Map<State, Long> possibleStates = new LinkedHashMap<>();

                Set<Character> availableKeys = findAvailableKeys(state);
                //System.out.println("Have " + state.keys + " can get " + availableKeys);
                for (Character key : availableKeys) {
                    Vector2ic keyPos = state.map.keyLocations.inverse().get(key);
                    int robot = robotPerKey.get(key);
                    AStar<Vector2ic> pathing = createPathing(state.map, state.keys, state.positions.get(robot), keyPos);
                    long dist = pathing.run();

                    Set<Character> keys = new LinkedHashSet<>(state.keys);
                    keys.add(key);

                    List<Vector2ic> positions = new ArrayList<>(state.positions);
                    positions.set(robot, keyPos);
                    possibleStates.put(new State(positions, keys, worldMap), dist);
                }
                return possibleStates;
            }
        );
    }

    public Set<Character> findAvailableKeys(State state) {
        Deque<Vector2ic> open = new ArrayDeque<>();
        Set<Vector2ic> closed = new HashSet<>();

        Set<Character> availableKeys = new LinkedHashSet<>();

        for (Vector2ic pos : state.positions) {
            open.add(pos);
            closed.add(pos);
        }
        while (!open.isEmpty()) {
            Vector2ic pos = open.pop();
            Character key = state.map.keyLocations.get(pos);
            if (key != null && !state.keys.contains(key)) {
                availableKeys.add(key);
            } else {
                for (Vector2ic nextPos : Arrays.stream(Direction.values()).map(x -> pos.add(x.toVector(), new Vector2i())).toList()) {
                    Character door = state.map.doorLocations.inverse().get(nextPos);
                    if (state.map.world[nextPos.x()][nextPos.y()] && (door == null || state.keys.contains(door)) && closed.add(nextPos)) {
                        open.add(nextPos);
                    }
                }
            }
        }
        return availableKeys;
    }

    public Set<Character> findAllPotentialKeys(WorldMap map, Vector2ic startPos) {
        Deque<Vector2ic> open = new ArrayDeque<>();
        Set<Vector2ic> closed = new HashSet<>();

        Set<Character> availableKeys = new LinkedHashSet<>();

        open.add(startPos);
        closed.add(startPos);
        while (!open.isEmpty()) {
            Vector2ic pos = open.pop();
            Character key = map.keyLocations.get(pos);
            if (key != null) {
                availableKeys.add(key);
            }

            for (Vector2ic nextPos : Arrays.stream(Direction.values()).map(x -> pos.add(x.toVector(), new Vector2i())).toList()) {
                if (map.world[nextPos.x()][nextPos.y()] && closed.add(nextPos)) {
                    open.add(nextPos);
                }
            }

        }
        return availableKeys;
    }

    record Section(Set<Vector2ic> locations, BiMap<Character, Vector2ic> doors, BiMap<Character, Vector2ic> keys, boolean containsRobot) {

        @Override
        public String toString() {
            return "Section{" +
                    "doors=" + doors.keySet() +
                    ", keys=" + keys.keySet() +
                    '}';
        }
    }

    record State(List<Vector2ic> positions, Set<Character> keys, WorldMap map) {
        @Override
        public String toString() {
            return "State{" +
                    "pos=" + positions +
                    ", keys=" + keys +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            State state = (State) o;

            if (Objects.equals(keys, state.keys)) {
                return keys.equals(map.keyLocations.values()) || positions.equals(state.positions);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return keys.hashCode();
        }
    }

}

