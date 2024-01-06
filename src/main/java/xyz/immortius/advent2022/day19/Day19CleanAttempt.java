package xyz.immortius.advent2022.day19;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day19CleanAttempt {
    private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day19CleanAttempt().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day19/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        List<Blueprint> blueprints = parse(lines);
        part1(blueprints);
        part2(blueprints.subList(0, Math.min(blueprints.size(), 3)));
    }

    private List<Blueprint> parse(List<String> lines) {
        List<Blueprint> result = new ArrayList<>();
        for (String line : lines) {
            Matcher matcher = linePattern.matcher(line);
            if (!matcher.matches()) {
                System.out.println("No match");
            }
            result.add(Blueprint.create(
                    Integer.parseInt(matcher.group("blueprint")),
                    Integer.parseInt(matcher.group("orerobotcost")),
                    Integer.parseInt(matcher.group("clayrobotcost")),
                    Integer.parseInt(matcher.group("obsidianrobotorecost")),
                    Integer.parseInt(matcher.group("obsidianrobotclaycost")),
                    Integer.parseInt(matcher.group("geoderobotorecost")),
                    Integer.parseInt(matcher.group("geoderobotobsidiancost"))));
        }
        return result;
    }

    private void part1(List<Blueprint> blueprints) {
        int timeLimit = 24;
        int total = 0;
        for (Blueprint blueprint : blueprints) {
            int max = findMaxGeodes(blueprint, new Resources(0,0,0,0,1,0,0,0), timeLimit, 0);
            System.out.println("Blueprint: " + blueprint.id + " - " + max + " geodes");
            total += blueprint.id * max;
        }
        System.out.println("Total: " + total);
    }

    private void part2(List<Blueprint> blueprints) {
        int timeLimit = 32;
        int result = 1;
        for (Blueprint blueprint : blueprints) {
            int max = findMaxGeodes(blueprint, new Resources(0,0,0,0,1,0,0,0), timeLimit, 0);
            System.out.println("Blueprint: " + blueprint.id + " - " + max + " geodes");
            result *= max;
        }
        System.out.println("Total: " + result);
    }

    private int findMaxGeodes(Blueprint blueprint, Resources resources, int timeLimit, int bestGeodeCountSeen) {
        int highestPotentialGeodes = calculatePotentialGeodes(blueprint, resources, timeLimit);
        if (highestPotentialGeodes < bestGeodeCountSeen) {
            return 0;
        }

        Set<Action> actions = blueprint.possibleActions(resources, timeLimit);
        if (actions.isEmpty()) {
            return resources.produce(timeLimit).geodes;
        }

        for (Action action : actions) {
            int result = findMaxGeodes(blueprint, action.newResources, action.turnsRemaining, bestGeodeCountSeen);
            bestGeodeCountSeen = Math.max(bestGeodeCountSeen, result);
        }
        return bestGeodeCountSeen;
    }

    // Note: This provides a performance boost... but the algorithm is plenty quick without it
    private int calculatePotentialGeodes(Blueprint blueprint, Resources resources, int timeLimit) {
        int expected = resources.geodes;
        expected += resources.geodeRobots * timeLimit;
        expected += timeLimit * timeLimit - timeLimit * (timeLimit + 1) / 2;
        return expected;
    }

    private record Blueprint(int id, int oreRobotCost, int clayRobotCost, int obsidianRobotOreCost,
                             int obsidianRobotClayCost, int geodeRobotOreCost, int geodeRobotObsidianCost, int maxOreCost) {

        public static Blueprint create(int id, int orerobotcost, int clayrobotcost, int obsidianrobotorecost, int obsidianrobotclaycost, int geoderobotorecost, int geoderobotobsidiancost) {
            return new Blueprint(id, orerobotcost, clayrobotcost, obsidianrobotorecost, obsidianrobotclaycost, geoderobotorecost, geoderobotobsidiancost, Math.max(orerobotcost, Math.max(clayrobotcost, Math.max(obsidianrobotorecost, geoderobotorecost))));
        }

        public Set<Action> possibleActions(Resources resources, int turnsRemaining) {
            Set<Action> results = new LinkedHashSet<>();

            if (resources.obsidianRobots > 0) {
                int turns = Math.max(1, Math.max((geodeRobotOreCost - resources.ore + resources.oreRobots - 1) / resources.oreRobots, (geodeRobotObsidianCost - resources.obsidian + resources.obsidianRobots - 1) / resources.obsidianRobots) + 1);
                if (turns < turnsRemaining) {
                    results.add(new Action(resources.produce(turns).update(-geodeRobotOreCost, 0, -geodeRobotObsidianCost, 0, 0, 0, 0, 1), turnsRemaining - turns));
                }
            }
            if (resources.clayRobots > 0 && (resources.obsidianRobots * turnsRemaining + resources.obsidian) / geodeRobotObsidianCost < turnsRemaining) {
                int turns = Math.max(1, Math.max((obsidianRobotClayCost - resources.clay + resources.clayRobots - 1) / resources.clayRobots, (obsidianRobotOreCost - resources.ore + resources.oreRobots - 1) / resources.oreRobots) + 1);
                if (turns < turnsRemaining) {
                    results.add(new Action(resources.produce(turns).update(-obsidianRobotOreCost, -obsidianRobotClayCost, 0, 0, 0, 0, 1, 0), turnsRemaining - turns));
                }
            }
            if ((resources.clayRobots * turnsRemaining + resources.clay) / obsidianRobotClayCost < turnsRemaining) {
                int turns = Math.max(1, (clayRobotCost - resources.ore + resources.oreRobots - 1) / resources.oreRobots + 1);
                if (turns < turnsRemaining) {
                    results.add(new Action(resources.produce(turns).update(-clayRobotCost, 0, 0, 0, 0, 1, 0, 0), turnsRemaining - turns));
                }
            }
            if ((resources.oreRobots * turnsRemaining + resources.ore) / maxOreCost < turnsRemaining) {
                int turns = Math.max(1, (oreRobotCost - resources.ore + resources.oreRobots - 1) / resources.oreRobots + 1);
                if (turns < turnsRemaining) {
                    results.add(new Action(resources.produce(turns).update(-oreRobotCost, 0, 0, 0, 1, 0, 0, 0), turnsRemaining - turns));
                }
            }

            return results;
        }
    }

    private record Resources(int ore, int clay, int obsidian, int geodes, int oreRobots, int clayRobots, int obsidianRobots, int geodeRobots) {
        Resources produce(int turns) {
            return new Resources(ore + turns * oreRobots, clay + turns * clayRobots, obsidian + turns * obsidianRobots, geodes + turns * geodeRobots, oreRobots, clayRobots, obsidianRobots, geodeRobots);
        }

        Resources update(int ore, int clay, int obsidian, int geodes, int oreRobots, int clayRobots, int obsidianRobots, int geodeRobots) {
            return new Resources(this.ore + ore, this.clay + clay, this.obsidian + obsidian, this.geodes + geodes, this.oreRobots + oreRobots, this.clayRobots + clayRobots, this.obsidianRobots + obsidianRobots, this.geodeRobots + geodeRobots);
        }
    }

    public record Action(Resources newResources, int turnsRemaining) {
    }
}