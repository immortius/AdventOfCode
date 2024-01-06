package xyz.immortius.advent2017.day3;

import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.advent2022.day15.SpiralIterator;

import java.io.IOException;
import java.util.*;

public class Day3 {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day3().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {

        part1();
        part2();
    }

    private void part1() {
        int input = 289326;
        int steps = input - 1;
        SpiralIterator iterator = new SpiralIterator(0,0);
        while (steps > 0) {
            iterator.next();
            steps--;
        }
        System.out.println("Part 1: " + new Vector2i(iterator.getX(), iterator.getY()).gridDistance(0,0));
    }

    private void part2() {
        int target = 289326;
        List<Vector2ic> adjacentOffsets = new ArrayList<>(Arrays.asList(new Vector2i(1,0), new Vector2i(1, 1), new Vector2i(0, 1), new Vector2i(-1, 1), new Vector2i(-1, 0), new Vector2i(-1, -1), new Vector2i(0, -1), new Vector2i(1, -1)));

        Map<Vector2ic, Integer> valueMap = new HashMap<>();
        valueMap.put(new Vector2i(), 1);

        SpiralIterator iterator = new SpiralIterator(0,0);
        int lastValue = 0;
        while (lastValue < target) {
            iterator.next();
            int total = 0;
            Vector2i pos = new Vector2i();
            for (Vector2ic offset : adjacentOffsets) {
                pos.set(iterator.getX(), iterator.getY()).add(offset);
                total += valueMap.getOrDefault(pos, 0);
            }
            valueMap.put(new Vector2i(iterator.getX(), iterator.getY()), total);
            lastValue = total;
        }
        System.out.println("Part 2: " + lastValue);
    }



}

