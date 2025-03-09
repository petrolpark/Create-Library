package com.petrolpark.util;

import java.util.Set;
import java.util.function.Function;
import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
import java.lang.Exception;

public class GraphHelper {
    
    public static <T> Set<T> getAllDescendants(T parent, Function<T, ? extends Collection<? extends T>> childGetter) throws CircularReferenceException {
        Set<T> descendants = new HashSet<>();
        Queue<T> toAdd = new LinkedList<>(childGetter.apply(parent));
        while (!toAdd.isEmpty()) {
            T child = toAdd.poll();
            if (child == parent) throw new CircularReferenceException();
            if (descendants.add(child)) toAdd.addAll(childGetter.apply(child));
        };
        return descendants;
    };

    public static class CircularReferenceException extends Exception {};
};
