package xyz.immortius.advent2019.day5;

import com.google.common.io.CharStreams;
import xyz.immortius.advent2019.day2.ConsoleInputStream;
import xyz.immortius.advent2019.day2.IntCodeComputer;
import xyz.immortius.advent2019.day2.IntCodeHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Day5 {

    private static final String YEAR = "2019";
    private static final String DAY = "5";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        new Day5().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws Exception {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        long[] program = IntCodeHelper.parse(lines);
        part1(program);
    }

    private void part1(long[] program) throws Exception {
        IntCodeComputer computer = new IntCodeComputer(program, new ConsoleInputStream());
        computer.run();
        System.out.println(computer);
    }



}

