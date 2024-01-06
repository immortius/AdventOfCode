package xyz.immortius.advent2018.day19;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Day19 {

    private static final String YEAR = "2018";
    private static final String DAY = "19";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day19().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        Computer computer = parse(lines);
        part1(new Computer(computer));
        part2(new Computer(computer));
    }

    private Computer parse(List<String> lines) {
        Computer computer = new Computer(6);
        for (String line : lines) {
            if (line.startsWith("#ip")) {
                computer.setInstructionRegister(Integer.parseInt(line.substring(4)));
            } else {
                String[] parts = line.split("\s+");
                if (parts.length >= 4) {
                    Instruction instruction = new Instruction(OpCodes.parse(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
                    computer.addInstruction(instruction);
                }
            }
        }
        return computer;
    }

    private void part1(Computer computer) {
        computer.run();
        System.out.println("Part 1: " + computer.getRegister(0));
    }

    private void part2(Computer computer) {
        computer.setRegister(0, 1);
        computer.run();
        System.out.println("Part 2: " + computer.getRegister(0));
    }



}

