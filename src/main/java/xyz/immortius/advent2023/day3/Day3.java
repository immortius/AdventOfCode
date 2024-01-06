package xyz.immortius.advent2023.day3;

import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day3 {

    private static final String YEAR = "2023";
    private static final String DAY = "3";
    private static final boolean REAL_INPUT = true;

    private static final List<Vector2ic> ADJACENT_OFFSETS = Arrays.asList(new Vector2i(1, 0), new Vector2i(1, 1), new Vector2i(0, 1), new Vector2i(-1, 1), new Vector2i(-1, 0), new Vector2i(-1, -1), new Vector2i(0, -1), new Vector2i(1, -1));

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day3().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            lines = CharStreams.readLines(reader);
        }

        Schematic input = parse(lines);
        check(input);
        part1(input);
        part2(input);
    }

    private Schematic parse(List<String> lines) {
        List<Label> result = new ArrayList<>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                char c = line.charAt(x);
                if (c != '.') {
                    if (Character.isDigit(c)) {
                        int xEnd = x;
                        while (xEnd < line.length() && Character.isDigit(line.charAt(xEnd))) {
                            xEnd++;
                        }
                        List<Vector2ic> positions = new ArrayList<>();
                        for (int i = x; i < xEnd; i++) {
                            positions.add(new Vector2i(i, y));
                        }
                        result.add(new Label(line.substring(x, xEnd), positions));
                        x = xEnd - 1;
                    } else {
                        result.add(new Label(String.valueOf(c), List.of(new Vector2i(x, y))));
                    }
                }
            }
        }
        return new Schematic(result, new Vector2i(lines.get(0).length(), lines.size()));
    }

    private void part1(Schematic input) {
        int total = 0;
        for (Label label : input.labels.stream().filter(Label::isNumber).toList()) {
            Set<Label> adjacent = input.findAdjacent(label);
            if (adjacent.stream().anyMatch(x -> !x.isNumber())) {
                total += label.asNumber();
            }
        }
        System.out.println("Part 1: " + total);
    }

    private void part2(Schematic input) {
        int total = 0;
        for (Label label : input.labels.stream().filter(x -> x.value.equals("*")).toList()) {
            Set<Label> adjacent = input.findAdjacent(label);
            List<Label> values = adjacent.stream().filter(Label::isNumber).toList();
            if (values.size() == 2) {
                total += values.get(0).asNumber() * values.get(1).asNumber();
            }
        }
        System.out.println("Part 2: " + total);
    }

    private void check(Schematic input) {
        for (Label label : input.labels.stream().filter(Label::isNumber).toList()) {
            Set<Label> adjacent = input.findAdjacent(label);
            List<Label> values = adjacent.stream().filter(x -> !x.isNumber()).toList();
            if (values.size() > 1) {
                System.out.println(label.value + " adjacent to multiple parts");
            }
        }
    }

    private static class Schematic {
        private final List<Label> labels;
        private final Vector2ic size;
        private final Map<Vector2ic, Label> lookup;

        public Schematic(List<Label> labels, Vector2ic size) {
            this.labels = labels;
            this.size = size;
            lookup = new LinkedHashMap<>();
            for (Label label : labels) {
                for (Vector2ic position : label.positions) {
                    lookup.put(position, label);
                }
            }
        }

        public Set<Label> findAdjacent(Label label) {
            Set<Label> result = new LinkedHashSet<>();
            for (Vector2ic pos : label.positions) {
                for (Vector2ic offset : ADJACENT_OFFSETS) {
                    Vector2ic adjPos = pos.add(offset, new Vector2i());
                    Label adjLabel = lookup.get(adjPos);
                    if (adjLabel != null && !adjLabel.equals(label)) {
                        result.add(adjLabel);
                    }
                }
            }
            return result;
        }
    }

    private record Label(String value, List<Vector2ic> positions) {
        boolean isNumber() {
            return Character.isDigit(value.charAt(0));
        }

        public int asNumber() {
            return Integer.parseInt(value);
        }
    }

}

