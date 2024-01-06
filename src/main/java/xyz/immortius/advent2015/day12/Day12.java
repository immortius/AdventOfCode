package xyz.immortius.advent2015.day12;

import com.google.common.io.CharStreams;
import com.squareup.moshi.JsonReader;
import okio.Buffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Day12 {

    private static final String YEAR = "2015";
    private static final String DAY = "12";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day12().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        part1(lines.get(0));
        part2(lines.get(0));
    }

    private void part1(String line) {
        int total = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if ((c == '-' && Character.isDigit(line.charAt(i + 1))) || Character.isDigit(c)) {
                StringBuilder builder = new StringBuilder();
                builder.append(c);
                i++;
                while (Character.isDigit(line.charAt(i))) {
                    builder.append(line.charAt(i++));
                }
                total += Integer.parseInt(builder.toString());
            }
        }
        System.out.println("Part 1: " + total);
    }

    private void part2(String line) throws IOException {
        Deque<Integer> total = new ArrayDeque<>();
        total.push(0);
        Deque<Boolean> red = new ArrayDeque<>();
        red.push(false);
        JsonReader reader = JsonReader.of(new Buffer().writeUtf8(line));
        while (reader.peek() != JsonReader.Token.END_DOCUMENT) {
            switch (reader.peek()) {
                case NAME -> {
                    String name = reader.nextName();
                }
                case NUMBER -> {
                    int value = reader.nextInt();
                    total.push(total.pop() + value);
                }
                case STRING -> {
                    if ("red".equals(reader.nextString())) {
                        red.pop();
                        red.push(true);
                    }
                }
                case NULL, BOOLEAN -> {
                    reader.skipValue();
                }
                case BEGIN_ARRAY -> {
                    red.push(false);
                    reader.beginArray();
                }
                case BEGIN_OBJECT -> {
                    reader.beginObject();
                    total.push(0);
                    red.push(false);
                }
                case END_ARRAY -> {
                    red.pop();
                    reader.endArray();
                }
                case END_OBJECT -> {
                    int objValue = total.pop();
                    if (!red.pop()) {
                        total.push(total.pop() + objValue);
                    }
                    reader.endObject();
                }
            }
        }
        if (red.pop()) {
            System.out.println("Part 2: 0");
        } else {
            System.out.println("Part 2: " + total.pop());
        }
    }



}

