package xyz.immortius.advent2022.day6;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day6 {
    public static void main(String[] args) throws IOException {
        new Day6().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day6/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        int packetStart = findStartMarker(lines.get(0), 4);
        System.out.println("Start marker found at " + (packetStart + 1));
        int msgStart = findStartMarker(lines.get(0).substring(packetStart), 14);
        System.out.println("Start marker found at " + (packetStart + msgStart + 1));
    }

    private int findStartMarker(String msg, int length) {
        List<Character> buffer = new ArrayList<>();
        for (int index = 0; index < msg.length(); index++) {
            int match = buffer.indexOf(msg.charAt(index));
            if (match != -1) {
                buffer = buffer.subList(match + 1, buffer.size());
            } else if (buffer.size() == length - 1) {
                return index;
            }
            buffer.add(msg.charAt(index));
        }
        return -1;
    }

}
