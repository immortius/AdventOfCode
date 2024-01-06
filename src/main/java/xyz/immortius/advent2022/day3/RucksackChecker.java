package xyz.immortius.advent2022.day3;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class RucksackChecker
{
    public static void main(String[] args) throws IOException {
        new RucksackChecker().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day3/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        totalMisplacedItems(lines);
        totalBadges(lines);
    }

    private void totalBadges(List<String> lines) {
        int total = 0;
        for (int group = 0; group * 3 < lines.size(); group++) {
            int lineIndex = 3 * group;
            char sharedChar = 'a';
            for (char c : lines.get(lineIndex).toCharArray()) {
                if (lines.get(lineIndex + 1).indexOf(c) != -1 && lines.get(lineIndex + 2).indexOf(c) != -1) {
                    sharedChar = c;
                    break;
                }
            }
            total += calculatePriority(sharedChar);

        }
        System.out.println("Badge Total: " + total);
    }


    private void totalMisplacedItems(List<String> lines) {
        int result = lines.stream().map(line -> {
            String half1 = line.substring(0, line.length() / 2);
            String half2 = line.substring(line.length() / 2);
            for (char c : half1.toCharArray()) {
                int index = half2.indexOf(c);
                if (index != -1) {
                    return c;
                }
            }
            System.out.println("Failed to find match");
            return 'a';
        }).map(this::calculatePriority).reduce(0, Integer::sum);
        System.out.println("Total priority of misplaced items: " + result);
    }

    private int calculatePriority(Character c) {
        int score;
        if (c > 'a') {
            score = 1 + c - 'a';
        } else {
            score = 27 + c - 'A';
        }
        return score;
    }


}
