package xyz.immortius.advent2019.day13;

import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.advent2019.day2.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Day13 {

    private static final String YEAR = "2019";
    private static final String DAY = "13";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day13().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        long[] program = IntCodeHelper.parse(lines);

        part1(program);
        part2(program);
    }

    private void part1(long[] program) {
        GameScreen gameScreen = new GameScreen();
        new IntCodeComputer(program, new ConsoleInputStream(), gameScreen).run();
        System.out.println("Part 1: " + gameScreen.screen.values().stream().filter(x -> x == Tile.Block).count());

        Vector2i min = new Vector2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Vector2i max = new Vector2i(0,0);
        for (Vector2ic pos : gameScreen.screen.keySet()) {
            min.min(pos);
            max.max(pos);
        }
        System.out.println("Screen extents: " + min.x + "," + min.y + "  " + max.x + "," + max.y);
    }

    private void part2(long[] program) {
        program[0] = 2;
        GameScreen gameScreen = new GameScreen();
        new IntCodeComputer(program, gameScreen, gameScreen).run();
        gameScreen.printScreen();
    }

    public class GameScreen implements IntCodeOutputStream, IntCodeInputStream {

        List<Long> queue = new ArrayList<>();

        Map<Vector2ic, Tile> screen = new LinkedHashMap<>();
        Vector2i ballLocation = new Vector2i();
        Vector2i paddleLocation = new Vector2i();
        int ballDirection = 0;
        long score = 0;

        int image = 0;

        @Override
        public void receive(long value) {
            queue.add(value);
            if (queue.size() == 3) {
                Vector2i pos = new Vector2i(queue.get(0).intValue(), queue.get(1).intValue());
                if (pos.equals(-1, 0)) {
                    score = queue.get(2);
                } else {
                    Tile tile = Tile.values()[queue.get(2).intValue()];
                    if (tile == Tile.Empty) {
                        screen.remove(pos);
                    } else {
                        screen.put(pos, tile);
                    }
                    switch (tile) {
                        case Ball -> {
                            Vector2i move = pos.sub(ballLocation, new Vector2i());
                            ballDirection = Integer.signum(move.x);
                            ballLocation.set(pos);
                        }
                        case Paddle -> paddleLocation.set(pos);
                    }
                }
                queue.clear();
            }

        }

        @Override
        public long send() {
            printScreen();
            return Integer.signum(ballLocation.x - paddleLocation.x);
        }

        public void printScreen() {
            System.out.println("Score: " + score);
            Vector2i pos = new Vector2i();
            for (pos.y = 0; pos.y <= 21; pos.y++) {
                for (pos.x = 0; pos.x <= 36; pos.x++) {
                    System.out.print(screen.getOrDefault(pos, Tile.Empty).getRepresentation());
                }
                System.out.println();
            }
        }

    }

    private enum Tile {
        Empty(" "),
        Wall("█"),
        Block("□"),
        Paddle("="),
        Ball("O");

        private final String representation;

        private Tile(String c) {
            this.representation = c;
        }

        public String getRepresentation() {
            return representation;
        }
    }

}

