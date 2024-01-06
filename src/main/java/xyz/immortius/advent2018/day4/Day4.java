package xyz.immortius.advent2018.day4;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multiset;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day4 {

    //private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    private static final String YEAR = "2018";
    private static final String DAY = "4";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day4().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<GuardSleepEvent> events = parse(lines);
        part1(events);
        part2(events);
    }

    private List<GuardSleepEvent> parse(List<String> lines) {
        lines.sort(String::compareTo);
        int guard = 0;
        int start = 0;
        List<GuardSleepEvent> events = new ArrayList<>();
        for (String line : lines) {
            String data = line.substring("[1518-10-12 00:00] ".length());
            if (data.startsWith("Guard #")) {
                guard = Integer.parseInt(data.split("\\s")[1].substring(1));
            } else if (data.startsWith("falls")) {
                start = Integer.parseInt(line.substring("[1518-10-12 00:".length(), "[1518-10-12 00:".length() + 2));
            } else {
                events.add(new GuardSleepEvent(guard, start, Integer.parseInt(line.substring("[1518-10-12 00:".length(), "[1518-10-12 00:".length() + 2)) - start));
            }
        }
        return events;
    }

    private void part1(List<GuardSleepEvent> events) {
        ListMultimap<Integer, GuardSleepEvent> guardEvents = ArrayListMultimap.create();
        for (GuardSleepEvent event : events) {
            guardEvents.put(event.guard, event);
        }

        int sleepiestGuard = 0;
        int sleepAmount = 0;
        for (int guard : guardEvents.keySet()) {
            int count = guardEvents.get(guard).stream().map(x -> x.length).reduce(0, Integer::sum);
            if (count > sleepAmount) {
                sleepiestGuard = guard;
                sleepAmount = count;
            }
        }

        Multiset<Integer> sleptMinutes = HashMultiset.create();
        for (GuardSleepEvent event : guardEvents.get(sleepiestGuard)) {
            for (int i = 0; i < event.length; i++) {
                sleptMinutes.add(i + event.start);
            }
        }

        int minute = sleptMinutes.entrySet().stream().max(Comparator.comparingInt(Multiset.Entry::getCount)).get().getElement();
        System.out.println("Count: " + sleptMinutes.count(minute));



        System.out.println("Part 1: " + sleepiestGuard + " " + minute + " = " + (minute * sleepiestGuard));
    }

    private void part2(List<GuardSleepEvent> events) {
        ListMultimap<Integer, GuardSleepEvent> guardEvents = ArrayListMultimap.create();
        for (GuardSleepEvent event : events) {
            guardEvents.put(event.guard, event);
        }

        int sleepiestGuard = 0;
        int sleepiestMinute = 0;
        int sleepCount = 0;
        for (int guard : guardEvents.keySet()) {
            Multiset<Integer> sleptMinutes = HashMultiset.create();
            for (GuardSleepEvent event : guardEvents.get(guard)) {
                for (int i = 0; i < event.length; i++) {
                    sleptMinutes.add(i + event.start);
                }
            }
            int minute = sleptMinutes.entrySet().stream().max(Comparator.comparingInt(Multiset.Entry::getCount)).get().getElement();
            if (sleptMinutes.count(minute) > sleepCount) {
                sleepiestMinute = minute;
                sleepiestGuard = guard;
                sleepCount = sleptMinutes.count(minute);
                System.out.println("Count: " + sleptMinutes.count(minute));
            }

        }


        System.out.println("Part 1: " + sleepiestGuard + " " + sleepiestMinute + " = " + (sleepiestMinute * sleepiestGuard));
    }

    private record GuardSleepEvent(int guard, int start, int length) {
    }


}

