package xyz.immortius.advent2017.day6;

import java.io.IOException;
import java.util.*;

public class Day6 {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day6().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        example();
        part1();
    }

    private void example() {
        List<Integer> banks = new ArrayList<>(Arrays.asList(0, 2, 7, 0));
        Set<List<Integer>> seenStates = new HashSet<>();
        int steps = 0;
        while (seenStates.add(banks)) {
            steps++;
            banks = reallocate(banks);
        }
        System.out.println("Example: " + steps);
    }

    private void part1() {
        List<Integer> banks = new ArrayList<>(Arrays.asList(11, 11, 13, 7, 0, 15, 5, 5, 4, 4, 1, 1, 7, 1, 15, 11));
        Set<List<Integer>> seenStates = new HashSet<>();
        int steps = 0;
        while (seenStates.add(banks)) {
            steps++;
            banks = reallocate(banks);
        }
        System.out.println("Part 1: " + steps);
        steps = 0;
        seenStates.clear();
        while (seenStates.add(banks)) {
            steps++;
            banks = reallocate(banks);
        }
        System.out.println("Part 2: " + steps);
    }

    private List<Integer> reallocate(List<Integer> banks) {
        int highestIndex = 0;
        int highestSize = banks.get(0);
        for (int i = 1; i < banks.size(); i++) {
            if (banks.get(i) > highestSize) {
                highestIndex = i;
                highestSize = banks.get(i);
            }
        }

        int redistAmount = highestSize / banks.size();
        int remainder = highestSize % banks.size();
        banks.set(highestIndex, 0);
        for (int i = 0; i < banks.size(); i++) {
            int index = (highestIndex + 1 + i) % banks.size();
            banks.set(index, banks.get(index) + redistAmount + ((remainder > 0) ? 1 : 0));
            remainder--;
        }

        return banks;
    }


}

