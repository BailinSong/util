package com.blueline.util;

import java.util.concurrent.*;

public class TimeoutCache<K, V> {
    ConcurrentHashMap<K, CacheItem<K, V>> cache;

    BlockingQueue<K> blockingQueue;

    DelayQueue<CacheItem<K, V>> delayQueue;

    long timeout;
    IHandler handler;

    static Thread timeoutThread;

    public TimeoutCache(int capacity, long timeout) {
        this(capacity, timeout, new IHandler<K, V>() {
            @Override
            public boolean handle(CacheItem<K, V> item) {
                System.out.println("time out " + item);
                return true;
            }
        });
    }

    public TimeoutCache(int capacity, long timeout, IHandler timeoutHandler) {
        this.timeout = timeout;
        this.handler = timeoutHandler;
        cache = new ConcurrentHashMap<K, CacheItem<K, V>>(capacity);
        blockingQueue = new ArrayBlockingQueue<K>(capacity);
        delayQueue = new DelayQueue<CacheItem<K, V>>();
        timeoutThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        CacheItem cacheItem = delayQueue.take();
                        cacheItem = cache.remove(cacheItem.key);
                        if (cacheItem != null) {
                            blockingQueue.remove(cacheItem.key);
                            handler.handle(cacheItem);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        timeoutThread.setDaemon(true);
        timeoutThread.start();
    }

    public int size() {
        return cache.size();
    }

    public void put(K key, V value) throws InterruptedException {
        CacheItem<K, V> cacheItem = new CacheItem(key, value);
        blockingQueue.put(cacheItem.key);
        cacheItem.setTimeout(this.timeout);
        cache.put(key, cacheItem);
        delayQueue.put(cacheItem);


    }

    public V get(K key) {
        CacheItem<K, V> item = cache.get(key);
        if (item != null) {
            return item.value;
        } else {
            return null;
        }
    }

    public V remove(K key) {
        CacheItem cacheItem = cache.remove(key);
        if (cacheItem != null) {
            delayQueue.remove(cacheItem);
            blockingQueue.remove(cacheItem.key);
            return (V) cacheItem.value;
        } else {
            return null;
        }
    }

    public void clear() {
        delayQueue.clear();
        blockingQueue.clear();
        cache.clear();
    }

    @Override
    public String toString() {
        return "SlidingWindow{" +
                "cache=" + cache +
                ", blockingQueue=" + blockingQueue +
                ", delayQueue=" + delayQueue +
                ", timeout=" + timeout +
                ", handler=" + handler +
                ", timeoutThread=" + timeoutThread +
                '}';
    }

    static class CacheItem<K, V> implements Delayed {

        K key;
        V value;
        long timeout;

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public long getTimeout() {
            return timeout;
        }

        public void setTimeout(long timeout) {
            this.timeout = System.currentTimeMillis() + timeout;
        }

        public CacheItem(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public int compareTo(Delayed o) {
            long c = getDelay(TimeUnit.MILLISECONDS);
            long a = o.getDelay(TimeUnit.MILLISECONDS);
            if (c > a) return 1;
            if (c < a) return -1;
            return 0;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return timeout - System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return "CacheItem{" +
                    "key=" + key +
                    ", value=" + value +
                    ", timeout=" + timeout +
                    '}';
        }
    }

    static interface IHandler<K, V> {
        boolean handle(CacheItem<K, V> item);
    }
}
