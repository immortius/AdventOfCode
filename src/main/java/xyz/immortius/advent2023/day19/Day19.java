package xyz.immortius.advent2023.day19;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import org.joml.Vector4i;
import org.joml.Vector4ic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day19 {

    private final Pattern partPattern = Pattern.compile("\\{x=(?<x>[0-9]+),m=(?<m>[0-9]+),a=(?<a>[0-9]+),s=(?<s>[0-9]+)}");

    private static final String YEAR = "2023";
    private static final String DAY = "19";
    private static final boolean REAL_INPUT = true;

    private static final Map<String,Integer> labelToComponent = ImmutableMap.of("x", 0, "m", 1, "a", 2, "s", 3);

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day19().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            lines = CharStreams.readLines(reader);
        }

        Input input = parse(lines);
        part1(input);
        part2(input);
    }

    private Input parse(List<String> lines) {
        List<Vector4ic> parts = new ArrayList<>();
        Map<String, Workflow> workflows = new LinkedHashMap<>();

        int index = 0;
        while (!lines.get(index).isEmpty()) {
            String line = lines.get(index++);
            String[] labelRules = line.split("\\{");
            String[] rules = labelRules[1].split(",");
            List<Rule> processedRules = new ArrayList<>();
            for (int i = 0; i < rules.length - 1; i++) {
                String[] compTarget = rules[i].split(":");
                int component = labelToComponent.get(compTarget[0].substring(0, 1));
                boolean lt = compTarget[0].charAt(1) == '<';
                int value = Integer.parseInt(compTarget[0].substring(2));
                processedRules.add(new Rule(component, lt, value,compTarget[1]));
            }
            workflows.put(labelRules[0], new Workflow(processedRules, rules[rules.length - 1].substring(0, rules[rules.length - 1].length() - 1)));
        }
        for (int i = index + 1; i < lines.size(); i++) {
            String line = lines.get(i);
            Matcher matcher = partPattern.matcher(line);
            if (matcher.matches()) {
                parts.add(new Vector4i(Integer.parseInt(matcher.group("x")), Integer.parseInt(matcher.group("m")), Integer.parseInt(matcher.group("a")), Integer.parseInt(matcher.group("s"))));
            } else {
                System.out.println("No match: " + line);
            }
        }
        return new Input(parts, workflows);
    }

    private void part1(Input input) {
        long total = 0;
        for (Vector4ic part : input.parts) {
            String bucket = "in";
            while (!"A".equals(bucket) && !"R".equals(bucket)) {
                bucket = input.workflows.get(bucket).process(part);
            }
            if ("A".equals(bucket)) {
                total += part.x() + part.y() + part.z() + part.w();
            }
        }

        System.out.println("Part 1: " + total);
    }

    private void part2(Input input) {

        long total = 0;
        Range initial = new Range(new Vector4i(1), new Vector4i(4000));

        Deque<Output> outputs = new ArrayDeque<>(input.workflows.get("in").process(initial));
        while (!outputs.isEmpty()) {
            Output output = outputs.pop();
            if ("A".equals(output.bucket)) {
                total += output.range.size();
            } else if (!"R".equals(output.bucket)) {
                outputs.addAll(input.workflows.get(output.bucket).process(output.range));
            }
        }

        System.out.println("Part 2: " + total);
    }


    private record Input(List<Vector4ic> parts, Map<String, Workflow> workflows) {}

    private record Workflow(List<Rule> rules, String fallback) {
        public String process(Vector4ic part) {
            for (Rule rule : rules) {
                if (rule.process(part)) {
                    return rule.label;
                }
            }
            return fallback;
        }

        public List<Output> process(Range range) {
            Range residual = range;
            List<Output> outputs = new ArrayList<>();
            for (Rule rule : rules) {
                Split split = rule.process(residual);
                if (split.toTarget != null) {
                    outputs.add(new Output(split.toTarget, rule.label));
                }
                residual = split.remainder;
                if (residual == null) {
                    break;
                }
            }
            if (residual != null) {
                outputs.add(new Output(residual, fallback));
            }
            return outputs;
        }
    }

    private record Rule(int component, boolean lt, int target, String label) {
        boolean process(Vector4ic part) {
            if (lt) {
                return part.get(component) < target;
            } else {
                return part.get(component) > target;
            }
        }

        Split process(Range range) {
            Range captured = null;
            Range remainder = range;
            if (lt) {
                if (range.min.get(component) < target) {
                    Vector4i newMax = new Vector4i(range.max);
                    newMax.setComponent(component, Math.min(newMax.get(component), target - 1));
                    captured = new Range(range.min, newMax);
                    if (range.max.get(component) > target - 1) {
                        Vector4i newMin = new Vector4i(range.min);
                        newMin.setComponent(component, Math.max(newMin.get(component), target));
                        remainder = new Range(newMin, range.max);
                    } else {
                        remainder = null;
                    }
                }
            } else {
                if (range.max.get(component) > target) {
                    Vector4i newMin = new Vector4i(range.min);
                    newMin.setComponent(component, Math.max(newMin.get(component), target + 1));
                    captured = new Range(newMin, range.max);
                    if (range.min.get(component) < target + 1) {
                        Vector4i newMax = new Vector4i(range.max);
                        newMax.setComponent(component, Math.min(newMax.get(component), target));
                        remainder = new Range(range.min, newMax);
                    } else {
                        remainder = null;
                    }
                }
            }
            return new Split(captured, remainder);
        }
    }

    private record Range(Vector4ic min, Vector4ic max) {
        public long size() {
            return (long)(max.x() - min.x() + 1) * (long) (max.y() - min.y() + 1) * (long)(max.z() - min.z() + 1) * (long)(max.w() - min.w() + 1);
        }
    }

    private record Output(Range range, String bucket) {}

    private record Split(Range toTarget, Range remainder) {}
}

