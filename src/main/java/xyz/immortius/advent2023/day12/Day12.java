package xyz.immortius.advent2023.day12;

import com.google.common.base.Strings;
import com.google.common.io.CharStreams;
import xyz.immortius.util.MathUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day12 {

    private static final String YEAR = "2023";
    private static final String DAY = "12";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day12().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Row> rows = parse(lines);

        part1(rows);
        part2(rows);
    }

    private List<Row> parse(List<String> lines) {
        return lines.stream().map(line -> {
            String[] parts = line.split(" ");
            return new Row(parts[0], Arrays.stream(parts[1].split(",")).map(Integer::parseInt).toList());
        }).toList();
    }

    private void part1(List<Row> rows) {
        long total = 0;
        for (Row row : rows) {
            total += calcPermutations(row);
        }
        System.out.println("Part 1: " + total);
    }

    private void part2(List<Row> rows) {
        long total = 0;
        for (Row row : rows) {
            total += calcPermutations(row.expand());
        }
        System.out.println("Part 2: " + total);
    }

    private final Map<Row, Long> cache = new LinkedHashMap<>();
    private long calcPermutations(Row row) {
        Long cachedResult = cache.get(row);
        if (cachedResult == null) {
            cachedResult = calcPermutationsUncached(row);
            cache.put(row, cachedResult);
        }
        return cachedResult;
    }

    private long calcPermutationsUncached(Row row) {
        List<Integer> remainingClues = new ArrayList<>(row.clues);
        char[] data = row.info.toCharArray();

        // Quick out when not possible
        int spaceRequired = remainingClues.stream().reduce(0, Integer::sum) + remainingClues.size() - 1;
        if (data.length < spaceRequired) {
            return 0;
        }

        // Process to first uncertain
        int pos = 0;
        int priorSprings = 0;
        while (pos < data.length && (data[pos] != '?' || priorSprings > 0)) {
            switch (data[pos]) {
                case '#':
                    priorSprings++;
                    break;
                case '.':
                    if (priorSprings != 0) {
                        if (remainingClues.isEmpty()) {
                            return 0; // invalid possibility
                        } else if (priorSprings == remainingClues.get(0)) {
                            remainingClues.remove(0);
                        } else {
                            return 0; // invalid combo;
                        }
                        priorSprings = 0;
                    }
                    break;
                case '?':
                    if (remainingClues.isEmpty()) {
                        return 0;
                    } else if (priorSprings == remainingClues.get(0)) {
                        priorSprings = 0; // Act like this is '.'
                        remainingClues.remove(0);
                    } else if (priorSprings < remainingClues.get(0)) {
                        priorSprings++; // Act like this is '#', need to complete clue
                    }
                    break;
            }
            pos++;
        }

        // Process unknown block
        if (pos < data.length) {
            int unknownSectionLength = 0;
            for (int j = pos; j < data.length && data[j] == '?'; j++) {
                unknownSectionLength++;
            }

            int trailingSprings = 0;
            for (int j = pos + unknownSectionLength; j < data.length && data[j] == '#'; j++) {
                trailingSprings++;
            }

            String remainingData = row.info.substring(pos + unknownSectionLength);
            List<Integer> sectionClues = new ArrayList<>();
            long result = 0;

            if (trailingSprings == 0) {
                // No trailing springs, so sum the permutations of putting increasingly larger portions of the clue into the unknown section
                long sectionCombinations = combinationsInUnknown(unknownSectionLength, sectionClues);
                while (sectionCombinations > 0) {
                    result += sectionCombinations * calcPermutations(new Row(remainingData, new ArrayList<>(remainingClues)));
                    if (remainingClues.isEmpty()) {
                        sectionCombinations = 0;
                    } else {
                        sectionClues.add(remainingClues.remove(0));
                        sectionCombinations = combinationsInUnknown(unknownSectionLength, sectionClues);
                    }
                }
            } else {
                // Trailing springs, so in addition to the above consider that the trailing springs my fulfil the next clue by expanding into the unknown section
                int nextClue = (remainingClues.isEmpty()) ? 0 : remainingClues.get(0);

                // The usable section length is one less, because there needs to be a space before the trailing springs
                for (int pad = 0; pad <= Math.max(0, nextClue - trailingSprings) && pad <= unknownSectionLength; pad++) {
                    result += calcPermutations(new Row(Strings.repeat("#", pad) + remainingData, new ArrayList<>(remainingClues)));
                }
                while (!remainingClues.isEmpty()) {
                    sectionClues.add(remainingClues.remove(0));
                    int usingCluesLength = sectionClues.stream().reduce(0, Integer::sum) + sectionClues.size();
                    nextClue = (remainingClues.isEmpty()) ? 0 : remainingClues.get(0);
                    long comb = combinationsInUnknown(unknownSectionLength - 1, sectionClues);
                    if (comb == 0) break;
                    for (int pad = 0; pad <= Math.max(0, nextClue - trailingSprings) && pad <= unknownSectionLength - usingCluesLength; pad++) {
                        long comb2 = combinationsInUnknown(unknownSectionLength - 1 - pad, sectionClues);
                        result += comb2 * calcPermutations(new Row(Strings.repeat("#", pad) + remainingData, new ArrayList<>(remainingClues)));
                    }
                }
            }
            return result;
        }

        // End of data, work out if the permutation is valid
        if (priorSprings != 0) {
            if (remainingClues.isEmpty()) {
                return 0; // invalid
            } else if (priorSprings == remainingClues.get(0)) {
                remainingClues.remove(0);
            } else {
                return 0; // invalid combo;
            }
        }
        if (remainingClues.isEmpty()) {
            return 1;
        }
        return 0;
    }

    private record Row(String info, List<Integer> clues) {

        public Row expand() {
            List<Integer> newClues = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                newClues.addAll(clues);
            }
            return new Row(info + "?" + info + "?" + info + "?" + info + "?" + info, newClues);
        }
    }

    public static long combinationsInUnknown(int length, List<Integer> clue) {
        int spaces = length - (clue.size() - 1 + clue.stream().reduce(0, Integer::sum));
        if (spaces < 0) {
            return 0;
        }
        int places = clue.size() + 1;
        return MathUtil.combinationsWithRepetitions(spaces, places);
    }
}

