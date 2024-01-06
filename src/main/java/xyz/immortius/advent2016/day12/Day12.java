package xyz.immortius.advent2016.day12;

import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day12 {

    private static final String YEAR = "2016";
    private static final String DAY = "12";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day12().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Instruction> instructions = parse(lines);
        part1(instructions);
        part2(instructions);
    }

    private List<Instruction> parse(List<String> lines) {
        List<Instruction> instructions = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(" ");
            String cmd = parts[0];
            if ("cpy".equals(cmd)) {
                if (parts[1].charAt(0) >= 'a' && parts[1].charAt(0) <= 'z') {
                    instructions.add(new Instruction(cmd + "reg", parts[2].charAt(0), parts[1].charAt(0), 0, 0));
                } else {
                    instructions.add(new Instruction(cmd + "int", parts[2].charAt(0), ' ', Integer.parseInt(parts[1]), 0));
                }
            } else if ("jnz".equals(cmd)) {
                if (parts[1].charAt(0) >= 'a' && parts[1].charAt(0) <= 'z') {
                    instructions.add(new Instruction(cmd + "reg", parts[1].charAt(0), ' ', Integer.parseInt(parts[2]), 0));
                } else {
                    instructions.add(new Instruction(cmd + "int", ' ', ' ', Integer.parseInt(parts[2]), Integer.parseInt(parts[1])));
                }
            } else {
                instructions.add(new Instruction(cmd, parts[1].charAt(0), ' ', 0, 0));
            }
        }
        return instructions;
    }

    private void part1(List<Instruction> instructions) {
        Computer computer = new Computer(instructions);
        computer.run();
        System.out.println("Part 1: " + computer.getRegisterValue('a'));
    }

    private void part2(List<Instruction> instructions) {
        Computer computer = new Computer(instructions);
        computer.registers.put('c', 1);
        computer.run();
        System.out.println("Part 2: "+ computer.getRegisterValue('a'));
    }

    public static class Computer {
        private final List<Instruction> instructions;
        private int pointer = 0;
        private Map<Character, Integer> registers = new HashMap<>();

        public Computer(List<Instruction> instructions) {
            this.pointer = 0;
            this.instructions = ImmutableList.copyOf(instructions);
        }

        public void run() {
            while (pointer >= 0 && pointer < instructions.size()) {
                Instruction instruction = instructions.get(pointer);
                switch (instruction.cmd) {
                    case "cpyreg" -> registers.put(instruction.register1, getRegisterValue(instruction.register2));
                    case "cpyint" -> registers.put(instruction.register1, instruction.value1);
                    case "inc" -> registers.put(instruction.register1, getRegisterValue(instruction.register1) + 1);
                    case "dec" -> registers.put(instruction.register1, getRegisterValue(instruction.register1) - 1);
                    case "jnzint" -> {
                        if (instruction.value2 != 0) {
                            pointer += instruction.value1 - 1;
                        }
                    }
                    case "jnzreg" -> {
                        if (getRegisterValue(instruction.register1) != 0) {
                            pointer += instruction.value1 - 1;
                        }
                    }
                    default -> {
                        System.out.println("Unknown command " + instruction.cmd);
                        pointer = -1;
                    }
                }
                pointer++;
            }
        }

        public int getRegisterValue(char register) {
            return registers.getOrDefault(register, 0);
        }

    }

    public record Instruction(String cmd, char register1, char register2, int value1, int value2) {}

}

