package xyz.immortius.util;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class AStar<T> {

    private final Map<T, Long> fScore = new HashMap<>();
    private final Map<T, Long> gScore = new HashMap<>();
    private final Map<T, T> cameFrom = new HashMap<>();
    private final PriorityQueue<T> open = new PriorityQueue<>(Comparator.comparingLong(fScore::get));

    private final Function<T, Long> estimateFunc;
    private final Function<T, Map<T, Long>> connectionFunc;
    private Predicate<T> end;
    private final T start;

    private long cutoff = Long.MAX_VALUE;
    private T finalValue;

    public AStar(T start, T end, Function<T, Long> estimate, Function<T, Map<T, Long>> connectionFunc) {
        this.estimateFunc = estimate;
        gScore.put(start, 0L);
        fScore.put(start, gScore.get(start) + estimateFunc.apply(start));
        open.add(start);
        this.start = start;
        this.end = end::equals;
        this.connectionFunc = connectionFunc;
    }

    public AStar(T start, Predicate<T> end, Function<T, Long> estimate, Function<T, Map<T, Long>> connectionFunc) {
        this.estimateFunc = estimate;
        gScore.put(start, 0L);
        fScore.put(start, gScore.get(start) + estimateFunc.apply(start));
        open.add(start);
        this.start = start;
        this.end = end;
        this.connectionFunc = connectionFunc;
    }

    public long run() {
        while (!open.isEmpty()) {
            T current = open.remove();
            if (end.test(current)) {
                finalValue = current;
                return gScore.get(current);
            }

            if (fScore.get(current) >= cutoff) {
                break;
            }

            for (Map.Entry<T, Long> neighbourConnection : connectionFunc.apply(current).entrySet()) {
                long neighbourGScore = gScore.get(current) + neighbourConnection.getValue();
                T neighbour = neighbourConnection.getKey();
                if (neighbourGScore <= cutoff && neighbourGScore < gScore.getOrDefault(neighbour, Long.MAX_VALUE)) {
                    cameFrom.put(neighbour, current);
                    gScore.put(neighbour, neighbourGScore);
                    fScore.put(neighbour, neighbourGScore + estimateFunc.apply(neighbour));
                    open.remove(neighbour);
                    open.add(neighbour);
                }
            }
        }
        if (gScore.get(finalValue) != null) {
            return gScore.get(finalValue);
        }
        return -1;
    }

    public List<T> getPath() {
        List<T> path = new ArrayList<>();
        T next = finalValue;
        while (!start.equals(next)) {
            path.add(0, next);
            next = cameFrom.get(next);
        }
        return path;
    }

    public void setCutoff(long cutoff) {
        this.cutoff = cutoff;
    }

    public int getReachable() {
        return gScore.size();
    }
}
