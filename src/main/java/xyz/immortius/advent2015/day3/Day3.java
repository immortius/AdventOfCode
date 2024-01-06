package xyz.immortius.advent2015.day3;

import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day3 {

    public static void main(String[] args) throws IOException {
        new Day3().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2015/day3/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        part1(lines.get(0));
        part2(lines.get(0));
    }

    private void part1(String input) {
        Vector2i santaPos = new Vector2i();
        Set<Vector2ic> visited = new HashSet<>();
        visited.add(new Vector2i(santaPos));
        for (char c : input.toCharArray()) {
            switch (c) {
                case '>' -> santaPos.add(1, 0);
                case '<' -> santaPos.add(-1, 0);
                case '^' -> santaPos.add(0, 1);
                case 'v' -> santaPos.add(0, -1);
            }
            visited.add(new Vector2i(santaPos));
        }
        System.out.println("Part 1: " + visited.size());
    }

    private void part2(String input) {
        List<Vector2i> santas = new ArrayList<>();
        santas.add(new Vector2i(0,0));
        santas.add(new Vector2i(0,0));
        Set<Vector2ic> visited = new HashSet<>();
        visited.add(new Vector2i(0,0));
        int turn = 0;
        for (char c : input.toCharArray()) {
            Vector2i santaPos = santas.get(turn);
            switch (c) {
                case '>' -> santaPos.add(1, 0);
                case '<' -> santaPos.add(-1, 0);
                case '^' -> santaPos.add(0, 1);
                case 'v' -> santaPos.add(0, -1);
            }
            visited.add(new Vector2i(santaPos));
            turn = (turn + 1) % santas.size();
        }
        System.out.println("Part 2: " + visited.size());
    }
}

