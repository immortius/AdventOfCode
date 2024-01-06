package xyz.immortius.advent2017.day8;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.BiFunction;

public class Day8 {

    private static final String YEAR = "2017";
    private static final String DAY = "8";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day8().run();
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
            String[] parts = line.split("\\s+");
            String target = parts[0];
            int delta = Integer.parseInt(parts[2]);
            if ("dec".equals(parts[1])) {
                delta *= -1;
            }
            String checkReg = parts[4];
            int against = Integer.parseInt(parts[6]);
            BiFunction<Integer, Integer, Boolean> check =
            switch (parts[5]) {
                case "<" -> (a, b) -> a < b;
                case ">" -> (a, b) -> a > b;
                case "<=" -> (a, b) -> a <= b;
                case ">=" -> (a, b) -> a >= b;
                case "!=" -> (a, b) -> !Objects.equals(a, b);
                case "==" -> Objects::equals;
                default -> throw new RuntimeException("Unsupported operation " + parts[5]);
            };
            instructions.add(new Instruction(target, delta, checkReg, check, against));
        }
        return instructions;
    }

    private void part1(List<Instruction> instructions) {
        Map<String, Integer> registers = new HashMap<>();
        for (Instruction instruction : instructions) {
            if (instruction.check.apply(registers.computeIfAbsent(instruction.checkReg, (x) -> 0), instruction.against)) {
                registers.put(instruction.target, registers.getOrDefault(instruction.target, 0) + instruction.delta);
            }
        }
        int max = registers.values().stream().max(Integer::compareTo).get();
        System.out.println("Part 1: " + max);
    }

    private void part2(List<Instruction> instructions) {
        int max = 0;
        Map<String, Integer> registers = new HashMap<>();
        for (Instruction instruction : instructions) {
            if (instruction.check.apply(registers.computeIfAbsent(instruction.checkReg, (x) -> 0), instruction.against)) {
                int newValue = registers.getOrDefault(instruction.target, 0) + instruction.delta;
                max = Math.max(max, newValue);
                registers.put(instruction.target, newValue);
            }
        }
        System.out.println("Part 2: " + max);
    }

    public record Instruction(String target, int delta, String checkReg, BiFunction<Integer, Integer, Boolean> check, int against) {}

}

