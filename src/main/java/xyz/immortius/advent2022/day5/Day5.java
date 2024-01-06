package xyz.immortius.advent2022.day5;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Day5 {
    public static void main(String[] args) throws IOException {
        new Day5().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day5/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        cran9001(lines);
    }

    private void cran9000(List<String> lines) {
        List<Deque<Character>> stacks = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            stacks.add(new ArrayDeque<>());
        }

        int stackBaseLineIndex = 0;
        while (!lines.get(stackBaseLineIndex).startsWith(" 1")) {
            stackBaseLineIndex++;
        }

        for (int level = stackBaseLineIndex - 1; level >= 0; level--) {
            String contents = lines.get(level);
            for (int i = 0; (i * 4 + 1) < contents.length(); i++) {
                char item = contents.charAt((i * 4) + 1);
                if (item != ' ') {
                    stacks.get(i).push(item);
                }
            }
        }

        List<String> commands = lines.subList(stackBaseLineIndex + 2, lines.size());
        for (String command : commands) {
            String[] tokens = command.split(" ");
            int amount = Integer.parseInt(tokens[1]);
            int from = Integer.parseInt(tokens[3]) - 1;
            int to = Integer.parseInt(tokens[5]) - 1;
            for (int i = 0; i < amount; i++) {
                stacks.get(to).push(stacks.get(from).pop());
            }
        }
        for (Deque<Character> stack: stacks) {
            System.out.println(stack.peek());
        }
    }

    private void cran9001(List<String> lines) {
        List<Deque<Character>> stacks = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            stacks.add(new ArrayDeque<>());
        }

        int stackBaseLineIndex = 0;
        while (!lines.get(stackBaseLineIndex).startsWith(" 1")) {
            stackBaseLineIndex++;
        }

        for (int level = stackBaseLineIndex - 1; level >= 0; level--) {
            String contents = lines.get(level);
            for (int i = 0; (i * 4 + 1) < contents.length(); i++) {
                char item = contents.charAt((i * 4) + 1);
                if (item != ' ') {
                    stacks.get(i).push(item);
                }
            }
        }

        List<String> commands = lines.subList(stackBaseLineIndex + 2, lines.size());
        for (String command : commands) {
            String[] tokens = command.split(" ");
            int amount = Integer.parseInt(tokens[1]);
            int from = Integer.parseInt(tokens[3]) - 1;
            int to = Integer.parseInt(tokens[5]) - 1;

            Deque<Character> temp = new ArrayDeque<>();
            for (int i = 0; i < amount; i++) {
                temp.push(stacks.get(from).pop());
            }
            for (int i = 0; i < amount; i++) {
                stacks.get(to).push(temp.pop());
            }
        }
        for (Deque<Character> stack : stacks) {
            System.out.println(stack.peek());
        }
    }
}
