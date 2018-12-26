package com.blueline.util;

import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;

public class TimeoutCacheTest {

    @Test
    public void test() throws InterruptedException {
        //Number of timeout
        final AtomicLong timeoutCount = new AtomicLong(0);
        //Number of deletion successful
        final AtomicLong removeCount = new AtomicLong(0);
        //Number of deletion failures
        final AtomicLong removeFailCount = new AtomicLong(0);

        final int numberOfProducers=4;
        final int numberOfConsumers=4;
        final int totalNumberOfData=25000000;

        final TimeoutCache<String, String> sw = new TimeoutCache<String, String>(16, 5000, new TimeoutCache.IHandler<String,String>() {

            @Override
            public boolean handle(TimeoutCache.CacheItem item) {
                timeoutCount.incrementAndGet();
                return true;
            }
        });


        final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();



        for(int i = 0; i<numberOfProducers; i++) {

            final int finalI = i;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(System.currentTimeMillis());
                    for (int dataindex = finalI; dataindex < totalNumberOfData; dataindex += numberOfProducers) {
                        try {
                            sw.put("key" + dataindex, dataindex + "");
                            queue.put("key" + dataindex);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(System.currentTimeMillis());

                }
            });
            t.setDaemon(true);
            t.start();
        }

        for(int i = 0; i<numberOfConsumers; i++) {
            Thread tx = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            String key = queue.take();
                            if (sw.remove(key) == null) {

                                removeFailCount.incrementAndGet();
                            } else {
                                removeCount.incrementAndGet();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            tx.setDaemon(true);
            tx.start();
        }

        Thread.sleep(30000);


//        System.out.println(sw);
        System.out.println("remove count:"+removeCount.get());
        System.out.println("remove fial count:"+removeFailCount.get());
        System.out.println("time out count:"+timeoutCount.get());

        assert removeFailCount.get()==timeoutCount.get() && removeCount.get()+removeFailCount.get()==totalNumberOfData ;
    }
}