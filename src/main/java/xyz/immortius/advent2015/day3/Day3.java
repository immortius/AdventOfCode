package xyz.immortius.advent2015.day3;

import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day3 {
    private final Set<Vector2ic> visited = new HashSet<>();

    public static void main(String[] args) throws IOException {
        new Day3().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2015/day3/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        process(lines.get(0));
    }

    private void process(String input) {
        List<Vector2i> santas = new ArrayList<>();
        santas.add(new Vector2i(0,0));
        santas.add(new Vector2i(0,0));
        visit(new Vector2i(0,0));
        int turn = 0;
        for (char c : input.toCharArray()) {
            switch (c) {
                case '>' -> visit(santas.get(turn).add(1, 0));
                case '<' -> visit(santas.get(turn).add(-1, 0));
                case '^' -> visit(santas.get(turn).add(0, 1));
                case 'v' -> visit(santas.get(turn).add(0, -1));
            }
            turn = (turn + 1) % santas.size();
        }
        System.out.println(visited.size());

    }

    private void visit(Vector2ic location) {
        visited.add(new Vector2i(location));
    }

}

