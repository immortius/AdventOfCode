package xyz.immortius.advent2022.day13;

import com.google.common.base.Joiner;
import com.google.common.io.CharStreams;
import org.checkerframework.checker.units.qual.A;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.util.Direction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day13 {
    public static void main(String[] args) throws IOException {
        new Day13().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day13/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        process(lines);
    }

    private void process(List<String> lines) {
        int total = 0;

        List<PacketItem> packets = new ArrayList<>();

        for (int index = 0; index <= lines.size() / 3; index++) {
            int leftIndex = index * 3;
            int rightIndex = leftIndex + 1;
            PacketItem left = parse(lines.get(leftIndex));
            PacketItem right = parse(lines.get(rightIndex));
            packets.add(left);
            packets.add(right);

            int result = left.compareTo(right);
            if (result < 1) {
                total += index + 1;
            }

        }
        System.out.println(total);

        PacketItem indicatorPacketA = new PacketList(Arrays.asList(new PacketInteger(2)));
        PacketItem indicatorPacketB = new PacketList(Arrays.asList(new PacketInteger(6)));
        packets.add(indicatorPacketA);
        packets.add(indicatorPacketB);
        packets.sort(Comparable::compareTo);
        System.out.println((packets.indexOf(indicatorPacketA) + 1) * (packets.indexOf(indicatorPacketB) + 1));
    }

    private PacketItem parse(String s) {
        Deque<List<PacketItem>> items = new ArrayDeque<>();
        items.push(new ArrayList<>());
        StringBuilder number = new StringBuilder();
        for (char c : s.toCharArray()) {
            switch (c) {
                case '[' -> items.push(new ArrayList<>());
                case ']' -> {
                    if (number.length() > 0) {
                        items.peek().add(new PacketInteger(Integer.parseInt(number.toString())));
                        number.setLength(0);
                    }
                    List<PacketItem> list = items.pop();
                    items.peek().add(new PacketList(list));
                }
                case ',' -> {
                    if (!number.isEmpty()) {
                        items.peek().add(new PacketInteger(Integer.parseInt(number.toString())));
                        number.setLength(0);
                    }
                }
                default -> number.append(c);
            }
        }
        PacketItem result = items.pop().get(0);
        if (!result.toString().equals(s)) {
            System.out.println("Mismatch: " + s + " != " + result);
        }
        return result;
    }

    interface PacketItem extends Comparable<PacketItem> {
        boolean isList();

        int value();

        List<PacketItem> getList();
    }

    private static class PacketInteger implements PacketItem {

        int value;

        PacketInteger(int value) {
            this.value = value;
        }

        @Override
        public boolean isList() {
            return false;
        }

        @Override
        public int value() {
            return value;
        }

        @Override
        public List<PacketItem> getList() {
            return Collections.singletonList(this);
        }

        @Override
        public int compareTo(PacketItem item) {
            if (item.isList()) {
                return new PacketList(getList()).compareTo(item);
            } else {
                if (value < item.value()) {
                    return -1;
                } else if (value > item.value()) {
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public String toString() {
            return Integer.toString(value);
        }

    }

    private static class PacketList implements PacketItem {
        private final List<PacketItem> list;

        public PacketList(List<PacketItem> subPackets) {
            this.list = subPackets;
        }

        public boolean isList() {
            return true;
        }

        @Override
        public List<PacketItem> getList() {
            return list;
        }

        @Override
        public int value() {
            throw new IllegalStateException();
        }

        @Override
        public int compareTo(PacketItem item) {
            List<PacketItem> otherList = item.getList();

            System.out.println("Comparing " + this + " to " + item);
            for (int i = 0; i < list.size() && i < otherList.size(); i++) {
                int result = list.get(i).compareTo(otherList.get(i));
                if (result != 0) {
                    return result;
                }
            }

            if (list.size() < otherList.size()) {
                System.out.println("Left side exhausted");
                return -1;
            } else if (list.size() > otherList.size()) {
                System.out.println("Right side exhausted");
                return 1;
            }
            return 0;
        }

        @Override
        public String toString() {
            return "[" + Joiner.on(',').join(list) + "]";
        }
    }




}

