package com.blueline.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;


public class PriorityPool<E>
        extends CopyOnWriteArrayList<E> {

    Comparator<E> comparatorHandler;
    Vector<E> using = new Vector<E>();

    public PriorityPool(final Comparator<E> comparatorHandler) {
        super();
        this.comparatorHandler = new Comparator<E>() {
            @Override
            public int compare(E o1, E o2) {
                if (using.contains(o1)) return -1;
                return comparatorHandler.compare(o1, o2);
            }
        };
    }

    public E borrow() {
        E item = Collections.max(this, comparatorHandler);
        using.add(item);
        return item;
    }

    public void release(E object) {
        using.remove(object);
    }

}
