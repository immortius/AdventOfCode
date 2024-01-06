package xyz.immortius.advent2019.day2;

public interface IntCodeOutputStream {
    void receive(long value) throws InterruptedException;
}
