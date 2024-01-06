package xyz.immortius.advent2016.day4;

import com.google.common.collect.HashMultiset;
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

    private static final String YEAR = "2016";
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

        part1(lines);
        part2(lines);
    }

    private void part1(List<String> lines) {
        int sectorIdSum = 0;
        for (String line : lines) {
            RoomId room = new RoomId(line);
            if (room.isLegit()) {
                sectorIdSum += room.getSectorId();
            }
        }
        System.out.println("Part 1: " + sectorIdSum);
    }

    private void part2(List<String> lines) {
        List<RoomId> rooms = new ArrayList<>();
        for (String line : lines) {
            RoomId room = new RoomId(line);
            if (room.isLegit()) {
                rooms.add(room);
            }
        }
        for (RoomId room : rooms) {
            String name = room.getName();
            if (name.contains("north")) {
                System.out.println("Part 2: " + name + " - " + room.getSectorId());
            }
        }
    }


    private static class RoomId {
        private final String roomId;
        private final int sectorId;
        private final String encryptedName;
        private final boolean legit;

        public RoomId(String roomId) {
            this.roomId = roomId;
            String checksum = roomId.substring(roomId.length() - 6, roomId.length() - 1);
            int sectorIdIndex = roomId.lastIndexOf('-');
            sectorId = Integer.parseInt(roomId.substring(sectorIdIndex + 1, roomId.length() - 7));
            encryptedName = roomId.substring(0, sectorIdIndex);

            Multiset<Character> characterCounts = HashMultiset.create();
            for (char c : encryptedName.toCharArray()) {
                if (c != '-') {
                    characterCounts.add(c);
                }
            }
            List<CharacterCount> counts = characterCounts.elementSet().stream().map(x -> new CharacterCount(x, characterCounts.count(x))).sorted(Comparator.comparingInt(CharacterCount::count).reversed().thenComparingInt(o -> o.c)).toList();
            StringBuilder checkBuilder = new StringBuilder();
            for (int i = 0; i < 5 && i < counts.size(); i++) {
                checkBuilder.append(counts.get(i).c);
            }
            legit = checkBuilder.toString().equals(checksum);
        }

        public String getName() {
            int shift = sectorId % 26;
            StringBuilder nameBuilder = new StringBuilder();
            for (char c : encryptedName.toCharArray()) {
                if (c == '-' ) {
                    nameBuilder.append(' ');
                } else {
                    nameBuilder.append((char)(((c - 'a' + shift) % 26) + 'a'));
                }
            }
            return nameBuilder.toString();
        }

        public boolean isLegit() {
            return legit;
        }

        public int getSectorId() {
            return sectorId;
        }
    }

    private record CharacterCount(char c, int count) {}

}

