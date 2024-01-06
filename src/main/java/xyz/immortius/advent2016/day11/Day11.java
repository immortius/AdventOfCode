package xyz.immortius.advent2016.day11;

import com.google.common.base.Objects;
import com.google.common.collect.Comparators;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day11 {

    private static final String YEAR = "2016";
    private static final String DAY = "11";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day11().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<List<Item>> floors = parse(lines);
        part1(floors);
        part2(floors);
    }

    private List<List<Item>> parse(List<String> lines) {
        List<List<Item>> floors = new ArrayList<>();
        for (String line : lines) {
            List<Item> floorContents = new ArrayList<>();
            floors.add(floorContents);
            String itemList = line.substring(line.indexOf(" contains ") + " contains ".length(), line.length() - 1);
            if (itemList.equals("nothing relevant")) {
                continue;
            }
            String[] parts = itemList.substring(2).split(",?( and)? a ");
            for (String part : parts) {
                if (part.endsWith("generator")) {
                    floorContents.add(new Item(ItemType.Generator, part.split(" ")[0]));
                } else {
                    floorContents.add(new Item(ItemType.Microchip, part.split("-")[0]));
                }
            }

        }
        return floors;
    }

    private void part1(List<List<Item>> floors) {
        int steps = stepsToCollectAll(State2.create(0, floors));

        System.out.println("Part 1: " + steps);
    }

    private void part2(List<List<Item>> floors) {
        floors.get(0).add(new Item(ItemType.Generator, "elerium"));
        floors.get(0).add(new Item(ItemType.Microchip, "elerium"));
        floors.get(0).add(new Item(ItemType.Generator, "dilithium"));
        floors.get(0).add(new Item(ItemType.Microchip, "dilithium"));

        int steps = stepsToCollectAll(State2.create(0, floors));

        System.out.println("Part 2: " + steps);
    }

    private int stepsToCollectAll(State2 initialState) {
        Set<State2> states = new LinkedHashSet<>();
        states.add(initialState);
        Set<State2> pastStates = new LinkedHashSet<>();
        pastStates.add(initialState);
        int steps = 0;
        while (states.size() > 0) {
            steps++;
            Set<State2> nextStates = new LinkedHashSet<>();
            for (State2 state : states) {
                nextStates.addAll(state.possibleNextStates());
            }

            if (nextStates.stream().anyMatch(State2::complete)) {
                return steps;
            }
            nextStates.removeIf(pastStates::contains);
            pastStates.addAll(nextStates);

            System.out.println("Step " + steps + ": " + nextStates.size());

            states = nextStates;
        }
        System.out.println("No valid moves remain");
        return 0;
    }

    public static boolean validConfiguration(List<Item> items) {
        boolean generatorPresent = items.stream().anyMatch(x -> x.type == ItemType.Generator);
        boolean unshieldedChip = items.stream().filter(x -> x.type == ItemType.Microchip).anyMatch(x -> !items.contains(new Item(ItemType.Generator, x.name)));

        return !generatorPresent || !unshieldedChip;
    }

    private record Item(ItemType type, String name) {}

    public enum ItemType {
        Generator,
        Microchip
    }

    public record PartLocation(int chip, int generator) {

        boolean isShielded() { return chip == generator; }

        boolean isComplete() {return chip == 3 && generator == 3;}

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PartLocation that = (PartLocation) o;
            return chip == that.chip && generator == that.generator;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(chip, generator);
        }
    }

    public static class State2 {
        private final List<PartLocation> partLocations;
        private final int currentFloor;

        public State2(int currentFloor, List<PartLocation> parts) {
            this.currentFloor = currentFloor;
            this.partLocations = ImmutableList.copyOf(parts.stream().sorted(Comparator.comparingInt(PartLocation::chip).thenComparingInt(PartLocation::generator)).toList());
        }

        public static State2 create(int currentFloor, List<List<Item>> floors) {
            Map<String, Integer> chips = new LinkedHashMap<>();
            Map<String, Integer> generators = new HashMap<>();
            for (int floor = 0; floor < floors.size(); floor++) {
                for (Item item : floors.get(floor)) {
                    if (item.type == ItemType.Microchip) {
                        chips.put(item.name, floor);
                    } else {
                        generators.put(item.name, floor);
                    }
                }
            }
            List<PartLocation> locations = new ArrayList<>();
            for (String name : chips.keySet()) {
                locations.add(new PartLocation(chips.get(name), generators.get(name)));
            }
            return new State2(currentFloor, locations);
        }

        public boolean complete() {
            return partLocations.stream().allMatch(PartLocation::isComplete);
        }

        public boolean isValid() {
            for (int i = 0; i < 4; i++) {
                int floor = i;
                boolean hasGenerator = partLocations.stream().anyMatch(x -> x.generator == floor);
                boolean hasUnshieldedChip = partLocations.stream().anyMatch(x -> !x.isShielded() && x.chip == floor);
                if (hasGenerator && hasUnshieldedChip) {
                    return false;
                }
            }
            return true;
        }

        public Set<State2> possibleNextStates() {
            Set<State2> possibleNextStates = new LinkedHashSet<>();
            List<PartLocation> partsOnCurrentFloor = partLocations.stream().filter(x -> x.chip == currentFloor || x.generator == currentFloor).toList();

            List<Integer> toFloors = new ArrayList<>();
            if (currentFloor < 3) {
                toFloors.add(currentFloor + 1);
            }
            if (currentFloor > 0) {
                toFloors.add(currentFloor - 1);
            }
            for (int toFloor : toFloors) {
                for (int i = 0; i < partsOnCurrentFloor.size(); i++) {
                    PartLocation part1 = partsOnCurrentFloor.get(i);
                    if (part1.chip == part1.generator) {
                        List<PartLocation> newLocations = new ArrayList<>(partLocations);
                        newLocations.remove(part1);
                        newLocations.add(new PartLocation(toFloor, toFloor));
                        possibleNextStates.add(new State2(toFloor, newLocations));
                    }
                    List<List<PartLocation>> singleShifts = new ArrayList<>();
                    if (part1.chip == currentFloor) {
                        List<PartLocation> singleShiftLocations = new ArrayList<>(partLocations);
                        singleShiftLocations.remove(part1);
                        singleShiftLocations.add(new PartLocation(toFloor, part1.generator));
                        singleShifts.add(singleShiftLocations);
                        possibleNextStates.add(new State2(toFloor, singleShiftLocations));
                    }
                    if (part1.generator == currentFloor) {
                        List<PartLocation> singleShiftLocations = new ArrayList<>(partLocations);
                        singleShiftLocations.remove(part1);
                        singleShiftLocations.add(new PartLocation(part1.chip, toFloor));
                        singleShifts.add(singleShiftLocations);
                        possibleNextStates.add(new State2(toFloor, singleShiftLocations));
                    }
                    for (List<PartLocation> singleShift : singleShifts) {
                        for (int i2 = i + 1; i2 < partsOnCurrentFloor.size(); i2++) {
                            PartLocation part2 = partsOnCurrentFloor.get(i2);
                            if (part2.chip == currentFloor) {
                                List<PartLocation> dualShift = new ArrayList<>(singleShift);
                                dualShift.remove(part2);
                                dualShift.add(new PartLocation(toFloor, part2.generator));
                                possibleNextStates.add(new State2(toFloor, dualShift));
                            }
                            if (part2.generator == currentFloor) {
                                List<PartLocation> dualShift = new ArrayList<>(singleShift);
                                dualShift.remove(part2);
                                dualShift.add(new PartLocation(part2.chip, toFloor));
                                possibleNextStates.add(new State2(toFloor, dualShift));
                            }
                        }
                    }

                }
            }
            return possibleNextStates.stream().filter(State2::isValid).collect(Collectors.toSet());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State2 state2 = (State2) o;
            return currentFloor == state2.currentFloor && Objects.equal(partLocations, state2.partLocations);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(partLocations, currentFloor);
        }
    }


    public static class State {
        private final List<List<Item>> contents;
        private final int currentFloor;

        public State(int elevatorFloor, List<List<Item>> items) {
            this.contents = new ArrayList<>();
            for (List<Item> floor : items) {
                contents.add(ImmutableList.copyOf(floor));
            }
            this.currentFloor = elevatorFloor;
        }

        public boolean complete() {
            for (int i = 0; i < contents.size() - 1; i++) {
                if (!contents.get(i).isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        public State move(int from, int to, Item ... items) {
            List<List<Item>> newContents = new ArrayList<>(contents);
            List<Item> newFromFloor = new ArrayList<>(contents.get(from));
            newFromFloor.removeAll(List.of(items));
            newContents.set(from, newFromFloor);

            List<Item> newToFloor = new ArrayList<>(contents.get(to));
            newToFloor.addAll(List.of(items));
            newContents.set(to, newToFloor);

            return new State(to, newContents);
        }

        public List<State> possibleNextStates() {
            List<State> result = new ArrayList<>();
            if (currentFloor < contents.size() - 1) {
                int nextFloor = currentFloor + 1;
                List<Item> currentFloorItems = contents.get(currentFloor);
                for (int i1 = 0; i1 < currentFloorItems.size() - 1; i1++) {
                    for (int i2 = i1 + 1; i2 < currentFloorItems.size(); i2++) {
                        State state = move(currentFloor, nextFloor, currentFloorItems.get(i1), currentFloorItems.get(i2));
                        if (state.isValid()) {
                            result.add(state);
                        }
                    }
                }
                if (result.isEmpty()) {
                    for (Item currentFloorItem : currentFloorItems) {
                        State state = move(currentFloor, nextFloor, currentFloorItem);
                        if (state.isValid()) {
                            result.add(state);
                        }
                    }
                }
            }
            if (currentFloor > 0) {
                boolean movedSingle = false;
                int nextFloor = currentFloor - 1;
                List<Item> currentFloorItems = contents.get(currentFloor);
                for (Item currentFloorItem : currentFloorItems) {
                    State state = move(currentFloor, nextFloor, currentFloorItem);
                    if (state.isValid()) {
                        result.add(state);
                        movedSingle = true;
                    }
                }
                if (!movedSingle) {
                    for (int i1 = 0; i1 < currentFloorItems.size() - 1; i1++) {
                        for (int i2 = i1 + 1; i2 < currentFloorItems.size(); i2++) {
                            State state = move(currentFloor, nextFloor, currentFloorItems.get(i1), currentFloorItems.get(i2));
                            if (state.isValid()) {
                                result.add(state);
                            }
                        }
                    }
                }
            }
            return result;
        }

        private int getLowestRelevantFloor() {
            int lowestRelevantFloor = 0;
            while (contents.get(lowestRelevantFloor).size() == 0) {
                lowestRelevantFloor++;
            }
            return lowestRelevantFloor;
        }

        public boolean isValid() {
            for (List<Item> floor : contents) {
                if (!validConfiguration(floor)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return currentFloor == state.currentFloor && Objects.equal(contents, state.contents);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(contents, currentFloor);
        }
    }


}

