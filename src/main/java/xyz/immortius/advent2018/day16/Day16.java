package xyz.immortius.advent2018.day16;

import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day16 {

    private static final String YEAR = "2018";
    private static final String DAY = "16";
    private static final boolean REAL_INPUT = true;

    private List<OpCode> OP_CODES = ImmutableList.of(
            new OpCode("addr", true, true, Integer::sum),
            new OpCode("addi", true, false, Integer::sum),
            new OpCode("mulr", true, true, (a, b) -> a * b),
            new OpCode("muli", true, false, (a, b) -> a * b),
            new OpCode("banr", true, true, (a, b) -> a & b),
            new OpCode("bani", true, false, (a, b) -> a & b),
            new OpCode("borr", true, true, (a, b) -> a | b),
            new OpCode("bori", true, false, (a, b) -> a | b),
            new OpCode("setr", true, false, (a, b) -> a),
            new OpCode("seti", false, false, (a, b) -> a),
            new OpCode("gtir", false, true, (a, b) -> (a > b) ? 1 : 0),
            new OpCode("gtri", true, false, (a, b) -> (a > b) ? 1 : 0),
            new OpCode("gtrr", true, true, (a, b) -> (a > b) ? 1 : 0),
            new OpCode("eqir", false, true, (a, b) -> (a == b) ? 1 : 0),
            new OpCode("eqri", true, false, (a, b) -> (a == b) ? 1 : 0),
            new OpCode("eqrr", true, true, (a, b) -> (a == b) ? 1 : 0)
    );

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day16().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        Input input = parse(lines);
        part1(input.testCases);
        part2(input);
    }

    private Input parse(List<String> lines) {
        List<TestCase> testCases = new ArrayList<>();
        List<Instruction> program = new ArrayList<>();
        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            String line = lines.get(lineIndex);
            if (line.startsWith("Before: [")) {
                int[] precondition = readArray(line.substring("Before: [".length(), line.length() - 1));
                Instruction instruction = Instruction.create(readArray(lines.get(lineIndex + 1)));
                String afterLine = lines.get(lineIndex + 2);
                int[] postCondition = readArray(afterLine.substring("After:  [".length(), afterLine.length() - 1));
                testCases.add(new TestCase(precondition, instruction, postCondition));
                lineIndex += 3;
            } else if (!line.isEmpty()) {
                program.add(Instruction.create(readArray(line)));
            }
        }

        return new Input(testCases, program);
    }

    private record Input(List<TestCase> testCases, List<Instruction> program) {}

    private int[] readArray(String input) {
        return Arrays.stream(input.split(",?\s+")).mapToInt(Integer::parseInt).toArray();
    }

    private void part1(List<TestCase> testCases) {
        int threeOrMoreMatches = 0;
        for (TestCase test : testCases) {
            int matches = 0;
            for (OpCode code : OP_CODES) {
                int[] registers = Arrays.copyOf(test.precondition, test.precondition.length);
                code.apply(registers, test.instruction);
                if (Arrays.equals(registers, test.postCondition)) {
                    matches++;
                }
            }
            if (matches >= 3) {
                threeOrMoreMatches++;
            }
        }
        System.out.println("Part 1: " + threeOrMoreMatches);
    }

    private void part2(Input input) {
        Set<Integer> determined = new HashSet<>();
        List<OpCode> unsolvedOpcodes = new ArrayList<>(OP_CODES);

        OpCode[] opCodeMap = new OpCode[16];
        for (TestCase test : input.testCases) {
            if (determined.contains(test.instruction.opCode)) {
                continue;
            }
            List<OpCode> matches = new ArrayList<>();
            for (OpCode code : unsolvedOpcodes) {
                int[] registers = Arrays.copyOf(test.precondition, test.precondition.length);
                code.apply(registers, test.instruction);
                if (Arrays.equals(registers, test.postCondition)) {
                    matches.add(code);
                }
            }
            if (matches.size() == 1 && determined.add(test.instruction.opCode)) {
                OpCode code = matches.get(0);
                unsolvedOpcodes.remove(code);
                opCodeMap[test.instruction.opCode] = code;
                System.out.println("Solved " + test.instruction.opCode + " as " + code.toString());
            }
        }

        int[] registers = new int[4];
        for (Instruction instruction : input.program) {
            opCodeMap[instruction.opCode].apply(registers, instruction);
        }

        System.out.println("Part 2: " + registers[0]);
    }


    public record TestCase(int[] precondition, Instruction instruction, int[] postCondition) {}

    private record Instruction(int opCode, int inA, int inB, int outC) {
        public static Instruction create(int[] array) {
            return new Instruction(array[0], array[1], array[2], array[3]);
        }
    }

    public record OpCode(String name, boolean fromRegisterA, boolean fromRegisterB, OpFunc function) {
        void apply(int[] registers, Instruction instruction) {
            int a = (fromRegisterA) ? registers[instruction.inA] : instruction.inA;
            int b = (fromRegisterB) ? registers[instruction.inB] : instruction.inB;
            registers[instruction.outC] = function.apply(a, b);
        }

        @Override
        public String toString() {
            return "OpCode{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    public interface OpFunc {
        int apply(int a, int b);
    }

}

