package io.obhub.receiver;

import exception.SendException;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class OTLPOutput<T> {

    private final BlockingQueue<T> queue;

    public OTLPOutput(BlockingQueue<T> queue) {
        this.queue = queue;
    }

    public static <T> OTLPOutput<T> newBounded(int capacity) {
        return new OTLPOutput<>(new LinkedBlockingQueue<>(capacity));
    }

    public void send(T events) throws SendException {
        try {
            boolean success = queue.offer(events, 5, TimeUnit.SECONDS);
            if (!success) {
                throw new SendException("Queue is full, failed to send events");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SendException("Interrupted while sending events", e);
        }
    }

    public T take() throws InterruptedException {
        return queue.take();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
