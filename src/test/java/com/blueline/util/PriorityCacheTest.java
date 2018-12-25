package com.blueline.util;

import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;

public class PriorityCacheTest {

    @org.junit.Test
    public void get() {
        PriorityCache<AtomicInteger> priorityQueue
                = new PriorityCache<AtomicInteger>(new Comparator<AtomicInteger>() {
            @Override
            public int compare(AtomicInteger o1, AtomicInteger o2) {
                return -1 * (o1.get() - o2.get());
            }
        });

        priorityQueue.add(new AtomicInteger(1));
        priorityQueue.add(new AtomicInteger(0));
        priorityQueue.add(new AtomicInteger(0));
        priorityQueue.add(new AtomicInteger(0));
        priorityQueue.add(new AtomicInteger(0));
        priorityQueue.add(new AtomicInteger(0));
        priorityQueue.add(new AtomicInteger(0));
        priorityQueue.add(new AtomicInteger(0));
        priorityQueue.add(new AtomicInteger(0));
        priorityQueue.add(new AtomicInteger(0));

        AtomicInteger value;
        for (int i = 0; i < 20; i++) {
            value = priorityQueue.get();
            System.out.println(value.hashCode() + ":" + value);
        }


    }
}