package com.blueline.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class PriorityCache<E>
        extends CopyOnWriteArrayList<E> {

    Comparator<E> comparatorHandler;
    ConcurrentHashMap<E, Long> lastGetTime = new ConcurrentHashMap<E, Long>();

    public PriorityCache(final Comparator<E> comparatorHandler) {
        super();
        this.comparatorHandler = new Comparator<E>() {
            @Override
            public int compare(E o1, E o2) {
                int r = comparatorHandler.compare(o1, o2);
                if (r == 0) {
                    Long t1 = lastGetTime.get(o1);
                    Long t2 = lastGetTime.get(o2);
                    t1 = (t1 == null) ? 0 : t1;
                    t2 = (t2 == null) ? 0 : t2;
                    long r1 = t1 - t2;
                    if (r1 > 0) return -1;
                    else return 1;
                } else {
                    return r;
                }
            }
        };
    }

    public E get() {
        E item = Collections.max(this, comparatorHandler);
        lastGetTime.put(item, System.nanoTime());
        return item;
    }

}
