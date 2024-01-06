package xyz.immortius.advent2015.day23;

import com.google.common.io.CharStreams;
import com.google.common.primitives.UnsignedInteger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day23 {

    private static final String YEAR = "2015";
    private static final String DAY = "23";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day23().run();
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
        List<Command> result = new ArrayList<>();
        for (String line : lines) {
            String cmd = line.substring(0, 3);
            String[] parts = line.substring(4).split(", ");
            char register = '\0';
            int offset = 0;
            if (parts[0].length() == 1) {
                register = parts[0].charAt(0);
            } else {
                offset = Integer.parseInt(parts[0]);
            }
            if (parts.length > 1) {
                offset = Integer.parseInt(parts[1]);
            }
            result.add(new Command(cmd, register, offset));
        }
        return result;
    }

    private void part1(List<Command> commands) {
        Computer computer = new Computer(commands);
        computer.run();
        System.out.println(computer);
        System.out.println("Part 1: " + computer.get('b'));
    }

    private void part2(List<Command> commands) {
        Computer computer = new Computer(commands);
        computer.set('a', UnsignedInteger.ONE);
        computer.run();
        System.out.println(computer);
        System.out.println("Part 2: " + computer.get('b'));
    }

    private static class Computer {
        private final UnsignedInteger[] registers;
        private final List<Command> commands;
        private int pointer = 0;

        public Computer(List<Command> commands) {
            registers = new UnsignedInteger[2];
            for (int i = 0; i < 2; i++) {
                registers[i] = UnsignedInteger.ZERO;
            }
            this.commands = commands;
        }

        public UnsignedInteger get(char register) {
            return registers[register - 'a'];
        }

        public void set(char register, UnsignedInteger value) {
            registers[register - 'a'] = value;
        }

        public void run() {
            while (pointer >= 0 && pointer < commands.size()) {
                Command command = commands.get(pointer);
                switch (command.instruction) {
                    case "hlf" -> {
                        set(command.register, get(command.register).dividedBy(UnsignedInteger.valueOf(2)));
                        pointer++;
                    }
                    case "tpl" -> {
                        set(command.register, get(command.register).times(UnsignedInteger.valueOf(3)));
                        pointer++;
                    }
                    case "inc" -> {
                        set(command.register, get(command.register).plus(UnsignedInteger.ONE));
                        pointer++;
                    }
                    case "jmp" -> {
                        pointer += command.offset;
                    }
                    case "jie" -> {
                        if (get(command.register).mod(UnsignedInteger.valueOf(2)).intValue() == 0) {
                            pointer += command.offset;
                        } else {
                            pointer++;
                        }
                    }
                    case "jio" -> {
                        if (get(command.register).intValue() == 1) {
                            pointer += command.offset;
                        } else {
                            pointer++;
                        }
                    }
                }
            }
        }

        @Override
        public String toString() {
            return "Computer{" +
                    "registers=" + Arrays.toString(registers) +
                    ", pointer=" + pointer +
                    '}';
        }
    }

    private record Command(String instruction, char register, int offset) {}


}

