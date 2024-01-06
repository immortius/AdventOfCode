package xyz.immortius.advent2022.day20;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Day20 {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day20().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day20/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        List<Element> values = parse(lines);
        part1(values);
        part2(values);
    }

    private List<Element> parse(List<String> lines) {
        List<Element> list = new ArrayList<>();
        for (String line : lines) {
            list.add(new Element(Integer.parseInt(line)));
        }
        return list;
    }

    private void part1(List<Element> startArray) {
        CircularArray array = new CircularArray(startArray);
        for (Element value : startArray) {
            array.mix(value);
        }

        int zeroIndex = array.indexOf(0);
        System.out.println(array.get(zeroIndex + 1000) + array.get(zeroIndex + 2000) + array.get(zeroIndex + 3000));
    }

    private void part2(List<Element> startArray) {
        List<Element> ordering = startArray.stream().map(x -> new Element(x.value * 811589153)).collect(Collectors.toList());

        CircularArray array = new CircularArray(ordering);
        for (int i = 0; i < 10; i++) {
            for (Element value : ordering) {
                array.mix(value);
            }
        }

        int zeroIndex = array.indexOf(0);
        System.out.println(array.get(zeroIndex + 1000) + array.get(zeroIndex + 2000) + array.get(zeroIndex + 3000));
    }

    private record Element(long value) {
    }

    private static class CircularArray {
        private final Element[] internal;

        public CircularArray(List<Element> source) {
            internal = source.toArray(new Element[]{});
        }

        public long get(int index) {
            return internal[index % internal.length].value;
        }

        public int indexOf(long value) {
            int index = 0;
            while (internal[index].value != value) {
                index++;
            }
            return index;
        }

        public void mix(Element element) {
            int fromIndex = 0;
            while (internal[fromIndex] != element) {
                fromIndex++;
            }

            // 'element.value / (internal.length - 1)' accounts for skipping over the element when cycling multiple times
            int toIndex = (int)((fromIndex + element.value + element.value / (internal.length - 1)) % internal.length);
            boolean after = element.value > 0;
            if (toIndex < 0) {
                toIndex = internal.length + toIndex;
            }

            if (toIndex > fromIndex && !after) {
                toIndex--;
            } else if (toIndex < fromIndex && after) {
                toIndex++;
            }

            if (toIndex > fromIndex) {
                System.arraycopy(internal, fromIndex + 1, internal, fromIndex, toIndex - fromIndex);
                internal[toIndex] = element;
            } else if (toIndex < fromIndex) {
                System.arraycopy(internal, toIndex, internal, toIndex + 1, fromIndex - toIndex);
                internal[toIndex] = element;
            }
        }

        public List<Long> toList() {
            List<Long> list = new ArrayList<>();
            for (int i = 0; i < internal.length; i++) {
                list.add(get(i));
            }
            return list;
        }
    }

}