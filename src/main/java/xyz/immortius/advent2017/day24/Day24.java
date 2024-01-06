package xyz.immortius.advent2017.day24;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Day24 {

    //private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    private static final String YEAR = "2017";
    private static final String DAY = "24";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day24().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Joint> joints = parse(lines);
        part1(joints);
        part2(joints);
    }

    private List<Joint> parse(List<String> lines) {
        List<Joint> joints = new ArrayList<>();
        for (String line : lines) {
            String[] ends = line.split("/");
            joints.add(new Joint(Integer.parseInt(ends[0]), Integer.parseInt(ends[1])));
        }
        return joints;
    }

    private void part1(List<Joint> joints) {

        int score = highestScore(0, joints);

        System.out.println("Part 1: " + score);
    }

    private void part2(List<Joint> joints) {

        List<List<Joint>> options = findValid(0, joints);
        int targetLength = options.stream().map(List::size).max(Integer::compareTo).get();
        options.removeIf(x -> x.size() < targetLength);

        int maxScore = 0;
        for (List<Joint> bridges : options) {
            maxScore = Math.max(maxScore, bridges.stream().map(x -> x.a + x.b).reduce(0, Integer::sum));
        }

        System.out.println("Part 2: " + maxScore);
    }

    private List<List<Joint>> findValid(int open, List<Joint> joints) {
        List<List<Joint>> options = new ArrayList<>();
        for (Joint joint : joints) {
            if (joint.a == open) {
                List<Joint> remaining = new ArrayList<>(joints);
                remaining.remove(joint);
                List<List<Joint>> subbridges = findValid(joint.b, remaining);
                if (subbridges.isEmpty()) {
                    List<Joint> list = new ArrayList<>();
                    list.add(joint);
                    options.add(list);
                } else {
                    subbridges.forEach(x -> x.add(joint));
                    options.addAll(subbridges);
                }
            } else if (joint.b == open) {
                List<Joint> remaining = new ArrayList<>(joints);
                remaining.remove(joint);
                List<List<Joint>> subbridges = findValid(joint.a, remaining);
                if (subbridges.isEmpty()) {
                    List<Joint> list = new ArrayList<>();
                    list.add(joint);
                    options.add(list);
                } else {
                    subbridges.forEach(x -> x.add(joint));
                    options.addAll(subbridges);
                }
            }
        }
        return options;
    }

    private int highestScore(int open, List<Joint> joints) {
        int highest = 0;
        for (Joint joint : joints) {
            if (joint.a == open) {
                List<Joint> remaining = new ArrayList<>(joints);
                remaining.remove(joint);
                highest = Math.max(highest, joint.a + joint.b + highestScore(joint.b, remaining));
            } else if (joint.b == open) {
                List<Joint> remaining = new ArrayList<>(joints);
                remaining.remove(joint);
                highest = Math.max(highest, joint.a + joint.b + highestScore(joint.a, remaining));
            }
        }
        return highest;
    }

    record Joint(int a, int b) {}

}

