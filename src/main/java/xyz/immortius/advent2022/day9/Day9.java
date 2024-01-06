package xyz.immortius.advent2022.day9;

import com.google.common.io.CharStreams;
import org.joml.Vector2ic;
import xyz.immortius.util.Direction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day9 {
    public static void main(String[] args) throws IOException {
        new Day9().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day9/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        process(lines);
    }

    private void process(List<String> lines) {
        List<Command> commands = lines.stream().map(line -> {
            String[] parts = line.split(" ");
            return new Command(Direction.parse(parts[0].charAt(0)), Integer.parseInt(parts[1]));
        }).toList();

        Set<Vector2ic> tailPositions = new HashSet<>();
        RopeSimulation simulation = new RopeSimulation(10);
        for (Command command : commands) {
            for (int i = 0; i < command.steps; i++) {
                simulation.moveHead(command.dir);
                tailPositions.add(simulation.getTailPos());
            }
        }

        System.out.println(tailPositions.size());
    }

    private record Command(Direction dir, int steps) {
    }
}

