package xyz.immortius.advent2019.day9;

import com.google.common.io.CharStreams;
import xyz.immortius.advent2019.day2.IntCodeComputer;
import xyz.immortius.advent2019.day2.IntCodeHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Day9 {

    private static final String YEAR = "2019";
    private static final String DAY = "9";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day9().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        long[] program = IntCodeHelper.parse(lines);
        new IntCodeComputer(program).run();
    }
}

