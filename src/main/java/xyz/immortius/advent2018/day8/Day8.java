package xyz.immortius.advent2018.day8;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day8 {

    private static final String YEAR = "2018";
    private static final String DAY = "8";
    private static final boolean REAL_INPUT = true;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day8().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent" + YEAR + "/day" + DAY + "/" + ((REAL_INPUT) ? "input.txt" : "example.txt"))))) {
            lines = CharStreams.readLines(reader);
        }

        List<Integer> data = Arrays.stream(lines.get(0).split("\\s")).map(Integer::parseInt).toList();
        TreeBuilder builder = new TreeBuilder(data);
        Node root = builder.build();
        part1(root);
        part2(root);
    }

    private static class TreeBuilder {

        private final List<Integer> data;
        private int index;

        public TreeBuilder(List<Integer> data) {
            this.data = data;
        }

        public Node build() {
            this.index = 0;
            return readNode();
        }


        private Node readNode() {
            int numChildren = data.get(index++);
            int numMetadata = data.get(index++);
            List<Node> children = new ArrayList<>();
            for (int i = 0; i < numChildren; i++) {
                children.add(readNode());
            }
            List<Integer> metadata = new ArrayList<>();
            for (int i = 0; i < numMetadata; i++) {
                metadata.add(data.get(index++));
            }
            return new Node(children, metadata);
        }
    }


    private void part1(Node root) {
        System.out.println("Part 1: " + root.metadataTotal());
    }

    private void part2(Node root) {
        System.out.println("Part 2: " + root.value());
    }

    record Node(List<Node> children, List<Integer> metadata) {

        public int metadataTotal() {
            int total = metadata.stream().reduce(0, Integer::sum);
            for (Node child : children) {
                total += child.metadataTotal();
            }
            return total;
        }

        public int value() {
            if (children.isEmpty()) {
                return metadataTotal();
            } else {
                int total = 0;
                for (int index : metadata) {
                    if (index <= children.size()) {
                        total += children.get(index - 1).value();
                    }
                }
                return total;
            }
        }
    }

}

