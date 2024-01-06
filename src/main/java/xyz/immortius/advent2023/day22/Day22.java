package xyz.immortius.advent2023.day22;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.io.CharStreams;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day22 {

    //private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    private static final String YEAR = "2023";
    private static final String DAY = "22";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day22().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Brick> bricks = parse(lines);
        part1(bricks);
    }

    private List<Brick> parse(List<String> lines) {
        List<Brick> result = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String[] vectors = lines.get(i).split("~");
            result.add(new Brick(parseVector(vectors[0]), parseVector(vectors[1]), i));
        }
        return result;
    }

    private Vector3ic parseVector(String x) {
        String[] components = x.split(",");
        return new Vector3i(Integer.parseInt(components[0]), Integer.parseInt(components[1]), Integer.parseInt(components[2]));
    }

    private void part1(List<Brick> input) {
        List<Brick> bricks = new ArrayList<>(input.stream().sorted(Comparator.<Brick>comparingInt(x -> x.min().z()).thenComparingInt(x -> x.max().z())).toList());

        SetMultimap<Integer, Integer> supports = HashMultimap.create();
        SetMultimap<Integer, Integer> supportedBy = HashMultimap.create();

        for (int i = 0; i < bricks.size(); i++) {
            Brick brick = bricks.get(i);
            int supportingHeight = 0;
            List<Integer> supporting = new ArrayList<>();
            for (int j = i - 1; j >= 0; j--) {
                if (bricks.get(j).under(brick)) {
                    if (bricks.get(j).max.z() > supportingHeight) {
                        supporting.clear();
                        supportingHeight = bricks.get(j).max.z();
                        supporting.add(j);
                    } else if (bricks.get(j).max.z() == supportingHeight) {
                        supporting.add(j);
                    }
                }
            }
            bricks.set(i, brick.settleOn(supportingHeight));
            for (int index : supporting) {
                supports.put(index, i);
                supportedBy.put(i, index);
            }
        }

        int total = 0;
        for (int i = 0; i < bricks.size(); i++) {
            int whollySupports = 0;
            for (int j : supports.get(i)) {
                if (supportedBy.get(j).size() == 1) {
                    whollySupports++;
                }
            }

            if (whollySupports == 0) {
                total++;
            }
        }

        System.out.println("Part 1: " + total);

        bricks.sort(Comparator.<Brick>comparingInt(x -> x.min.z()).thenComparing(x -> x.max.z()));

        long total2 = 0L;
        for (int i = 0; i < bricks.size(); i++) {
            total2 += fallsRemoving(i, bricks, supports, supportedBy);
        }

        System.out.println("Part 2: " + total2);
    }

    private long fallsRemoving(int i, List<Brick> bricks, SetMultimap<Integer, Integer> supports, SetMultimap<Integer, Integer> supportedBy) {
        Set<Integer> removed = new LinkedHashSet<>();
        removed.add(i);

        Deque<Integer> hovering = new ArrayDeque<>();
        hovering.addAll(supports.get(i));

        while (!hovering.isEmpty()) {
            int hover = hovering.removeFirst();
            if (removed.containsAll(supportedBy.get(hover))) {
                removed.add(hover);
                hovering.addAll(supports.get(hover));
            }
        }
        for (int j = i + 1; j < bricks.size(); j++) {
            if (removed.containsAll(supportedBy.get(j))) {
                if (removed.add(j)) {
                 System.out.println("What");
                }
            }
        }
        return removed.size() - 1;
    }

    private static final class Brick {
        private final int id;
        private final Vector3ic min;
        private final Vector3ic max;

        private Brick(Vector3ic a, Vector3ic b, int id) {
            this.min = a.min(b, new Vector3i());
            this.max = a.max(b, new Vector3i());
            this.id = id;
        }

        public Vector3ic min() {
            return min;
        }

        public Vector3ic max() {
            return max;
        }

        public boolean under(Brick other) {
            return other.min.x() <= max.x() && other.max.x() >= min.x() && other.min.y() <= max.y() && other.max.y() >= min.y();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Brick) obj;
            return Objects.equals(this.min, that.min) &&
                    Objects.equals(this.max, that.max);
        }

        @Override
        public int hashCode() {
            return Objects.hash(min, max);
        }

        @Override
        public String toString() {
            return "Brick[" + id + ']';
        }

        public Brick settleOn(int supportingHeight) {
            int dist = min.z() - supportingHeight - 1;
            return new Brick(min.sub(0, 0, dist, new Vector3i()), max.sub(0, 0, dist, new Vector3i()), id);
        }
    }

}

