package xyz.immortius.advent2017.day23;

import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day23 {

    private static final String YEAR = "2017";
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
            instructions.add(new Instruction(line.substring(0, 3), Arrays.stream(line.substring(4).split("\\s+")).map(Element::new).toArray(Element[]::new)));
        }
        return instructions;
    }

    private void part1(List<Instruction> instructions) {
        Computer computer = new Computer(instructions);
        computer.run();
        System.out.println("Part 1: " + computer.muls);
    }

    private void part2(List<Instruction> instructions) {
//        Computer computer = new Computer(instructions);
//        computer.registers.put('a', 1L);
//        computer.run();
        System.out.println("Part 2: " + calc());
    }

    public long calc() {
        long h = 0;

        long b = 105700 - 17;
        long c = 122700;

        System.out.println((c - b) / 17);

        do {
            b += 17;
            boolean found = false;
            for (long d = 2; d < b; d++) {
                if (b % d == 0) {
                    found = true;
                    break;
                }
            }
//            long d = 2;
//            do {
//                long e = 2;
//                do {
//                    if (d * e == b) {
//                        found = true;
//                        break;
//                    }
//                    e++;
//                } while (e != b);
//                d++;
//            } while (d != b);
            if (found) {
                h++;
            }
        } while (b != c);
        return h;
    }

    //set f 1
    //set d 2
    //set e 2
    //set g d
    //mul g e
    //sub g b
    //jnz g 2
    //set f 0
    //sub e -1
    //set g e
    //sub g b
    //jnz g -8
    //sub d -1
    //set g d
    //sub g b
    //jnz g -13
    //jnz f 2
    //sub h -1
    //set g b
    //sub g c
    //jnz g 2
    //jnz 1 3
    //sub b -17
    //jnz 1 -23

    private static class Message {
        private long value;
        private boolean deadlock;

        public Message(long value) {
            this.value = value;
            this.deadlock = false;
        }

        public Message() {
            this.value = 0L;
            this.deadlock = true;
        }
    }

    public static class Computer {
        private final List<Instruction> instructions;
        private int pointer;
        private Map<Character, Long> registers = new LinkedHashMap<>();
        private int muls = 0;

        public Computer(List<Instruction> instructions) {
            this.instructions = ImmutableList.copyOf(instructions);
        }

        public void run() {
            while (pointer >= 0 && pointer < instructions.size()) {
                Instruction inst = instructions.get(pointer);
                switch (inst.cmd) {
                    case "set" -> inst.elements[0].writeValue(registers, inst.elements[1].getValue(registers));
                    case "add" -> inst.elements[0].writeValue(registers, inst.elements[0].getValue(registers) + inst.elements[1].getValue(registers));
                    case "sub" -> inst.elements[0].writeValue(registers, inst.elements[0].getValue(registers) - inst.elements[1].getValue(registers));
                    case "mul" -> {
                        inst.elements[0].writeValue(registers, inst.elements[0].getValue(registers) * inst.elements[1].getValue(registers));
                        muls++;
                    }
                    case "mod" -> inst.elements[0].writeValue(registers, inst.elements[0].getValue(registers) % inst.elements[1].getValue(registers));
                    case "jnz" -> {
                        if (inst.elements[0].getValue(registers) != 0) pointer += inst.elements[1].getValue(registers) - 1;
                    }
                }
                pointer++;
            }
        }

        public int getMuls() {
            return muls;
        }
    }

    public record Instruction(String cmd, Element... elements) {

        @Override
        public String toString() {
            return "Instruction{" +
                    "cmd='" + cmd + '\'' +
                    ", elements=" + Arrays.toString(elements) +
                    '}';
        }
    }

    public static class Element {

        private final char register;
        private final long value;

        public Element(String value) {
            if (value.length() == 1 && value.charAt(0) >= 'a' && value.charAt(0) <= 'z') {
                this.register = value.charAt(0);
                this.value = 0;
            } else {
                this.value = Long.parseLong(value);
                this.register = '\0';
            }
        }

        public Element(char register) {
            this.register = register;
            this.value = 0;
        }

        public Element(long value) {
            this.value = value;
            this.register = '\0';
        }

        public boolean isRegister() {
            return register != 0;
        }

        public long getValue(Map<Character, Long> registers) {
            if (isRegister()) {
                return registers.getOrDefault(register, 0L);
            } else {
                return value;
            }
        }

        public void writeValue(Map<Character, Long> registers, long value) {
            if (isRegister()) {
                registers.put(register, value);
            }
        }

        @Override
        public String toString() {
            if (register != 0) {
                return Character.toString(register);
            } else {
                return Long.toString(value);
            }
        }
    }


}

