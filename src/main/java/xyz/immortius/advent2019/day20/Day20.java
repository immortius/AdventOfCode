package xyz.immortius.advent2019.day20;

import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import xyz.immortius.util.AStar;
import xyz.immortius.util.Direction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day20 {

    //private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    private static final String YEAR = "2019";
    private static final String DAY = "20";
    private static final boolean REAL_INPUT = true;

    private static final Set<Character> IGNORE_LABEL_CHARS = new LinkedHashSet<>(Arrays.asList(' ','#','.'));

    private final Set<Vector2ic> passages = new LinkedHashSet<>();
    private final Map<Vector2ic, String> passageLabels = new LinkedHashMap<>();
    private final Map<String, Vector2ic> outLabels = new LinkedHashMap<>();
    private final Map<String, Vector2ic> inLabels = new LinkedHashMap<>();
    private Vector2ic start;
    private Vector2ic end;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day20().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        parse(lines);
        part1();
        part2();
    }

    private void parse(List<String> lines) {
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                switch (line.charAt(x)) {
                    case '.' -> passages.add(new Vector2i(x, y));
                    case '#',' ' -> {}
                    default -> {
                        if (x + 1 < line.length() && !IGNORE_LABEL_CHARS.contains(line.charAt(x + 1))) {
                            String label = line.substring(x, x + 2);
                            Vector2i labelledPassage;
                            if (x + 2 < line.length() && line.charAt(x + 2) == '.') {
                                labelledPassage = new Vector2i(x + 2, y);
                            } else {
                                labelledPassage = new Vector2i(x - 1, y);
                            }
                            passageLabels.put(labelledPassage, label);
                            if (x == 0 || y == 0 || x + 2 == line.length() || y + 2 == lines.size()) {
                                outLabels.put(label, labelledPassage);
                            } else {
                                inLabels.put(label, labelledPassage);
                            }
                        } else if (y + 1 < lines.size() && x < lines.get(y + 1).length() && !IGNORE_LABEL_CHARS.contains(lines.get(y + 1).charAt(x))) {
                            String label = String.valueOf(line.charAt(x)) + lines.get(y + 1).charAt(x);
                            Vector2i labelledPassage;
                            if (y + 2 < lines.size() && lines.get(y + 2).charAt(x) == '.') {
                                labelledPassage = new Vector2i(x, y + 2);
                            } else {
                                labelledPassage = new Vector2i(x, y - 1);
                            }
                            passageLabels.put(labelledPassage, label);
                            if (x == 0 || y == 0 || x + 2 == line.length() || y + 2 == lines.size()) {
                                outLabels.put(label, labelledPassage);
                            } else {
                                inLabels.put(label, labelledPassage);
                            }
                        }
                    }
                }
            }
        }
        start = outLabels.get("AA");
        outLabels.remove("AA");
        end = outLabels.get("ZZ");
        outLabels.remove("ZZ");
        passageLabels.remove(start);
        passageLabels.remove(end);
    }

    private void part1() {
        AStar<Vector2ic> aStar = new AStar<>(start, end, vector2ic -> 1L, pos -> {
            Map<Vector2ic, Long> adjacent = new LinkedHashMap<>();
            for (Direction dir : Direction.values()) {
                Vector2ic adj = pos.add(dir.toVector(), new Vector2i());
                if (passages.contains(adj)) {
                    adjacent.put(adj, 1L);
                }
                String label = passageLabels.get(pos);
                if (label != null) {
                    Vector2ic targetPos = outLabels.get(label);
                    if (targetPos.equals(pos)) {
                        targetPos = inLabels.get(label);
                    }
                    adjacent.put(targetPos, 1L);
                }
            }
            return adjacent;
        });

        long dist = aStar.run();
        System.out.println("Part 1: " + dist);
    }

    private void part2() {
        AStar<Vector3ic> aStar = new AStar<>(new Vector3i(start, 0), new Vector3i(end, 0), pos -> (long) pos.z(), pos -> {
            Map<Vector3ic, Long> adjacent = new LinkedHashMap<>();
            for (Direction dir : Direction.values()) {
                Vector2ic adjPos = dir.toVector().add(pos.x(), pos.y(), new Vector2i());
                if (passages.contains(adjPos)) {
                    adjacent.put(new Vector3i(adjPos, pos.z()), 1L);
                }
                String label = passageLabels.get(new Vector2i(pos.x(), pos.y()));
                if (label != null) {
                    if (inLabels.get(label).equals(new Vector2i(pos.x(), pos.y()))) {
                        adjacent.put(new Vector3i(outLabels.get(label), pos.z() + 1), 1L);
                    } else if (pos.z() > 0) {
                        adjacent.put(new Vector3i(inLabels.get(label), pos.z() - 1), 1L);
                    }
                }
            }
            return adjacent;
        });

        long dist = aStar.run();
        System.out.println("Part 2: " + dist);
    }


}

