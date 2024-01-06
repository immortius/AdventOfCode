package xyz.immortius.advent2018.day7;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day7 {

    //private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    private static final String YEAR = "2018";
    private static final String DAY = "7";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day7().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        SetMultimap<Character, Character> ordering = parse(lines);
        part1(ordering);
        part2(ordering);
    }

    private SetMultimap<Character, Character> parse(List<String> lines) {
        SetMultimap<Character, Character> ordering = HashMultimap.create();
        for (String line : lines) {
            String[] parts = line.split("\\s");
            ordering.put(parts[7].charAt(0), parts[1].charAt(0));
        }
        return ordering;
    }

    private void part1(SetMultimap<Character, Character> dependencies) {
        DependencySorter sorter = new DependencySorter(dependencies);
        List<Character> ordered = sorter.sort();
        StringBuilder builder = new StringBuilder();
        for (char c : ordered) {
            builder.append(c);
        }


        System.out.println("Part 1: " + builder);
    }

    private void part2(SetMultimap<Character, Character> dependencies) {
        Set<Character> allSteps = new HashSet<>();
        allSteps.addAll(dependencies.keySet());
        allSteps.addAll(dependencies.values());
        List<Character> steps = allSteps.stream().toList();

        SetMultimap<Character, Character> dependants = HashMultimap.create();
        for (Map.Entry<Character, Character> entry : dependencies.entries()) {
            dependants.put(entry.getValue(), entry.getKey());
        }

        int baseTime = 61;
        int concurrentWork = 5;

        List<Character> availableSteps = new ArrayList<>();
        for (char step : steps) {
            if (dependencies.get(step).isEmpty()) {
                availableSteps.add(step);
            }
        }

        int currentTime = 0;
        List<Task> ongoingTasks = new ArrayList<>();

        while (!availableSteps.isEmpty() || !ongoingTasks.isEmpty()) {
            availableSteps.sort(Character::compareTo);
            while (ongoingTasks.size() < concurrentWork && !availableSteps.isEmpty()) {
                char step = availableSteps.remove(0);
                ongoingTasks.add(new Task(step, step - 'A' + currentTime + baseTime));
            }
            if (availableSteps.size() > 0) {
                System.out.println("Work overflow");
            }
            if (!ongoingTasks.isEmpty()) {
                Task nextTask = ongoingTasks.stream().sorted(Comparator.comparingInt(x -> x.completionTime)).findFirst().get();
                ongoingTasks.remove(nextTask);
                System.out.println("Completed " + nextTask.step + " at " + nextTask.completionTime);
                currentTime = nextTask.completionTime;
                for (char dependantStep : dependants.get(nextTask.step)) {
                    if (dependencies.get(dependantStep).size() == 1) {
                        availableSteps.add(dependantStep);
                    }
                    dependencies.remove(dependantStep, nextTask.step);
                }
            }
        }

        System.out.println("Part 2: " + currentTime);
    }

    private record Task(char step, int completionTime) {}

    private static class DependencySorter {
        private final SetMultimap<Character, Character> dependencies;
        private final List<Character> steps;

        public DependencySorter(SetMultimap<Character, Character> dependencies) {
            this.dependencies = HashMultimap.create(dependencies);

            Set<Character> allSteps = new HashSet<>();
            allSteps.addAll(dependencies.keySet());
            allSteps.addAll(dependencies.values());
            steps = allSteps.stream().toList();
        }

        public List<Character> sort() {

            SetMultimap<Character, Character> dependants = HashMultimap.create();
            for (Map.Entry<Character, Character> entry : dependencies.entries()) {
                dependants.put(entry.getValue(), entry.getKey());
            }

            List<Character> available = new ArrayList<>();
            for (char c : steps) {
                if (!dependencies.containsKey(c)) {
                    available.add(c);
                }
            }


            List<Character> ordering = new ArrayList<>();
            while (!available.isEmpty()) {
                available.sort(Character::compareTo);
                char c = available.remove(0);
                ordering.add(c);
                for (Character dependant : dependants.get(c)) {
                    if (dependencies.get(dependant).size() == 1) {
                        available.add(dependant);
                    }
                    dependencies.remove(dependant, c);
                }
            }
            return ordering;
        }

        private void addStep(Character step, List<Character> ordering) {
            if (ordering.contains(step)) {
                return;
            }
            List<Character> dependencies = this.dependencies.get(step).stream().sorted().toList();
        }
    }

}

