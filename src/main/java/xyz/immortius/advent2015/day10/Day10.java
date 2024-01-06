package xyz.immortius.advent2015.day10;

import com.google.common.io.CharStreams;
import xyz.immortius.advent2022.day25.Day25;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        String value = "3113322113";
        for (int i = 0; i < 50; i++) {
            value = lookAndSay(value);
        }
        System.out.println("Part 1: " + value.length());
    }

    private String lookAndSay(String value) {
        char c = value.charAt(0);
        int repetitions = 1;
        StringBuilder result = new StringBuilder();
        for (int i = 1; i < value.length(); i++) {
            if (c == value.charAt(i)) {
                repetitions++;
            } else {
                result.append(repetitions);
                result.append(c);
                c = value.charAt(i);
                repetitions = 1;
            }
        }
        result.append(repetitions);
        result.append(c);
        return result.toString();
    }

    private void part2() {

    }



}

