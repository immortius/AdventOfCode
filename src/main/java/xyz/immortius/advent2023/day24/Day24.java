package xyz.immortius.advent2023.day24;

import com.google.common.io.CharStreams;
import com.google.common.math.LongMath;
import org.joml.Vector3d;
import xyz.immortius.util.Vector2lc;
import xyz.immortius.util.Vector3l;
import xyz.immortius.util.Vector3lc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Day24 {

    //private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    private static final String YEAR = "2023";
    private static final String DAY = "24";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day24().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Stone> input = parse(lines);
        part1(input);
        part2(input);
    }

    private void part2(List<Stone> input) {

        Vector3l offset = input.stream().map(x -> new Vector3l(x.position)).reduce(new Vector3l(Long.MAX_VALUE), (x, y) -> x.min(y, new Vector3l()));

        Stone a = input.get(0);
        Stone b = input.get(1);
        Stone c = input.get(3);

        Vector3lc vec0 = a.position.sub(offset, new Vector3l());
        Vector3lc vec1 = a.velocity;
        Vector3lc vec2 = b.position.sub(offset, new Vector3l());
        Vector3lc vec3 = b.velocity;
        Vector3lc vec4 = c.position.sub(offset, new Vector3l());
        Vector3lc vec5 = c.velocity;

        Vector3lc A = vec4.sub(vec0, new Vector3l());
        Vector3lc B = vec2.sub(vec0, new Vector3l());
        Vector3lc C = vec5.sub(vec1, new Vector3l());
        Vector3lc D = vec3.sub(vec5, new Vector3l());

        BigInteger axdz = BigInteger.valueOf(A.x()).multiply(BigInteger.valueOf(D.z())).subtract(BigInteger.valueOf(A.z()).multiply(BigInteger.valueOf(D.x())));
        BigInteger aybx = BigInteger.valueOf(A.y()).multiply(BigInteger.valueOf(B.x())).subtract(BigInteger.valueOf(A.x()).multiply(BigInteger.valueOf(B.y())));
        BigInteger axdy = BigInteger.valueOf(A.x()).multiply(BigInteger.valueOf(D.y())).subtract(BigInteger.valueOf(A.y()).multiply(BigInteger.valueOf(D.x())));
        BigInteger azbx = BigInteger.valueOf(A.z()).multiply(BigInteger.valueOf(B.x())).subtract(BigInteger.valueOf(A.x()).multiply(BigInteger.valueOf(B.z())));
        BigInteger cxdy = BigInteger.valueOf(D.y()).multiply(BigInteger.valueOf(C.x())).subtract(BigInteger.valueOf(D.x()).multiply(BigInteger.valueOf(C.y())));
        BigInteger cxdz = BigInteger.valueOf(D.z()).multiply(BigInteger.valueOf(C.x())).subtract(BigInteger.valueOf(D.x()).multiply(BigInteger.valueOf(C.z())));

        BigInteger e = cxdz.multiply(BigInteger.valueOf(LongMath.checkedMultiply(A.y(), C.x()) + LongMath.checkedMultiply(B.x(), C.y()) - LongMath.checkedMultiply(A.x() , C.y()) - LongMath.checkedMultiply(B.y(), C.x()))).subtract(cxdy.multiply(BigInteger.valueOf(LongMath.checkedMultiply(A.z(),C.x()) + LongMath.checkedMultiply(B.x(), C.z()) - LongMath.checkedMultiply(A.x(), C.z()) - LongMath.checkedMultiply(B.z(), C.x()))));
        // axdz * (A.y() * C.x() + B.x() * C.y() - A.x() * C.y() - B.y() * C.x()) + aybx * cxdz - axdy * (A.z() * C.x() + B.x() * C.z() - A.x() * C.z() - B.z() * C.x()) - azbx * cxdy;
        BigInteger f = axdz.multiply(BigInteger.valueOf(LongMath.checkedMultiply(A.y(), C.x()) + LongMath.checkedMultiply(B.x(), C.y()) - LongMath.checkedMultiply(A.x(), C.y()) - LongMath.checkedMultiply(B.y(), C.x()))).add(aybx.multiply(cxdz)).subtract(
                axdy.multiply(BigInteger.valueOf(LongMath.checkedMultiply(A.z(), C.x()) + LongMath.checkedMultiply(B.x(), C.z()) - LongMath.checkedMultiply(A.x(), C.z()) - LongMath.checkedMultiply(B.z(), C.x())))).subtract(azbx.multiply(cxdy));
        BigInteger g = axdz.multiply(aybx).subtract(axdy.multiply(azbx));

        List<Long> t0s = new ArrayList<>();
        if (e.longValue() == 0) {
            t0s.add(g.divide(f).negate().longValue());
            System.out.println(g.abs().mod(f.abs()));
        } else {
            BigInteger factor = f.multiply(f).subtract(BigInteger.valueOf(4).multiply(e).multiply(g)).sqrt();
            t0s.add(f.negate().add(factor).divide(BigInteger.valueOf(2).multiply(e)).longValue());
            t0s.add(f.negate().subtract(factor).divide(BigInteger.valueOf(2).multiply(e)).longValue());
        }

        for (long t0 : t0s) {

            long t1Denom = (A.x() * D.y() - A.y() * D.x() + t0 * (D.y() * C.x() - D.x() * C.y()));

            if (t1Denom == 0) {
                continue;
            }

            BigInteger t1Numer = BigInteger.valueOf(A.y()).multiply(BigInteger.valueOf(B.x())).subtract(BigInteger.valueOf(A.x()).multiply(BigInteger.valueOf(B.y()))).add(BigInteger.valueOf(t0).multiply(BigInteger.valueOf(A.y()).multiply(BigInteger.valueOf(C.x())).add(BigInteger.valueOf(B.x()).multiply(BigInteger.valueOf(C.y()))).subtract(BigInteger.valueOf(A.x()).multiply(BigInteger.valueOf(C.y()))).subtract(BigInteger.valueOf(B.y()).multiply(BigInteger.valueOf(C.x())))));
            //long t1Numer = A.y() * B.x() - A.x() * B.y() + t0 * (A.y() * C.x() + B.x() * C.y() - A.x() * C.y() - B.y() * C.x());
            if (t1Numer.abs().mod(BigInteger.valueOf(t1Denom).abs()).longValue() != 0) {
                continue;
            }

            //long t0 = (t1 * (A.x() * vec3.y() - A.y() * vec3.x()) + A.y() * B.x() - A.x() * B.y()) / C;
            long t1 = t1Numer.divide(BigInteger.valueOf(t1Denom)).longValue();
            long numerator = (A.x() + LongMath.checkedMultiply(C.x(), t0));
            long denominator = (B.x() + LongMath.checkedMultiply(t1, D.x()) + LongMath.checkedMultiply(t0, C.x()));

            if (denominator == 0) {
                continue;
            }
            BigInteger timeFactor = BigInteger.valueOf(numerator).multiply(BigInteger.valueOf(t1 - t0));
            System.out.println(timeFactor.abs().mod(BigInteger.valueOf(denominator).abs()));

            long t2 = t0 + timeFactor.divide(BigInteger.valueOf(denominator)).longValue();

            System.out.println("t0: " + t0);
            System.out.println("t1: " + t1);
            System.out.println("t2: " + t2);

            Vector3l t0pos = a.velocity.mul(t0, new Vector3l()).add(a.position);
            Vector3l t1pos = b.velocity.mul(t1, new Vector3l()).add(b.position);

            Vector3l posDiff = t1pos.sub(t0pos, new Vector3l());
            long tdiff = t1 - t0;
            Vector3l vel = posDiff.div(tdiff, new Vector3l());
            Vector3l start = t0pos.sub(vel.mul(t0));
            System.out.println(start.x + " , " + start.y + " , " + start.z);

            System.out.println("Part 2: " + (start.x + start.y + start.z));
        }
    }

    public long findDistance(Stone a, Stone b, Stone c, long t1, long t2) {
        Vector3lc pos1 = a.velocity.mul(t1, new Vector3l()).add(a.position);
        Vector3lc pos2 = b.velocity.mul(t2, new Vector3l()).add(b.position);

        Vector3lc pos3 = c.position;
        Vector3lc pos4 = c.velocity.add(pos3, new Vector3l());

        BigInteger numeratorA = BigInteger.valueOf(pos1.x() - pos3.x()).multiply(BigInteger.valueOf(pos3.y() - pos4.y())).subtract(BigInteger.valueOf(pos1.y() - pos3.y()).multiply(BigInteger.valueOf(pos3.x() - pos4.x())));
        BigInteger numeratorB = BigInteger.valueOf(pos1.x() - pos3.x()).multiply(BigInteger.valueOf(pos1.y() - pos2.y())).subtract(BigInteger.valueOf(pos1.y() - pos3.y()).multiply(BigInteger.valueOf(pos1.x() - pos2.x())));
        BigInteger denominator = BigInteger.valueOf(pos1.x() - pos2.x()).multiply(BigInteger.valueOf(pos3.y() - pos4.y())).subtract(BigInteger.valueOf(pos1.y() - pos2.y()).multiply(BigInteger.valueOf(pos3.x() - pos4.x())));
        double t = numeratorA.divide(denominator).doubleValue();
        Vector3d posT = new Vector3d(pos2.x() - pos1.x(), pos2.y() - pos1.y(), pos2.z() - pos1.z()).mul(t).add(pos1.x(), pos1.y(), pos1.z());
        double u = numeratorB.divide(denominator).doubleValue();
        Vector3d posU = new Vector3d(c.velocity.x(), c.velocity.y(), c.velocity.z()).mul(u).add(pos3.x(), pos3.y(), pos3.z());

        long diff = (long) (posT.z - posU.z);
        System.out.println(t1 + " " + t2 + ": " + diff);
        return diff;
    }

    private List<Stone> parse(List<String> lines) {
        return lines.stream().map(x -> x.split("\\s+@\\s+")).map(x -> new Stone(parseVector(x[0]), parseVector(x[1]))).toList();
    }

    private void part1(List<Stone> input) {
        long min = 200000000000000L;
        long max = 400000000000000L;

        long count = 0;
        //List<LineSegment2> lineSegments = input.stream().map(x -> x.toLineSegment2(min, max)).toList();
        for (int i = 0; i < input.size() - 1; i++) {
            Stone a = input.get(i);
            Vector3lc pos2 = a.pos2();
            for (int j = i + 1; j < input.size(); j++) {
                Stone b = input.get(j);
                Vector3lc pos4 = b.pos2();
                long numeratorA = (a.position.x() - b.position.x()) * (b.position.y() - pos4.y()) - (a.position.y() - b.position.y()) * (b.position.x() - pos4.x());
                long numeratorB = (a.position.x() - b.position.x()) * (a.position.y() - pos2.y()) - (a.position.y() - b.position.y()) * (a.position.x() - pos2.x());
                long denominator = (a.position.x() - pos2.x()) * (b.position.y() - pos4.y()) - (a.position.y() - pos2.y()) * (b.position.x() - pos4.x());
                if (denominator != 0) {
                    double t = (double) numeratorA / denominator;
                    double u = (double) numeratorB / denominator;
                    Vector3d pos = new Vector3d(a.velocity.x(), a.velocity.y(), a.velocity.z()).mul(t).add(a.position.x(), a.position.y(), a.position.z());
                    if (t >= 0 && u >= 0 && pos.x >= min && pos.x <= max && pos.y >= min && pos.y <= max) {
                        count++;
                    }
                }
            }
        }

        System.out.println("Part 1: " + count);
    }

    public record Stone(Vector3lc position, Vector3lc velocity) {

        public Vector3lc pos2() {
            return position.add(velocity, new Vector3l());
        }
    }

    public record LineSegment2(Vector2lc a, Vector2lc b) {
    }

    public Vector3lc parseVector(String vector) {
        String[] parts = vector.split(",\\s+");
        return new Vector3l(Long.parseLong(parts[0]), Long.parseLong(parts[1]), Long.parseLong(parts[2]));
    }

}

