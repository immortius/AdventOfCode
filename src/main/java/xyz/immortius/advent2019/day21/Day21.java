package xyz.immortius.advent2019.day21;

import com.google.common.io.CharStreams;
import xyz.immortius.advent2019.day2.IntCodeComputer;
import xyz.immortius.advent2019.day2.IntCodeHelper;
import xyz.immortius.advent2019.day2.IntCodeInputStream;
import xyz.immortius.advent2019.day2.IntCodeOutputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Day21 {

    private static final String YEAR = "2019";
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

        long[] program = IntCodeHelper.parse(lines);

        IntCodeComputer computer = new IntCodeComputer(program, new FixedAsciiInputStream(
                "NOT A J",
                "NOT B T",
                "OR T J",
                "NOT C T",
                "OR T J",
                "AND D J",
                "WALK"
        ), new AsciiOutputStream());
        computer.run();

        computer = new IntCodeComputer(program, new FixedAsciiInputStream(
                "NOT A J",
                "NOT B T",
                "OR T J",
                "NOT C T",
                "OR T J",
                "AND D J",
                "NOT E T",
                "NOT T T",
                "OR H T",
                "AND T J",
                "RUN"
        ), new AsciiOutputStream());
        computer.run();

    }

    private static class FixedAsciiInputStream implements IntCodeInputStream {
        private final Deque<Long> inputs;

        public FixedAsciiInputStream(String ... instructions) {
            this.inputs = new ArrayDeque<>();
            for (String instruction : instructions) {
                for (char c : instruction.toCharArray()) {
                    inputs.add((long)c);
                }
                inputs.add(10L);
            }
        }

        @Override
        public long send() throws InterruptedException {
            if (inputs.isEmpty()) {
                throw new InterruptedException("No more input");
            }
            return inputs.pop();
        }
    }

    private static class AsciiOutputStream implements IntCodeOutputStream {

        @Override
        public void receive(long value) throws InterruptedException {
            if (value < 255) {
                System.out.print((char) value);
            } else {
                System.out.println(value);
            }
        }
    }

}

