package xyz.immortius.advent2018.day14;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Day14 {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day14().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        part1();
        part2();
    }

    private void part1() {
        int target = 920831;
        List<Integer> recipes = new ArrayList<>(target + 10);
        recipes.add(3);
        recipes.add(7);

        int[] elves = {0, 1};
        while (recipes.size() < target + 10) {
            int score = recipes.get(elves[0]) + recipes.get(elves[1]);

            if (score >= 10) {
                recipes.add(1);
            }
            recipes.add(score % 10);

            for (int i = 0; i < elves.length; i++) {
                elves[i] = (elves[i] + recipes.get(elves[i]) + 1) % recipes.size();
            }
        }

        StringBuilder result = new StringBuilder();
        for (int i = target; i < target + 10; i++) {
            result.append(recipes.get(i));
        }

        System.out.println("Part 1: " + result);
    }

    private void part2() {
        int[] target = {9,2,0,8,3,1};
        List<Integer> recipes = new ArrayList<>();
        recipes.add(3);
        recipes.add(7);

        int[] elves = {0, 1};
        while (true) {
            int score = recipes.get(elves[0]) + recipes.get(elves[1]);

            if (score >= 10) {
                recipes.add(1);
                if (check(recipes, target)) {
                    break;
                }
            }
            recipes.add(score % 10);
            if (check(recipes, target)) {
                break;
            }

            for (int i = 0; i < elves.length; i++) {
                elves[i] = (elves[i] + recipes.get(elves[i]) + 1) % recipes.size();
            }
        }


        System.out.println("Part 2: " + (recipes.size() - target.length));
    }

    private boolean check(List<Integer> recipes, int[] target) {
        int start = recipes.size() - target.length;
        if (recipes.size() < target.length) {
            return false;
        }
        for (int i = 0; i < target.length; i++) {
            if (recipes.get(start + i) != target[i]) {
                return false;
            }
        }
        return true;
    }


}

