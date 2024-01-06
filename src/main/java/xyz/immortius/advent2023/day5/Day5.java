package xyz.immortius.advent2023.day5;

import com.google.common.io.CharStreams;
import xyz.immortius.util.Range;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Day5 {

    private static final String YEAR = "2023";
    private static final String DAY = "5";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day5().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            lines = CharStreams.readLines(reader);
        }

        Almanac almanac = parse(lines);
        long start = System.currentTimeMillis();
        part1Redux(almanac);
        part2(almanac);
        long end = System.currentTimeMillis();
        System.out.println(end - start + "ms");
    }

    private Almanac parse(List<String> lines) {
        List<Long> seeds = Arrays.stream(lines.get(0).split(": ")[1].split(" ")).map(Long::parseLong).toList();

        List<TransformSet> transformations = new ArrayList<>();
        for (int i = 2; i < lines.size(); i++) {
            String type = lines.get(i++);
            List<TransformRange> ranges = new ArrayList<>();
            while (i < lines.size() && !lines.get(i).isEmpty()) {
                long[] values = Arrays.stream(lines.get(i++).split(" ")).map(Long::parseLong).mapToLong((x) -> x).toArray();
                ranges.add(new TransformRange(values[0], values[1], values[2]));
            }
            transformations.add(new TransformSet(ranges));
        }
        return new Almanac(seeds, transformations);
    }

    private void part1(Almanac almanac) {
        long minLocation = Integer.MAX_VALUE;
        for (long seed : almanac.seeds) {
            long location = almanac.transform(seed);
            minLocation = Math.min(minLocation, location);
        }

        System.out.println("Part 1: " + minLocation);
    }

    private void part1Redux(Almanac almanac) {
        almanac = almanac.flatten();
        long minLocation = Integer.MAX_VALUE;
        for (long seed : almanac.seeds) {
            long location = almanac.transform(seed);
            minLocation = Math.min(minLocation, location);
        }

        System.out.println("Part 1: " + minLocation);
    }

    private void part2(Almanac almanac) {
        //almanac = almanac.flatten();
        long minLocation = Integer.MAX_VALUE;
        List<Range> seedRanges = new ArrayList<>();
        for (int i = 0; i < almanac.seeds.size(); i += 2) {
            seedRanges.add(Range.createMinLength(almanac.seeds.get(i), almanac.seeds.get(i + 1)));
        }

        List<Range> results = almanac.transform(seedRanges);
        for (Range result : results) {
            minLocation = Math.min(result.min(), minLocation);
        }
        System.out.println("Part 2: " + minLocation);
    }


    public record Almanac(List<Long> seeds, List<TransformSet> transformations) {

        long transform(long seed) {
            long result = seed;
            for (TransformSet transform : transformations) {
                result = transform.apply(result);
            }
            return result;
        }

        List<Range> transform(List<Range> seeds) {
            List<Range> result = seeds;
            for (TransformSet transformations : transformations()) {
                    result = transformations.transform(result);
            }
            return result;
        }

        public Almanac flatten() {
            TransformSet finalRangeSet = transformations.get(0);
            for (int i = 1; i < transformations.size(); i++) {
                finalRangeSet = transformations.get(i).transform(finalRangeSet);
            }
            return new Almanac(seeds, List.of(finalRangeSet));
        }
    }

    public record TransformSet(List<TransformRange> ranges) {

        public long apply(long input) {
            for (TransformRange range : ranges) {
                if (range.covers(input)) {
                    return range.apply(input);
                }
            }
            return input;
        }

        public TransformSet transform(TransformSet sources) {
            List<TransformRange> result = new ArrayList<>();
            for (TransformRange rangeA : sources.ranges) {
                result.addAll(transform(rangeA));
            }

            List<TransformRange> remainder = new ArrayList<>(ranges);
            for (TransformRange rangeA : sources.ranges) {
                List<TransformRange> newRemainder = new ArrayList<>();
                for (TransformRange b : remainder) {
                    if (b.srcRange().overlaps(rangeA.srcRange())) {
                        for (Range rem : b.srcRange().deintersect(rangeA.srcRange())) {
                            newRemainder.add(new TransformRange(b.destStart + rem.min() - b.srcStart, rem.min(), rem.length()));
                        }
                    } else {
                        newRemainder.add(b);
                    }
                }
                remainder = newRemainder;
            }
            result.addAll(remainder);

            return new TransformSet(result);
        }

        private List<TransformRange> transform(TransformRange initial) {
            List<TransformRange> remainder = new ArrayList<>();
            remainder.add(initial);
            List<TransformRange> result = new ArrayList<>();
            for (TransformRange b : ranges) {
                List<TransformRange> newRemainder = new ArrayList<>();
                for (TransformRange a : remainder) {
                    if (a.destRange().overlaps(b.srcRange())) {
                        for (Range end : a.destRange().deintersect(b.srcRange())) {
                            long endOffset = end.min() - a.destStart;
                            newRemainder.add(new TransformRange(a.destStart + endOffset, a.srcStart + endOffset, end.length()));
                        }
                        Range overlap = a.destRange().intersect(b.srcRange());
                        long offsetA = overlap.min() - a.destStart;
                        long offsetB = overlap.min() - b.srcStart;
                        result.add(new TransformRange(b.destStart + offsetB, a.srcStart + offsetA, overlap.length()));
                    } else {
                        newRemainder.add(a);
                    }
                }
                remainder = newRemainder;
            }
            result.addAll(remainder);
            return result;
        }

        public List<Range> transform(List<Range> seeds) {
            List<Range> remainder = new ArrayList<>(seeds);
            List<Range> result = new ArrayList<>();
            for (TransformRange b : ranges) {
                List<Range> newRemainder = new ArrayList<>();
                for (Range a : remainder) {
                    if (a.overlaps(b.srcRange())) {
                        newRemainder.addAll(a.deintersect(b.srcRange()));
                        Range intersect = a.intersect(b.srcRange());
                        result.add(Range.createMinLength(b.destStart + intersect.min() - b.srcStart, intersect.length()));
                    } else {
                        newRemainder.add(a);
                    }
                }
                remainder = newRemainder;
            }
            result.addAll(remainder);
            return result;
        }
    }

    public record TransformRange(long destStart, long srcStart, long range) {

        boolean covers(long input) {
            return input >= srcStart && input < srcStart + range;
        }

        long apply(long input) {
            return input - srcStart + destStart;
        }

        public Range srcRange() {
            return Range.createMinLength(srcStart, range);
        }

        public Range destRange() {
            return Range.createMinLength(destStart, range);
        }

    }
}

