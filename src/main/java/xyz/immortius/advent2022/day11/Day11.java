package xyz.immortius.advent2022.day11;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class Day11 {
    public static void main(String[] args) throws IOException {
        new Day11().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day11/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        process(lines);
    }

    private void process(List<String> lines) {
        List<Monkey> monkeys = parse(lines);

        for (int i = 0; i < 10000; i++) {
            processRound(monkeys);
         }

        List<Long> results = monkeys.stream().map(x -> x.inspections).sorted(Comparator.reverseOrder()).toList();
        System.out.println(results.get(0) * results.get(1));
    }

    private void processRound(List<Monkey> monkeys) {
        for (Monkey monkey : monkeys) {
            while (!monkey.items.isEmpty()) {
                monkey.inspections += monkey.items.size();
                for (Item item : monkey.items) {
                    monkey.operation.accept(item);
                    if (item.getRemainder(monkey.test) == 0) {
                        monkeys.get(monkey.trueTarget).items.add(item);
                    } else {
                        monkeys.get(monkey.falseTarget).items.add(item);
                    }
                }
                monkey.items.clear();
            }
        }
    }

    private List<Monkey> parse(List<String> lines) {
        List<Monkey> monkeys = new ArrayList<>();

        int lineIndex = 0;
        Monkey monkey = new Monkey();

        while (lineIndex < lines.size()) {
            lineIndex++;
            monkey.initialItems.addAll(Arrays.stream(lines.get(lineIndex).substring("  Starting items: ".length()).split(", ")).map(Integer::parseInt).toList());
            lineIndex++;
            String[] parts = lines.get(lineIndex).substring("  Operation: new = ".length()).split(" ");
            if ("old".equals(parts[2])) {
                monkey.operation = Item::square;
            } else {
                int b = Integer.parseInt(parts[2]);
                switch (parts[1]) {
                    case "*" -> monkey.operation = (x) -> x.multiply(b);
                    case "+" -> monkey.operation = (x) -> x.add(b);
                }
            }
            lineIndex++;
            monkey.test = Integer.parseInt(lines.get(lineIndex).substring("  Test: divisible by ".length()));
            lineIndex++;
            monkey.trueTarget = Integer.parseInt(lines.get(lineIndex).substring("    If true: throw to monkey ".length()));
            lineIndex++;
            monkey.falseTarget = Integer.parseInt(lines.get(lineIndex).substring("    If false: throw to monkey ".length()));
            lineIndex++;
            monkeys.add(monkey);
            monkey = new Monkey();
            lineIndex++;
        }
        List<Integer> primes = new ArrayList<>();
        for (Monkey m : monkeys) {
            primes.add(m.test);
        }
        for (Monkey m : monkeys) {
            for (int item : m.initialItems) {
                m.items.add(new Item(primes, item));
            }
        }

        return monkeys;
    }

    private static class Monkey {
        List<Integer> initialItems = new ArrayList<>();
        List<Item> items = new ArrayList<>();
        Consumer<Item> operation;
        int test;
        int trueTarget;
        int falseTarget;
        long inspections = 0;
    }

    private static class Item {
        int[] primes;
        int[] remainders;

        public Item(List<Integer> primes, int initialNumber) {
            this.primes = new int[primes.size()];
            this.remainders = new int[primes.size()];
            for (int i = 0; i < primes.size(); i++) {
                this.primes[i] = primes.get(i);
                this.remainders[i] = initialNumber % primes.get(i);
            }
        }

        public void add(int value) {
            for (int i = 0; i < primes.length; i++) {
                remainders[i] = (remainders[i] + value) % primes[i];
            }
        }

        public void multiply(int value) {
            for (int i = 0; i < primes.length; i++) {
                remainders[i] = (remainders[i] * value) % primes[i];
            }
        }

        public void square() {
            for (int i = 0; i < primes.length; i++) {
                remainders[i] = (remainders[i] * remainders[i]) % primes[i];
            }
        }

        public int getRemainder(int value) {
            for (int i = 0; i < primes.length; i++) {
                if (primes[i] == value) {
                    return remainders[i];
                }
            }
            throw new IllegalStateException("Unsupported prime: " + value);
        }
    }


}

