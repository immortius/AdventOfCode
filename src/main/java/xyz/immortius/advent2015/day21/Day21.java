package xyz.immortius.advent2015.day21;

import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.List;

public class Day21 {

    private static final boolean REAL_INPUT = false;

    private static final Character REAL_BOSS = new Character(103, 9, 2);
    private static final Character EXAMPLE_BOSS = new Character(12, 7, 2);

    private static final List<Equipment> WEAPONS = ImmutableList.of(
            new Equipment("Dagger", 8, 4, 0),
            new Equipment("Shortsword", 10, 5,0),
            new Equipment("Warhammer", 25, 6, 0),
            new Equipment("Longsword", 40, 7, 0),
            new Equipment("Greataxe", 74, 8, 0)
    );

    private static final List<Equipment> ARMOR = ImmutableList.of(
            new Equipment("None", 0, 0, 0),
            new Equipment("Leather", 13, 0, 1),
            new Equipment("Chainmail", 31, 0,2),
            new Equipment("Splintmail", 53, 0, 3),
            new Equipment("Bandedmail", 75, 0, 4),
            new Equipment("Platemail", 102, 0, 5)
    );

    private static final List<Equipment> RINGS = ImmutableList.of(
            new Equipment("None", 0, 0, 0),
            new Equipment("None", 0, 0, 0),
            new Equipment("Damage +1", 25, 1, 0),
            new Equipment("Damage +2", 50, 2,0),
            new Equipment("Damage +3", 100, 3, 0),
            new Equipment("Defense +1", 20, 0, 1),
            new Equipment("Defense +2", 40, 0, 2),
            new Equipment("Defense +3", 80, 0, 3)
    );

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day21().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        part1();
        part2();
    }

    private void part1() {
        int minCost = Integer.MAX_VALUE;
        for (Equipment weapon : WEAPONS) {
            for (Equipment armor : ARMOR) {
                for (int ringA = 0; ringA < RINGS.size() - 1; ringA++) {
                    for (int ringB = ringA + 1; ringB < RINGS.size(); ringB++) {
                        Character player = new Character(100, weapon.damage + RINGS.get(ringA).damage + RINGS.get(ringB).damage, armor.armor + RINGS.get(ringA).armor + RINGS.get(ringB).armor);
                        if (determineOutcome(player, REAL_BOSS)) {
                            minCost = Math.min(minCost, weapon.cost + armor.cost + RINGS.get(ringA).cost + RINGS.get(ringB).cost);
                        }
                    }
                }
            }
        }
        System.out.println("Part 1: " + minCost);
    }

    private boolean determineOutcome(Character player, Character boss) {
        int playerDamage = Math.max(1, player.damage - boss.armor);
        int bossDamage = Math.max(1, boss.damage - player.armor);
        int turnsToPlayerDeath = (player.hp + bossDamage - 1) / bossDamage;
        int turnsToBossDeath = (boss.hp + playerDamage - 1) / playerDamage;
        return turnsToPlayerDeath >= turnsToBossDeath;
    }

    private void part2() {
        int maxCost = Integer.MIN_VALUE;
        for (Equipment weapon : WEAPONS) {
            for (Equipment armor : ARMOR) {
                for (int ringA = 0; ringA < RINGS.size() - 1; ringA++) {
                    for (int ringB = ringA + 1; ringB < RINGS.size(); ringB++) {
                        Character player = new Character(100, weapon.damage + RINGS.get(ringA).damage + RINGS.get(ringB).damage, armor.armor + RINGS.get(ringA).armor + RINGS.get(ringB).armor);
                        if (!determineOutcome(player, REAL_BOSS)) {
                            maxCost = Math.max(maxCost, weapon.cost + armor.cost + RINGS.get(ringA).cost + RINGS.get(ringB).cost);
                        }
                    }
                }
            }
        }
        System.out.println("Part 2: " + maxCost);
    }

    private record Character(int hp, int damage, int armor) {}

    private record Equipment(String name, int cost, int damage, int armor) {}

}

