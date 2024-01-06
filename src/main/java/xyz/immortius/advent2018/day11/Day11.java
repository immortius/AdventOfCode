package xyz.immortius.advent2018.day11;

import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector3i;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Day11 {


    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day11().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {

        example();
        part1();
        //example2();
        part2();
    }

    private void example() {
        int serial = 18;

        int[][] map =  buildCellMap(serial);
        Vector3i best = findBestCell(map, 3, 3);

        System.out.println("Example: " + best.x + ", " + best.y);
    }

    private int[][] buildCellMap(int serial) {
        int[][] result = new int[301][301];
        for (int y = 1; y <= 300; y++) {
            for (int x = 1; x <= 300; x++) {
                result[x][y] = powerLevel(x, y, serial);
            }
        }
        return result;
    }

    @NotNull
    private Vector3i findBestCell(int[][] map, int minSize, int maxSize) {
        Vector3i best = new Vector3i(1,1, 1);
        int bestValue = -100;
        for (int size = minSize; size <= maxSize; size++) {
            for (int y = 1; y <= 301 - size; y++) {
                for (int x = 1; x <= 301 - size; x++) {
                    int value = 0;
                    for (int a = 0; a < size; a++) {
                        for (int b = 0; b < size; b++) {
                            value += map[x + a][y + b];
                        }
                    }
                    if (value > bestValue) {
                        best.x = x;
                        best.y = y;
                        best.z = size;
                        bestValue = value;
                    }
                }
            }
        }
        return best;
    }

    private int powerLevel(int x, int y, int serial) {
        int rackId = x + 10;
        int power = rackId * y;
        power += serial;
        power *= rackId;
        power = (power / 100) % 10;
        power -= 5;
        return power;
    }

    private void part1() {
        int serial = 6042;

        int[][] map =  buildCellMap(serial);
        Vector3i best = findBestCell(map, 3, 3);

        System.out.println("Part 1: " + best.x + ", " + best.y);
    }

    private void example2() {
        int serial = 18;

        int[][] map =  buildCellMap(serial);
        Vector3i best = findBestCell(map, 1, 300);

        System.out.println("Example: " + best.x + ", " + best.y + ", " + best.z);
    }

    private void part2() {
        int serial = 6042;

        int[][] map =  buildCellMap(serial);
        Vector3i best = findBestCell(map, 1, 300);

        System.out.println("Part 2: " + best.x + ", " + best.y + ", " + best.z);
    }



}

