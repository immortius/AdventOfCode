package xyz.immortius.advent2023.day7;

import com.google.common.io.CharStreams;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Day7 {

    private static final String YEAR = "2023";
    private static final String DAY = "7";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day7().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt")))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Hand> hands = parse(lines);
        part1(hands);
        part2(hands);
    }

    private List<Hand> parse(List<String> lines) {
        return lines.stream().map(x -> x.split(" ")).map(x -> {
            int[] cards = new int[5];
            for (int i = 0; i < 5; i++) {
                char c = x[0].charAt(i);
                cards[i] = switch (c) {
                    case 'A' -> 14;
                    case 'K' -> 13;
                    case 'Q' -> 12;
                    case 'J' -> 11;
                    case 'T' -> 10;
                    default -> (c - '0');
                };
            }
            return new Hand(cards, Long.parseLong(x[1]));
        }).toList();
    }

    private void part1(List<Hand> initialHands) {
        List<Hand> hands = initialHands.stream().sorted(Comparator.reverseOrder()).toList();
        long totalScore = 0;
        for (int i = 0; i < hands.size(); i++) {
            Hand hand = hands.get(i);
            totalScore += hand.bid * (i + 1);
        }
        System.out.println("Part 1: " + totalScore);
    }

    private void part2(List<Hand> initialHands) {
        List<Hand> hands = initialHands.stream().map(Hand::convertToJokerHand).sorted(Comparator.reverseOrder()).toList();
        for (Hand hand : hands) {
            System.out.println(hand);
        }
        long totalScore = 0;
        for (int i = 0; i < hands.size(); i++) {
            Hand hand = hands.get(i);
            totalScore += hand.bid * (i + 1);
        }
        System.out.println("Part 1: " + totalScore);
    }

    private record Hand(int[] cards, long bid) implements Comparable<Hand> {
        @Override
        public String toString() {
            return handToString() + " - " + getType() + " - " + bid;
        }

        public Hand convertToJokerHand() {
            int[] newCards = Arrays.copyOf(cards, cards.length);
            for (int i = 0; i < newCards.length; i++) {
                if (newCards[i] == 11) {
                    newCards[i] = 1;
                }
            }
            return new Hand(newCards, bid);
        }

        public String handToString() {
            StringBuilder builder = new StringBuilder();
            for (int c : cards) {
                builder.append((char) switch (c) {
                    case 14 -> 'A';
                    case 13 -> 'K';
                    case 12 -> 'Q';
                    case 11 -> 'J';
                    case 10 -> 'T';
                    default -> c + '0';
                });
            }
            return builder.toString();
        }

        public HandType getType() {
            int[] sortedCards = Arrays.copyOf(cards, cards.length);
            Arrays.sort(sortedCards);
            List<Integer> occurrences = new ArrayList<>();
            int lastCard = sortedCards[0];
            int count = 1;
            for (int i = 1; i < 5; i++) {
                if (lastCard == sortedCards[i]) {
                    count++;
                } else {
                    occurrences.add(count);
                    count = 1;
                    lastCard = sortedCards[i];
                }
            }
            occurrences.add(count);
            if (sortedCards[0] == 1 && occurrences.size() > 1) {
                int jokerCount = occurrences.get(0);
                occurrences.remove(0);
                HandType best = HandType.HIGH_CARD;
                for (int i = 0; i < occurrences.size(); i++) {
                    List<Integer> checkList = new ArrayList<>(occurrences);
                    checkList.set(i, checkList.get(i) + jokerCount);
                    HandType testHandType = calcHandType(checkList);
                    if (testHandType.compareTo(best) < 0) {
                        best = testHandType;
                    }
                }
                return best;
            } else {
                return calcHandType(occurrences);
            }
        }

        @NotNull
        private HandType calcHandType(List<Integer> occurrences) {
            return switch (occurrences.size()) {
                case 1 -> HandType.FIVE_OF_A_KIND;
                case 2 -> {
                    if (occurrences.contains(4)) {
                        yield HandType.FOUR_OF_A_KIND;
                    } else {
                        yield HandType.FULL_HOUSE;
                    }
                }
                case 3 -> {
                    if (occurrences.contains(2)) {
                        yield HandType.TWO_PAIR;
                    } else {
                        yield HandType.THREE_OF_A_KIND;
                    }
                }
                case 4 -> HandType.ONE_PAIR;
                case 5 -> HandType.HIGH_CARD;
                default -> throw new IllegalStateException("Unexpected value: " + occurrences.size());
            };
        }

        @Override
        public int compareTo(@NotNull Hand o) {
            int typeCompare = getType().compareTo(o.getType());
            if (typeCompare == 0) {
                for (int i = 0; i < 5; i++) {
                    int cardCompare = o.cards[i] - cards[i];
                    if (cardCompare != 0) {
                        return cardCompare;
                    }
                }
                System.out.println("Non total ordering");
                return 0;
            }
            return typeCompare;
        }
    }

    public enum HandType implements Comparable<HandType> {
        FIVE_OF_A_KIND,
        FOUR_OF_A_KIND,
        FULL_HOUSE,
        THREE_OF_A_KIND,
        TWO_PAIR,
        ONE_PAIR,
        HIGH_CARD
    }

}

