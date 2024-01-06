package xyz.immortius.advent2018.day23;

import com.google.common.io.CharStreams;
import org.joml.Vector3i;
import org.joml.Vector3ic;
import xyz.immortius.util.Vector3l;
import xyz.immortius.util.Vector3lc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day23 {

    private static final String YEAR = "2018";
    private static final String DAY = "23";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day23().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Nanobot> nanobots = parse(lines);
        part1(nanobots);
        part2(nanobots);
    }

    private List<Nanobot> parse(List<String> lines) {
        List<Nanobot> result = new ArrayList<>();
        for (String line : lines) {
            int endPos = line.indexOf('>');
            long[] dims = Arrays.stream(line.substring(5, endPos).split(",")).mapToLong(Long::parseLong).toArray();
            Vector3l pos = new Vector3l(dims[0], dims[1], dims[2]);
            long range = Long.parseLong(line.substring(line.lastIndexOf('=') + 1));
            result.add(new Nanobot(pos, range));
        }
        return result;
    }

    private void part1(List<Nanobot> nanobots) {
        Nanobot strongest = nanobots.stream().max(Comparator.comparingLong(Nanobot::range)).get();
        long inRange = 0;
        for (Nanobot nanobot : nanobots) {
            if (nanobot.position.gridDistance(strongest.position) <= strongest.range) {
                inRange++;
            }
        }

        System.out.println("Part 1: " + inRange);
    }

    private void part2(List<Nanobot> nanobots) {
        List<Nanobot> largestOverlappingSet = findMaxOverlaps(nanobots);

//        Vector3i min = new Vector3i(Integer.MIN_VALUE);
//        Vector3i max = new Vector3i(Integer.MAX_VALUE);
//        for (Nanobot bot : largestOverlappingSet) {
//            Vector3i minPos = bot.position.sub(bot.range, bot.range, bot.range, new Vector3i());
//            Vector3i maxPos = bot.position.add(bot.range, bot.range, bot.range, new Vector3i());
//            min.max(minPos);
//            max.min(maxPos);
//        }
//
        //Nanobot smallestRange = largestOverlappingSet.stream().min(Comparator.comparingInt(Nanobot::range)).get();

        Prism prism = new Prism(largestOverlappingSet.get(0));
        for (int i = 1; i < largestOverlappingSet.size(); i++) {
            prism = prism.overlap(new Prism(largestOverlappingSet.get(i)));
        }

        long bestDistance = Integer.MAX_VALUE;
        int bestOverlaps = 0;
        Vector3l bestPos = new Vector3l();

        Vector3l pos = new Vector3l();
        for (Vector3lc corner : prism.getCorners()) {
            {
                pos.set(corner);

                int overlaps = countOverlaps(pos, nanobots);
                long dist = pos.gridDistance(0, 0, 0);
                if (overlaps > bestOverlaps || (overlaps == bestOverlaps && dist < bestDistance)) {
                    bestDistance = dist;
                    bestOverlaps = overlaps;
                    bestPos.set(pos);
                }
            }
        }
//
//        for (int x = Math.max(-smallestRange.range + smallestRange.position.x(), min.x); x <= Math.min(smallestRange.range + smallestRange.position.x(), max.x()); x++) {
//
//            int yzRange = smallestRange.range - Math.abs(x - smallestRange.position.x());
//            for (int y = Math.max(-yzRange + smallestRange.position.y(), min.y); y <= Math.min(yzRange + smallestRange.position.y(), max.y); y++) {
//                int z = yzRange - Math.abs(y - smallestRange.position.y());
//                for (int zSign = -1; zSign <= 1; zSign++) {
//                    Vector3i pos = new Vector3i(x, y, z * zSign + smallestRange.position.z());
//                    if (overlaps(pos, largestOverlappingSet)) {
//                        long dist = pos.gridDistance(0,0,0);
//                        if (dist < bestDistance) {
//                            bestPos.set(pos);
//                            bestDistance = dist;
//                        }
//                    }
//                }
//            }
//        }

        System.out.println("Part 2: " + bestDistance + " (" + bestOverlaps + ")");
    }

    private int countOverlaps(Vector3lc pos, List<Nanobot> nanobots) {
        int count = 0;
        for (Nanobot bot : nanobots) {
            long dist = bot.position.gridDistance(pos);
            if (dist <= bot.range) {
                count++;
            }
        }
        return count;
    }

    private List<Nanobot> findMaxOverlaps(List<Nanobot> unconsidered) {
        List<List<Nanobot>> overlappingSets = new ArrayList<>();
        for (int i = 0; i < unconsidered.size(); i++) {
            Nanobot nanobot = unconsidered.get(i);
            boolean overlapFound = false;
            for (List<Nanobot> set : overlappingSets) {
                if (overlaps(nanobot, set)) {
                    set.add(nanobot);
                    overlapFound = true;
                }
            }
            if (!overlapFound) {
                List<Nanobot> newSet = new ArrayList<>();
                newSet.add(nanobot);
                for (int j = 0; j < i; j++) {
                    if (overlaps(unconsidered.get(j), newSet)) {
                        newSet.add(unconsidered.get(j));
                    }
                }
                overlappingSets.add(newSet);
            }
        }
        return overlappingSets.stream().max(Comparator.comparingInt(List::size)).get();
    }

    private boolean overlaps(Nanobot n, List<Nanobot> overlapWith) {
        for (Nanobot other : overlapWith) {
            if (other.position.gridDistance(n.position) > n.range + other.range - 1) {
                return false;
            }
        }
        return true;
    }

    private boolean overlaps(Vector3l pos, List<Nanobot> overlapWith) {
        for (Nanobot other : overlapWith) {
            if (other.position.gridDistance(pos) > other.range) {
                return false;
            }
        }
        return true;
    }

    private record Nanobot(Vector3lc position, long range) {
    }

    private static class Prism {
        private long pXpYpZ;
        private long pXnYpZ;
        private long nXpYpZ;
        private long nXnYpZ;
        private long pXpYnZ;
        private long pXnYnZ;
        private long nXpYnZ;
        private long nXnYnZ;

        public Prism(Nanobot bot) {
            pXpYpZ = bot.position.x() + bot.position.y() + bot.position.z() + bot.range;
            nXnYnZ = bot.position.x() + bot.position.y() + bot.position.z() - bot.range;

            pXnYnZ = bot.position.x() - bot.position.y() - bot.position.z() + bot.range;
            nXpYpZ = bot.position.x() - bot.position.y() - bot.position.z() - bot.range;

            pXnYpZ = bot.position.x() - bot.position.y() + bot.position.z() + bot.range;
            nXpYnZ = bot.position.x() - bot.position.y() + bot.position.z() - bot.range;

            pXpYnZ = bot.position.x() + bot.position.y() - bot.position.z() + bot.range;
            nXnYpZ = bot.position.x() + bot.position.y() - bot.position.z() - bot.range;
        }

        private Prism() {}

        public Prism overlap(Prism other) {
            Prism result = new Prism();
            result.pXpYpZ = Math.min(pXpYpZ, other.pXpYpZ);
            result.nXnYnZ = Math.max(nXnYnZ, other.nXnYnZ);
            result.pXnYnZ = Math.min(pXnYnZ, other.pXnYnZ);
            result.nXpYpZ = Math.max(nXpYpZ, other.nXpYpZ);
            result.pXnYpZ = Math.min(pXnYpZ, other.pXnYpZ);
            result.nXpYnZ = Math.max(nXpYnZ, other.nXpYnZ);
            result.pXpYnZ = Math.min(pXpYnZ, other.pXpYnZ);
            result.nXnYpZ = Math.max(nXnYpZ, other.nXnYpZ);
            return result;
        }

        public List<Vector3l> getCorners() {
            List<Vector3l> corners = new ArrayList<>();


//            long pY = (pXpYpZ - nXpYnZ) / 2;
//            long pZ = (pXpYpZ - nXnYpZ) / 2;
//
//            long nX = (- nXnYnZ - nXpYpZ) / 2;
//            long nY = (pXnYpZ - nXnYnZ) / 2;
//            long nZ = (pXpYnZ - nXnYnZ) / 2;
//
//
//            long pXpZ = pXpYpZ - pY;
//            long pXpY = pXpYpZ - pZ;
//
//            long nYnZ = -nXnYnZ - nX;
//            long nXnZ = -nXnYnZ - nY;
//            long nXnY = -nXnYnZ - nZ;

            // pX corner
            {
                long pX = (pXpYpZ + pXnYnZ) / 2;
                long pYpZ = pXpYpZ - pX;
                long pYnZ = pXpYnZ - pX;
                long pY = (pYpZ + pYnZ) / 2;
                corners.add(new Vector3l(pX, pY, pYpZ - pY));
            }
            {
                long pY = (pXpYpZ - nXpYnZ) / 2;
                long pXpZ = pXpYpZ - pY;
                long pXnZ = pXpYnZ - pY;
                long pX = (pXpZ + pXnZ) / 2;
                corners.add(new Vector3l(pX, pY, pXpZ - pX));
            }
            {
                long pZ = (pXpYpZ - nXnYpZ) / 2;
                long pXpY = pXpYpZ - pZ;
                long pXnY = pXnYpZ - pZ;
                long pX = (pXpY + pXnY) / 2;
                corners.add(new Vector3l(pX, pXpY - pX, pZ));
            }
            {
                long nX = (- nXnYnZ - nXpYpZ) / 2;
                long pYpZ = nXpYpZ - nX;
                long pYnZ = nXpYnZ - nX;
                long pY = (pYpZ + pYnZ) / 2;
                corners.add(new Vector3l(nX, pY, pYpZ - pY));
            }
            {
                long nY = (pXnYpZ - nXnYnZ) / 2;
                long pXpZ = pXnYpZ - nY;
                long pXnZ = pXnYnZ - nY;
                long pX = (pXpZ + pXnZ) / 2;
                corners.add(new Vector3l(pX, nY, pXpZ - pX));
            }
            {
                long nZ = (pXpYnZ - nXnYnZ) / 2;
                long pXpY = pXpYnZ - nZ;
                long pXnY = pXnYnZ - nZ;
                long pX = (pXpY + pXnY) / 2;
                corners.add(new Vector3l(pX, pXpY - pX, nZ));
            }
            return corners;
        }
    }
}


