package xyz.immortius.advent2019.day22;

import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Day22 {

    //private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    private static final String YEAR = "2019";
    private static final String DAY = "22";

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day22().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + "example.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        List<Shuffle> exampleShuffles = parse(lines);
        example(exampleShuffles);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + "input.txt")))) {
            lines = CharStreams.readLines(reader);
        }
        List<Shuffle> shuffles = parse(lines);

        part1(shuffles);
        part2(shuffles);
    }

    private List<Shuffle> parse(List<String> lines) {
        List<Shuffle> steps = new ArrayList<>();
        for (String line : lines) {
            if (line.equals("deal into new stack")) {
                steps.add(new DealIntoNewStack());
            } else if (line.startsWith("deal")) {
                steps.add(new DealWithIncrement(Long.parseLong(line.substring(line.lastIndexOf(' ') + 1))));
            } else {
                steps.add(new Cut(Long.parseLong(line.split(" ")[1])));
            }
        }
        return steps;
    }

    private void example(List<Shuffle> shuffles) {
        printAllPositions(shuffles, 10);
        printAllLocations(shuffles, 10);
        printViaEquations(shuffles, 10);
    }

    private void part1(List<Shuffle> shuffles) {
        long deckSize = 10007;

        long currentPos = 2019;
        for (Shuffle shuffle : shuffles) {
            currentPos = shuffle.map(currentPos, deckSize);
        }

        System.out.println("Part 1: " + currentPos);
    }

    private void part2(List<Shuffle> shuffles) {
        long deckSize = 119315717514047L;
        long shuffleCount = 101741582076661L;
        long index = 2020;

        List<Equation> equations = new ArrayList<>();
        Equation equation = new Equation(1, 0);
        for (Shuffle shuffle : Lists.reverse(shuffles)) {
            equation = shuffle.applyReverse(equation, deckSize);
        }
        equations.add(equation);
        long result1 = equation.calc(equation.calc(2020, deckSize), deckSize);
        long result2 = equation.doubleShuffle(deckSize).calc(2020, deckSize);
        System.out.println(result1 + " vs " + result2);

        System.out.println(equation);

        long doubling = 1;
        while (doubling < deckSize) {
            equation = equation.doubleShuffle(deckSize);
            System.out.println(equation);
            equations.add(equation);
            doubling *= 2;
        }

        long remainingShuffles = shuffleCount;
        int shuffleSize = 0;
        long result = index;
        while (remainingShuffles > 0) {
            if (remainingShuffles % 2 == 1) {
                result = equations.get(shuffleSize).calc(result, deckSize);
            }
            remainingShuffles = remainingShuffles >> 1;
            shuffleSize++;
        }

        System.out.println("Part 2: " + result);
    }

    private void printAllPositions(List<Shuffle> shuffles, int deckSize) {
        int[] result = new int[deckSize];
        for (int i = 0; i < deckSize; i++) {
            long currentPos = i;
            for (Shuffle shuffle : shuffles) {
                currentPos = shuffle.map(currentPos, deckSize);
            }
            result[(int)currentPos] = i;
        }
        for (int i = 0; i < deckSize; i++) {
            System.out.print(result[i]);
            System.out.print(" ");
        }
        System.out.println();
    }


    private void printViaEquations(List<Shuffle> shuffles, int deckSize) {
        Equation equation = new Equation(1, 0);
        for (Shuffle shuffle : Lists.reverse(shuffles)) {
            equation = shuffle.applyReverse(equation, deckSize);
        }

        int[] result = new int[deckSize];
        for (int i = 0; i < deckSize; i++) {
            long currentPos = equation.calc(i, deckSize);
            result[i] = (int)currentPos;
        }
        for (int i = 0; i < deckSize; i++) {
            System.out.print(result[i]);
            System.out.print(" ");
        }
        System.out.println();
    }

    private void printAllLocations(List<Shuffle> shuffles, int deckSize) {
        List<Shuffle> reverse = Lists.reverse(shuffles);
        int[] result = new int[deckSize];
        for (int i = 0; i < deckSize; i++) {
            long currentPos = i;
            for (Shuffle shuffle : reverse) {
                currentPos = shuffle.reverseMap(currentPos, deckSize);
            }
            result[i] = (int)currentPos;
        }
        for (int i = 0; i < deckSize; i++) {
            System.out.print(result[i]);
            System.out.print(" ");
        }
        System.out.println();
    }


    public interface Shuffle {
        long map(long index, long deckSize);
        long reverseMap(long index, long deckSize);
        Equation apply(Equation prev, long deckSize);
        Equation applyReverse(Equation prev, long deckSize);
    }

    public record Equation(long a, long c) {
        Equation doubleShuffle(long deckSize) {
            return new Equation((BigInteger.valueOf(a).multiply(BigInteger.valueOf(a)).mod(BigInteger.valueOf(deckSize)).longValue() + deckSize) % deckSize, ((BigInteger.valueOf(a).multiply(BigInteger.valueOf(c)).add(BigInteger.valueOf(c)).mod(BigInteger.valueOf(deckSize)).longValue()) % deckSize) % deckSize);
        }

        public long calc(long index, long deckSize) {

            return (BigInteger.valueOf(a).multiply(BigInteger.valueOf(index)).add(BigInteger.valueOf(c)).mod(BigInteger.valueOf(deckSize)).longValue() + deckSize) % deckSize;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(a);
            builder.append("x ");
            if (c > 0) {
                builder.append("+ ").append(c);
            } else {
                builder.append("- ").append(-c);
            }
            return builder.toString();
        }
    }

    public record DealWithIncrement(long increment) implements Shuffle {
        @Override
        public long map(long index, long deckSize) {
            return index * increment % deckSize;
        }

        @Override
        public Equation apply(Equation prev, long deckSize) {
            return new Equation(((prev.a * increment % deckSize) + deckSize) % deckSize, prev.c * increment % deckSize);
        }

        @Override
        public Equation applyReverse(Equation prev, long deckSize) {
            int i = 0;
            while ((i * deckSize + prev.a) % increment != 0) {
                i++;
            }

            int j = 0;
            while ((j * deckSize + prev.c) % increment != 0) {
                j++;
            }

            return new Equation((((i * deckSize + prev.a) / increment) + deckSize) % deckSize, (((j * deckSize + prev.c) / increment) + deckSize) % deckSize);
        }

        @Override
        public long reverseMap(long index, long deckSize) {
            int i = 0;
            while ((Math.multiplyExact(i, deckSize) + index) % increment != 0) {
                i++;
            }
            return ((Math.multiplyExact(i, deckSize) + index) / increment + deckSize) % deckSize;
        }
    }

    public record DealIntoNewStack() implements Shuffle {
        @Override
        public long map(long index, long deckSize) {
            return deckSize - index - 1;
        }

        @Override
        public Equation apply(Equation prev, long deckSize) {
            return new Equation(-prev.a, (deckSize - prev.c  - 1) % deckSize);
        }

        @Override
        public Equation applyReverse(Equation prev, long deckSize) {
            return new Equation(-prev.a, (deckSize - prev.c  - 1) % deckSize);
        }

        @Override
        public long reverseMap(long index, long deckSize) {
            return deckSize - index - 1;
        }
    }

    public record Cut(long location) implements Shuffle {
        @Override
        public long map(long index, long deckSize) {
            return (index - location + deckSize) % deckSize;
        }

        @Override
        public Equation apply(Equation prev, long deckSize) {
            return new Equation(prev.a, (prev.c  - location + deckSize) % deckSize);
        }

        @Override
        public Equation applyReverse(Equation prev, long deckSize) {
            return new Equation(prev.a, (prev.c + location + deckSize) % deckSize);
        }

        @Override
        public long reverseMap(long index, long deckSize) {
            return (index + location + deckSize) % deckSize;
        }
    }

}

