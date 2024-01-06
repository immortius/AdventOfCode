package xyz.immortius.advent2019.day7;

import com.google.common.io.CharStreams;
import xyz.immortius.advent2019.day2.ConnectingStream;
import xyz.immortius.advent2019.day2.IntCodeComputer;
import xyz.immortius.advent2019.day2.IntCodeHelper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Day7 {

    private static final String YEAR = "2019";
    private static final String DAY = "7";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        new Day7().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws Exception {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        long[] input = IntCodeHelper.parse(lines);
        part1(Arrays.copyOf(input, input.length));
        part2(Arrays.copyOf(input, input.length));
    }

    private void part1(long[] input) throws Exception {
        List<List<Integer>> combinations = createCombinations(Arrays.asList(0, 1, 2, 3, 4));
        long maxValue = Integer.MIN_VALUE;
        for (List<Integer> phaseList : combinations) {
            long value = 0;
            for (int phase : phaseList) {
                ConnectingStream inputStream = new ConnectingStream();
                ConnectingStream outputStream = new ConnectingStream();
                IntCodeComputer computer = new IntCodeComputer(input, inputStream, outputStream);
                inputStream.receive(phase);
                inputStream.receive(value);
                computer.run();
                value = outputStream.send();
            }
            if (value > maxValue) {
                maxValue = value;
            }
        }

        System.out.println("Part 1: " + maxValue);
    }

    private void part2(long[] input) throws Exception {
        List<List<Integer>> combinations = createCombinations(Arrays.asList(5, 6, 7, 8, 9));
        long maxValue = Integer.MIN_VALUE;
        for (List<Integer> phaseList : combinations) {
            List<IntCodeComputer> amplifiers = new ArrayList<>();
            List<ConnectingStream> streams = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                streams.add(new ConnectingStream());
            }
            for (int i = 0; i < 5; i++) {
                ConnectingStream inputStream = streams.get(i);
                ConnectingStream outputStream = streams.get((i + 1) % streams.size());
                inputStream.receive(phaseList.get(i));
                IntCodeComputer computer = new IntCodeComputer(input, inputStream, outputStream);
                amplifiers.add(computer);
            }
            streams.get(0).receive(0);

            ExecutorService executorService = Executors.newFixedThreadPool(phaseList.size());
            for (IntCodeComputer amplifier : amplifiers) {
                executorService.submit(amplifier::run);
            }
            executorService.shutdown();
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                System.out.println("Amplifier timeout");
            }

            long value = streams.get(0).send();
            if (value > maxValue) {
                maxValue = value;
            }
        }

        System.out.println("Part 2: " + maxValue);
    }

    private List<List<Integer>> createCombinations(List<Integer> options) {
        List<List<Integer>> results = new ArrayList<>();
        if (options.size() == 1) {
            results.add(new ArrayList<>(options));
        } else {
            for (Integer item : options) {
                List<Integer> residual = new ArrayList<>(options);
                residual.remove(item);
                List<List<Integer>> subResults = createCombinations(residual);
                for (List<Integer> result : subResults) {
                    result.add(item);
                }
                results.addAll(subResults);
            }
        }
        return results;
    }



}

