package xyz.immortius.advent2017.day13;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Day13 {

    //private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    private static final String YEAR = "2017";
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

        Map<Integer, Integer> firewalls = parse(lines);
        part1(firewalls);
        part2(firewalls);
    }

    private Map<Integer, Integer> parse(List<String> lines) {
        Map<Integer, Integer> firewall = new LinkedHashMap<>();
        for (String line : lines) {
            String[] parts = line.split(": ");
            firewall.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
        return firewall;
    }

    private void part1(Map<Integer, Integer> firewallSizes) {
        Map<Integer, Integer> firewallPositions = new HashMap<>();
        for (Map.Entry<Integer, Integer> firewall : firewallSizes.entrySet()) {
            firewallPositions.put(firewall.getKey(), firewall.getKey() % (firewall.getValue() + firewall.getValue() - 2));
        }

        int totalSeverity = 0;
        for (Map.Entry<Integer, Integer> firewall : firewallPositions.entrySet()) {
            if (firewall.getValue() == 0) {
                System.out.println("Caught at layer " + firewall.getKey());
                totalSeverity += firewall.getKey() * firewallSizes.get(firewall.getKey());
            }
        }


        System.out.println("Part 1: " + totalSeverity);
    }

    private void part2(Map<Integer, Integer> firewallSizes) {
        long offset = 0;
        boolean clear = false;
        while (!clear) {
            clear = true;
            for (Map.Entry<Integer, Integer> firewall : firewallSizes.entrySet()) {
                if (((offset + firewall.getKey()) % (firewall.getValue() + firewall.getValue() - 2)) == 0) {
                    offset++;
                    clear = false;
                    break;
                }
            }
        }

        System.out.println("Part 2: " + offset);
    }



}

