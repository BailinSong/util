package com.blueline.util;

import java.util.Comparator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class PriorityCacheTest {

    @org.junit.Test
    public void get() throws InterruptedException {
        final PriorityCache<AtomicInteger> cache
                = new PriorityCache<AtomicInteger>(new Comparator<AtomicInteger>() {
            @Override
            public int compare(AtomicInteger o1, AtomicInteger o2) {
                return -1 * (o1.get() - o2.get());
            }
        });

        cache.add(new AtomicInteger(1));
        cache.add(new AtomicInteger(0));
        cache.add(new AtomicInteger(5));
        cache.add(new AtomicInteger(12));
        cache.add(new AtomicInteger(0));
        cache.add(new AtomicInteger(446));
        cache.add(new AtomicInteger(6));
        cache.add(new AtomicInteger(123));
        cache.add(new AtomicInteger(436));
        cache.add(new AtomicInteger(0));

        final int numberOfConsumers=4;
        final int totalNumberOfTests=1000000000;
        final CountDownLatch finshSignal = new CountDownLatch(numberOfConsumers);

        for(int i = 0; i<numberOfConsumers; i++) {
            final int finalI = i;
            final Thread tx = new Thread(new Runnable() {
                @Override
                public void run() {
                    AtomicInteger value=null;
                    for (int testCount = finalI; testCount<totalNumberOfTests; testCount += numberOfConsumers) {
                            value = cache.get();
                            assert value.get()==0;
                    }
                    finshSignal.countDown();
                    System.out.println(value.hashCode()+": " + value);
                }
            });
            tx.setDaemon(true);
            tx.start();
        }
        finshSignal.await();


    }
}