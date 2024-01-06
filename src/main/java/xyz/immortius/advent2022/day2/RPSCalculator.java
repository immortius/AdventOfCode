package xyz.immortius.advent2022.day2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RPSCalculator {

    public static void main(String[] args) throws IOException {
        new RPSCalculator().run();
    }

    private void run() throws IOException {
        List<String> games = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day2/input.txt")))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                games.add(line);
            }
        }

        int totalScore = 0;
        for (String game : games) {
            totalScore += scoreGame(game);
        }
        System.out.println("Total score: " + totalScore);
    }

    private int scoreGame(String game) {
        int oppMove = game.charAt(0) - 'A';
        int result = game.charAt(2) - 'X';

        int yourMove = (oppMove + result - 1 + 3) % 3;

        if ((oppMove + 1) % 3 == yourMove) {
            result = 2;
        } else if (oppMove == yourMove) {
            result = 1;
        } else {
            result = 0;
        }

        int score = 1 + yourMove + 3 * result;
        System.out.println(game + " = " + score);
        return score;
    }
}
