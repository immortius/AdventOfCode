package xyz.immortius.advent2015.day10;

import java.io.IOException;

public class Day10 {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day10().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() {
        process("3113322113");
    }

    private void process(String input) {
        String value = input;
        for (int i = 0; i < 40; i++) {
            value = lookAndSay(value);
        }
        System.out.println("Part 1: " + value.length());
        for (int i = 0; i < 10; i++) {
            value = lookAndSay(value);
        }
        System.out.println("Part 2: " + value.length());
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

}

