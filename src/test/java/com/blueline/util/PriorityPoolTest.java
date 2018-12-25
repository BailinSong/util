package com.blueline.util;

import org.junit.Test;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class PriorityPoolTest {

    @Test
    public void borrow() {

        PriorityPool<AtomicInteger> priorityQueue
                = new PriorityPool<AtomicInteger>(new Comparator<AtomicInteger>() {
            @Override
            public int compare(AtomicInteger o1, AtomicInteger o2) {
                return o1.get() - o2.get();
            }
        });

        priorityQueue.add(new AtomicInteger(1));
        priorityQueue.add(new AtomicInteger(2));
        priorityQueue.add(new AtomicInteger(3));
        priorityQueue.add(new AtomicInteger(4));
        priorityQueue.add(new AtomicInteger(5));
        priorityQueue.add(new AtomicInteger(6));
        priorityQueue.add(new AtomicInteger(7));
        priorityQueue.add(new AtomicInteger(8));
        priorityQueue.add(new AtomicInteger(9));
        priorityQueue.add(new AtomicInteger(0));

        AtomicInteger value;
        for (int i = 0; i < 10; i++) {
            value = priorityQueue.borrow();
            System.out.println(value);
            priorityQueue.release(value);
        }


    }

    @Test
    public void release() {
    }
}