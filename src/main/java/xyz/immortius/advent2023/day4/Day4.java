package xyz.immortius.advent2023.day4;

import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Day4 {

    private static final String YEAR = "2023";
    private static final String DAY = "4";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day4().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Card> cards = parse(lines);
        part1(cards);
        part2(cards);
    }

    private List<Card> parse(List<String> lines) {
        List<Card> cards = new ArrayList<>();
        for (String line : lines) {
            String[] idRest = line.split(" *: +");
            int id = Integer.parseInt(idRest[0].split(" +")[1]);
            String[] winningAndNumbers = idRest[1].split(" +\\| +");
            Set<Integer> winningNumbers = Arrays.stream(winningAndNumbers[0].split("[ ]+")).map(Integer::parseInt).collect(Collectors.toSet());
            Set<Integer> numbers = Arrays.stream(winningAndNumbers[1].split("[ ]+")).map(Integer::parseInt).collect(Collectors.toSet());
            cards.add(new Card(id, numbers, winningNumbers));
        }
        return cards;
    }

    private void part1(List<Card> cards) {
        long total = cards.stream().map(Card::score).reduce(0L, Long::sum);
        System.out.println("Part 1: " + total);
    }

    private void part2(List<Card> cards) {
        long total = 0;
        int[] copies = new int[cards.size()];
        for (int i = 0; i < cards.size(); i++) {
            Card card = cards.get(i);
            long count = 1 + copies[i];
            total += count;
            for (int j = 1; j <= card.matches() && i + j < copies.length; j++) {
                copies[i + j] += count;
            }
        }
        System.out.println("Part 2: " + total);
    }

    private record Card(int id, Set<Integer> numbers, Set<Integer> winningNumbers) {

        long matches() {
            return Sets.intersection(numbers, winningNumbers).size();
        }

        long score() {
            long matches = Sets.intersection(numbers, winningNumbers).size();
            if (matches > 0) {
                return 1L << (matches - 1);
            }
            return 0;
        }

        @Override
        public String toString() {
            return "Card{" +
                    "id=" + id +
                    ", score=" + score() +
                    '}';
        }
    }
}

