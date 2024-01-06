package xyz.immortius.advent2019.day14;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;
import xyz.immortius.util.NewtonEstimation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day14 {

    private static final String YEAR = "2019";
    private static final String DAY = "14";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day14().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        ListMultimap<String, Recipe> requirements = parse(lines);
        part1(requirements);
    }

    private ListMultimap<String, Recipe> parse(List<String> lines) {
        ListMultimap<String, Recipe> requirements = ArrayListMultimap.create();
        for (String line : lines) {
            String[] inOut = line.split(" => ");
            String[] parts = inOut[1].split(" ");
            int amount = Integer.parseInt(parts[0]);
            String item = parts[1];
            List<Ingredient> ingredients = new ArrayList<>();
            for (String ing : inOut[0].split(", ")) {
                String[] ingParts = ing.split(" ");
                int ingAmount = Integer.parseInt(ingParts[0]);
                ingredients.add(new Ingredient(ingParts[1], ingAmount));
            }
            requirements.put(item, new Recipe(ingredients, item, amount));
        }
        return requirements;
    }

    private void part1(ListMultimap<String, Recipe> input) {
        Set<String> items = new LinkedHashSet<>(input.keySet());
        SetMultimap<String, String> dependencyMap = HashMultimap.create();
        for (String item : items) {
            List<Recipe> recipes = input.get(item);
            if (recipes.size() > 1) {
                System.out.println(item + " has multiple paths");
            }
            for (Recipe recipe : recipes) {
                for (Ingredient ingredient : recipe.ingredients) {
                    dependencyMap.put(recipe.output, ingredient.item);
                }
            }
        }
        boolean addedMore = true;
        while (addedMore) {
            addedMore = false;
            for (String item : items) {
                for (String dep : new ArrayList<>(dependencyMap.get(item))) {
                    for (String depDep : new ArrayList<>(dependencyMap.get(dep))) {
                        addedMore |= dependencyMap.put(item, depDep);
                    }
                }
            }
        }
        List<String> dependencyOrder = new ArrayList<>();
        Deque<String> toAdd = new ArrayDeque<>(items);
        while (!toAdd.isEmpty()) {
            String next = toAdd.pollFirst();
            boolean later = false;
            for (String remaining : toAdd) {
                if (dependencyMap.get(remaining).contains(next)) {
                    later = true;
                    break;
                }
            }
            if (later) {
                toAdd.addLast(next);
            } else {
                dependencyOrder.add(next);
            }
        }

        System.out.println(dependencyOrder);
        System.out.println("Part 1: " + calcOreRequired(input, dependencyOrder, 1));
        NewtonEstimation estimation = new NewtonEstimation((a) -> calcOreRequired(input, dependencyOrder, a));
        long fuel = estimation.findValue(1, 1000000000000L, 1000000000000L);
        // 1000000000000L
        // 999135666

        System.out.println("Part 2: " + fuel);
    }


    @NotNull
    private long calcOreRequired(ListMultimap<String, Recipe> input, List<String> dependencyOrder, long fuel) {
        Map<String, Long> requirements = new LinkedHashMap<>();
        requirements.put("FUEL", fuel);
        for (String item : dependencyOrder) {
            Long amountDesired = requirements.remove(item);
            if (amountDesired != null) {
                Recipe recipe = input.get(item).get(0);
                long multiplier = amountDesired / recipe.amount + Long.signum(amountDesired % recipe.amount);
                for (Ingredient ingredient : recipe.ingredients) {
                    long required = requirements.getOrDefault(ingredient.item, 0L);
                    required += ingredient.amount * multiplier;
                    requirements.put(ingredient.item, required);

                }
            }
        }
        return requirements.get("ORE");
    }

    public record Recipe(List<Ingredient> ingredients, String output, int amount) {}

    public record Ingredient(String item, int amount) {}
}

