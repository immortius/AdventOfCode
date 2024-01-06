package xyz.immortius.advent2017.day20;

import com.google.common.io.CharStreams;
import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day20 {

    private final Pattern linePattern = Pattern.compile("p=<(?<position>[0-9-]+,[0-9-]+,[0-9-]+)>, v=<(?<velocity>[0-9-]+,[0-9-]+,[0-9-]+)>, a=<(?<acceleration>[0-9-]+,[0-9-]+,[0-9-]+)>");

    private static final String YEAR = "2017";
    private static final String DAY = "20";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day20().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        part1(parse(lines));
        part2(parse(lines));
    }

    private List<Particle> parse(List<String> lines) {
        List<Particle> particles = new ArrayList<>();
        int idSource = 0;
        for (String line : lines) {
            Matcher matcher = linePattern.matcher(line);
            if (matcher.matches()) {
                particles.add(new Particle(idSource++, parseVector(matcher.group("position")), parseVector(matcher.group("velocity")), parseVector(matcher.group("acceleration"))));
            }
        }
        return particles;
    }

    private Vector3i parseVector(String input) {
        String[] dims = input.split(",");
        return new Vector3i(Integer.parseInt(dims[0]), Integer.parseInt(dims[1]), Integer.parseInt(dims[2]));
    }

    private void part1(List<Particle> particles) {
        long minAcceleration = particles.stream().map(x -> x.acceleration.gridDistance(0, 0, 0)).min(Long::compareTo).get();
        List<Particle> relevantParticles = particles.stream().filter(x -> x.acceleration().gridDistance(0, 0, 0) == minAcceleration).toList();

        while (!checkResolved(relevantParticles)) {
            simulate(relevantParticles);
        }

        Particle particle = relevantParticles.stream().min(Comparator.comparingLong(x -> x.position.gridDistance(0, 0, 0))).get();


        System.out.println("Part 1: " + particle.id);
    }

    private void simulate(List<Particle> particles) {
        for (Particle particle : particles) {
            particle.velocity.add(particle.acceleration);
            particle.position.add(particle.velocity);
        }
    }

    private boolean checkResolved(List<Particle> particles) {
        for (Particle particle : particles) {
            for (int i = 0; i < 3; i++) {
                if ((particle.acceleration.get(i) != 0 && Integer.signum(particle.acceleration.get(i)) != Integer.signum(particle.velocity.get(i)))
                        || (particle.velocity.get(i) != 0 && Integer.signum(particle.velocity.get(i)) != Integer.signum(particle.position.get(i)))) {
                    return false;
                }
            }
        }
        return true;

    }

    private void part2(List<Particle> particles) {
        int steps = 0;
        while (steps < 5000) {
            steps++;
            simulate(particles);
            List<Particle> eliminated = new ArrayList<>();
            for (int a = 0; a < particles.size(); a++) {
                for (int b = a + 1; b < particles.size(); b++) {
                    if (particles.get(a).position.equals(particles.get(b).position)) {
                        eliminated.add(particles.get(a));
                        eliminated.add(particles.get(b));
                    }
                }
            }
            particles.removeAll(eliminated);
        }
        System.out.println("Part 2: " + particles.size());
    }

    static final class Particle {
        private final int id;
        private final Vector3i position;
        private final Vector3i velocity;
        private final Vector3i acceleration;

        Particle(Particle other) {
            this(other.id, other.position, other.velocity, other.acceleration);
        }

        Particle(int id, Vector3ic position, Vector3ic velocity, Vector3ic acceleration) {
            this.id = id;
            this.position = new Vector3i(position);
            this.velocity = new Vector3i(velocity);
            this.acceleration = new Vector3i(acceleration);
        }

        @Override
        public String toString() {
            return "Particle{" +
                    "id=" + id +
                    ", position=" + position.x + "," + position.y + "," + position.z +
                    ", velocity=" + velocity.x + "," + velocity.y + "," + velocity.z +
                    ", acceleration=" + acceleration.x + "," + acceleration.y + "," + acceleration.z +
                    '}';
        }

        public int id() {
            return id;
        }

        public Vector3i position() {
            return position;
        }

        public Vector3i velocity() {
            return velocity;
        }

        public Vector3i acceleration() {
            return acceleration;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Particle) obj;
            return this.id == that.id &&
                    Objects.equals(this.position, that.position) &&
                    Objects.equals(this.velocity, that.velocity) &&
                    Objects.equals(this.acceleration, that.acceleration);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, position, velocity, acceleration);
        }

    }

}

