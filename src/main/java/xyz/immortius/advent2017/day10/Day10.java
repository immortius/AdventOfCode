package xyz.immortius.advent2017.day10;

import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day10 {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day10().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {

        part1();
        part2();
    }

    private void part1() {
        int listSize = 256;
        int[] lengths = new int[] {106,118,236,1,130,0,235,254,59,205,2,87,129,25,255,118};

        int[] list = new int[listSize];
        for (int i = 0; i < listSize; i++) {
            list[i] = i;
        }

        int current = 0;
        int skipSize = 0;

        for (int length : lengths) {
            // reverse
            int start = current;
            int end = current + length - 1;
            while (start < end) {
                int tmp = list[start % listSize];
                list[start % listSize] = list[end % listSize];
                list[end % listSize] = tmp;
                start++;
                end--;
            }

            current = (current + length + skipSize++) % listSize;
        }

        System.out.println("Part 1: " + list[0] * list[1]);
    }

    private void part2() {
       System.out.println("Part 2: " + knotHash("106,118,236,1,130,0,235,254,59,205,2,87,129,25,255,118"));
    }

    @NotNull
    public static String knotHash(String input) {
        List<Integer> lengths = new ArrayList<>();
        for (char c : input.toCharArray()) {
            lengths.add((int)c);
        }
        lengths.addAll(Arrays.asList(17, 31, 73, 47, 23));

        int[] sparse = new int[256];
        for (int i = 0; i < 256; i++) {
            sparse[i] = i;
        }

        int current = 0;
        int skipSize = 0;

        for (int round = 0; round < 64; round++) {
            for (int length : lengths) {
                // reverse
                int start = current;
                int end = current + length - 1;
                while (start < end) {
                    int tmp = sparse[start % 256];
                    sparse[start % 256] = sparse[end % 256];
                    sparse[end % 256] = tmp;
                    start++;
                    end--;
                }

                current = (current + length + skipSize++) % 256;
            }
        }

        int[] dense = makeDense(sparse);
        StringBuilder result = new StringBuilder();
        for (int val : dense) {
            String stringVal = Integer.toHexString(val);
            if (stringVal.length() < 2) {
                result.append("0");
            }
            result.append(stringVal);
        }
        return result.toString();
    }

    @NotNull
    private static int[] makeDense(int[] list) {
        int[] dense = new int[16];
        for (int i = 0; i < 16; i ++) {
            int start = i * 16;
            int value = list[start];
            for (int j = 1; j < 16; j++) {
                value ^= list[start + j];
            }
            dense[i] = value;
        }
        return dense;
    }


}

