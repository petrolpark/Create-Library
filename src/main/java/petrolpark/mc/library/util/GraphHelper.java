package petrolpark.mc.library.util;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class GraphHelper {

    /**
     * @param <T> Child and parent
     * @param parent
     * @param childGetter Function that returns direct children of the parent
     * @return Set of all descendants (not just closest-generation)
     * @throws CircularReferenceException If a parent includes itself as a descendant
     * @see GraphHelper#getAllDescendants(Object, Function, Predicate)
     */
    public static <T> Set<T> getAllDescendants(T parent, Function<T, ? extends Iterable<? extends T>> childGetter) throws CircularReferenceException {
        return getAllDescendants(parent, childGetter, obj -> false);
    };
    
    /**
     * @param <T> Child and parent
     * @param parent
     * @param childGetter Function that returns direct children of the parent
     * @param noChildren Predicate of objects to not bother checking for further children
     * @return Set of all descendants (not just closest-generation)
     * @throws CircularReferenceException If a parent includes itself as a descendant
     * @see GraphHelper#getAllDescendants(Object, Function)
     */
    public static <T> Set<T> getAllDescendants(T parent, Function<T, ? extends Iterable<? extends T>> childGetter, Predicate<T> noChildren) throws CircularReferenceException {
        Set<T> descendants = new HashSet<>();
        Queue<T> toAdd = new LinkedList<>();
        childGetter.apply(parent).forEach(toAdd::add);
        while (!toAdd.isEmpty()) {
            T child = toAdd.poll();
            if (child == parent) throw new CircularReferenceException();
            if (descendants.add(child) & !noChildren.test(child)) childGetter.apply(child).forEach(toAdd::add);
        };
        return descendants;
    };

    public static class CircularReferenceException extends Exception {};
};
