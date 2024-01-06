package xyz.immortius.advent2019.day19;

import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.advent2019.day2.IntCodeComputer;
import xyz.immortius.advent2019.day2.IntCodeHelper;
import xyz.immortius.advent2019.day2.IntCodeInputStream;
import xyz.immortius.advent2019.day2.IntCodeOutputStream;
import xyz.immortius.util.CircularBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Day19 {

    private static final String YEAR = "2019";
    private static final String DAY = "19";
    private static final boolean REAL_INPUT = true;

    private long[] program;

    public static void main(String[] args) throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        new Day19().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException, InterruptedException {
        readProgram();
        scanInitialArea();
        Vector2ic vector2ic = minRangeForSantasShip();
        System.out.println("Found at " + vector2ic.x() + "," + vector2ic.y() + " for value " + (10000 * vector2ic.x() + vector2ic.y()));
    }

    private void readProgram() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }
        program = IntCodeHelper.parse(lines.get(0));
    }

    private void scanInitialArea() throws InterruptedException {
        QueuedOutputStream out = new QueuedOutputStream();
        long total = 0;
        for (long y = 0; y < 50; y++) {
            for (long x = 0; x < 50; x++) {
                IntCodeComputer computer = new IntCodeComputer(program, new FixedInputStream(x, y), out);
                computer.run();
                long result = out.read();
                total += result;
                System.out.print(result);
            }
            System.out.println();
        }
        System.out.println("Total scanned in 50*50 = " + total);
    }

    private Vector2ic minRangeForSantasShip() {
        int target = 100;
        CircularBuffer<Long> starts = CircularBuffer.create(target - 1);
        CircularBuffer<Long> ends = CircularBuffer.create(target - 1);

        long y = 0;
        long minX = 0;
        long buffer = 5;

        while (true) {
            long start = findStart(y, minX, buffer);
            if (start != -1) {
                long end = findEnd(y, start);
                if (end - start + 1 >= target && starts.size() == target - 1 && start >= starts.get(0) && start <= ends.get(0) && start + target - 1 >= starts.get(0) && start + target - 1 <= ends.get(0)) {
                    return new Vector2i((int)start, (int)(y - target + 1));
                }
                starts.add(start);
                ends.add(end);
                minX = start;
            } else {
                starts.add(-1L);
                ends.add(-1L);
            }
            y++;
        }

    }

    private long findEnd(long y, long start) {
        long next = start + 1;
        while (scan(next, y)) {
            next++;
        }
        return next - 1;
    }

    private long findStart(long y, long minX, long buffer) {
        for (long x = minX; x < minX + buffer; x++) {
            if (scan(x, y)) {
                return x;
            }
        }
        return -1;
    }

    private boolean scan(long x, long y) {
        try {
            QueuedOutputStream out = new QueuedOutputStream();
            IntCodeComputer computer = new IntCodeComputer(program, new FixedInputStream(x, y), out);
            computer.run();
            return out.read() == 1L;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private static class FixedInputStream implements IntCodeInputStream {
        private Deque<Long> inputs;

        public FixedInputStream(Long ... inputs) {
            this.inputs = new ArrayDeque<>();
            this.inputs.addAll(Arrays.asList(inputs));
        }


        @Override
        public long send() throws InterruptedException {
            if (inputs.isEmpty()) {
                throw new InterruptedException("No more input");
            }
            return inputs.pop();
        }
    }

    private static class QueuedOutputStream implements IntCodeOutputStream {
        private BlockingDeque<Long> outputs = new LinkedBlockingDeque<>();

        @Override
        public void receive(long value) throws InterruptedException {
            outputs.putLast(value);
        }

        public long read() throws InterruptedException {
            return outputs.takeFirst();
        }
    }


}

