package xyz.immortius.advent2019.day16;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        //part2t2();
    }

    private void example() {
        String result = FTT("12345678", 4, 1, 0);
        System.out.println("Example: " + result);
    }

    private void part1() {
        String input = "59713137269801099632654181286233935219811755500455380934770765569131734596763695509279561685788856471420060118738307712184666979727705799202164390635688439701763288535574113283975613430058332890215685102656193056939765590473237031584326028162831872694742473094498692690926378560215065112055277042957192884484736885085776095601258138827407479864966595805684283736114104361200511149403415264005242802552220930514486188661282691447267079869746222193563352374541269431531666903127492467446100184447658357579189070698707540721959527692466414290626633017164810627099243281653139996025661993610763947987942741831185002756364249992028050315704531567916821944";
        System.out.println("Part 1: " + FTT(input, 100, 1, 0));
    }

    private void part2() {
        String input = "59713137269801099632654181286233935219811755500455380934770765569131734596763695509279561685788856471420060118738307712184666979727705799202164390635688439701763288535574113283975613430058332890215685102656193056939765590473237031584326028162831872694742473094498692690926378560215065112055277042957192884484736885085776095601258138827407479864966595805684283736114104361200511149403415264005242802552220930514486188661282691447267079869746222193563352374541269431531666903127492467446100184447658357579189070698707540721959527692466414290626633017164810627099243281653139996025661993610763947987942741831185002756364249992028050315704531567916821944";
        System.out.println(input.length());
        System.out.println("Part 2: " + fastFTT(input, 100, 10000, Integer.parseInt(input.substring(0, 7))));
    }

    private String FTT(String input, int phases, int reps, int offset) {

        int[] current = new int[input.length() * reps - offset];
        System.out.println(current.length);
        for (int i = 0; i < current.length; i++) {
            current[i] = input.charAt((i + offset) % input.length()) - '0';
        }
        for (int i = 0; i < phases; i++) {
            current = applyPhase(current, offset);
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            result.append(current[i]);
        }
        return result.toString();
    }

    private int[] applyPhase(int[] current, int offset) {
        int[] result = new int[current.length];
        for (int element = 0; element < current.length; element++) {
            int value = 0;
            int index = element;
            int mult = 1;
            while (index < current.length) {
                for (int i = 0; i < element + offset + 1 && index < current.length; i++) {
                    value += mult * current[index];
                    index++;
                }
                index += element + offset + 1;
                mult *= -1;
            }
            result[element] = Math.abs(value % 10);
        }
        return result;
    }

    private String fastFTT(String input, int phases, int reps, int offset) {

        int[] current = new int[input.length() * reps - offset];
        System.out.println(current.length);
        for (int i = 0; i < current.length; i++) {
            current[i] = input.charAt((i + offset) % input.length()) - '0';
        }
        for (int i = 0; i < phases; i++) {
            current = applyFastPhase(current);
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            result.append(current[i]);
        }
        return result.toString();
    }

    private int[] applyFastPhase(int[] current) {
        int[] result = new int[current.length];
        int sum = 0;
        for (int element = current.length - 1; element >= 0; element--) {
            sum = (sum + current[element]) % 10;
            result[element] = sum;
        }
        return result;
    }


}

