package xyz.immortius.advent2016.day19;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Day19 {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day19().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        part1();
        part2();
    }

    private void part1() {
        Elf initial = new Elf(1);
        Elf lastLink = initial;
        for (int i = 2; i <= 3012210; i++) {
            lastLink.next = new Elf(i);
            lastLink = lastLink.next;
        }
        lastLink.next = initial;

        Elf current = initial;
        while (current.next != current) {
            current.removeNext();
            current = current.next;
        }

        System.out.println("Part 1: " + current.id);
    }


    private void part2() {
        int count = 3012210;

        List<Elf> elves = new ArrayList<>();
        Elf initial = new Elf(1);
        elves.add(initial);

        Elf lastLink = initial;
        for (int i = 2; i <= count; i++) {
            lastLink.next = new Elf(i);
            lastLink.next.prev = lastLink;
            lastLink = lastLink.next;
            elves.add(lastLink);
        }
        lastLink.next = initial;
        initial.prev = lastLink;

        int offset = elves.size() / 2;
        Elf opposite = elves.get(offset);

        int elvesRemaining = elves.size();
        while (elvesRemaining > 1) {
            opposite.remove();
            elvesRemaining--;
            opposite = opposite.next;
            if (elvesRemaining % 2 == 0) {
                opposite = opposite.next;
            }
        }

        System.out.println("Part 2: " + opposite.id);
    }


    public static class Elf {
        final int id;
        Elf next;
        Elf prev;

        public Elf(int id) {
            this.id = id;
        }

        public void removeNext() {
            next = next.next;
        }

        public void remove() {
            next.prev = prev;
            prev.next = next;
        }
    }
}

