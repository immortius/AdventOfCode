package xyz.immortius.advent2022.day12;

import org.joml.Vector2ic;

import java.util.*;
import java.util.function.Function;

public class AStar {

    private Map<Vector2ic, Long> fScore = new HashMap<>();
    private Map<Vector2ic, Long> gScore = new HashMap<>();
    private Map<Vector2ic, Vector2ic> cameFrom = new HashMap<>();
    private PriorityQueue<Vector2ic> open = new PriorityQueue<>(Comparator.comparingLong((Vector2ic a) -> fScore.get(a)));

    private Function<Vector2ic, Long> estimateFunc;
    private Function<Vector2ic, List<Vector2ic>> connectionFunc;
    private Vector2ic end;

    private long cutoff = Long.MAX_VALUE;

    public AStar(Vector2ic start, Vector2ic end, Function<Vector2ic, Long> estimate, Function<Vector2ic, List<Vector2ic>> connectionFunc) {
        this.estimateFunc = estimate;
        gScore.put(start, 0L);
        fScore.put(start, gScore.get(start) + estimateFunc.apply(start));
        open.add(start);
        this.end = end;
        this.connectionFunc = connectionFunc;
    }

    public List<Vector2ic> run() {
        while (!open.isEmpty()) {
            Vector2ic current = open.remove();
            if (current.equals(end)) {
                List<Vector2ic> path = new ArrayList<>();
                while (current != null) {
                    path.add(current);
                    current = cameFrom.get(current);
                }
                return path;
            }

            for (Vector2ic neighbour : connectionFunc.apply(current)) {
                long neighbourGScore = gScore.get(current) + 1;
                if (neighbourGScore <= cutoff && neighbourGScore < gScore.getOrDefault(neighbour, Long.MAX_VALUE)) {
                    cameFrom.put(neighbour, current);
                    gScore.put(neighbour, neighbourGScore);
                    fScore.put(neighbour, neighbourGScore + estimateFunc.apply(neighbour));
                    if (!open.contains(neighbour)) {
                        open.add(neighbour);
                    }
                }
            }
        }
        return Collections.emptyList();
    }

    public void setCutoff(int cutoff) {
        this.cutoff = cutoff;
    }

    public int getReachable() {
        return gScore.size();
    }
}
