package xyz.immortius.advent2022.day1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ElfCalorieCalculator {

    public static void main(String[] args) throws IOException {
        new ElfCalorieCalculator().calculate();
    }

    private void calculate() throws IOException {
        List<Integer> elfCalories = new ArrayList<>();

        int currentTotal = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/advent2022/day1/input.txt")))) {
            String line = reader.readLine();
            while (line != null) {
                if (line.isEmpty()) {
                    elfCalories.add(currentTotal);
                    currentTotal = 0;
                } else {
                    currentTotal += Integer.parseInt(line);
                }
                line = reader.readLine();
            }
        }
        if (currentTotal > 0) {
            elfCalories.add(currentTotal);
        }

        elfCalories.sort(Comparator.reverseOrder());
        int top3total = 0;
        for (int i = 0; i < 3; i++) {
            System.out.println("Elf #" + i + " has " + elfCalories.get(i));
            top3total += elfCalories.get(i);
        }
        System.out.println("Total: " + top3total);
    }

}
