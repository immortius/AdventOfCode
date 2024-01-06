package xyz.immortius.advent2015.day15;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day15 {

    private final Pattern linePattern = Pattern.compile("(?<name>[a-zA-Z]+): capacity (?<capacity>-?[0-9]+), durability (?<durability>-?[0-9]+), flavor (?<flavor>-?[0-9]+), texture (?<texture>-?[0-9]+), calories (?<calories>-?[0-9]+)");

    private static final String YEAR = "2015";
    private static final String DAY = "15";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day15().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Ingredient> ingredients = parse(lines);
        part1(ingredients);
        part2(ingredients);
    }

    private List<Ingredient> parse(List<String> lines) {
        List<Ingredient> ingredients = new ArrayList<>();
        for (String line : lines) {
            Matcher matcher = linePattern.matcher(line);
            if (matcher.matches()) {
                ingredients.add(new Ingredient(matcher.group("name"), Integer.parseInt(matcher.group("capacity")), Integer.parseInt(matcher.group("durability")), Integer.parseInt(matcher.group("flavor")), Integer.parseInt(matcher.group("texture")), Integer.parseInt(matcher.group("calories"))));
            } else {
                System.out.println("Failed to match - " + line);
            }
        }
        return ingredients;
    }

    private void part1(List<Ingredient> ingredients) {
        int[] amounts = new int[ingredients.size()];
        long highest = 0;

        PermutationsIterator iterator = new PermutationsIterator(100, amounts.length);
        while (iterator.hasNext()) {
            highest = Math.max(score(ingredients, iterator.next(), false), highest);
        }

        System.out.println("Part 1: " + highest);
    }

    private long score(List<Ingredient> ingredients, int[] amounts, boolean checkCalories) {
        if (checkCalories) {
            int calories = 0;
            for (int i = 0; i < ingredients.size(); i++) {
                calories += ingredients.get(i).calories * amounts[i];
            }
            if (calories != 500) {
                return 0;
            }
        }
        long[] properties = new long[4];
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            int count = amounts[i];
            properties[0] += (long) count * ingredient.capacity;
            properties[1] += (long) count * ingredient.durability;
            properties[2] += (long) count * ingredient.flavor;
            properties[3] += (long) count * ingredient.texture;
        }
        for (int i = 0; i < properties.length; i++) {
            if (properties[i] < 0) {
                properties[i] = 0;
            }
        }
        return properties[0] * properties[1] * properties[2] * properties[3];
    }

    private void part2(List<Ingredient> ingredients) {
        int[] amounts = new int[ingredients.size()];
        long highest = 0;

        PermutationsIterator iterator = new PermutationsIterator(100, amounts.length);
        while (iterator.hasNext()) {
            highest = Math.max(score(ingredients, iterator.next(), true), highest);
        }

        System.out.println("Part 2: " + highest);
    }


    public record Ingredient(String name, int capacity, int durability, int flavor, int texture, int calories) {}

    public static class PermutationsIterator implements Iterator<int[]> {
        private int[] amounts;
        private int iteratingIndex;

        public PermutationsIterator(int total, int buckets) {
            amounts = new int[buckets];
            amounts[0] = total;
        }

        @Override
        public boolean hasNext() {
            return amounts[0] > 0;
        }

        @Override
        public int[] next() {
            int[] result = Arrays.copyOf(amounts, amounts.length);

            if (iteratingIndex > 0 && amounts[iteratingIndex] == 0) {
                while (iteratingIndex > 0 && amounts[iteratingIndex] == 0) {
                    iteratingIndex--;
                }
            }

            amounts[iteratingIndex]--;
            int subTotal = 1;
            for (int i = iteratingIndex + 1; i < amounts.length; i++) {
                subTotal += amounts[i];
                amounts[i] = 0;
            }
            amounts[iteratingIndex + 1] = subTotal;
            if (iteratingIndex + 1 < amounts.length - 1) {
                iteratingIndex++;
            }

            return result;
        }
    }

}

