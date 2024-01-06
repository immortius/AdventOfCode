package xyz.immortius.advent2017.day7;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day7 {

    private static final String YEAR = "2017";
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

        Map<String, Tower> towers = parse(lines);
        part1(towers);
        part2(towers);
    }

    private Map<String, Tower> parse(List<String> lines) {
        Map<String, Tower> towers = new HashMap<>();
        for (String line : lines) {
            String[] parts = line.split(",?\\s+");
            String name = parts[0];
            Tower tower = towers.computeIfAbsent(name, Tower::new);
            tower.weight = Integer.parseInt(parts[1].substring(1, parts[1].length() - 1));
            for (int i = 3; i < parts.length; i++) {
                Tower subTower = towers.computeIfAbsent(parts[i], Tower::new);
                tower.subtowers.put(parts[i], subTower);
                subTower.parent = tower;
            }
        }
        return towers;
    }

    private void part1(Map<String, Tower> towers) {
        String name = towers.keySet().stream().findAny().get();
        Tower tower = towers.get(name);
        while (tower.parent != null) {
            tower = tower.parent;
        }
        System.out.println("Part 1: " + tower.name);
    }

    private void part2(Map<String, Tower> towers) {
        String name = towers.keySet().stream().findAny().get();
        Tower tower = towers.get(name);
        while (tower.parent != null) {
            tower = tower.parent;
        }
        System.out.println("Part 2: " + tower.findUnbalanced());
    }

    private static class Tower {
        public final String name;
        public final Map<String, Tower> subtowers = new HashMap<>();
        public int weight;
        public Tower parent;

        public Tower(String name) {
            this.name = name;
        }

        public int totalWeight() {
            return weight + subtowers.values().stream().map(Tower::totalWeight).reduce(0, Integer::sum);
        }

        public int findUnbalanced() {
            List<String> subNames = subtowers.keySet().stream().toList();
            int weight0 = subtowers.get(subNames.get(0)).totalWeight();
            int weight1 = subtowers.get(subNames.get(1)).totalWeight();
            if (weight0 == weight1) {
                for (int i = 2; i < subtowers.size(); i++) {
                    Tower subtower = subtowers.get(subNames.get(i));
                    if (subtower.totalWeight() != weight0) {
                        int change = subtowers.get(subNames.get(i)).findUnbalanced();
                        if (change == 0) {
                            return weight0 - subtower.totalWeight() + subtower.weight;
                        }
                        return change;
                    }
                }
            } else if (weight0 == subtowers.get(subNames.get(2)).totalWeight()) {
                Tower subtower = subtowers.get(subNames.get(1));
                int change = subtower.findUnbalanced();
                if (change == 0) {
                    return weight0 - subtower.totalWeight() + subtower.weight;
                }
                return change;
            } else {
                Tower subtower = subtowers.get(subNames.get(0));
                int change = subtower.findUnbalanced();
                if (change == 0) {
                    return weight1 - subtower.totalWeight() + subtower.weight;
                }
                return change;
            }
            return 0;
        }

        @Override
        public String toString() {
            return "Tower{" +
                    "name='" + name + '\'' +
                    "total='" + totalWeight() + '\'' +
                    '}';
        }
    }

}

