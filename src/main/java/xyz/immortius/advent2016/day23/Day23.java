package xyz.immortius.advent2016.day23;

import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;
import xyz.immortius.advent2016.day12.Day12;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day23 {

    private static final String YEAR = "2016";
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

        List<Instruction> instructions = parse(lines);
        part1(instructions);
        part2(instructions);
    }

    private List<Instruction> parse(List<String> lines) {
        List<Instruction> instructions = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split(" ");
            String cmd = parts[0];
            if (parts.length == 2) {
                instructions.add(new Instruction(cmd, parseElement(parts[1])));
            } else {
                instructions.add(new Instruction(cmd, parseElement(parts[1]), parseElement(parts[2])));
            }
        }
        return instructions;
    }

    private Element parseElement(String text) {
        if (text.charAt(0) >= 'a' && text.charAt(0) <= 'z') {
            return new Element(text.charAt(0));
        } else {
            return new Element(Integer.parseInt(text));
        }
    }

    private void part1(List<Instruction> instructions) {
        Computer computer = new Computer(instructions);
        computer.registers.put('a', 7L);
        computer.run();
        System.out.println("Part 1: " + computer.getRegisterValue('a'));
    }

    private void part2(List<Instruction> instructions) {
        Computer computer = new Computer(instructions);
        computer.registers.put('a', 12L);
        computer.run();
        System.out.println("Part 1: " + computer.getRegisterValue('a'));
    }

    public static class Computer {
        private final List<Instruction> instructions;
        private int pointer = 0;
        private Map<Character, Long> registers = new HashMap<>();

        public Computer(List<Instruction> instructions) {
            this.pointer = 0;
            this.instructions = new ArrayList<>(instructions);
        }

        public void run() {
            while (pointer >= 0 && pointer < instructions.size()) {
                Instruction instruction = instructions.get(pointer);
                switch (instruction.cmd) {
                    case "tgl" -> {
                        int value = (int)(pointer + instruction.elements[0].getValue(registers));
                        System.out.println("Toggle " + value);

                        if (value < instructions.size()) {
                            instructions.set(value, instructions.get(value).toggle());
                        }
                    }
                    case "cpy" -> {
                        instruction.elements[1].writeValue(registers, instruction.elements[0].getValue(registers));
                    }
                    case "inc" -> instruction.elements[0].writeValue(registers, instruction.elements[0].getValue(registers) + 1);
                    case "dec" -> instruction.elements[0].writeValue(registers, instruction.elements[0].getValue(registers) - 1);
                    case "jnz" -> {
                        if (instruction.elements[0].getValue(registers) != 0) {
                            pointer += instruction.elements[1].getValue(registers) - 1;
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


        public long getRegisterValue(char register) {
            return registers.getOrDefault(register, 0L);
        }

    }

    public static class Element {

        private final char register;
        private final int value;

        public Element(char register) {
            this.register = register;
            this.value = 0;
        }

        public Element(int value) {
            this.value = value;
            this.register = '\0';
        }

        boolean isRegister() {
            return register != 0;
        }

        long getValue(Map<Character, Long> registers) {
            if (isRegister()) {
                return registers.get(register);
            } else {
                return value;
            }
        }

        void writeValue(Map<Character, Long> registers, long value) {
            if (isRegister()) {
                registers.put(register, value);
            }
        }
    }


    public record Instruction(String cmd, Element... elements) {
        public Instruction toggle() {
            return switch (cmd) {
                case "inc" -> new Instruction("dec", elements);
                case "jnz" -> new Instruction("cpy", elements);
                case "cpy" -> new Instruction("jnz", elements);
                default -> new Instruction("inc", elements);
            };
        }
    }

}

