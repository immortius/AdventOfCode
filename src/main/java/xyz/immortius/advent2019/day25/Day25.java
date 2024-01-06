package xyz.immortius.advent2019.day25;

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

public class Day25 {

    private static final String YEAR = "2019";
    private static final String DAY = "25";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        new Day25().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException, InterruptedException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        long[] program = IntCodeHelper.parse(lines);

        AsciiInterface asciiInterface = new AsciiInterface();
        asciiInterface
                .queue("south", "west")
                .queue("take fuel cell")
                .queue("west")
                .queue("take easter egg")
                .queue("east", "east", "north", "east")
                .queue("take ornament")
                .queue("east")
                .queue("take hologram")
                .queue("east")
                .queue("take dark matter")
                .queue("north", "north", "east")
                .queue("take klein bottle")
                .queue("north")
                .queue("take hypercube")
                .queue("north")
        ;
        IntCodeComputer computer = new IntCodeComputer(program, asciiInterface, asciiInterface);
        computer.run();
        asciiInterface.printRemainder();
    }

    private class AsciiInterface implements IntCodeOutputStream, IntCodeInputStream {
        private StringBuilder outputBuffer = new StringBuilder();
        private Deque<Long> inputBuffer = new ArrayDeque<>();
        private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        @Override
        public long send() throws InterruptedException {
            if (inputBuffer.isEmpty()) {
                return 10L;
            }
            return inputBuffer.pop();
        }

        @Override
        public void receive(long value) throws InterruptedException {
            outputBuffer.append((char)value);
            if (outputBuffer.toString().endsWith("Command?")) {
                System.out.println(outputBuffer);
                outputBuffer.setLength(0);
                if (inputBuffer.isEmpty()) {
                    try {
                        String line = reader.readLine();
                        for (char c : line.toCharArray()) {
                            inputBuffer.addLast((long) c);
                        }
                        inputBuffer.addLast(10L);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new InterruptedException();
                    }
                }
            } else if (outputBuffer.length() > 1000) {
                System.out.print(outputBuffer);
                outputBuffer.setLength(0);
            }
        }

        public void printRemainder() {
            System.out.println(outputBuffer);
            outputBuffer.setLength(0);
        }

        public AsciiInterface queue(String ... values) {
            for (String value : values) {
                for (char c : value.toCharArray()) {
                    inputBuffer.addLast((long) c);
                }
                inputBuffer.addLast(10L);
            }
            return this;
        }
    }
}

