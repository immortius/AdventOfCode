package xyz.immortius.advent2015.day1;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Day1 {
    public static void main(String[] args) throws IOException {
        new Day1().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2015/day1/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        part1(lines.get(0));
        part2(lines.get(0));
    }


    private void part1(String input) {
        int floor = 0;
        for (char c : input.toCharArray()) {
            switch (c) {
                case '(' -> floor++;
                case ')' -> floor--;
            }
        }
        System.out.println("Part 1: " + floor);
    }

    private void part2(String input) {
        int floor = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '(' -> floor++;
                case ')' -> floor--;
            }
            if (floor == -1) {
                System.out.println("Part 2: " + (i + 1));
                break;
            }
        }
    }

}

