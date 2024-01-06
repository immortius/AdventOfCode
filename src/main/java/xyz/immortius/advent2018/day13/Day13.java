package xyz.immortius.advent2018.day13;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Day13 {

    private static final String YEAR = "2018";
    private static final String DAY = "13";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day13().run();
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
        Track[][] track = new Track[lines.size()][lines.stream().map(String::length).reduce(0, Math::max)];
        List<Cart> carts = new ArrayList<>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                Direction dir = Direction.parse(line.charAt(x));
                if (dir != null) {
                    carts.add(new Cart(carts.size(), new Vector2i(x, y), dir, -1));
                    if (dir == Direction.Down || dir == Direction.Up) {
                        track[y][x] = Track.Vertical;
                    } else {
                        track[y][x] = Track.Horizontal;
                    }
                } else {
                    track[y][x] = Track.parse(line.charAt(x));
                }
            }
            for (int x = line.length(); x < track[y].length; x++) {
                track[y][x] = Track.None;
            }
        }
        return new State(track, carts);
    }

    private void part1(State state) {
        List<Cart> carts = new ArrayList<>(state.carts);
        Set<Vector2ic> locations = new HashSet<>();
        for (Cart cart : carts) {
            locations.add(cart.location);
        }
        Vector2ic crashLocation = null;
        while (crashLocation == null) {
            List<Cart> updatedCarts = new ArrayList<>();
            for (Cart cart : carts) {
                locations.remove(cart.location);
                Track track = state.track[cart.location.y()][cart.location.x()];
                Cart updatedCart = cart.move(track);
                if (!locations.add(updatedCart.location)) {
                    System.out.println("Crash at " + updatedCart.location.x() + "," + updatedCart.location.y());
                    crashLocation = updatedCart.location;
                }
                updatedCarts.add(updatedCart);
            }
            carts = updatedCarts.stream().sorted(Comparator.<Cart>comparingInt(x -> x.location.y()).thenComparingInt(x -> x.location.x())).toList();
        }
        System.out.println("Part 1: " + crashLocation.x() + "," + crashLocation.y());
    }

    private void part2(State state) {
        List<Cart> carts = new ArrayList<>(state.carts);
        Map<Vector2ic, Cart> cartLocations = new HashMap<>();
        for (Cart cart : carts) {
            cartLocations.put(cart.location, cart);
        }
        while (carts.size() > 1) {
            List<Cart> updatedCarts = new ArrayList<>();
            for (int i = 0; i < carts.size(); i++) {
                Cart cart = carts.get(i);
                cartLocations.remove(cart.location);
                Track track = state.track[cart.location.y()][cart.location.x()];
                Cart updatedCart = cart.move(track);
                Cart crashingCart = cartLocations.put(updatedCart.location, updatedCart);
                if (crashingCart != null) {
                    if (!updatedCarts.remove(crashingCart)) {
                        carts.remove(crashingCart);
                    }
                    cartLocations.remove(updatedCart.location);
                    System.out.println("Crash at " + updatedCart.location.x() + "," + updatedCart.location.y() + " between " + updatedCart.id + " and " + crashingCart.id);
                } else {
                    updatedCarts.add(updatedCart);
                }
            }
            updatedCarts.sort(Comparator.<Cart>comparingInt(x -> x.location.y()).thenComparingInt(x -> x.location.x()));
            carts = updatedCarts;
        }
        System.out.println("Part 2: " + carts.get(0).location.x() + "," + carts.get(0).location.y());
    }

    public record State(Track[][] track, List<Cart> carts) {
        public void print() {
            Map<Vector2ic, Cart> locMap = carts.stream().collect(Collectors.toMap(c -> c.location, c -> c));
            for (int y = 0; y < track.length; y++) {
                for (int x = 0; x < track[y].length; x++) {
                    Cart cart = locMap.get(new Vector2i(x, y));
                    if (cart != null) {
                        System.out.print(cart.dir.id);
                    } else {
                        System.out.print(track[y][x].display);
                    }
                }
                System.out.println();
            }
        }
    }

    public enum Track {
        None(' '),
        Vertical('|'),
        Horizontal('-'),
        RightBend('/'),
        LeftBend('\\'),
        Intersection('+');

        private final char display;
        private static final Map<Character, Track> idLookup;

        static {
            ImmutableMap.Builder<Character,Track> builder = new ImmutableMap.Builder<>();
            for (Track track : Track.values()) {
                builder.put(track.display, track);
            }
            idLookup = builder.build();
        }

        private Track(char display) {
            this.display = display;
        }

        public static Track parse(char c) {
            return idLookup.get(c);
        }
    }

    public record Cart(int id, Vector2ic location, Direction dir, int nextDir) {

        public Cart move(Track track) {
            return switch (track) {
                case RightBend -> {
                    Direction newDir = switch (dir) {
                        case Up -> Direction.Right;
                        case Down -> Direction.Left;
                        case Left -> Direction.Down;
                        case Right -> Direction.Up;
                    };
                    yield new Cart(id, location.add(newDir.toVector(), new Vector2i()), newDir, nextDir);
                }
                case LeftBend -> {
                    Direction newDir = switch (dir) {
                        case Up -> Direction.Left;
                        case Down -> Direction.Right;
                        case Left -> Direction.Up;
                        case Right -> Direction.Down;
                    };
                    yield new Cart(id, location.add(newDir.toVector(), new Vector2i()), newDir, nextDir);
                }
                case Intersection ->  {
                    Direction newDir = dir.turn(nextDir);
                    Vector2ic newPos = location.add(newDir.toVector(), new Vector2i());
                    int newNextDir = (nextDir == 1) ? -1 : nextDir + 1;
                    yield new Cart(id, newPos, newDir, newNextDir);
                }
                default -> new Cart(id, location.add(dir.toVector(), new Vector2i()), dir, nextDir);
            };
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cart cart = (Cart) o;
            return id == cart.id;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }
    }

    public enum Direction {
        Up('^', new Vector2i(0, -1)),
        Right('>', new Vector2i(1, 0)),
        Down('v', new Vector2i(0, 1)),
        Left('<', new Vector2i(-1, 0));


        private static final Map<Character, Direction> idLookup;

        private char id;
        private Vector2ic vector;

        Direction(char c, Vector2ic vector) {
            this.id = c;
            this.vector = vector;
        }

        static {
            ImmutableMap.Builder<Character,Direction> builder = new ImmutableMap.Builder<>();
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

        public Direction turn(int stepsClockwise) {
            return values()[Math.floorMod(ordinal() + stepsClockwise, values().length)];
        }
    }


}

