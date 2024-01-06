package xyz.immortius.advent2019.day2;

public class ConsoleOutputStream implements IntCodeOutputStream {
    @Override
    public void receive(long value) {
        System.out.println(value);
    }
}
