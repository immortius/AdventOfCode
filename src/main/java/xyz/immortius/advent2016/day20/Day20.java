package xyz.immortius.advent2016.day20;

import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Day20 {

    //private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    private static final String YEAR = "2016";
    private static final String DAY = "20";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day20().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Range> blockedRanges = parse(lines);
        part1(blockedRanges);
    }

    private List<Range> parse(List<String> lines) {
        List<Range> ranges = new ArrayList<>();
        for (String line : lines) {
            String[] split = line.split("-");
            ranges.add(new Range(Long.parseLong(split[0]), Long.parseLong(split[1])));
        }
        return ranges;
    }

    private void part1(List<Range> blockedRanges) {
        Range validRange = new Range(0, 4294967295L);
        List<Range> validRanges = new ArrayList<>();
        validRanges.add(validRange);
        for (Range blockedRange : blockedRanges) {
            List<Range> newValidRanges = new ArrayList<>();
            for (Range range : validRanges) {
                newValidRanges.addAll(range.deintersect(blockedRange));
            }
            validRanges = newValidRanges;
        }
        validRanges = validRanges.stream().sorted().toList();
        System.out.println("Part 1: " + validRanges);
        long count = validRanges.stream().map(x -> x.size()).reduce(0L, Long::sum);
        System.out.println("Part 2: " + count);

    }

    private record Range(long min, long max) implements Comparable<Range> {
        @Override
        public int compareTo(@NotNull Range o) {
            return Long.signum(min - o.min);
        }

        public Collection<Range> deintersect(Range blockedRange) {
            if (blockedRange.min > max || blockedRange.max < min) {
                return Collections.singletonList(this);
            }
            List<Range> result = new ArrayList<>();
            if (blockedRange.min > min) {
                result.add(new Range(min, blockedRange.min - 1));
            }
            if (blockedRange.max < max) {
                result.add(new Range(blockedRange.max + 1, max));
            }
            return result;
        }

        public long size() {
            return max - min + 1;
        }
    }

}

