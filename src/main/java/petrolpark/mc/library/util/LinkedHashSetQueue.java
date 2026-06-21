package petrolpark.mc.library.util;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Queue;

public class LinkedHashSetQueue<T> extends LinkedHashSet<T> implements Queue<T> {
    
    @Override
    public T poll() {
        Iterator<T> it = iterator();
        if (it.hasNext()) {
            T next = it.next();
            it.remove();  // remove from set (FIFO)
            return next;
        }
        return null;
    };

    @Override
    public T element() {
        Iterator<T> it = iterator();
        if (it.hasNext()) {
            T next = it.next();
            return next;
        }
        throw new NoSuchElementException();
    };

    @Override
    public boolean offer(T obj) {
        return add(obj);
    };

    @Override
    public T peek() {
        Iterator<T> it = iterator();
        if (it.hasNext()) {
            T next = it.next();
            return next;
        }
        return null;
    };

    @Override
    public T remove() {
        Iterator<T> it = iterator();
        if (it.hasNext()) {
            T next = it.next();
            it.remove();  // remove from set (FIFO)
            return next;
        }
        throw new NoSuchElementException();
    };
};
