package com.koi.kredis.memory;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author koi
 * @date 2023/7/29 19:51
 */
/*
 * 线程不安全的BlockingQueue，尽量单线程使用，尽量使用offer和poll两个方法
 * */
public class SimpleRingQueue<E> extends AbstractQueue<E> implements Serializable {

    static final int MAXIMUM_CAPACITY = 1 << 29;

    static final int MAXIMUM_SUBAREA = 1 << 12;

    Object data[][];

    volatile int readIndex = -1;
    volatile int writeIndex = -1;
    private final AtomicInteger count = new AtomicInteger();
    int capacity;
    int rowOffice;
    int colOffice;
    int rowSize;
    int bitHigh;
    int subareaSize;
    int maxSize;

    /**
     * 初始化队列
     *
     * @param subareaSize 分片数
     * @param capacity    容量
     */
    public SimpleRingQueue(int subareaSize, int capacity) {
        this(subareaSize, capacity, 1);
    }

    /**
     * 初始化队列
     *
     * @param subareaSize 分片数
     * @param capacity    容量
     * @param concurrency 并发数
     */
    public SimpleRingQueue(int subareaSize, int capacity, int concurrency) {
        if (subareaSize > capacity || capacity < 0 || subareaSize < 0) {
            throw new IllegalArgumentException("Illegal initial capacity:subareaSize>capacity||capacity<0||subareaSize<0");
        }
        maxSize = capacity;
        subareaSize = subareaSizeFor(subareaSize);
        capacity = tableSizeFor(capacity);
        rowSize = tableSizeFor(capacity / subareaSize);
        capacity = rowSize * subareaSize;

        data = new Object[rowSize][subareaSize];
        this.capacity = capacity;
        bitHigh = getIntHigh(subareaSize);
        this.subareaSize = subareaSize;
        rowOffice = rowSize - 1;
        colOffice = subareaSize - 1;
    }

    public SimpleRingQueue(Collection<? extends E> c) {
        this(8888, 88888);
        int n = 0;
        for (E e : c) {
            if (e == null) {
                throw new NullPointerException();
            }
            if (n == capacity) {
                throw new IllegalStateException("Queue full");
            }
            offer(e);
            ++n;
        }
        count.set(n);
    }

    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    static final int getIntHigh(int cap) {
        int high = 0;
        while ((cap & 1) == 0) {
            high++;
            cap = cap >> 1;
        }
        return high;
    }

    static final int subareaSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_SUBAREA) ? MAXIMUM_SUBAREA : n + 1;
    }

    void refreshIndex() {
        if (readIndex > capacity) {
            synchronized (this) {
                if (readIndex > capacity) {
                    writeIndex -= capacity;
                    readIndex -= capacity;
                }
            }
        }
    }


    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean offer(Object o) {
        int localWriteIndex = 0;
        synchronized (this) {
            if (writeIndex > readIndex + maxSize) {
                return false;
            }
            count.incrementAndGet();
            localWriteIndex = ++writeIndex;
        }
        int row = (localWriteIndex >> bitHigh) & (rowOffice);
        int column = localWriteIndex & (colOffice);
        if (column == 0 && row == 0) {
            refreshIndex();
        }
        data[row][column] = o;
        return true;
    }

    @Override
    public E poll() {
        int localReadIndex = 0;
        synchronized (this) {
            if (writeIndex > readIndex) {
                localReadIndex = ++readIndex;
                count.getAndDecrement();
            } else {
                return null;
            }
        }
        int row = (localReadIndex >> bitHigh) & (rowOffice);
        int column = localReadIndex & (colOffice);
        if (column == 0 && row == 0) {
            refreshIndex();
        }
        return (E) data[row][column];
    }

    E ergodic(Integer index) {
        int localReadIndex = 0;
        if (index > writeIndex || index < readIndex) {
            return null;
        }
        int row = (index >> bitHigh) & (rowOffice);
        int column = index & (colOffice);
        if (column == 0 && row == 0) {
            refreshIndex();
        }
        return (E) data[row][column];
    }

    @Override
    public E peek() {
        int localReadIndex = 0;
        synchronized (this) {
            if (writeIndex > readIndex) {
                localReadIndex = readIndex;
            } else {
                return null;
            }
        }
        int row = (localReadIndex >> bitHigh) & (rowOffice);
        int column = localReadIndex & (colOffice);
        if (column == 0 && row == 0) {
            refreshIndex();
        }
        return (E) data[row][column];
    }

    public int remainingCapacity() {
        return capacity - count.get();
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    public boolean isFull() {
        return count.get() == capacity;
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        for (int index = readIndex; readIndex >= index || index <= writeIndex; index++) {
            if (o.equals(ergodic(index))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object[] toArray() {
        int size = count.get();
        Object[] a = new Object[size];
        int k = 0;
        for (int index = readIndex; readIndex >= index || index <= writeIndex; index++) {
            a[k++] = ergodic(index);
        }
        return a;

    }

    @Override
    public <E> E[] toArray(E[] a) {

        int size = count.get();
        if (a.length < size) {
            a = (E[]) java.lang.reflect.Array.newInstance
                    (a.getClass().getComponentType(), size);
        }

        int k = 0;
        for (int index = readIndex; readIndex >= index || index <= writeIndex; index++) {
            a[k++] = (E) ergodic(index);
        }
        if (a.length > k) {
            a[k] = null;
        }
        return a;
    }


    @Override
    public boolean add(E e) {
        if (offer(e)) {
            return true;
        } else {
            throw new IllegalStateException("Queue full");
        }
    }

    @Override
    public E remove() {
        E x = poll();
        if (x != null) {
            return x;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public E element() {
        E x = peek();
        if (x != null) {
            return x;
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public void clear() {
        while (poll() != null) {
            ;
        }
    }


    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        boolean modified = false;
        for (E e : c) {
            if (add(e)) {
                modified = true;
            }
        }
        return modified;
    }
}
