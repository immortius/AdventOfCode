package xyz.immortius.advent2016.day16;

import java.io.IOException;

public class Day16 {


    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day16().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
        example();
        part1();
        part2();
    }

    private void example() {
        boolean[] initial = new boolean[]{true, false, false, false, false};
        boolean[] data = curve(initial, 20);
        boolean[] checksum = checksum(data);
        System.out.println("Example: " + toString(checksum));
    }

    private void part1() {
        boolean[] initial = fromString("10010000000110000");
        boolean[] data = curve(initial, 272);
        boolean[] checksum = checksum(data);
        System.out.println("Part 1: " + toString(checksum));
    }

    private void part2() {
        boolean[] initial = fromString("10010000000110000");
        boolean[] data = curve(initial, 35651584);
        boolean[] checksum = checksum(data);
        System.out.println("Part 2: " + toString(checksum));
    }

    private boolean[] fromString(String s) {
        boolean[] result = new boolean[s.length()];
        for (int i = 0; i < s.length(); i++) {
            result[i] = s.charAt(i) == '1';
        }
        return result;
    }

    private boolean[] curve(boolean[] initial, int length) {
        boolean[] generated = curveStep(initial);
        while (generated.length < length) {
            generated = curveStep(generated);
        }
        boolean[] cropped = new boolean[length];
        System.arraycopy(generated, 0, cropped, 0, cropped.length);
        return cropped;
    }

    private boolean[] curveStep(boolean[] a) {
        boolean[] b = new boolean[a.length * 2 + 1];
        for (int i = 0; i < a.length; i++) {
            b[i + a.length + 1] = !a[a.length - i - 1];
        }
        System.arraycopy(a, 0, b, 0, a.length);
        return b;
    }

    private boolean[] checksum(boolean[] a) {
        boolean[] checksum = checkStep(a);
        while (checksum.length % 2 == 0) {
            checksum = checksum(checksum);
        }
        return checksum;
    }

    private boolean[] checkStep(boolean[] a) {
        boolean[] b = new boolean[a.length / 2];
        for (int i = 0; i < b.length; i += 1) {
            b[i] = a[2 * i] == a[2 * i + 1];
        }
        return b;
    }

    private String toString(boolean[] bs) {
        StringBuilder builder = new StringBuilder();
        for (boolean b : bs) {
            builder.append(b ? '1' : '0');
        }
        return builder.toString();
    }




}

