package xyz.immortius.advent2022.day4;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class CampCleanup {
    public static void main(String[] args) throws IOException {
        new CampCleanup().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day4/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        long count = lines.stream()
                .map(x -> x.split(","))
                .map(x -> {
                    Range[] ranges = new Range[2];
                    for (int i = 0; i < 2; i++) {
                        String[] range = x[i].split("-");
                        ranges[i] = new Range(Integer.parseInt(range[0]), Integer.parseInt(range[1]));
                    }
                    return ranges;
                })
                .map(x -> {
                    boolean result = x[0].min() <= x[1].max() && x[0].max() >= x[1].min() ||
                            x[0].max() <= x[1].min() && x[0].min() >= x[1].max();
                    if (result) {
                        System.out.println("Ranges " + x[0].min() + "-" + x[0].max() + " overlaps " + x[1].min() + "-" + x[1].max());
                    }
                    return result;
                })
                .filter(x -> x).count();
        System.out.println(count);
    }

    record Range(int min, int max) {}
}
