package xyz.immortius.advent2019.day12;

import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day12 {

    private static final String YEAR = "2019";
    private static final String DAY = "12";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day12().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Moon> moons = parse(lines);
        part1(moons.stream().map(Moon::new).toList());
        part2(moons.stream().map(Moon::new).toList());
    }

    private List<Moon> parse(List<String> lines) {
        List<Moon> moons = new ArrayList<>();
        for (String line : lines) {
            Vector3i position = new Vector3i(Arrays.stream(line.substring(1, line.length() - 1).split(", ")).map(x -> x.split("=")[1]).mapToInt(Integer::parseInt).toArray());
            moons.add(new Moon(position, new Vector3i()));
        }
        return moons;
    }

    private void part1(List<Moon> moons) {
        for (int step = 0; step < 1000; step++) {
            applyGravity(moons);
            applyMovement(moons);
        }
        int energy = calculateSystemEnergy(moons);
        System.out.println("Part 1: " + energy);
    }

    private void part2(List<Moon> moons) {
        long xLoop = findDimensionLoop(moons, 0);
        long yLoop = findDimensionLoop(moons, 1);
        long zLoop = findDimensionLoop(moons, 2);

        long xySteps = xLoop;
        while (xySteps % yLoop != 0) {
            xySteps += xLoop;
        }
        long xyzSteps = xySteps;
        while (xyzSteps % zLoop != 0) {
            xyzSteps += xySteps;
        }


        System.out.println("Part 2: " + xyzSteps);
    }

    @NotNull
    private long findDimensionLoop(List<Moon> moons, int dimension) {
        DimState xState = new DimState(moons, dimension);
        DimState initial = new DimState(xState);
        long step = 0;
        do {
            xState.applyGravity();
            xState.applyMovement();
            step++;
        } while (!initial.equals(xState));
        return step;
    }


    private int calculateSystemEnergy(List<Moon> moons) {
        int energy = 0;
        for (Moon moon : moons) {
            int ke = 0;
            int pe = 0;
            for (int i = 0; i < 3; i++) {
                pe += Math.abs(moon.position.get(i));
                ke += Math.abs(moon.velocity.get(i));
            }
            energy += ke * pe;
        }
        return energy;
    }

    private void applyMovement(List<Moon> moons) {
        for (Moon moon : moons) {
            moon.position.add(moon.velocity);
        }
    }

    private void applyGravity(List<Moon> moons) {
        for (int m1 = 0; m1 < moons.size() - 1; m1++) {
            Moon moon1 = moons.get(m1);
            for (int m2 = m1 + 1; m2 < moons.size(); m2++) {
                Moon moon2 = moons.get(m2);
                applyGravity(moon1, moon2);
            }
        }
    }

    private void applyGravity(Moon fromMoon, Moon toMoon) {
        for (int component = 0; component < 3; component++) {
            int diff = Integer.signum(toMoon.position.get(component) - fromMoon.position.get(component));
            toMoon.velocity.setComponent(component, toMoon.velocity.get(component) - diff);
            fromMoon.velocity.setComponent(component, fromMoon.velocity.get(component) + diff);
        }
    }

    private record Moon(Vector3i position, Vector3i velocity) {
        public Moon(Moon other) {
            this(new Vector3i(other.position), new Vector3i(other.velocity));
        }
    }

    private static class DimState {
        private long[] pos;
        private long[] vel;

        public DimState(List<Moon> moons, int comp) {
            pos = new long[4];
            vel = new long[4];
            for (int i = 0; i < moons.size(); i++) {
                pos[i] = moons.get(i).position.get(comp);
            }
        }

        public DimState(DimState other) {
            pos = Arrays.copyOf(other.pos, other.pos.length);
            vel = Arrays.copyOf(other.vel, other.vel.length);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DimState dimState = (DimState) o;

            if (!Arrays.equals(pos, dimState.pos)) return false;
            return Arrays.equals(vel, dimState.vel);
        }

        @Override
        public int hashCode() {
            int result = Arrays.hashCode(pos);
            result = 31 * result + Arrays.hashCode(vel);
            return result;
        }

        public void applyGravity() {
            for (int i = 0; i < pos.length - 1; i++) {
                for (int k = i + 1; k < pos.length; k++) {
                    int diff = Long.signum(pos[k] - pos[i]);
                    vel[k] -= diff;
                    vel[i] += diff;
                }
            }
        }

        public void applyMovement() {
            for (int i = 0; i < pos.length; i++) {
                pos[i] += vel[i];
            }
        }
    }


}

