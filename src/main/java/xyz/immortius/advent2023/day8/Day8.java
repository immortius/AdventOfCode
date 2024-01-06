package xyz.immortius.advent2023.day8;

import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;
import xyz.immortius.util.MathUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day8 {

    private final Pattern linePattern = Pattern.compile("(?<from>[0-9A-Z]{3}) = \\((?<left>[0-9A-Z]{3}), (?<right>[0-9A-Z]{3})\\)");

    private static final String YEAR = "2023";
    private static final String DAY = "8";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day8().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example2.txt")))))) {
            lines = CharStreams.readLines(reader);
        }

        Input input = parse(lines);
        part1(input);
        part2(input);
    }

    private Input parse(List<String> lines) {
        boolean[] directions = new boolean[lines.get(0).length()];
        for (int i = 0; i < lines.get(0).length(); i++) {
            directions[i] = lines.get(0).charAt(i) == 'R';
        }

        Map<String, String> lefts = new LinkedHashMap<>();
        Map<String, String> rights = new LinkedHashMap<>();
        for (int i = 2; i < lines.size(); i++) {
            String line = lines.get(i);
            Matcher matcher = linePattern.matcher(line);
            if (matcher.matches()) {
                String from = matcher.group("from");
                lefts.put(from, matcher.group("left"));
                rights.put(from, matcher.group("right"));
            } else {
                System.out.println("Failed to match " + line);
            }
        }
        return new Input(directions, lefts, rights);
    }

    private void part1(Input input) {
        String location = "AAA";
        int steps = 0;
        while (!"ZZZ".equals(location)) {
            for (int i = 0; i < input.directions.length && !"ZZZ".equals(location); i++) {
                steps++;
                location = input.directions[i] ? input.rights.get(location) : input.lefts.get(location);
            }
        }
        System.out.println("Part 1: " + steps);
    }

    private void part2(Input input) {
        String[] locations = Sets.union(input.lefts.keySet(), input.rights.keySet()).stream().filter(x -> x.endsWith("A")).toList().toArray(String[]::new);
        Set<String> targets = Sets.union(input.lefts.keySet(), input.rights.keySet()).stream().filter(x -> x.endsWith("Z")).collect(Collectors.toSet());

        long[] initialHit = new long[locations.length];
        for (int i = 0; i < locations.length; i++) {
            String current = locations[i];
            long steps = 0;
            while (initialHit[i] == 0) {
                int dir = (int) (steps % input.directions.length);
                steps++;
                current = input.directions[dir] ? input.rights.get(current) : input.lefts.get(current);
                if (targets.contains(current)) {
                    initialHit[i] = steps;
                }
            }
        }

        System.out.println("Part 2: " + MathUtil.lcm(Arrays.stream(initialHit).boxed().toList()));
    }

    private record Input(boolean[] directions, Map<String, String> lefts, Map<String, String> rights) {}

}

