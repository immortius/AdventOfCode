package xyz.immortius.advent2022.day8;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Day8 {
    public static void main(String[] args) throws IOException {
        new Day8().run();
    }

    private void run() throws IOException {
        List<String> lines;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day8/input.txt")))) {
            lines = CharStreams.readLines(reader);
        }

        int[][] trees = new int[lines.size()][];
        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            String line = lines.get(lineIndex);
            int[] treeLine = new int[line.length()];
            for (int i = 0; i < line.length(); i++) {
                treeLine[i] = line.charAt(i) - '0';
            }
            trees[lineIndex] = treeLine;
        }


        int total = 0;
        int maxViewDistance = 0;
        for (int i = 0; i < trees.length; i++) {
            for (int j = 0; j < trees[i].length; j++) {
                if (isVisible(trees, i, j)) {
                    total++;
                }
                int viewDistance = calcViewDistance(trees, i, j);
                if (viewDistance > maxViewDistance) {
                    maxViewDistance = viewDistance;
                }
            }
        }

        System.out.println(total);
        System.out.println(maxViewDistance);
    }

    private boolean isVisible(int[][] trees, int i, int j) {
        int treeHeight = trees[i][j];

        int x = i - 1;
        while (x >= 0 && trees[x][j] < treeHeight) {
            x--;
        }
        if (x == -1) {
            return true;
        }

        x = i + 1;
        while (x < trees.length && trees[x][j] < treeHeight) {
            x++;
        }
        if (x == trees.length) {
            return true;
        }

        int y = j - 1;
        while (y >= 0 && trees[i][y] < treeHeight) {
            y--;
        }
        if (y == -1) {
            return true;
        }
        y = j + 1;
        while (y < trees[i].length && trees[i][y] < treeHeight) {
            y++;
        }
        if (y == trees[i].length) {
            return true;
        }

        return false;
    }

    private int calcViewDistance(int[][] trees, int i, int j) {
        int treeHeight = trees[i][j];

        int totalViewDistance = 1;

        int x = i - 1;
        int viewDistance = 0;
        while (x >= 0) {
            viewDistance += 1;
            if (trees[x][j] >= treeHeight) {
                break;
            }
            x--;
        }
        totalViewDistance *= viewDistance;


        viewDistance = 0;
        x = i + 1;
        while (x < trees.length) {
            viewDistance += 1;
            if (trees[x][j] >= treeHeight) {
                break;
            }
            x++;
        }
        totalViewDistance *= viewDistance;

        viewDistance = 0;
        int y = j - 1;
        while (y >= 0) {
            viewDistance += 1;
            if (trees[i][y] >= treeHeight) {
                break;
            }
            y--;
        }
        totalViewDistance *= viewDistance;

        viewDistance = 0;
        y = j + 1;
        while (y < trees[i].length) {
            viewDistance += 1;
            if (trees[i][y] >= treeHeight) {
                break;
            }
            y++;
        }
        totalViewDistance *= viewDistance;
        return totalViewDistance;
    }
}

