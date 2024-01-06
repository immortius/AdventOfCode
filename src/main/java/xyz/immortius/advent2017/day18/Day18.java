package xyz.immortius.advent2017.day18;

import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.*;

public class Day18 {

    private static final String YEAR = "2017";
    private static final String DAY = "18";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day18().run();
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
        System.out.println("Part 1: " + computer.getLastRecoveredFrequency());
    }

    private void part2(List<Instruction> instructions) {
        DualComputer computerA = new DualComputer(instructions, 0);
        DualComputer computerB = new DualComputer(instructions, 1);
        computerA.setRemote(computerB);
        computerB.setRemote(computerA);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(computerA::run);
        executorService.submit(computerB::run);
        executorService.shutdown();
        try {
            executorService.awaitTermination(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            System.out.println("Interrupted");
        }

        System.out.println("Part 2: " + computerB.sends);
    }

    public static class DualComputer {
        private static final Object SHARED_LOCK = new Object();

        private final long id;
        private DualComputer remote;

        private final List<Instruction> instructions;
        private int pointer;
        private Map<Character, Long> registers = new LinkedHashMap<>();

        private volatile boolean waiting = false;
        private final Semaphore semaphore = new Semaphore(0);
        private final Deque<Message> sendQueue = new ArrayDeque<>();
        private int sends = 0;

        public DualComputer(List<Instruction> instructions, long id) {
            this.instructions = ImmutableList.copyOf(instructions);
            registers.put('p', id);
            this.id = id;
        }

        public void setRemote(DualComputer remote) {
            this.remote = remote;
        }

        public void run() {
            while (pointer >= 0 && pointer < instructions.size()) {
                Instruction inst = instructions.get(pointer);
                switch (inst.cmd) {
                    case "snd" -> enqueue(inst.elements[0].getValue(registers));
                    case "set" -> inst.elements[0].writeValue(registers, inst.elements[1].getValue(registers));
                    case "add" -> inst.elements[0].writeValue(registers, inst.elements[0].getValue(registers) + inst.elements[1].getValue(registers));
                    case "mul" -> inst.elements[0].writeValue(registers, inst.elements[0].getValue(registers) * inst.elements[1].getValue(registers));
                    case "mod" -> inst.elements[0].writeValue(registers, inst.elements[0].getValue(registers) % inst.elements[1].getValue(registers));
                    case "rcv" -> {
                        synchronized (SHARED_LOCK) {
                            waiting = true;
                            if (remote.isWaiting() && sendQueue.isEmpty()) {
                                System.out.println("Deadlock detected");
                                sendQueue.push(new Message());
                                semaphore.release();
                                return;
                            }
                        }
                        try {
                            inst.elements[0].writeValue(registers, remote.receive());
                        } catch (RuntimeException e) {
                            return;
                        }
                        synchronized (SHARED_LOCK) {
                            waiting = false;
                        }
                    }
                    case "jgz" -> {
                        if (inst.elements[0].getValue(registers) > 0) pointer += inst.elements[1].getValue(registers) - 1;
                    }
                }
                pointer++;
            }
        }

        private synchronized boolean isWaiting() {
            return waiting && sendQueue.isEmpty();
        }

        private void enqueue(long value) {
            sends++;
            synchronized (this) {
                sendQueue.addLast(new Message(value));
            }
            semaphore.release();
        }

        private long receive() {
            try {
                semaphore.acquire();
                synchronized (this) {
                    Message msg = sendQueue.pop();
                    if (msg.deadlock) {
                        throw new RuntimeException();
                    }
                    return msg.value;
                }
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
                throw new RuntimeException(e);
            }
        }


    }

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
        private long lastFrequency;
        private long lastRecoveredFrequency;

        public Computer(List<Instruction> instructions) {
            this.instructions = ImmutableList.copyOf(instructions);
        }

        public void run() {
            while (pointer >= 0 && pointer < instructions.size()) {
                Instruction inst = instructions.get(pointer);
                switch (inst.cmd) {
                    case "snd" -> lastFrequency = inst.elements[0].getValue(registers);
                    case "set" -> inst.elements[0].writeValue(registers, inst.elements[1].getValue(registers));
                    case "add" -> inst.elements[0].writeValue(registers, inst.elements[0].getValue(registers) + inst.elements[1].getValue(registers));
                    case "mul" -> inst.elements[0].writeValue(registers, inst.elements[0].getValue(registers) * inst.elements[1].getValue(registers));
                    case "mod" -> inst.elements[0].writeValue(registers, inst.elements[0].getValue(registers) % inst.elements[1].getValue(registers));
                    case "rcv" -> {
                        if (inst.elements[0].getValue(registers) != 0) {
                            lastRecoveredFrequency = lastFrequency;
                            return;
                        }
                    }
                    case "jgz" -> {
                        if (inst.elements[0].getValue(registers) > 0) pointer += inst.elements[1].getValue(registers) - 1;
                    }
                }
                pointer++;
            }
        }

        public long getLastFrequency() {
            return lastFrequency;
        }

        public long getLastRecoveredFrequency() {
            return lastRecoveredFrequency;
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

