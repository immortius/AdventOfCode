package xyz.immortius.advent2018.day21;

import com.google.common.io.CharStreams;
import xyz.immortius.advent2018.day19.Instruction;
import xyz.immortius.advent2018.day19.Computer;
import xyz.immortius.advent2018.day19.OpCodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Day21 {

    private static final String YEAR = "2018";
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

        Computer computer = parse(lines);
        //part1(new IntCodeComputer(computer));
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
        int shortest = 0;
        int shortestStepCount = Integer.MAX_VALUE;
        computer.setMaxSteps(Short.MAX_VALUE);
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            Computer instance = new Computer(computer);
            instance.setRegister(0, i);
            instance.run();
            if (instance.getStepsRun() < shortestStepCount) {
                shortest = i;
                shortestStepCount = instance.getStepsRun();
                computer.setMaxSteps(shortestStepCount);
                System.out.println("" + i + " (" + shortestStepCount + ")");
            }
        }

        System.out.println("Part 1: " + shortest + " (" + shortestStepCount + ")");
    }

    private void part2(Computer computer) {
//        IntCodeComputer instance = new IntCodeComputer(computer);
//        instance.setRegister(0, 1212);
//        instance.run();
//        System.out.println("Part 2: " + instance.getStepsRun());
        Computer instance2 = new Computer(computer);
        instance2.setRegister(0, 0);
        instance2.run();
        System.out.println("Part 2: " + instance2.getStepsRun());

    }



}

