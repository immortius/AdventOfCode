package xyz.immortius.advent2018.day15;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.util.Direction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Day15 {

    private static final String YEAR = "2018";
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

        State state = parse(lines);
        part1(state);
        part2(state);
    }

    private State parse(List<String> lines) {
        boolean[][] traversable = new boolean[lines.get(0).length()][lines.size()];
        List<Entity> entities = new ArrayList<>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                traversable[x][y] = line.charAt(x) != '#';
                if (line.charAt(x) == 'G' || line.charAt(x) == 'E') {
                    entities.add(new Entity(line.charAt(x), new Vector2i(x, y)));
                }
            }
        }

        return new State(traversable, entities);
    }

    private void part1(State state) {
        Game game = new Game(state);
        game.run();
        System.out.println("Rounds completed: " + game.roundsCompleted);
        int totalHealth = game.livingEntities.stream().map(x -> x.health).reduce(0, Integer::sum);
        System.out.println("Total health remaining: " + totalHealth);
        System.out.println("Part 1: " + (game.roundsCompleted * totalHealth));
    }

    private void part2(State state) {

        int elfAttackPower = 3;
        Game game;
        do {
            elfAttackPower++;
            game = new Game(state);
            game.allowableElfDeath = 0;
            for (Entity entity : game.livingEntities) {
                if (entity.type == 'E') {
                    entity.attackPower = elfAttackPower;
                }
            }
            game.run();
        } while (game.elfDeaths > 0);
        System.out.println("Attack Power: " + elfAttackPower);
        System.out.println("Rounds completed: " + game.roundsCompleted);
        int totalHealth = game.livingEntities.stream().map(x -> x.health).reduce(0, Integer::sum);
        System.out.println("Total health remaining: " + totalHealth);
        System.out.println("Part 2: " + (game.roundsCompleted * totalHealth));
    }

    private static class Entity {

        private char type;
        private Vector2i position;
        private int health = 200;
        private int attackPower = 3;

        public Entity(char type, Vector2ic position) {
            this.type = type;
            this.position = new Vector2i(position);
        }

        public Entity(Entity entity) {
            this.type = entity.type;
            this.position = new Vector2i(entity.position);
        }

        public char getType() {
            return type;
        }

        public Vector2i getPosition() {
            return position;
        }

        public int getHealth() {
            return health;
        }

        public int getAttackPower() {
            return attackPower;
        }

        public void setHealth(int health) {
            this.health = health;
        }

        public boolean isAlive() {
            return health > 0;
        }
    }

    private record State(boolean[][] traversable, List<Entity> entities) {

    }


    private static class Game {
        private boolean[][] traversable;
        private List<Entity> livingEntities;
        private Entity[][] entityLookup;
        Vector2i bounds;
        int roundsCompleted = 0;
        int elfDeaths = 0;
        int allowableElfDeath = Integer.MAX_VALUE;

        public Game(State state) {
            this.traversable = state.traversable;
            bounds = new Vector2i(traversable.length, traversable[0].length);
            livingEntities = new ArrayList<>();
            entityLookup = new Entity[bounds.x][bounds.y];
            for (Entity entity : state.entities) {
                Entity copy = new Entity(entity);
                entityLookup[entity.position.x][entity.position.y] = copy;
                livingEntities.add(copy);
            }
        }

        public void run() {
            boolean gameOver = false;
            //print();
            while (!gameOver) {
                gameOver = round();
            }
            print();
        }

        private void print() {
            for (int y = 0; y < bounds.y; y++) {
                for (int x = 0; x < bounds.x; x++) {
                    if (!traversable[x][y]) {
                        System.out.print('#');
                    } else if (entityLookup[x][y] == null) {
                        System.out.print('.');
                    } else {
                        System.out.print(entityLookup[x][y].type);
                    }
                }
                System.out.println();
            }
        }

        public boolean round() {
            List<Entity> roundOrder = determineRoundOrder();
            for (Entity entity : roundOrder) {
                if (!entity.isAlive()) {
                    continue;
                }
                if (elfDeaths > allowableElfDeath) {
                    return true;
                }
                List<Entity> targets = findTargets(entity);
                if (targets.isEmpty()) {
                    return true;
                }
                move(entity, targets);
                attack(entity);
            }
            roundsCompleted++;
            return false;
        }

        private void attack(Entity entity) {
            Entity target = null;
            for (Direction dir : Direction.values()) {
                Entity possibleTarget = entityLookup[entity.position.x + dir.toVector().x()][entity.position.y + dir.toVector().y()];
                if (possibleTarget != null && possibleTarget.type != entity.type) {
                    if (target == null) {
                        target = possibleTarget;
                    } else if (possibleTarget.health < target.health) {
                        target = possibleTarget;
                    }
                }
            }

            if (target != null) {
                target.setHealth(target.getHealth() - entity.getAttackPower());
                if (!target.isAlive()) {
                    if (target.type == 'E') {
                        elfDeaths++;
                    }
                    entityLookup[target.position.x][target.position.y] = null;
                    livingEntities.remove(target);
                }
            }
        }

        private void move(Entity entity, List<Entity> targets) {
            Set<Vector2ic> attackRangeSpaces = findAdjacentSpaces(targets);
            if (attackRangeSpaces.contains(entity.getPosition())) {
                return;
            }
            Set<Vector2ic> openSpaces = attackRangeSpaces.stream().filter(pos -> traversable[pos.x()][pos.y()]).filter(pos -> entityLookup[pos.x()][pos.y()] == null).collect(Collectors.toSet());
            if (openSpaces.isEmpty()) {
                return;
            }
            List<Vector2ic> reachableSpaces = getReachableSpaces(entity, openSpaces);
            if (reachableSpaces.isEmpty()) {
                return;
            }
            Optional<Vector2ic> closestReachable = reachableSpaces.stream().min(Comparator.comparingInt(Vector2ic::y).thenComparingInt(Vector2ic::x));
            if (closestReachable.isEmpty()) {
                return;
            }
            Vector2ic step = getShortestPathStep(entity, closestReachable.get());

            entityLookup[entity.position.x][entity.position.y] = null;
            entity.position.set(step);
            entityLookup[entity.position.x][entity.position.y] = entity;
        }

        @NotNull
        private List<Vector2ic> getReachableSpaces(Entity entity, Set<Vector2ic> openSpaces) {
            Set<Vector2ic> closed = new HashSet<>();
            Set<Vector2ic> open = new HashSet<>();
            List<Vector2ic> found = new ArrayList<>();
            closed.add(entity.position);
            open.add(entity.position);
            while (found.isEmpty() && !open.isEmpty()) {
                Set<Vector2ic> newOpen = new HashSet<>();
                for (Vector2ic from : open) {
                    for (Direction dir : Direction.values()) {
                        Vector2ic proposed = from.add(dir.toVector(), new Vector2i());
                        if (openSpaces.contains(proposed)) {
                            found.add(proposed);
                        } else if (!closed.contains(proposed) && traversable[proposed.x()][proposed.y()] && entityLookup[proposed.x()][proposed.y()] == null) {
                            closed.add(proposed);
                            newOpen.add(proposed);
                        }
                    }
                }
                open = newOpen;
            }
            return found;
        }

        @NotNull
        private Vector2ic getShortestPathStep(Entity entity, Vector2ic target) {
            Set<Vector2ic> closed = new HashSet<>();
            Set<Vector2ic> open = new HashSet<>();
            closed.add(target);
            open.add(target);
            List<Vector2ic> found = new ArrayList<>();
            while (found.isEmpty() && !open.isEmpty()) {
                Set<Vector2ic> newOpen = new HashSet<>();
                for (Vector2ic from : open) {
                    for (Direction dir : Direction.values()) {
                        Vector2ic proposed = from.add(dir.toVector(), new Vector2i());
                        if (entity.position.equals(proposed)) {
                            found.add(from);
                        } else if (!closed.contains(proposed) && traversable[proposed.x()][proposed.y()] && entityLookup[proposed.x()][proposed.y()] == null) {
                            closed.add(proposed);
                            newOpen.add(proposed);
                        }
                    }
                }
                open = newOpen;
            }

            return found.stream().min(Comparator.comparingInt(Vector2ic::y).thenComparingInt(Vector2ic::x)).get();
        }


        private Set<Vector2ic> findAdjacentSpaces(List<Entity> targets) {
            Set<Vector2ic> adjacent = new HashSet<>();
            for (Entity target : targets) {
                for (Direction dir : Direction.values()) {
                    adjacent.add(target.position.add(dir.toVector(), new Vector2i()));
                }
            }
            return adjacent;
        }

        private List<Entity> findTargets(Entity entity) {
            return livingEntities.stream().filter(x -> x.type != entity.type).toList();
        }

        private List<Entity> determineRoundOrder() {
            return livingEntities.stream().sorted(Comparator.<Entity>comparingInt(e -> e.position.y).thenComparingInt(e -> e.position.x)).toList();
        }
    }

    public enum Direction {
        Up('U', new Vector2i(0, -1)),
        Left('L', new Vector2i(-1, 0)),
        Right('R', new Vector2i(1, 0)),
        Down('D', new Vector2i(0, 1));

        private char id;
        private Vector2ic vector;

        Direction(char c, Vector2ic vector) {
            this.id = c;
            this.vector = vector;
        }

        public Vector2ic toVector() {
            return vector;
        }

        public char getId() {
            return id;
        }
    }

}

