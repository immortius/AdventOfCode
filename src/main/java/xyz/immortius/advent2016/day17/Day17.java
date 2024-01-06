package xyz.immortius.advent2016.day17;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import com.google.common.hash.HashCode;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.util.Direction;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Day17 {

    private MessageDigest md5 = MessageDigest.getInstance("MD5");

    public Day17() throws NoSuchAlgorithmException {
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        long start = System.currentTimeMillis();
        new Day17().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException, NoSuchAlgorithmException {
        example();
        part1();
        part2();
    }

    private void example() throws NoSuchAlgorithmException {
        State state = findShortestPath("ulqzkmiv");
        System.out.println("Example shortest: " + state.path.size() + " - " + state.getPathString(""));

        state = findLongestPath("ulqzkmiv");
        System.out.println("Example longest: " + state.path.size() + " - " + state.getPathString(""));
    }

    private void part1() throws NoSuchAlgorithmException {
        State state = findShortestPath("awrkjxxr");
        System.out.println("Part 1: " + state.path.size() + " - " + state.getPathString(""));
    }

    private void part2() throws NoSuchAlgorithmException {
        State state = findLongestPath("awrkjxxr");
        System.out.println("Part 2: " + state.path.size() + " - " + state.getPathString(""));
    }

    private State findShortestPath(String input) {
        Vector2i target = new Vector2i(3,-3);
        Set<State> states = Sets.newLinkedHashSet();
        states.add(new State(new ArrayList<>(), new Vector2i()));

        while (!states.isEmpty()) {
            Set<State> nextStates = new LinkedHashSet<>();
            for (State state : states) {
                md5.update(state.getPathString(input).getBytes(StandardCharsets.US_ASCII));
                String hash = HashCode.fromBytes(md5.digest()).toString();
                md5.reset();
                for (Direction dir : Direction.values()) {
                    Vector2i adjPos = state.position.add(dir.toVector(), new Vector2i());
                    if (adjPos.x >= 0 && adjPos.y <= 0 && adjPos.x < 4 && adjPos.y > -4 && isOpen(hash.charAt(dir.ordinal()))) {
                        State newState = new State(state.path, dir, adjPos);
                        if (adjPos.equals(target)) {
                            return newState;
                        }
                        nextStates.add(newState);
                    }
                }
            }
            states = nextStates;
        }
        System.out.println("Failed to find exit");
        return new State(new ArrayList<>(), new Vector2i());
    }

    private State findLongestPath(String input) {
        Vector2i target = new Vector2i(3,-3);
        Set<State> states = Sets.newLinkedHashSet();
        states.add(new State(new ArrayList<>(), new Vector2i()));

        State longest = new State(new ArrayList<>(), new Vector2i());

        while (!states.isEmpty()) {
            Set<State> nextStates = new LinkedHashSet<>();
            for (State state : states) {
                md5.update(state.getPathString(input).getBytes(StandardCharsets.US_ASCII));
                String hash = HashCode.fromBytes(md5.digest()).toString();
                md5.reset();
                for (Direction dir : Direction.values()) {
                    Vector2i adjPos = state.position.add(dir.toVector(), new Vector2i());
                    if (adjPos.x >= 0 && adjPos.y <= 0 && adjPos.x < 4 && adjPos.y > -4 && isOpen(hash.charAt(dir.ordinal()))) {
                        State newState = new State(state.path, dir, adjPos);
                        if (adjPos.equals(target)) {
                            longest = newState;
                        } else {
                            nextStates.add(newState);
                        }
                    }
                }
            }
            states = nextStates;
        }
        return longest;
    }

    private boolean isOpen(char c) {
        return c > 'a';
    }

    public static class State {
        private final List<Direction> path;
        private final Vector2ic position;

        public State(List<Direction> path, Vector2ic position) {
            this.path = new ArrayList<>(path);
            this.position = new Vector2i(position);
        }

        public State(List<Direction> path, Direction dir, Vector2i adjPos) {
            this.path = new ArrayList<>(path);
            this.path.add(dir);
            this.position = new Vector2i(adjPos);
        }

        public String getPathString(String input) {
            StringBuilder builder = new StringBuilder();
            builder.append(input);
            for (Direction dir : path) {
                builder.append(dir.getId());
            }
            return builder.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return Objects.equal(path, state.path);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(path);
        }

        @Override
        public String toString() {
            return getPathString("");
        }
    }



}

