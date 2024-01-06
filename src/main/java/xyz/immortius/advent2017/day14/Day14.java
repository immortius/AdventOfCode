package xyz.immortius.advent2017.day14;

import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.advent2017.day10.Day10;
import xyz.immortius.util.Direction;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class Day14 {

    private static final String YEAR = "2017";
    private static final String DAY = "9";
    private static final boolean REAL_INPUT = false;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day14().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        part1();
    }

    private void part1() {
        String input = "hwlqcszp";
        boolean[][] field = new boolean[128][128];
        int used = 0;
        for (int row = 0; row < 128; row++) {
            String hash = Day10.knotHash(input + "-" + row);
            for (int charIndex = 0; charIndex < hash.length(); charIndex++) {
                int val = Integer.parseInt(Character.toString(hash.charAt(charIndex)), 16);
                for (int bit = 0; bit < 4; bit++) {
                    boolean b = (val & (0x1 << (3 - bit))) != 0;
                    field[row][bit + 4 * charIndex] = b;
                    if (b) {
                        System.out.print('1');
                        used++;
                    } else {
                        System.out.print('0');
                    }
                }
            }
            System.out.println();
        }

        System.out.println("Part 1: " + used) ;

        int uniqueGroups = 0;
        for (int row = 0; row < 128; row++) {
            for (int col = 0; col < 128; col++) {
                if (field[row][col]) {
                    uniqueGroups++;
                    removeAdj(field, row, col);
                }
            }
        }

        System.out.println("Part 2: " + uniqueGroups) ;

    }

    private void removeAdj(boolean[][] field, int row, int col) {
        Deque<Vector2ic> open = new ArrayDeque<>();
        Set<Vector2ic> closed = new HashSet<>();

        open.push(new Vector2i(row, col));

        while (!open.isEmpty()) {
            Vector2ic pos = open.pop();
            if (field[pos.x()][pos.y()]) {
                field[pos.x()][pos.y()] = false;
                for (Direction dir : Direction.values()) {
                    Vector2i adj = pos.add(dir.toVector(), new Vector2i());
                    if (adj.x >= 0 && adj.y >= 0 && adj.x < 128 && adj.y < 128 && closed.add(adj)) {
                        open.add(adj);
                    }
                }
            }
        }
    }



}

