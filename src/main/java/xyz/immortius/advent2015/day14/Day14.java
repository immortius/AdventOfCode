package xyz.immortius.advent2015.day14;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day14 {

    private final Pattern linePattern = Pattern.compile("(?<deer>[A-Za-z]+) can fly (?<speed>[0-9]+) km/s for (?<time>[0-9]+) seconds, but then must rest for (?<resttime>[0-9]+) seconds.");

    private static final String YEAR = "2015";
    private static final String DAY = "14";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day14().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<DeerInfo> deerInfo = parse(lines);
        part1(deerInfo);
        part2(deerInfo);
    }

    private List<DeerInfo> parse(List<String> lines) {
        List<DeerInfo> deer = new ArrayList<>();
        for (String line : lines) {
            Matcher matcher = linePattern.matcher(line);
            if (!matcher.matches()) {
                System.out.println("No match: " + line);
            }
            String name = matcher.group("deer");
            int speed = Integer.parseInt(matcher.group("speed"));
            int time = Integer.parseInt(matcher.group("time"));
            int restTime = Integer.parseInt(matcher.group("resttime"));
            deer.add(new DeerInfo(name, speed, time, restTime));
        }
        return deer;
    }

    private void part1(List<DeerInfo> deerInfo) {
        long totalTime = 2503L;
        long maxDistance = 0;
        for (DeerInfo deer : deerInfo) {
            long fullCycles = totalTime / (deer.time + deer.restTime);
            long remainderTime = totalTime % (deer.time + deer.restTime);

            long distance = deer.time * deer.speed * fullCycles + Math.min(deer.time, remainderTime) * deer.speed;
            maxDistance = Math.max(maxDistance, distance);
        }
        System.out.println("Part 1: " + maxDistance);
    }

    private void part2(List<DeerInfo> deerInfo) {
        long totalTime = 2503L;
        int[] distances = new int[deerInfo.size()];
        int[] score = new int[deerInfo.size()];

        for (int raceSecond = 0; raceSecond < totalTime; raceSecond++) {
            for (int deerIndex = 0; deerIndex < deerInfo.size(); deerIndex++) {
                DeerInfo deer = deerInfo.get(deerIndex);
                if (raceSecond % deer.getCycleTime() < deer.time()) {
                    distances[deerIndex] += deer.speed();
                }
            }
            int maxDist = distances[0];
            List<Integer> leaders = new ArrayList<>();
            leaders.add(0);
            for (int i = 1; i < deerInfo.size(); i++) {
                if (distances[i] > maxDist) {
                    maxDist = distances[i];
                    leaders.clear();
                    leaders.add(i);
                } else if (distances[i] == maxDist) {
                    leaders.add(i);
                }
            }
            for (int index : leaders) {
                score[index]++;
            }

        }

        System.out.println("Part 2: " + Arrays.stream(score).max().getAsInt());
    }

    private record DeerInfo(String name, int speed, int time, int restTime) {
        public int getCycleTime() {
            return time + restTime;
        }
    }

}

