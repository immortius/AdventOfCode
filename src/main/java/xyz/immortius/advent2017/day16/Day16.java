package xyz.immortius.advent2017.day16;

import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day16 {

    private static final String YEAR = "2017";
    private static final String DAY = "16";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day16().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        Data data = parse(lines);
        part1(data);
        part2(data);
    }

    private Data parse(List<String> lines) {
        int programs = Integer.parseInt(lines.get(0));

        String[] rawCommands = lines.get(1).split(",");
        List<Command> commands = new ArrayList<>();
        for (String cmd : rawCommands) {
            switch (cmd.charAt(0)) {
                case 's' -> commands.add(new Command(CommandType.Spin, Integer.parseInt(cmd.substring(1)), 0));
                case 'x' -> {
                    List<Integer> targets = Arrays.stream(cmd.substring(1).split("/")).map(Integer::parseInt).toList();
                    commands.add(new Command(CommandType.Exchange, targets.get(0), targets.get(1)));
                }
                case 'p' -> commands.add(new Command(CommandType.Partner, cmd.charAt(1) - 'a', cmd.charAt(3) - 'a' ));
            }
        }

        return new Data(programs, commands);
    }

    private void part1(Data data) {
        int[] programs = new int[data.programCount];
        for (int i = 0; i < data.programCount; i++) {
            programs[i] = i;
        }
        int offset = 0;

        for (Command cmd : data.commands) {
            switch (cmd.type) {
                case Spin -> offset = (((offset - cmd.a) % programs.length) + programs.length) % programs.length;
                case Exchange -> {
                    int aIndex = (cmd.a + offset) % programs.length;
                    int bIndex = (cmd.b + offset) % programs.length;
                    swap(programs, aIndex, bIndex);
                }
                case Partner -> {
                    int aIndex = indexOf(programs, cmd.a);
                    int bIndex = indexOf(programs, cmd.b);
                    swap(programs, aIndex, bIndex);
                }
            }
        }

        String result = toString(programs, offset);

        System.out.println("Part 1: " + result);
    }

    @NotNull
    private String toString(int[] programs, int offset) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < programs.length; i++) {
            int value = programs[(i + offset) % programs.length];
            result.append((char)('a' + value));
        }
        String v = result.toString();
        return v;
    }

    private void swap(int[] programs, int aIndex, int bIndex) {
        int temp = programs[aIndex];
        programs[aIndex] = programs[bIndex];
        programs[bIndex] = temp;
    }

    private int indexOf(int[] array, int target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }

    private void part2(Data data) {
        int[] programs = new int[data.programCount];
        for (int i = 0; i < data.programCount; i++) {
            programs[i] = i;
        }
        int offset = 0;

        List<String> states = new ArrayList<>();
        for (long dance = 0; dance < 1_000_000_000; dance++) {
            for (Command cmd : data.commands) {
                switch (cmd.type) {
                    case Spin -> offset = (((offset - cmd.a) % programs.length) + programs.length) % programs.length;
                    case Exchange -> {
                        int aIndex = (cmd.a + offset) % programs.length;
                        int bIndex = (cmd.b + offset) % programs.length;
                        swap(programs, aIndex, bIndex);
                    }
                    case Partner -> {
                        int aIndex = indexOf(programs, cmd.a);
                        int bIndex = indexOf(programs, cmd.b);
                        swap(programs, aIndex, bIndex);
                    }
                }
            }
            String state = toString(programs, offset);

            int lastIndex = states.indexOf(state);
            if (lastIndex != -1) {
                int cycleLength = states.size() - lastIndex;
                System.out.println("Loop detected at " + dance + " from " + lastIndex + " - cycle length " + cycleLength);
                dance = 1_000_000_000 - (1_000_000_000 - dance) % cycleLength;
                states.clear();
            }
            states.add(state);
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < programs.length; i++) {
            int value = programs[(i + offset) % programs.length];
            result.append((char)('a' + value));
        }

        System.out.println("Part 2: " + result);
    }

    public record Data(int programCount, List<Command> commands) {}

    public record Command(CommandType type, int a, int b) {

    }

    public enum CommandType {
        Spin,
        Exchange,
        Partner
    }


}

