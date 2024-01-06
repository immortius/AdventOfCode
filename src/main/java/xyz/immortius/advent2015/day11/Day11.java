package xyz.immortius.advent2015.day11;

import java.io.IOException;

public class Day11 {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day11().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() {
        String nextPassword = getNextPassword("vzbxkghb");
        System.out.println("Part 1: " + nextPassword);
        System.out.println("Part 2: " + getNextPassword(nextPassword));
    }

    private String getNextPassword(String currentPassword) {
        String password = incrementPassword(currentPassword);
        while (!validate(password)) {
            password = incrementPassword(password);
        }
        return password;
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
}

