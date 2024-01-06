package xyz.immortius.advent2018.day24;

import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day24 {

    private final Pattern linePattern = Pattern.compile("(?<count>[0-9]+) units each with (?<hp>[0-9]+) hit points (\\((?<immunitiesAndWeaknesses>[^)]+)\\) )?with an attack that does (?<damage>[0-9]+) (?<damageType>\\S+) damage at initiative (?<initiative>[0-9]+)");

    private static final String YEAR = "2018";
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

        List<Unit> input = parse(lines);
        part1(input);
        part2(input);
    }

    private List<Unit> parse(List<String> lines) {
        List<Unit> units = new ArrayList<>();
        int infectionStart = lines.indexOf("Infection:");
        int id = 1;
        for (String line : lines.subList(1, infectionStart - 1)) {
            Matcher matcher = linePattern.matcher(line);
            if (matcher.matches()) {
                units.add(parseUnit(matcher, id++, "immune"));
            } else {
                System.out.println("No match: '" + line + "'");
            }
        }
        id = 1;
        for (String line : lines.subList(infectionStart + 1, lines.size())) {
            Matcher matcher = linePattern.matcher(line);
            if (matcher.matches()) {
                units.add(parseUnit(matcher, id++, "infect"));
            } else {
                System.out.println("No match: '" + line + "'");
            }
        }
        return units;
    }

    @NotNull
    private Unit parseUnit(Matcher matcher, int id, String side) {
        int count = Integer.parseInt(matcher.group("count"));
        int hp = Integer.parseInt(matcher.group("hp"));
        List<String> immunities = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();
        String immunitiesAndWeaknesses = matcher.group("immunitiesAndWeaknesses");
        if (immunitiesAndWeaknesses != null) {
            String[] sections = immunitiesAndWeaknesses.split("; ");
            for (String section : sections) {
                if (section.startsWith("weak to ")) {
                    weaknesses.addAll(Arrays.stream(section.substring("weak to ".length()).split(", ")).toList());
                } else if (section.startsWith("immune to ")) {
                    immunities.addAll(Arrays.stream(section.substring("immune to ".length()).split(", ")).toList());
                }
            }
        }

        int damage = Integer.parseInt(matcher.group("damage"));
        String damageType = matcher.group("damageType");
        int initiative = Integer.parseInt(matcher.group("initiative"));
        return new Unit(id, count, hp, immunities, weaknesses, damage, damageType, initiative, side);
    }

    private void part1(List<Unit> input) {
        CombatSimulator simulator = new CombatSimulator(input);
        simulator.run();
        System.out.println("Part 1: " + simulator.units.stream().map(x -> x.count).reduce(0, Integer::sum));
    }

    private void part2(List<Unit> input) {
        String winner = "infect";
        int unitsRemaining = 0;
        while (!winner.equals("immune")) {
            CombatSimulator simulator = new CombatSimulator(input);
            simulator.run();
            winner = simulator.units.get(0).side;
            input.stream().filter(x -> x.side.equals("immune")).forEach(x -> x.damage++);
            unitsRemaining = simulator.units.stream().map(x -> x.count).reduce(0, Integer::sum);
            System.out.println("Winner: " + winner + " with units remaining " + unitsRemaining);
        }

        System.out.println("Part 2: " + unitsRemaining);
    }

    public static final class Unit {
        private int count;
        private final int id;
        private final int hp;
        private final List<String> immunities;
        private final List<String> weaknesses;
        private int damage;
        private final String damageType;
        private final int initiative;
        private final String side;

        public Unit(int id, int count, int hp, List<String> immunities, List<String> weaknesses, int damage, String damageType, int initiative, String side) {
            this.id = id;
            this.count = count;
            this.hp = hp;
            this.immunities = immunities;
            this.weaknesses = weaknesses;
            this.damage = damage;
            this.damageType = damageType;
            this.initiative = initiative;
            this.side = side;
        }

        public Unit(Unit other) {
            this(other.id, other.count, other.hp, other.immunities, other.weaknesses, other.damage, other.damageType, other.initiative, other.side);
        }

        public int effectivePower() {
            return count * damage;
        }

        public int count() {
            return count;
        }

        public int hp() {
            return hp;
        }

        public List<String> immunities() {
            return immunities;
        }

        public List<String> weaknesses() {
            return weaknesses;
        }

        public int damage() {
            return damage;
        }

        public String damageType() {
            return damageType;
        }

        public int initiative() {
            return initiative;
        }

        public String side() {
            return side;
        }

        public boolean isAlive() {
            return count > 0;
        }

        @Override
        public String toString() {
            return side + " " + id;
        }

        public int receiveDamage(int damage) {
            int destroyed = Math.min(damage / hp, count);
            //System.out.println(Math.min(destroyed, count) + " units destroyed (" + (count - destroyed) + " remain)");
            count -= destroyed;
            return destroyed;
        }
    }

    public static class CombatSimulator {
        private final List<Unit> units;

        public CombatSimulator(List<Unit> units) {
            this.units = units.stream().map(Unit::new).collect(Collectors.toList());
        }

        public void run() {
            int round = 1;
            while (!over()) {
                //System.out.println("Round " + round++);
                round();
            }
        }

        private boolean over() {
            return units.stream().noneMatch(x -> x.side.equals("immune")) || units.stream().noneMatch(x -> x.side.equals("infect"));
        }

        public void round() {
            Map<Unit, Unit> targets = determineTargets();

            int destroyed = 0;

            List<Unit> unitOrder = units.stream().sorted(Comparator.comparingInt(Unit::initiative).reversed()).toList();
            for (Unit unit : unitOrder) {
                if (!unit.isAlive()) {
                    continue;
                }
                Unit target = targets.get(unit);
                if (target != null && target.isAlive()) {
                    int damage = unit.effectivePower();
                    if (target.weaknesses().contains(unit.damageType)) {
                        damage *= 2;
                    }
                    //System.out.println(unit + " deals " + damage + " to " + target);
                    destroyed += target.receiveDamage(damage);
                    if (!target.isAlive()) {
                        units.remove(target);
                    }
                }
            }

            if (destroyed == 0) {
                // Stalemate, remove all immune
                System.out.println("Stalemate detected, losing the war");
                units.removeIf(x -> x.side.equals("immune"));
            }
        }

        @NotNull
        private Map<Unit, Unit> determineTargets() {
            List<Unit> unitOrder = units.stream().sorted(Comparator.comparingInt(Unit::effectivePower).thenComparingInt(Unit::initiative).reversed()).toList();

            Map<Unit, Unit> targets = new HashMap<>();
            Set<Unit> targetted = new HashSet<>();
            for (Unit unit : unitOrder) {
                Unit bestTarget = null;
                int bestPotentialDamage = 0;
                for (Unit potential : units) {
                    if (!targetted.contains(potential) && !potential.side.equals(unit.side) && !potential.immunities.contains(unit.damageType)) {
                        int potentialDamage = unit.effectivePower();
                        if (potential.weaknesses.contains(unit.damageType)) {
                            potentialDamage *= 2;
                        }
                        if (potentialDamage > bestPotentialDamage) {
                            bestTarget = potential;
                            bestPotentialDamage = potentialDamage;
                        } else if (potentialDamage == bestPotentialDamage && bestTarget.effectivePower() < potential.effectivePower()) {
                            bestTarget = potential;
                        } else if (potentialDamage == bestPotentialDamage && bestTarget.effectivePower() == potential.effectivePower() && bestTarget.initiative() < potential.initiative) {
                            bestTarget = potential;
                        }
                    }
                }
                if (bestTarget != null) {
                    targets.put(unit, bestTarget);
                    targetted.add(bestTarget);
                }
            }
            return targets;
        }

    }
}

