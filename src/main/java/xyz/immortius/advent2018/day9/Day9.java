package xyz.immortius.advent2018.day9;

import java.io.IOException;
import java.util.Arrays;

public class Day9 {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day9().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        example();
        part1();
        part2();
    }

    private void example() {
        long winningScore = playMarbles(30, 5807);
        System.out.println("Example: " + winningScore);
    }

    private long playMarbles(int numPlayers, int numMarbles) {
        long[] scores = new long[numPlayers];

        Node current = new Node(0);
        for (int i = 1; i <= numMarbles; i++) {
            if (i % 23 == 0) {
                int player = i % numPlayers;
                current = current.getPrevious(7);
                scores[player] += current.value + i;
                current = current.remove();
            } else {
                current = current.next.addAfter(i);
            }
        }
        return Arrays.stream(scores).max().getAsLong();
    }

    private void part1() {
        long winningScore = playMarbles(423, 71944);
        System.out.println("Part 1: " + winningScore);
    }

    private void part2() {
        long winningScore = playMarbles(423, 7194400);
        System.out.println("Part 2: " + winningScore);
    }

    private static class Node {
        private Node prev;
        private Node next;

        private final int value;

        public Node(int value) {
            this.prev = this;
            this.next = this;
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public Node getNext() {
            return next;
        }

        public Node getPrevious(int steps) {
            Node result = this;
            for (int i = 0; i < steps; i++) {
                result = result.prev;
            }
            return result;
        }

        public Node addAfter(int value) {
            Node after = new Node(value);
            after.prev = this;
            after.next = next;
            this.next.prev = after;
            this.next = after;
            return after;
        }

        public Node remove() {
            prev.next = next;
            next.prev = prev;
            return next;
        }
    }



}

