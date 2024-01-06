package xyz.immortius.advent2015.day25;

import com.google.common.io.CharStreams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Day25 {


    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        new Day25().run();
        long end = System.currentTimeMillis();

        System.out.println(end - start + "ms");
    }

    private void run() throws IOException {
       part1();
    }


    private void part1() {
        //row 2978, column 3083
        long value = 20151125;
        int row = 1;
        int column = 1;
        while (row != 2978 || column != 3083) {
            if (row == 1) {
                row = column + 1;
                column = 1;
            } else {
                row--;
                column++;
            }
            value = (value * 252533L) % 33554393;
            if (row <7 && column < 7) {
                System.out.println(column + "," + row + ": " + value);
            }
        }
        System.out.println("Part 1: " + value);
    }



}

