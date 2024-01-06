package xyz.immortius.advent2019.day2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleInputStream implements IntCodeInputStream{
    private BufferedReader reader;

    public ConsoleInputStream() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public long send() {
        do {
            System.out.print("> ");
            try {
                return Long.parseLong(reader.readLine());
            } catch (IOException|NumberFormatException e) {
                System.out.println("Invalid number");
            }
        } while (true);
    }

}
