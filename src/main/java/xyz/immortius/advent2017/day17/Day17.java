package xyz.immortius.advent2017.day17;

import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Day17 {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day17().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        //example();
        part1();
        part2();
    }

    private void example() {
        int steps = 3;
        List<Integer> buffer = generateBuffer(steps, 2018);

        int index = (buffer.indexOf(2017) + 1) % buffer.size();
        System.out.println("Example: " + buffer.get(index));
    }

    @NotNull
    private List<Integer> generateBuffer(int steps, int size) {
        List<Integer> buffer = new ArrayList<>(size);
        buffer.add(0);
        int pos = 0;
        for (int i = 1; i < size; i++) {
            pos = (pos + steps) % buffer.size() + 1;
            if (pos == 1) {
                System.out.println("Inserted: " + i);
            }
            buffer.add(pos, i);
        }
        return buffer;
    }

    private void part1() {
        int steps = 394;
        List<Integer> buffer = generateBuffer(steps, 2018);

        int index = (buffer.indexOf(2017) + 1) % buffer.size();
        System.out.println("Part 1: " + buffer.get(index));
    }

    private void part2() {
        int steps = 394;

        int afterZero = 0;

        int pos = 0;
        for (int i = 1; i < 50_000_001; i++) {
            pos = (pos + steps) % i + 1;
            if (pos == 1) {
                System.out.println("Inserting " + i);
                afterZero = i;
            }
        }
        System.out.println("Part 2: " + afterZero);
    }



}

