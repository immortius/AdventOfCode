package xyz.immortius.advent2018.day18;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Day18 {

    private static final String YEAR = "2018";
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

        State state = parse(lines);
        part1(state);
        part2(state);
    }

    private State parse(List<String> lines) {
        Content[] content = new Content[lines.size() * lines.get(0).length()];
        int pos = 0;
        for (String line : lines) {
            for (char c : line.toCharArray()) {
                content[pos++] = Content.parse(c);
            }
        }
        return new State(content, lines.get(0).length(), lines.size());
    }

    private void part1(State initialState) {
        State state = initialState;
        for (int i = 0; i < 10; i++) {
            state = state.step();
        }
        int wood = state.adjCount(Content.Tree);
        int lumbar = state.adjCount(Content.Lumber);
        System.out.println("Final Wood: " + wood);
        System.out.println("Final Lumbar: " + lumbar);

        System.out.println("Part 1: " + (wood * lumbar));
    }

    private void part2(State initialState) {
        State state = initialState;
        List<State> previousStates = new ArrayList<>();
        previousStates.add(initialState);

        int desiredStates = 1000000000;
        for (int i = 0; i < desiredStates; i++) {
            state = state.step();
            int prevIndex = previousStates.indexOf(state);
            if (prevIndex != -1) {
                int remaining = desiredStates - i;
                int cycles = remaining / (previousStates.size() - prevIndex);
                i += cycles * (previousStates.size() - prevIndex);
                previousStates.clear();
            } else {
                previousStates.add(state);
            }
        }
        int wood = state.adjCount(Content.Tree);
        int lumbar = state.adjCount(Content.Lumber);
        System.out.println("Final Wood: " + wood);
        System.out.println("Final Lumbar: " + lumbar);

        System.out.println("Part 2: " + (wood * lumbar));
    }

    public record State(Content[] content, int width, int height) {

        public Content getAt(int x, int y) {
            return content[y * width + x];
        }

        public State step() {
            Content[] newContent = new Content[content.length];

            int pos = 0;
            for (int y = 0; y < height(); y++) {
                for (int x = 0; x < width(); x++) {
                    Content current = getAt(x, y);
                    newContent[pos++] = switch (current) {
                        case Open -> adjCount(x, y, Content.Tree) >= 3 ? Content.Tree : Content.Open;
                        case Tree -> adjCount(x, y, Content.Lumber) >= 3 ? Content.Lumber : Content.Tree;
                        case Lumber -> (adjCount(x, y, Content.Tree) > 0 && adjCount(x, y, Content.Lumber) > 0) ? Content.Lumber : Content.Open;
                    };
                }
            }
            return new State(newContent, width, height);
        }

        private int adjCount(int x, int y, Content type) {
            int result = 0;
            for (int adjY = Math.max(0, y - 1); adjY < Math.min(y + 2, height()); adjY++) {
                for (int adjX = Math.max(0, x - 1); adjX < Math.min(x + 2, width()); adjX++) {
                    if (x == adjX && y == adjY) {
                        continue;
                    }
                    if (getAt(adjX, adjY) == type) {
                        result++;
                    }
                }
            }
            return result;
        }

        public int adjCount(Content type) {
            int result = 0;
            for (int y = 0; y < height(); y++) {
                for (int x = 0; x < width(); x++) {
                    if (getAt(x, y) == type) {
                        result++;
                    }
                }
            }
            return result;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return Arrays.equals(content, state.content);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(content);
        }
    }

    public enum Content {
        Open('.'),
        Tree('|'),
        Lumber('#');

        private static final Map<Character, Content> lookup;

        static {
            ImmutableMap.Builder<Character, Content> builder = ImmutableMap.builder();
            for (Content state : Content.values()) {
                builder.put(state.getRepresentation(), state);
            }
            lookup = builder.build();
        }

        private final char representation;

        private Content(char rep) {
            this.representation = rep;
        }

        public char getRepresentation() {
            return representation;
        }

        public static Content parse(char c) {
            return lookup.get(c);
        }

        @Override
        public String toString() {
            return Character.toString(representation);
        }
    }

}

