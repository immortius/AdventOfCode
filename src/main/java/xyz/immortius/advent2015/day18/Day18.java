package xyz.immortius.advent2015.day18;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class Day18 {

    private static final String YEAR = "2015";
    private static final String DAY = "18";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day18().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        Board board = parse(lines);
        part1(board);
        part2(board);
    }

    private Board parse(List<String> lines) {
        Board board = new Board(lines.get(0).length(), lines.size());
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                board.set(x, y, line.charAt(x) == '#');
            }
        }
        return board;
    }

    private void part1(Board board) {
        for (int i = 0; i < 100; i++) {
            board = step(board);
            //System.out.println("Step " + i);
            //board.print();
        }
        board.print();

        int count = 0;
        for (int i = 0; i < board.board.length; i++) {
            if (board.board[i]) {
                count++;
            }
        }

        System.out.println("Part 1: " + count);
    }

    private void part2(Board board) {
        board.set(0,0,true);
        board.set(0, board.sizeY - 1, true);
        board.set(board.sizeX - 1, 0,true);
        board.set(board.sizeX - 1, board.sizeY - 1, true);
        for (int i = 0; i < 100; i++) {
            board = step(board);
            board.set(0,0,true);
            board.set(0, board.sizeY - 1, true);
            board.set(board.sizeX - 1, 0,true);
            board.set(board.sizeX - 1, board.sizeY - 1, true);
        }
        board.print();

        int count = 0;
        for (int i = 0; i < board.board.length; i++) {
            if (board.board[i]) {
                count++;
            }
        }

        System.out.println("Part 2: " + count);
    }

    private Board step(Board board) {
        Board result = new Board(board.sizeX, board.sizeY);
        for (int y = 0; y < board.sizeY; y++) {
            for (int x = 0; x < board.sizeX; x++) {
                int count = 0;
                for (int xAdj = x - 1; xAdj <= x + 1; xAdj++) {
                    for (int yAdj = y - 1; yAdj <= y + 1; yAdj++) {
                        if (board.get(xAdj, yAdj)) {
                            count++;
                        }
                    }
                }
                boolean current = board.get(x, y);
                if (current) {
                    count--;
                }
                if (count == 3) {
                    result.set(x, y, true);
                } else if (count == 2) {
                    result.set(x, y, board.get(x, y));
                } else {
                    result.set(x, y, false);
                }
            }
        }
        return result;
    }

    private static class Board {
        private final boolean[] board;
        private final int sizeX;
        private final int sizeY;


        public Board(int sizeX, int sizeY) {
            this.board = new boolean[sizeY * sizeX];
            this.sizeX = sizeX;
            this.sizeY = sizeY;
        }

        public Board(Board other) {
            this.board = Arrays.copyOf(other.board, other.board.length);
            this.sizeX = other.sizeX;
            this.sizeY = other.sizeY;
        }

        private int indexOf(int x, int y) {
            return y * sizeX + x;
        }

        public boolean isInbounds(int x, int y) {
            return x >= 0 && y >= 0 && x < sizeX && y < sizeY;
        }

        public boolean get(int x, int y) {
            if (isInbounds(x, y)) {
                return board[indexOf(x, y)];
            }
            return false;
        }

        public void set(int x, int y, boolean value) {
            if (isInbounds(x, y)) {
                board[indexOf(x, y)] = value;
            }
        }

        public void print() {
            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < sizeX; x++) {
                    System.out.print(get(x, y) ? '#' : '.');
                }
                System.out.println();
            }
        }
    }

}

