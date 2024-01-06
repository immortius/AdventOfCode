package xyz.immortius.advent2016.day21;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day21 {

    //private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    private static final String YEAR = "2016";
    private static final String DAY = "21";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day21().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Command> commands = parse(lines);
        part1(commands);
        part2(commands);
    }

    private List<Command> parse(List<String> lines) {
        List<Command> commands = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith("swap position ")) {
                List<Integer> positions = Arrays.stream(line.substring("swap position ".length()).split(" with position ")).map(Integer::parseInt).toList();
                int posA = positions.get(0);
                int posB = positions.get(1);
                commands.add(new SwapPos(posA, posB));
            } else if (line.startsWith("swap letter ")) {
                String[] letters = line.substring("swap letter ".length()).split(" with letter ");
                commands.add(new SwapChar(letters[0].charAt(0), letters[1].charAt(0)));
            } else if (line.startsWith("rotate based ")) {
                char letter = line.charAt(line.length() - 1);
                commands.add(new RotateBased(letter));
            } else if (line.startsWith("rotate ")) {
                String subline = line.substring("rotate ".length());
                int dir = 1;
                if (subline.startsWith("left ")) {
                    subline = subline.substring("left ".length());
                    dir = -1;
                } else {
                    subline = subline.substring("right ".length());
                }
                String[] parts = subline.split(" ");
                int steps = dir * Integer.parseInt(parts[0]);
                commands.add(new Rotate(steps));
            } else if (line.startsWith("reverse ")) {
                List<Integer> positions = Arrays.stream(line.substring("reverse positions ".length()).split(" through ")).map(Integer::parseInt).toList();
                int posA = positions.get(0);
                int posB = positions.get(1);
                commands.add(new Reverse(posA, posB));
            } else if (line.startsWith("move ")) {
                List<Integer> positions = Arrays.stream(line.substring("move position ".length()).split(" to position ")).map(Integer::parseInt).toList();
                int posA = positions.get(0);
                int posB = positions.get(1);
                commands.add(new Move(posA, posB));
            } else {
                System.out.println("Unsupported command " + line);
            }
        }
        return commands;
    }

    private void part1(List<Command> commands) {
        char[] input = "abcdefgh".toCharArray();
        for (Command cmd : commands) {
            cmd.accept(input);
            System.out.println(new String(input));
        }
        System.out.println("Part 1: " + new String(input));
    }

    private void part2(List<Command> commands) {
        char[] input = "fbgdceah".toCharArray();

        for (int i = commands.size() - 1; i >= 0; i--) {
            commands.get(i).reverse().accept(input);
            System.out.println(new String(input));
        }
        System.out.println("Part 2: " + new String(input));
    }

    private static void swap(int posA, int posB, char[] x) {
        char a = x[posA];
        x[posA] = x[posB];
        x[posB] = a;
    }

    private static int indexOf(char[] array, char c) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == c) {
                return i;
            }
        }
        return -1;
    }

    private static void rotate(char[] x, int amount) {
        int rot = ((amount % x.length) + x.length) % x.length;
        char[] copy = Arrays.copyOf(x, x.length);
        System.arraycopy(copy, 0, x, rot, x.length - rot);
        System.arraycopy(copy, x.length - rot, x, 0, rot);
    }

    private interface Command {
        void accept(char[] x);

        Command reverse();
    }

    private static class SwapPos implements Command {
        private final int posA;
        private final int posB;

        SwapPos(int posA, int posB) {
            this.posA = posA;
            this.posB = posB;
        }

        @Override
        public void accept(char[] x) {
            swap(posA, posB, x);
        }

        @Override
        public Command reverse() {
            return this;
        }
    }

    private static class SwapChar implements Command {
        private final char a;
        private final char b;

        SwapChar(char a, char b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public void accept(char[] x) {
            int posA = indexOf(x, a);
            int posB = indexOf(x, b);
            swap(posA, posB, x);
        }

        @Override
        public Command reverse() {
            return this;
        }
    }

    private static class Rotate implements Command {
        private final int amount;

        Rotate(int amount) {
            this.amount = amount;
        }

        @Override
        public void accept(char[] x) {
            rotate(x, amount);
        }

        @Override
        public Command reverse() {
            return new Rotate(-amount);
        }
    }

    private static class RotateBased implements Command {
        private final char letter;

        RotateBased(char letter) {
            this.letter = letter;
        }


        @Override
        public void accept(char[] x) {
            int rot = indexOf(x, letter) + 1;
            if (rot >= 5) rot++;
            rotate(x, rot);
        }

        @Override
        public Command reverse() {
            return new ReverseRotateBased(letter);
        }
    }

    private static class ReverseRotateBased implements Command {

        private final char letter;

        public ReverseRotateBased(char letter) {
            this.letter = letter;
        }

        @Override
        public void accept(char[] x) {
            int finalPos = indexOf(x, letter);
            for (int i = x.length - 1; i >= 0; i--) {
                int rot = i + 1;
                if (rot >= 5) rot++;
                if ((i + rot) % x.length == finalPos) {
                    rotate(x, -rot);
                    return;
                }
            }
        }

        @Override
        public Command reverse() {
            return new RotateBased(letter);
        }
    }

    private static class Reverse implements Command {
        private final int posA;
        private final int posB;

        public Reverse(int a, int b) {
            this.posA = a;
            this.posB = b;
        }

        @Override
        public void accept(char[] x) {
            int a = posA;
            int b = posB;
            while (a < b) {
                swap(a, b, x);
                a++;
                b--;
            }
        }

        @Override
        public Command reverse() {
            return this;
        }
    }

    private static class Move implements Command {
        private final int posA;
        private final int posB;

        public Move(int posA, int posB) {
            this.posA = posA;
            this.posB = posB;
        }

        @Override
        public void accept(char[] x) {
            char a = x[posA];
            if (posB < posA) {
                System.arraycopy(x, posB, x, posB + 1, posA - posB);
            } else {
                System.arraycopy(x, posA + 1, x, posA, posB - posA);
            }
            x[posB] = a;
        }

        @Override
        public Command reverse() {
            return new Move(posB, posA);
        }
    }


}

