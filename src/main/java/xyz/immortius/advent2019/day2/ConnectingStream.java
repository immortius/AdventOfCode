package xyz.immortius.advent2019.day2;

import java.util.concurrent.LinkedBlockingDeque;

public class ConnectingStream implements IntCodeInputStream, IntCodeOutputStream{
    private final LinkedBlockingDeque<Long> queue = new LinkedBlockingDeque<>();

    @Override
    public long send() throws InterruptedException {
        return queue.takeFirst();
    }

    @Override
    public void receive(long value) {
        queue.addLast(value);
    }
}
