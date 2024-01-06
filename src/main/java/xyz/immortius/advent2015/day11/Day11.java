package xyz.immortius.advent2015.day11;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Day11 {

    private static final String YEAR = "2015";
    private static final String DAY = "11";
    private static final boolean REAL_INPUT = false;

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day11().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() {
        part1();
        part2();
    }

    private void part1() {
        String initialPassword = "vzbxkghb";
        String password = incrementPassword(initialPassword);
        while (!validate(password)) {
            password = incrementPassword(password);
        }

        System.out.println("Part 1: " + password);
    }

    private String incrementPassword(String password) {
        char end = password.charAt(password.length() - 1);
        if (end == 'z') {
            return incrementPassword(password.substring(0, password.length() - 1)) + 'a';
        } else {
            return password.substring(0, password.length() - 1) + (char) (end + 1);
        }
    }

    private boolean validate(String password) {
        if (password.contains("i")) {
            return false;
        }
        if (password.contains("o")) {
            return false;
        }
        if (password.contains("l")) {
            return false;
        }

        int doubleLetters = 0;
        for (int pos = 0; pos < password.length() - 1; pos++) {
            if (password.charAt(pos) == password.charAt(pos + 1)) {
                doubleLetters++;
                pos++;
            }
        }
        if (doubleLetters < 2) {
            return false;
        }
        boolean straight = false;
        for (int pos = 0; pos < password.length() - 2; pos++) {
            char c = password.charAt(pos);
            if (c + 1 == password.charAt(pos + 1) && c + 2 == password.charAt(pos + 2) ) {
                straight = true;
                break;
            }
        }
        return straight;
    }

    private void part2() {
        String initialPassword = "vzbxxyzz";
        String password = incrementPassword(initialPassword);
        while (!validate(password)) {
            password = incrementPassword(password);
        }

        System.out.println("Part 2: " + password);
    }



}

