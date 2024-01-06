package xyz.immortius.advent2022.day23;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.io.CharStreams;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Day23 {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day23().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day23/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        Set<Vector2i> elfPositions = parse(lines);
        part1(elfPositions);
        part2(elfPositions);
    }

    private Set<Vector2i> parse(List<String> lines) {
        Set<Vector2i> elfPositions = new LinkedHashSet<>();
        for (int y = 0; y < lines.size(); y++) {
            for (int x = 0; x < lines.get(y).length(); x++) {
                if (lines.get(y).charAt(x) == '#') {
                    elfPositions.add(new Vector2i(x, y));
                }
            }
        }
        return elfPositions;
    }

    private void part1(Set<Vector2i> elfPositions) {
        Set<Vector2ic> elves = new LinkedHashSet<>(elfPositions);
        int directionOffset = 0;

        for (int round = 0; round < 10; round++) {
            ListMultimap<Vector2ic, Vector2ic> proposedMoves = calculateProposedMoves(elves, directionOffset);
            moveElves(elves, proposedMoves);

            directionOffset = (directionOffset + 1) % Direction.values().length;
        }

        Vector2i min = new Vector2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Vector2i max = new Vector2i(Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (Vector2ic elf : elves) {
            min.min(elf);
            max.max(elf);
        }
        max.add(1,1);

        int totalArea = (max.x - min.x) * (max.y - min.y);
        int clearArea = totalArea - elves.size();
        System.out.println("Part 1: " + clearArea);
    }

    private void part2(Set<Vector2i> elfPositions) {
        Set<Vector2ic> elves = new LinkedHashSet<>(elfPositions);

        int elvesMoved = elves.size();
        int round = 0;
        int directionOffset = 0;
        while (elvesMoved > 0) {
            round++;
            ListMultimap<Vector2ic, Vector2ic> proposedMoves = calculateProposedMoves(elves, directionOffset);
            elvesMoved = moveElves(elves, proposedMoves);

            directionOffset = (directionOffset + 1) % Direction.values().length;
        }

        System.out.println("Part 2: " + round);
    }

    private int moveElves(Set<Vector2ic> currentElfPositions, ListMultimap<Vector2ic, Vector2ic> proposedMoves) {
        int moves = 0;
        for (Vector2ic targetPos : proposedMoves.keys()) {
            List<Vector2ic> elves = proposedMoves.get(targetPos);
            if (elves.size() == 1) {
                currentElfPositions.remove(elves.get(0));
                currentElfPositions.add(targetPos);
                moves++;
            }
        }
        return moves;
    }

    private ListMultimap<Vector2ic, Vector2ic> calculateProposedMoves(Set<Vector2ic> elves, int directionOffset) {
        ListMultimap<Vector2ic, Vector2ic> proposedMoves = ArrayListMultimap.create();
        for (Vector2ic elf : elves) {
            boolean hasAdjacent = false;
            for (Vector2ic adjPos : adjacentVectors) {
                if (elves.contains(elf.add(adjPos, new Vector2i()))) {
                    hasAdjacent = true;
                    break;
                }
            }

            if (hasAdjacent) {
                for (int dirIndex = 0; dirIndex < Direction.values().length; dirIndex++) {
                    Direction dir = Direction.values()[(dirIndex + directionOffset) % Direction.values().length];
                    boolean isClear = true;
                    for (Vector2ic checkVec : dir.getCheckVectors()) {
                        if (elves.contains(elf.add(checkVec, new Vector2i()))) {
                            isClear = false;
                            break;
                        }
                    }

                    if (isClear) {
                        proposedMoves.put(elf.add(dir.getDirVector(), new Vector2i()), elf);
                        break;
                    }
                }
            }
        }
        return proposedMoves;
    }

    private void printState(Set<Vector2ic> elves) {
        Vector2i min = new Vector2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
        Vector2i max = new Vector2i(Integer.MIN_VALUE, Integer.MIN_VALUE);
        for (Vector2ic elf : elves) {
            min.min(elf);
            max.max(elf);
        }

        Vector2i pos = new Vector2i();
        for (pos.y = min.y; pos.y <= max.y; pos.y++) {
            for (pos.x = min.x; pos.x <= max.x; pos.x++) {
                System.out.print((elves.contains(pos) ? '#' : '.'));
            }
            System.out.println();
        }
        System.out.println();
    }

    private static final List<Vector2ic> adjacentVectors = ImmutableList.of(
            new Vector2i(-1, 0),
            new Vector2i(-1, 1),
            new Vector2i(0, 1),
            new Vector2i(1, 1),
            new Vector2i(1, 0),
            new Vector2i(1, -1),
            new Vector2i(0, -1),
            new Vector2i(-1, -1));

    private enum Direction {
        North(new Vector2i(0, -1), new Vector2i (-1, -1), new Vector2i(1, -1)),
        South(new Vector2i(0, 1), new Vector2i(-1, 1), new Vector2i(1, 1)),
        West(new Vector2i(-1, 0), new Vector2i(-1, -1), new Vector2i(-1, 1)),
        East(new Vector2i(1, 0), new Vector2i(1, -1), new Vector2i(1, 1));

        private final Vector2ic dirVector;
        private final List<Vector2ic> checkVectors;

        Direction(Vector2i vec, Vector2i... otherVecs) {
            dirVector = vec;
            checkVectors = ImmutableList.<Vector2ic>builder().add(vec).add(otherVecs).build();
        }

        public Vector2ic getDirVector() {
            return dirVector;
        }

        public List<Vector2ic> getCheckVectors() {
            return checkVectors;
        }
    }
}