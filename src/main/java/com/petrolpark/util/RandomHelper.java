package com.petrolpark.util;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

import javax.annotation.Nullable;

import net.minecraft.util.RandomSource;

public class RandomHelper {
    
    @Nullable
    public static final <T> T pick(RandomSource random, List<T> list) {
        if (list.size() == 0) return null;
        if (list.size() == 1) return list.get(0);
        return list.get(random.nextInt(list.size()));
    };

    @Nullable
    public static final <T> T remove(RandomSource random, List<T> list) {
        if (list.size() == 0) return null;
        if (list.size() == 1) return list.remove(0);
        return list.remove(random.nextInt(list.size()));
    };

    /**
     * Copy of {@link Collections#shuffle(List, java.util.Random)} to use Minecraft's single-threaded RandomSource
     * @param list
     * @param random
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static final void shuffle(List<?> list, RandomSource random) {
        int size = list.size();
        if (size < 5 || list instanceof RandomAccess) {
            for (int i=size; i>1; i--) Collections.swap(list, i-1, random.nextInt(i));
        } else {
            Object[] arr = list.toArray();

            for (int i=size; i>1; i--) swap(arr, i-1, random.nextInt(i));

            ListIterator it = list.listIterator();
            for (Object e : arr) {
                it.next();
                it.set(e);
            };
        }
    };

    private static final void swap(Object[] arr, int i, int j) {
        Object tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    };
};
