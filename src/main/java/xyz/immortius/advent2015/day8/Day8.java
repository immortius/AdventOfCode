package xyz.immortius.advent2015.day8;

import com.google.common.base.Charsets;
import com.google.common.base.Utf8;
import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;
import com.google.common.escape.UnicodeEscaper;
import com.google.common.io.CharStreams;

import javax.annotation.CheckForNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Supplier;

public class Day8 {
    public static void main(String[] args) throws IOException {
        new Day8().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2015/day8/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        part1(lines);
        part2(lines);
    }

    private void part2(List<String> lines) {
        int escapeIncrease = 0;

        for (String line : lines) {
            for (char c : line.toCharArray()) {
                if (c == '"' || c == '\\') {
                    escapeIncrease++;
                }
            }
            escapeIncrease += 2;
        }
        System.out.println("Part 2: " + escapeIncrease);
    }

    private void part1(List<String> lines) {
        int literalTotal = 0;
        int realTotal = 0;

        for (String line : lines) {
            String translatedLine = line.substring(1, line.length() - 1);
            translatedLine = translate(translatedLine);
            translatedLine = translatedLine.replaceAll("[^\\\\]?\\\\x..", ".");
            translatedLine = translatedLine.translateEscapes();
            literalTotal += line.length();
            realTotal += translatedLine.length();
        }
        System.out.println("Part 1: " + (literalTotal - realTotal));
    }

    private String translate(String line) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\\') {
                result.append('.');
                if (line.charAt(i + 1) == 'x') {
                    i += 3;
                } else {
                    i += 1;
                }
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }


}

