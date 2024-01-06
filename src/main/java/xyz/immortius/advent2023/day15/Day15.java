package xyz.immortius.advent2023.day15;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day15 {

    //private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    private static final String YEAR = "2023";
    private static final String DAY = "15";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day15().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        String input;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            input = CharStreams.readLines(reader).get(0);
        }

        part1(input);
        part2(input);
    }

    private void part1(String input) {
        System.out.println("Hash: " + hash("HASH"));
        int total = 0;
        for (String item : input.split(",")) {
            total += hash(item);
        }
        System.out.println("Part 1: " + total);
    }

    private void part2(String input) {
        List<List<LensEntry>> boxes = new ArrayList<>();
        for (int i = 0 ; i < 256; i++) {
            boxes.add(new ArrayList<>());
        }
        Map<String, LensEntry> lookup = new LinkedHashMap<>();

        for (String item : input.split(",")) {
            if (item.endsWith("-")) {
                String label = item.substring(0, item.length() - 1);
                boxes.get(hash(label)).removeIf(x -> x.label.equals(label));
                lookup.remove(label);
            } else {
                String[] parts = item.split("=");
                String label = parts[0];
                LensEntry lensEntry = lookup.get(label);
                if (lensEntry == null) {
                    lensEntry = new LensEntry(label, 0);
                    boxes.get(hash(label)).add(lensEntry);
                    lookup.put(label, lensEntry);
                }
                lensEntry.power = Integer.parseInt(parts[1]);
            }
        }

        int total = 0;
        for (int boxIndex = 0; boxIndex < boxes.size(); boxIndex++) {
            List<LensEntry> box = boxes.get(boxIndex);
            for (int lensIndex = 0; lensIndex < box.size(); lensIndex++) {
                total += (1 + boxIndex) * (1 + lensIndex) * box.get(lensIndex).power;
            }
        }
        System.out.println("Part 2: " + total);
    }

    private int hash(String input) {
        int result = 0;
        for (char c : input.toCharArray()) {
            result = (17 * (result + c)) % 256;
        }
        return result;
    }

    public static final class LensEntry {
        private final String label;
        private int power;

        public LensEntry(String label, int power) {
            this.label = label;
            this.power = power;
        }

        public String label() {
            return label;
        }

        public int power() {
            return power;
        }

        public void setPower(int power) {
            this.power = power;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (LensEntry) obj;
            return Objects.equals(this.label, that.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(label);
        }

        @Override
        public String toString() {
            return "LensEntry[" +
                    "label=" + label + ", " +
                    "power=" + power + ']';
        }
    }

}

