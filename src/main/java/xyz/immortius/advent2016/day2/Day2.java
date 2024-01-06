package xyz.immortius.advent2016.day2;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.util.Direction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Day2 {

    private static final String YEAR = "2016";
    private static final String DAY = "2";
    private static final boolean REAL_INPUT = true;

    private static final Map<Vector2ic, Character> KEYPAD = ImmutableMap.of(
            new Vector2i(0, 2), '1',
            new Vector2i(1, 2), '2',
            new Vector2i(2, 2), '3',
            new Vector2i(0, 1), '4',
            new Vector2i(1, 1), '5',
            new Vector2i(2, 1), '6',
            new Vector2i(0, 0), '7',
            new Vector2i(1, 0), '8',
            new Vector2i(2, 0), '9'
    );

    private static final Map<Vector2ic, Character> KEYPAD2 = ImmutableMap.<Vector2ic, Character>builder()
            .put(new Vector2i(0, 2), '1')
            .put(new Vector2i(-1, 1), '2')
            .put(new Vector2i(0, 1), '3')
            .put(new Vector2i(1, 1), '4')
            .put(new Vector2i(-2, 0), '5')
            .put(new Vector2i(-1, 0), '6')
            .put(new Vector2i(0, 0), '7')
            .put(new Vector2i(1, 0), '8')
            .put(new Vector2i(2, 0), '9')
            .put(new Vector2i(-1, -1), 'A')
            .put(new Vector2i(0, -1), 'B')
            .put(new Vector2i(1, -1), 'C')
            .put(new Vector2i(0, -2), 'D').build();

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day2().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<List<Direction>> moves = parse(lines);
        part1(moves);
        part2(moves);
    }

    private List<List<Direction>> parse(List<String> lines) {
        List<List<Direction>> moves = new ArrayList<>();
        for (String line : lines) {
            List<Direction> lineMoves = new ArrayList<>();
            for (char c : line.toCharArray()) {
                lineMoves.add(Direction.parse(c));
            }
            moves.add(lineMoves);
        }
        return moves;
    }

    private void part1(List<List<Direction>> moves) {
        StringBuilder codeBuilder = getCode(1, 1, moves, KEYPAD);

        System.out.println("Part 1: " + codeBuilder);
    }

    private void part2(List<List<Direction>> moves) {
        StringBuilder codeBuilder = getCode(-2, 0, moves, KEYPAD2);

        System.out.println("Part 2: " + codeBuilder);
    }

    @NotNull
    private StringBuilder getCode(int x, int y, List<List<Direction>> moves, Map<Vector2ic, Character> keypad) {
        Vector2i pos = new Vector2i(x, y);
        StringBuilder codeBuilder = new StringBuilder();
        for (List<Direction> input : moves) {
            for (Direction dir : input) {
                Vector2i newPos = new Vector2i(pos).add(dir.toVector());
                if (keypad.containsKey(newPos)) {
                    pos.set(newPos);
                }
            }
            codeBuilder.append(keypad.get(pos));
        }
        return codeBuilder;
    }
}

