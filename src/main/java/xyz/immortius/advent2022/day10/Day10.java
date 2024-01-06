package xyz.immortius.advent2022.day10;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Day10 {
    public static void main(String[] args) throws IOException {
        new Day10().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day10/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        process(lines);
    }

    private void process(List<String> lines) {
        int X = 1;
        List<Integer> strength = new ArrayList<>();
        for (String line : lines) {
            if (line.equals("noop")) {
                strength.add(X);
            } else {
                strength.add(X);
                strength.add(X);
                String[] parts = line.split(" ");
                X += Integer.parseInt(parts[1]);
            }
        }

        StringBuilder builder = new StringBuilder();
        for (int row = 0; row < 6; row++) {
            for (int i = 0; i < 40; i++) {
                int current = strength.get(i + 40*row);
                if (Math.abs(i - current) < 2) {
                    builder.append("█");
                } else {
                    builder.append(" ");
                }
            }
            builder.append("\n");
        }
        System.out.println(builder.toString());
        int total = 0;
        for (int i = 20; i < strength.size(); i += 40) {
          //  System.out.println("Cycle " + i + " strength " + strength.get(i) + " = " + strength.get(i) * i);
            total += strength.get(i) * i;
        }
        System.out.println(total);
        System.out.println(strength.size());

    }

}

