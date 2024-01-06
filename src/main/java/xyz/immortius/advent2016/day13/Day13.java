package xyz.immortius.advent2016.day13;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import xyz.immortius.advent2022.day12.AStar;
import xyz.immortius.util.Direction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day13 {

    private static final String YEAR = "2016";
    private static final String DAY = "13";

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day13().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        example();
        part1();
        part2();
    }

    private void example() {
        Vector2ic target = new Vector2i(7, 4);
        AStar aStar = new AStar(new Vector2i(1, 1), target, x -> x.gridDistance(target), x -> determineAdjacent(x, 10));
        List<Vector2ic> path = aStar.run();
        System.out.println("Example: " + (path.size() - 1));
    }

    private void part1() {
        Vector2ic target = new Vector2i(31,39);
        AStar aStar = new AStar(new Vector2i(1, 1), target, x -> x.gridDistance(target), x -> determineAdjacent(x, 1362));
        List<Vector2ic> path = aStar.run();
        System.out.println("Part 1: " + (path.size() - 1));
    }


    private void part2() {
        Vector2ic target = new Vector2i(50,50);
        AStar aStar = new AStar(new Vector2i(1, 1), target, x -> x.gridDistance(target), x -> determineAdjacent(x, 1362));
        aStar.setCutoff(50);
        aStar.run();
        System.out.println("Part 2: " + aStar.getReachable());
    }

    @NotNull
    private List<Vector2ic> determineAdjacent(Vector2ic x, int favourite) {
        List<Vector2ic> adjacent = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            Vector2i neighbour = x.add(dir.toVector(), new Vector2i());
            if (neighbour.x < 0 || neighbour.y < 0) {
                continue;
            }
            int magic = neighbour.x * neighbour.x + 3 * neighbour.x + 2 * neighbour.x * neighbour.y + neighbour.y + neighbour.y * neighbour.y + favourite;
            if (Integer.bitCount(magic) % 2 == 0) {
                adjacent.add(neighbour);
            }
        }
        return adjacent;
    }





}

