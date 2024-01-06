package xyz.immortius.advent2017.day25;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day25 {

    private static final String YEAR = "2017";
    private static final String DAY = "25";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day25().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        TuringMachine turingMachine = parse(lines);
        part1(turingMachine);
    }

    private TuringMachine parse(List<String> lines) {
        String initialStateLine = lines.get(0);
        char initialState = initialStateLine.charAt(initialStateLine.length() - 2);
        String[] diagnosticStepLineParts = lines.get(1).split("\\s+");
        int diagnosticStep = Integer.parseInt(diagnosticStepLineParts[diagnosticStepLineParts.length - 2]);

        Map<Character, State> states = new HashMap<>();
        for (int lineIndex = 3; lineIndex < lines.size(); lineIndex += 10) {
            char id = lines.get(lineIndex).charAt(lines.get(lineIndex).length() - 2);
            Action onFalse = new Action(lines.get(lineIndex + 2).charAt(lines.get(lineIndex + 2).length() - 2) == '1', lines.get(lineIndex + 3).endsWith("right.") ? 1 : -1, lines.get(lineIndex + 4).charAt(lines.get(lineIndex + 4).length() - 2));
            Action onTrue = new Action(lines.get(lineIndex + 6).charAt(lines.get(lineIndex + 6).length() - 2) == '1', lines.get(lineIndex + 7).endsWith("right.") ? 1 : -1, lines.get(lineIndex + 8).charAt(lines.get(lineIndex + 8).length() - 2));
            states.put(id, new State(id, onFalse, onTrue));
        }
        return new TuringMachine(initialState, diagnosticStep, states);
    }

    private void part1(TuringMachine turingMachine) {
        System.out.println("Part 1: " + turingMachine.run());
    }


    public static class TuringMachine {
        private final char initialState;
        private final int diagnosticStep;
        private final Map<Character, State> states;

        public TuringMachine(char initialState, int diagnosticStep, Map<Character, State> states) {
            this.initialState = initialState;
            this.diagnosticStep = diagnosticStep;
            this.states = states;

        }

        public long run() {
            List<Boolean> values = new ArrayList<>();
            values.add(false);
            int cursor = 0;
            char currentState = initialState;
            for (int i = 0; i < diagnosticStep; i++) {
                State state = states.get(currentState);
                Action action = values.get(cursor) ? state.onTrue : state.onFalse;
                values.set(cursor, action.write);
                currentState = action.newState;
                cursor += action.dir;
                if (cursor == -1) {
                    values.add(0, false);
                    cursor = 0;
                } else if (cursor == values.size()) {
                    values.add(false);
                }
            }

            return values.stream().filter(x -> x).count();
        }
    }

    public record State(char id, Action onFalse, Action onTrue) {}

    public record Action(boolean write, int dir, char newState) {}

}

