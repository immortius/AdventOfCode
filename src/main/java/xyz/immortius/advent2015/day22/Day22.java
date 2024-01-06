package xyz.immortius.advent2015.day22;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.io.IOException;
import java.util.*;

public class Day22 {

    private static final boolean REAL_INPUT = false;

    private static final NPC REAL_BOSS = new NPC(51, 9, 0);
    private static final NPC EXAMPLE_BOSS = new NPC(13, 8, 0);

    private static final List<Spell> SPELLS = ImmutableList.of(
            new Spell("Poison", 173, 0,0,new Effect(6, 0, 3, 0)),
            new Spell("Magic Missile", 53, 4, 0, null),
            new Spell("Drain", 73, 2, 2, null),
            new Spell("Shield", 113, 0,0,new Effect(6, 7,0,0)),
            new Spell("Recharge", 229, 0,0, new Effect(5, 0, 0, 101))
    );

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day22().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        part1();
        part2();
    }

    private void part1() {
        State bestState = minimiseManaToVictory(new State(new NPC(50, 0, 500), REAL_BOSS, Collections.emptyMap(), 0, false, Collections.emptyList()), Integer.MAX_VALUE);
        int minCost = bestState.manaSpent;
        System.out.println("Part 1: " + minCost);
    }

    private void part2() {
        State bestState = minimiseManaToVictory(new State(new NPC(49, 0, 500), REAL_BOSS, Collections.emptyMap(), 0, true, Collections.emptyList()), Integer.MAX_VALUE);
        int minCost = bestState.manaSpent;
        System.out.println("Part 2: " + minCost);
    }

    private State minimiseManaToVictory(State state, int bestSoFar) {
        if (state.boss.hp <= 0 || state.player.hp <= 0) {
            return state;
        }
        State result = new State(new NPC(0,0,0), state.boss, Collections.emptyMap(), bestSoFar, state.hardMode, Collections.emptyList());
        for (Spell spell : SPELLS) {
            if (state.player.mana > spell.cost && !state.activeEffects.containsKey(spell.effect) && state.manaSpent + spell.cost < result.manaSpent) {
                State newState = state.castSpell(spell);
                newState = minimiseManaToVictory(newState, result.manaSpent);
                if (newState.boss.hp <= 0 && newState.player.hp > 0 && newState.manaSpent < result.manaSpent) {
                    result = newState;
                }
            }
        }
        return result;
    }

    private record NPC(int hp, int damage, int mana) {
    }

    private record Spell(String name, int cost, int immediateDamage, int heal, Effect effect) {}

    private record Effect(int effectLength, int effectArmor, int effectDamage, int effectMana) {}

    private record State(NPC player, NPC boss, Map<Effect, Integer> activeEffects, int manaSpent, boolean hardMode, List<Spell> spellHistory) {
        public State castSpell(Spell spell) {
            List<Spell> newSpellHistory = new ArrayList<>(spellHistory);
            newSpellHistory.add(spell);

            Map<Effect, Integer> activeEffects = new LinkedHashMap<>(this.activeEffects);
            // Immediate effects
            int bossHp = boss.hp - spell.immediateDamage;
            int playerHp = player.hp + spell.heal;
            int playerMana = player.mana - spell.cost;
            if (spell.effect != null) {
                activeEffects.put(spell.effect, spell.effect.effectLength);
            }
            if (bossHp <= 0) {
                return new State(new NPC(playerHp, player.damage, playerMana), new NPC(bossHp, boss.damage, boss.mana), this.activeEffects, this.manaSpent + spell.cost, hardMode, newSpellHistory);
            }
            // Boss turn
            int armor = 0;
            Map<Effect, Integer> remainingEffects = new LinkedHashMap<>();
            for (Map.Entry<Effect, Integer> effect : activeEffects.entrySet()) {
                bossHp -= effect.getKey().effectDamage;
                playerMana += effect.getKey().effectMana;
                armor += effect.getKey().effectArmor;
                if (effect.getValue() - 1 > 0) {
                    remainingEffects.put(effect.getKey(), effect.getValue() - 1);
                }
            }
            if (bossHp <= 0) {
                return new State(new NPC(playerHp, player.damage, playerMana), new NPC(bossHp, boss.damage, boss.mana), remainingEffects, this.manaSpent + spell.cost, hardMode, newSpellHistory);
            } else {
                playerHp -= Math.max(1, boss.damage - armor);
            }

            if (playerHp <= 0) {
                return new State(new NPC(playerHp, player.damage, playerMana), new NPC(bossHp, boss.damage, boss.mana), remainingEffects, this.manaSpent + spell.cost, hardMode, newSpellHistory);
            }

            // Start of player turn
            if (hardMode) {
                playerHp--;
                if (playerHp <= 0) {
                    return new State(new NPC(playerHp, player.damage, playerMana), new NPC(bossHp, boss.damage, boss.mana), ImmutableMap.copyOf(remainingEffects), this.manaSpent + spell.cost, hardMode, newSpellHistory);
                }
            }

            Map<Effect, Integer> finalEffects = new LinkedHashMap<>();
            for (Map.Entry<Effect, Integer> effect : remainingEffects.entrySet()) {
                bossHp -= effect.getKey().effectDamage;
                playerMana += effect.getKey().effectMana;
                if (effect.getValue() - 1 > 0) {
                    finalEffects.put(effect.getKey(), effect.getValue() - 1);
                }
            }
            return new State(new NPC(playerHp, player.damage, playerMana), new NPC(bossHp, boss.damage, boss.mana), ImmutableMap.copyOf(finalEffects), this.manaSpent + spell.cost, hardMode, newSpellHistory);

        }
    }


}

