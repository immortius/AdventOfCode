package xyz.immortius.advent2019.day10;

import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day10 {

    private static final String YEAR = "2019";
    private static final String DAY = "10";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day10().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        Set<Vector2ic> asteroids = parse(lines);
        part1(asteroids);
    }

    private Set<Vector2ic> parse(List<String> lines) {
        Set<Vector2ic> asteroids = new LinkedHashSet<>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.length(); x++) {
                if (line.charAt(x) == '#') {
                    asteroids.add(new Vector2i(x, y));
                }
            }
        }
        return asteroids;
    }

    private void part1(Set<Vector2ic> asteroids) {
        Vector2i laserPos = determineBaseAsteroid(asteroids);

        List<Vector2ic> targetAsteroids = detectAsteroids(asteroids, laserPos);
        targetAsteroids.sort(Comparator.comparingDouble((x) -> {
            Vector2i dir = x.sub(laserPos, new Vector2i());
            double result = Math.atan2(dir.y, dir.x);
            if (result <= -0.5 * Math.PI - 0.001) {
                result += 2 * Math.PI;
            }
            return result;
        }));
        Vector2ic twoHundreth = targetAsteroids.get(199);
        System.out.println("Part 2: " + twoHundreth.x() + "," + twoHundreth.y() + " - " + (twoHundreth.x() * 100 + twoHundreth.y()));
    }

    @NotNull
    private Vector2i determineBaseAsteroid(Set<Vector2ic> asteroids) {
        List<Vector2ic> bestDetected = new ArrayList<>();
        Vector2i bestAsteroid = new Vector2i(0,0);
        for (Vector2ic asteroid : asteroids) {
            List<Vector2ic> detectedAsteroids = detectAsteroids(asteroids, asteroid);
            if (detectedAsteroids.size() > bestDetected.size()) {
                bestAsteroid.set(asteroid);
                bestDetected = detectedAsteroids;
            }
        }

        System.out.println("Part 1: " + bestDetected.size() + " at " + bestAsteroid.x + "," + bestAsteroid.y);
        return bestAsteroid;
    }

    @NotNull
    private List<Vector2ic> detectAsteroids(Set<Vector2ic> asteroids, Vector2ic from) {
        List<Vector2ic> detectedAsteroids = new ArrayList<>();
        for(Vector2ic other : asteroids) {
            if (other.equals(from)) {
                continue;
            }

            Vector2i dir = other.sub(from, new Vector2i());
            int smallestComponent = Math.min(Math.abs(dir.x), Math.abs(dir.y));
            if (smallestComponent == 0) {
                for (int i = 0; i < 2; i++) {
                    dir.setComponent(i, Math.signum(dir.get(i)));
                }
            } else {
                for (int factor = smallestComponent; factor > 1; factor--) {
                    if (dir.x % factor == 0 && dir.y % factor == 0) {
                        dir.div(factor);
                        break;
                    }
                }
            }

            Vector2i pos = new Vector2i(from).add(dir);
            boolean blocked = false;
            while (!pos.equals(other)) {
                if (asteroids.contains(pos)) {
                    blocked = true;
                    break;
                }
                pos.add(dir);
            }

            if (!blocked) {
                detectedAsteroids.add(other);
            }
        }
        return detectedAsteroids;
    }

}

