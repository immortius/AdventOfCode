package xyz.immortius.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

public class UniqueQueue<T> implements Queue<T> {

    private final Set<T> containedSet;
    private final Queue<T> internalQueue;

    /**
     * Creates a new, empty queue
     */
    public UniqueQueue() {
        this(Sets.<T>newHashSet(), Queues.<T>newArrayDeque());
    }

    private UniqueQueue(Set<T> internalSet, Queue<T> internalQueue) {
        this.containedSet = internalSet;
        this.internalQueue = internalQueue;
    }

    /**
     * @param <T> The type that can be contained in the queue
     * @return A new, empty queue
     */
    public static <T> UniqueQueue<T> create() {
        return new UniqueQueue<>();
    }

    /**
     * @param size The expected size of the queue
     * @param <T>  The type that can be contained in the queue
     * @return A new, empty queue.
     */
    public static <T> UniqueQueue<T> createWithExpectedSize(int size) {
        return new UniqueQueue<>(Sets.<T>newHashSetWithExpectedSize(size), Queues.<T>newArrayDeque());
    }

    @Override
    public int size() {
        return containedSet.size();
    }

    @Override
    public boolean isEmpty() {
        return containedSet.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return containedSet.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return Iterators.unmodifiableIterator(internalQueue.iterator());
    }

    @Override
    public Object[] toArray() {
        return internalQueue.toArray();
    }

    @Override
    public <U> U[] toArray(U[] a) {
        return internalQueue.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return containedSet.add(t) && internalQueue.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return containedSet.remove(o) && internalQueue.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return containedSet.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean changed = false;
        for (T item : c) {
            if (containedSet.add(item)) {
                internalQueue.add(item);
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return containedSet.removeAll(c) && internalQueue.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return containedSet.retainAll(c) && internalQueue.retainAll(c);
    }

    @Override
    public void clear() {
        containedSet.clear();
        internalQueue.clear();
    }

    @Override
    public boolean offer(T t) {
        return containedSet.add(t) && internalQueue.offer(t);
    }

    @Override
    public T remove() {
        T result = internalQueue.remove();
        containedSet.remove(result);
        return result;
    }

    @Override
    public T poll() {
        T result = internalQueue.poll();
        containedSet.remove(result);
        return result;
    }

    @Override
    public T element() {
        return internalQueue.element();
    }

    @Override
    public T peek() {
        return internalQueue.peek();
    }

    @Override
    public String toString() {
        return internalQueue.toString();
    }
}
