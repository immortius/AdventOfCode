package xyz.immortius.advent2019.day23;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.io.CharStreams;
import xyz.immortius.advent2019.day2.IntCodeComputer;
import xyz.immortius.advent2019.day2.IntCodeHelper;
import xyz.immortius.advent2019.day2.IntCodeInputStream;
import xyz.immortius.advent2019.day2.IntCodeOutputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.*;

public class Day23 {

    private static final String YEAR = "2019";
    private static final String DAY = "23";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        new Day23().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException, InterruptedException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        long[] program = IntCodeHelper.parse(lines);

        NISController controller = new NISController();
        List<NISInterface> nisInterfaces = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            NISInterface nis = new NISInterface(program);
            controller.register(nis);
            nisInterfaces.add(nis);
        }
        controller.start();
    }

    public record Packet(int address, long x, long y) {}

    private static class NISController {

        private final List<NISInterface> interfaceMap = new ArrayList<>();
        private ExecutorService executorService;

        private Packet natPacket;
        private long lastPacketTime;

        public NISController() {
        }

        public synchronized void register(NISInterface computer) {
            int id = interfaceMap.size();
            interfaceMap.add(computer);
            computer.register(this, id);
        }

        public void start() throws InterruptedException {
            executorService = Executors.newFixedThreadPool(interfaceMap.size());
            lastPacketTime = System.currentTimeMillis();
            for (NISInterface nis : interfaceMap) {
                executorService.submit(nis::start);
            }
            long lastValue = -1;
            while (natPacket == null || natPacket.y != lastValue) {
                if (natPacket != null && System.currentTimeMillis() - lastPacketTime > 200 && noMessagesQueued()) {
                    System.out.println("Sending NAT packet");
                    lastValue = natPacket.y;
                    send(255, new Packet(0, natPacket.x, natPacket.y));
                    natPacket = null;
                } else {
                    Thread.sleep(100);
                }
            }
            System.out.println("Duplicate nat y: " + lastValue);
            shutdown();
        }

        private boolean noMessagesQueued() {
            for (NISInterface nisInterface : interfaceMap) {
                if (nisInterface.queuedData() > 0) {
                    return false;
                }
            }
            return true;
        }

        public void shutdown() {
            executorService.shutdownNow();
        }

        public synchronized void send(int origin, Packet packet) {
            lastPacketTime = System.currentTimeMillis();
            if (packet.address == 255) {
                natPacket = packet;
            } else {
                NISInterface target = interfaceMap.get(packet.address);
                target.receivePacket(packet);
                System.out.println(origin + " --[" + packet.x + "," + packet.y + "]-->" + packet.address);
            }
        }
    }

    private static class NISInterface implements IntCodeOutputStream, IntCodeInputStream {


        private final IntCodeComputer computer;
        private final Deque<Long> inputBuffer = new ConcurrentLinkedDeque<>();
        private final List<Long> outputBuffer = new ArrayList<>();

        private NISController controller;
        private int id;

        public NISInterface(long[] program) {
            computer = new IntCodeComputer(program, this, this);
        }

        public void start() {
            computer.run();
        }

        public void register(NISController controller, int id) {
            this.controller = controller;
            inputBuffer.push((long) id);
            this.id = id;
        }

        public synchronized void receivePacket(Packet packet) {
            inputBuffer.addLast(packet.x);
            inputBuffer.addLast(packet.y);
        }

        @Override
        public synchronized long send() throws InterruptedException {
            if (inputBuffer.isEmpty()) {
                return -1L;
            }
            return inputBuffer.pop();
        }

        @Override
        public void receive(long value) throws InterruptedException {
            outputBuffer.add(value);
            if (outputBuffer.size() == 3) {
                controller.send(id, new Packet(outputBuffer.get(0).intValue(), outputBuffer.get(1), outputBuffer.get(2)));
                outputBuffer.clear();
            }
        }

        public int queuedData() {
            return inputBuffer.size();
        }
    }

}

