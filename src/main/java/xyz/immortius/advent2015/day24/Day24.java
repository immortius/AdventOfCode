package xyz.immortius.advent2015.day24;

import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day24 {

    private static final String YEAR = "2015";
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

        List<Integer> packages = parse(lines);
        part1(packages);
        part2(packages);
    }

    private List<Integer> parse(List<String> lines) {
        return lines.stream().map(Integer::parseInt).toList();
    }

    private void part1(List<Integer> packages) {
        int total = packages.stream().reduce(0, Integer::sum);
        int groupSize = total / 3;
        List<Integer> orderedPackages = packages.stream().sorted(Comparator.reverseOrder()).toList();
        List<List<Integer>> groupCandidates = getGroupsOfSize(orderedPackages, groupSize);

        groupCandidates.sort(Comparator.<List<Integer>>comparingInt(List::size).thenComparingLong(this::getQE));
        System.out.println(groupCandidates.get(0));
        System.out.println(groupCandidates.get(1));

        System.out.println("Part 1: " + getQE(groupCandidates.get(0)));
    }

    private void part2(List<Integer> packages) {
        int total = packages.stream().reduce(0, Integer::sum);
        int groupSize = total / 4;
        List<Integer> orderedPackages = packages.stream().sorted(Comparator.reverseOrder()).toList();
        List<List<Integer>> groupCandidates = getGroupsOfSize(orderedPackages, groupSize);

        groupCandidates.sort(Comparator.<List<Integer>>comparingInt(List::size).thenComparingLong(this::getQE));
        System.out.println(groupCandidates.get(0));
        System.out.println(groupCandidates.get(1));

        System.out.println("Part 2: " + getQE(groupCandidates.get(0)));
    }

    private long getQE(List<Integer> group) {
        return group.stream().map(x -> (long) x).reduce(1L, (a, b) -> a * b);
    }

    private List<List<Integer>> getGroupsOfSize(List<Integer> packages, int groupSize) {
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < packages.size(); i++) {
            int selected = packages.get(i);
            if (groupSize - selected == 0) {
                List<Integer> group = new ArrayList<>();
                group.add(selected);
                result.add(group);
            } else if (groupSize - selected >= 0) {
                List<List<Integer>> subGroups = getGroupsOfSize(packages.subList(i + 1, packages.size()), groupSize - selected);
                for (List<Integer> subGroup : subGroups) {
                    subGroup.add(selected);
                    result.add(subGroup);
                }
            }
        }
        return result;
    }




}

