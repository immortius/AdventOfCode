package xyz.immortius.advent2022.day19;

import com.google.common.base.Objects;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day19 {
    private final Pattern linePattern = Pattern.compile("Blueprint (?<blueprint>[0-9]+): Each ore robot costs (?<orerobotcost>[0-9]+) ore. Each clay robot costs (?<clayrobotcost>[0-9]+) ore. Each obsidian robot costs (?<obsidianrobotorecost>[0-9]+) ore and (?<obsidianrobotclaycost>[0-9]+) clay. Each geode robot costs (?<geoderobotorecost>[0-9]+) ore and (?<geoderobotobsidiancost>[0-9]+) obsidian.");

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day19().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day19/example.txt")))) {
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
            result.add(new Blueprint(
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
        int totalScore = 0;
        for (Blueprint blueprint : blueprints) {
            Set<State> current = new LinkedHashSet<>();
            current.add(new State(FactoryAction.NOTHING, new Resources(0,0,0, 0, 1, 0,0,0)));
            for (int i = 24; i > 0; i--) {
                System.out.println("Step: " + i + ", possible states: " + current.size());
                Set<State> next = new LinkedHashSet<>();
                for (State state : current) {
                    final Resources updatedResources = blueprint.doAction(state.nextAction, state.resources.produce());
                    if (i != 1) {
                        Set<FactoryAction> availableActions = blueprint.possibleActions(updatedResources, i);
                        for (FactoryAction action : availableActions) {
                            next.add(new State(action, updatedResources));
                        }
                    } else {
                        next.add(new State(FactoryAction.NOTHING, updatedResources));
                    }
                }
                if (i == 1) {
                    current = next;
                } else {
                    //int turnsRemaining = i;
                    //int best = next.stream().map(x -> x.resources.geodes + x.resources.geodeRobots * turnsRemaining).sorted(Comparator.reverseOrder()).findFirst().get();
                    current = next;//.stream().filter(x -> geodePotential(blueprint, x, turnsRemaining) >= best - turnsRemaining).collect(Collectors.toSet());
                }
            }

            int best = current.stream().map(x -> x.resources.geodes).sorted(Comparator.reverseOrder()).findFirst().get();
            System.out.println("Blueprint " + blueprint.id + " - " + best + " geodes, " + (blueprint.id * best) + " score");
            totalScore += (blueprint.id * best);
        }
        System.out.println("Total: " + totalScore);
    }

    private int geodePotential(Blueprint bp, State state, int turnsRemaining) {
        if (state.nextAction != FactoryAction.GEODE_ROBOT) {
            turnsRemaining--;
        }
        int potentialGeodeBotProduction = Math.min(turnsRemaining, (state.resources.obsidianRobots * turnsRemaining + state.resources.obsidian + turnsRemaining) / bp.geodeRobotObsidianCost);
        return state.resources.geodes + state.resources.geodeRobots * turnsRemaining + potentialGeodeBotProduction * turnsRemaining;
    }

    private void part2(List<Blueprint> blueprints) {
        int totalScore = 0;
        for (Blueprint blueprint : blueprints) {
            Set<State> current = new LinkedHashSet<>();
            current.add(new State(FactoryAction.NOTHING, new Resources(0,0,0, 0, 1, 0,0,0)));
            for (int i = 32; i > 0; i--) {
                System.out.println("Step: " + i + ", possible states: " + current.size());
                Set<State> next = new LinkedHashSet<>();
                for (State state : current) {
                    final Resources updatedResources = blueprint.doAction(state.nextAction, state.resources.produce());
                    if (i != 1) {
                        Set<FactoryAction> availableActions = blueprint.possibleActions(updatedResources, i);
                        for (FactoryAction action : availableActions) {
                            next.add(new State(action, updatedResources));
                        }
                    } else {
                        next.add(new State(FactoryAction.NOTHING, updatedResources));
                    }
                }
                if (i == 1) {
                    current = next;
                } else {
                    //int turnsRemaining = i;
                    //int best = next.stream().map(x -> x.resources.geodes + x.resources.geodeRobots * turnsRemaining).sorted(Comparator.reverseOrder()).findFirst().get();
                    current = next;//.stream().filter(x -> geodePotential(blueprint, x, turnsRemaining) >= best - turnsRemaining).collect(Collectors.toSet());
                }
            }

            int best = current.stream().map(x -> x.resources.geodes).sorted(Comparator.reverseOrder()).findFirst().get();
            System.out.println("Blueprint " + blueprint.id + " - " + best + " geodes, " + (blueprint.id * best) + " score");
            totalScore += (blueprint.id * best);
        }
        System.out.println("Total: " + totalScore);
    }

    private record State(FactoryAction nextAction, Resources resources) {

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return nextAction == state.nextAction && Objects.equal(resources, state.resources);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(nextAction, resources);
        }

        public int compare(State other) {
            return resources.compare(other.resources);
        }
    }

    private static final class Blueprint {
        private final int id;
        private final int oreRobotCost;
        private final int clayRobotCost;
        private final int obsidianRobotOreCost;
        private final int obsidianRobotClayCost;
        private final int geodeRobotOreCost;
        private final int geodeRobotObsidianCost;
        private final int maxOreCost;

        private Blueprint(int id, int oreRobotCost, int clayRobotCost, int obsidianRobotOreCost, int obsidianRobotClayCost, int geodeRobotOreCost, int geodeRobotObsidianCost) {
            this.id = id;
            this.oreRobotCost = oreRobotCost;
            this.clayRobotCost = clayRobotCost;
            this.obsidianRobotOreCost = obsidianRobotOreCost;
            this.obsidianRobotClayCost = obsidianRobotClayCost;
            this.geodeRobotOreCost = geodeRobotOreCost;
            this.geodeRobotObsidianCost = geodeRobotObsidianCost;

            this.maxOreCost = Math.max(oreRobotCost, Math.max(clayRobotCost, Math.max(obsidianRobotOreCost, geodeRobotOreCost)));
        }


        public Set<FactoryAction> possibleActions(Resources resources, int turnsRemaining) {
            Set<FactoryAction> results = new LinkedHashSet<>();

            // We don't need more robots than the maximum cost of a robot in each resource
            if (resources.ore >= oreRobotCost && (resources.oreRobots * turnsRemaining + resources.ore) / maxOreCost < turnsRemaining) {
                results.add(FactoryAction.ORE_ROBOT);
            }
            if (resources.ore >= clayRobotCost && resources.clayRobots < obsidianRobotClayCost) {
                results.add(FactoryAction.CLAY_ROBOT);
            }
            if (resources.ore >= obsidianRobotOreCost && resources.clay >= obsidianRobotClayCost && resources.obsidianRobots < geodeRobotObsidianCost) {
                results.add(FactoryAction.OBSIDIAN_ROBOT);
            }
            if (resources.ore >= geodeRobotOreCost && resources.obsidian >= geodeRobotObsidianCost) {
                results.add(FactoryAction.GEODE_ROBOT);
            }
            int maxPossibleActions = (resources.obsidianRobots > 0) ? 4 : (resources.clayRobots > 0) ? 3 : 2;

            // Only do nothing if there is a reason to do nothing
            if (results.size() < maxPossibleActions) {
                results.add(FactoryAction.NOTHING);
            }
            return results;
        }

        public Resources doAction(FactoryAction action, Resources resources) {
            switch (action) {
                case ORE_ROBOT -> {
                    return resources.update(-oreRobotCost, 0, 0, 0, 1, 0, 0, 0);
                }
                case CLAY_ROBOT -> {
                    return resources.update(-clayRobotCost, 0, 0, 0, 0, 1, 0, 0);
                }
                case OBSIDIAN_ROBOT -> {
                    return resources.update(-obsidianRobotOreCost, -obsidianRobotClayCost, 0, 0, 0, 0, 1, 0);
                }
                case GEODE_ROBOT -> {
                    return resources.update(-geodeRobotOreCost, 0, -geodeRobotObsidianCost, 0, 0, 0, 0, 1);
                }
                default -> {
                    return resources;
                }
            }
        }

        public int id() {
            return id;
        }

        public int oreRobotCost() {
            return oreRobotCost;
        }

        public int clayRobotCost() {
            return clayRobotCost;
        }

        public int obsidianRobotOreCost() {
            return obsidianRobotOreCost;
        }

        public int obsidianRobotClayCost() {
            return obsidianRobotClayCost;
        }

        public int geodeRobotOreCost() {
            return geodeRobotOreCost;
        }

        public int geodeRobotObsidianCost() {
            return geodeRobotObsidianCost;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Blueprint) obj;
            return this.id == that.id &&
                    this.oreRobotCost == that.oreRobotCost &&
                    this.clayRobotCost == that.clayRobotCost &&
                    this.obsidianRobotOreCost == that.obsidianRobotOreCost &&
                    this.obsidianRobotClayCost == that.obsidianRobotClayCost &&
                    this.geodeRobotOreCost == that.geodeRobotOreCost &&
                    this.geodeRobotObsidianCost == that.geodeRobotObsidianCost;
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(id, oreRobotCost, clayRobotCost, obsidianRobotOreCost, obsidianRobotClayCost, geodeRobotOreCost, geodeRobotObsidianCost);
        }

        @Override
        public String toString() {
            return "Blueprint[" +
                    "id=" + id + ", " +
                    "oreRobotCost=" + oreRobotCost + ", " +
                    "clayRobotCost=" + clayRobotCost + ", " +
                    "obsidianRobotOreCost=" + obsidianRobotOreCost + ", " +
                    "obsidianRobotClayCost=" + obsidianRobotClayCost + ", " +
                    "geodeRobotOreCost=" + geodeRobotOreCost + ", " +
                    "geodeRobotObsidianCost=" + geodeRobotObsidianCost + ']';
        }


    }

    private record Resources(int ore, int clay, int obsidian, int geodes, int oreRobots, int clayRobots, int obsidianRobots, int geodeRobots) {

        Resources produce() {
            return new Resources(ore + oreRobots, clay + clayRobots, obsidian + obsidianRobots, geodes + geodeRobots, oreRobots, clayRobots, obsidianRobots, geodeRobots);
        }

        Resources update(int ore, int clay, int obsidian, int geodes, int oreRobots, int clayRobots, int obsidianRobots, int geodeRobots) {
            return new Resources(this.ore + ore, this.clay + clay, this.obsidian + obsidian, this.geodes + geodes, this.oreRobots + oreRobots, this.clayRobots + clayRobots, this.obsidianRobots + obsidianRobots, this.geodeRobots + geodeRobots);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Resources resources = (Resources) o;
            return ore == resources.ore && clay == resources.clay && obsidian == resources.obsidian && geodes == resources.geodes && oreRobots == resources.oreRobots && clayRobots == resources.clayRobots && obsidianRobots == resources.obsidianRobots && geodeRobots == resources.geodeRobots;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(ore, clay, obsidian, geodes, oreRobots, clayRobots, obsidianRobots, geodeRobots);
        }

        public int compare(Resources other) {
            if (ore < other.ore && clay < other.clay && obsidian < other.obsidian && geodes < other.geodes && oreRobots < other.oreRobots && clayRobots < other.clayRobots && obsidianRobots < other.obsidianRobots && geodeRobots < other.geodeRobots) {
                return -1;
            } else if (ore > other.ore && clay > other.clay && obsidian > other.obsidian && geodes > other.geodes && oreRobots > other.oreRobots && clayRobots > other.clayRobots && obsidianRobots > other.obsidianRobots && geodeRobots > other.geodeRobots) {
                return 1;
            }
            return 0;
        }
    }

    public enum FactoryAction {
        NOTHING,
        ORE_ROBOT,
        CLAY_ROBOT,
        OBSIDIAN_ROBOT,
        GEODE_ROBOT
    }
}