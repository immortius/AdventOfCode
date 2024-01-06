package xyz.immortius.advent2022.day17;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day17 {
    // Note: These are upside down (0th row is the bottom)
    private final List<boolean[][]> BLOCKS = Arrays.asList(
            new boolean[][]{{true, true, true, true}},
            new boolean[][]{{false, true, false}, {true, true, true}, {false, true, false}},
            new boolean[][]{{true, true, true}, {false, false, true}, {false, false, true}},
            new boolean[][]{{true}, {true}, {true}, {true}},
            new boolean[][]{{true, true}, {true, true}});

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day17().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day17/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        process(lines.get(0));
    }

    private void process(String windCycle) {
        Grid grid = new Grid();

        long targetCount = 1000000000000L;
        long towerTop = -1;
        List<State> stateBuffer = new ArrayList<>();

        int windIndex = 0;
        int blockIndex = 0;
        for (long blockCount = 0; blockCount < targetCount; blockCount++) {
            boolean[][] rock = BLOCKS.get(blockIndex);
            int rockX = 2;
            long rockY = towerTop + 4;

            boolean blockPlaced = false;
            while (!blockPlaced) {
                int dir = (windCycle.charAt(windIndex) == '<') ? -1 : 1;

                if (!grid.overlap(rockX + dir, rockY, rock)) {
                    rockX += dir;
                }

                if (!grid.overlap(rockX, rockY - 1, rock)) {
                    rockY -= 1;
                } else { // We've landed
                    blockPlaced = true;
                    towerTop = Math.max(rockY + rock.length - 1, towerTop);
                    if (grid.placeBlock((char) ('0' + blockIndex), rock, rockX, rockY)) {
                        State state = new State(grid.getInternal(), blockIndex, windIndex, blockCount, towerTop);
                        for (State prevState : stateBuffer) {
                            if (prevState.equals(state)) {
                                long heightIncrease = towerTop - prevState.highest;
                                long blockIncrease = blockCount - prevState.blockCount;

                                long iterations = (targetCount - blockCount) / (blockCount - prevState.blockCount);

                                blockCount += iterations * blockIncrease;
                                towerTop += heightIncrease * iterations;
                                grid.droppedDepth += heightIncrease * iterations;
                                stateBuffer.clear();
                                break;
                            }
                        }
                        stateBuffer.add(state);
                    }
                    blockIndex = (blockIndex + 1) % BLOCKS.size();
                }
                windIndex = (windIndex + 1) % windCycle.length();
            }
        }

        System.out.println("Final state: " + grid);
        System.out.println("Tower height: " + towerTop + 1);

    }

    private static class State {
        private final List<char[]> map;
        private final int block;
        private final long blockCount;
        private final long highest;
        private final int command;

        public State(List<char[]> map, int block, int command, long blockCount, long towerTop) {
            this.map = map;
            this.block = block;
            this.blockCount = blockCount;
            this.highest = towerTop;
            this.command = command;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            State other = (State) o;
            if (block != other.block || command != other.command) {
                return false;
            }
            while (map.size() < other.map.size()) {
                map.add(".......".toCharArray());
            }
            while (map.size() > other.map.size()) {
                other.map.add(".......".toCharArray());
            }
            for (int line = 0; line < other.map.size(); line++) {
                if (!new String(map.get(line)).equals(new String(other.map.get(line)))) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = map.size() - 1; i >= 0; i--) {
                char[] row = map.get(i);
                for (char c : row) {
                    builder.append(c);
                }
                builder.append('\n');
            }
            return builder.toString();
        }
    }

    public static class Grid {
        private final List<char[]> map;
        long droppedDepth = 0;
        char defaultValue = '.';

        public Grid() {
            map = new ArrayList<>();
        }

        public boolean isInbounds(int x, long y) {
            return x >= 0 && x < 7 && (y - droppedDepth) >= 0;
        }

        public char get(int x, long y) {
            if (isInbounds(x, y)) {
                int line = (int) (y - droppedDepth);
                if (line >= map.size()) {
                    return '.';
                }
                return map.get(line)[x];
            }
            return defaultValue;
        }

        public boolean set(int x, long y, char value) {
            int line = (int) (y - droppedDepth);
            while (line >= map.size()) {
                map.add(new char[]{defaultValue, defaultValue, defaultValue, defaultValue, defaultValue, defaultValue, defaultValue});
            }
            char[] row = map.get(line);
            row[x] = value;
            for (char c : row) {
                if (c == '.') {
                    return false;
                }
            }
            // Full line
            map.subList(0, line + 1).clear();
            droppedDepth += line + 1;
            return true;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = map.size() - 1; i >= 0; i--) {
                char[] row = map.get(i);
                for (char c : row) {
                    builder.append(c);
                }
                builder.append('\n');
            }
            return builder.toString();
        }

        public List<char[]> getInternal() {
            List<char[]> result = new ArrayList<>();
            for (char[] array : map) {
                result.add(Arrays.copyOf(array, array.length));
            }
            return result;
        }

        public boolean overlap(int x, long y, boolean[][] rock) {
            for (int xBit = 0; xBit < rock[0].length; xBit++) {
                for (int yBit = 0; yBit < rock.length; yBit++) {
                    if (rock[yBit][xBit]) {
                        if (!isInbounds(x + xBit, y + yBit) || get(x + xBit, y + yBit) != '.') {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        public boolean placeBlock(char value, boolean[][] rock, int rockX, long rockY) {
            boolean removedLine = false;
            for (int xBit = 0; xBit < rock[0].length; xBit++) {
                for (int yBit = 0; yBit < rock.length; yBit++) {
                    if (rock[yBit][xBit]) {
                        removedLine |= set(rockX + xBit, rockY + yBit, value);
                    }
                }
            }
            return removedLine;
        }
    }
}
