package com.petrolpark.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.petrolpark.util.NestedSequenceBiMap.Entry.SubMapEntry;
import com.petrolpark.util.NestedSequenceBiMap.Entry.ValueEntry;

public final class EnumSequenceBiMap<K extends Enum<K>, V> extends NestedSequenceBiMap<K, V> {

    protected final EnumMap<K, Entry<K, V>> entryMap;
    protected final Supplier<Map<V, List<K>>> sequenceMap;

    public EnumSequenceBiMap(Class<K> enumClass, int size) {
        this(new EnumMap<>(enumClass));
    };

    public EnumSequenceBiMap(Map<K, Entry<K, V>> entryMap) {
        this(new EnumMap<>(entryMap));
    };

    protected EnumSequenceBiMap(EnumMap<K, Entry<K, V>> entryMap) {
        this.entryMap = entryMap;
        sequenceMap = Suppliers.memoize(() -> {
            final Map<V, List<K>> sequenceMap = new HashMap<>();
            populateSequenceMap(sequenceMap, new SubMapEntry<>(this), Collections.emptyList());
            return sequenceMap;
        });
    };

    @Override
    public Map<V, List<K>> getKeySequenceMap() {
        return sequenceMap.get();
    };

    /**
     * {@inheritDoc}
     * Not to be modified after intialization.
     */
    @Override
    protected Map<K, Entry<K, V>> getEntryMap() {
        return entryMap;
    };

    private static final <K extends Enum<K>, V> EnumSequenceBiMap<K, V> cast(NestedSequenceBiMap<K, V> map) {
        if (map instanceof EnumSequenceBiMap<K, V> enumMap) return enumMap;
        return null;
    };

    public static final <K extends Enum<K>, V> Codec<EnumSequenceBiMap<K, V>> codec(Codec<K> keyCodec, Codec<V> valueCodec) {
        return Codec.<EnumSequenceBiMap<K, V>>recursive("EnumSequenceBiMap", wrappedMapCodec -> 
            Codec.unboundedMap(
                keyCodec,
                Codec.<EnumSequenceBiMap<K, V>, V>either(wrappedMapCodec, valueCodec).<Entry<K, V>>flatComapMap(
                    either -> either.<Entry<K, V>>map(SubMapEntry::new, ValueEntry::new),
                    entry -> entry.asValue()
                        .map(Either::<EnumSequenceBiMap<K, V>, V>right)
                        .map(DataResult::success)
                        .orElse(entry.asMap()
                            .map(EnumSequenceBiMap::cast)
                            .map(Either::<EnumSequenceBiMap<K, V>, V>left)
                            .map(DataResult::success)
                            .orElse(DataResult.error(() -> "Not a value or submap"))
                        )
                )
            ).xmap(EnumSequenceBiMap::new, EnumSequenceBiMap::getEntryMap)
        );
    };

    /**
     * Create an {@link EnumSequenceBiMap} where all values {@link ISequenceBiMap#getKeySequence(Object) have random sequences} with one of two consecutive lengths.
     * @param <K> Key type
     * @param <V> Value type
     * @param enumClass
     * @param values Pre-randomized queue of values with which to populate the map
     * @return Random map
     */
    public static final <K extends Enum<K>, V> EnumSequenceBiMap<K, V> createRandom(Class<K> enumClass, Queue<V> values, Random random) {
        final EnumSequenceBiMap<K, V> map = new EnumSequenceBiMap<>(enumClass, values.size());
        final K[] enumValues = enumClass.getEnumConstants();
        if (enumValues == null) throw new IllegalStateException("Not an enum: "+enumClass.getName());

        final int depth = MathsHelper.floorLog(values.size(), enumValues.length);
        final int leaves = MathsHelper.exponentiate(enumValues.length, depth);
        final int smallLeafSize = values.size() / leaves;
        final int bigLeaves = values.size() % leaves;

        final Queue<Entry<K, V>> bigLeafEntries = IntStream.range(0, bigLeaves).mapToObj(i -> {
            List<V> leafValues = new ArrayList<>(smallLeafSize + 1);
            for (int v = 0; v <= smallLeafSize; v++) leafValues.add(values.poll());
            return createRandomLeafEntry(enumValues, leafValues, random);
        }).collect(Collectors.toCollection(ArrayDeque::new));

        final Queue<Entry<K, V>> smallLeafEntries = IntStream.range(0, leaves - bigLeaves).mapToObj(i -> {
            List<V> leafValues = new ArrayList<>(smallLeafSize);
            for (int v = 0; v < smallLeafSize; v++) leafValues.add(values.poll());
            return createRandomLeafEntry(enumValues, leafValues, random);
        }).collect(Collectors.toCollection(ArrayDeque::new));

        populateRecursively(enumClass, enumValues, map, bigLeafEntries, smallLeafEntries, random, depth);
    
        return map;
    };

    private static final <K extends Enum<K>, V> void populateRecursively(Class<K> enumClass, K[] enumValues, EnumSequenceBiMap<K, V> map, Queue<Entry<K, V>> bigLeafEntries, Queue<Entry<K, V>> smallLeafEntries, Random random, int recursionLeft) {
        if (recursionLeft == 0) {
            for (K key : enumValues) map.entryMap.put(key, pollRandom(bigLeafEntries, smallLeafEntries, random));
        } else {
            for (K key : enumValues) {
                EnumSequenceBiMap<K, V> subMap = new EnumSequenceBiMap<>(enumClass, 0);
                populateRecursively(enumClass, enumValues, subMap, bigLeafEntries, smallLeafEntries, random, recursionLeft - 1);
                map.entryMap.put(key, new SubMapEntry<>(subMap));
            };
        };
    };

    private static final <K extends Enum<K>, V> Entry<K, V> pollRandom(Queue<Entry<K, V>> bigLeafEntries, Queue<Entry<K, V>> smallLeafEntries, Random random) {
        if (bigLeafEntries.isEmpty()) {
            if (smallLeafEntries.isEmpty()) throw new IllegalStateException();
            else return smallLeafEntries.poll();
        };
        if (smallLeafEntries.isEmpty()) return bigLeafEntries.poll();
        return random.nextBoolean() ? smallLeafEntries.poll() : bigLeafEntries.poll();
    };

    /**
     * Generate a leaf (no children) {@link Entry} with fewer than {@code enumValues.length} total values.
     * @param <K> Key type
     * @param <V> Value type
     * @param enumValues Every value of the Enum
     * @param values Pre-randomized list of values of length between {@code 0} and {@code enumValues.length} exclusive
     * @param random
     * @return {@link Entry} with no children
     */
    private static final <K extends Enum<K>, V> Entry<K, V> createRandomLeafEntry(K[] enumValues, List<V> values, Random random) {
        if (values.size() == 0 || values.size() > enumValues.length) throw new IllegalArgumentException("Invalid number of values for leaf entry: "+values.size());
        if (values.size() == 1) return new ValueEntry<>(values.get(0));
        if (values.size() == enumValues.length) return new SubMapEntry<K, V>(
            new EnumSequenceBiMap<>(IntStream.range(0, enumValues.length)
                .boxed()
                .collect(Collectors.toMap(
                    i -> enumValues[i],
                    i -> new ValueEntry<>(values.get(i))
                ))
            )
        );
        List<K> randomEnumValues = List.of(enumValues);
        Collections.shuffle(randomEnumValues, random);
        randomEnumValues = randomEnumValues.subList(0, values.size()); // Pick n Enum values
        return new SubMapEntry<K, V>(
            new EnumSequenceBiMap<>(IntStream.range(0, values.size())
                .boxed()
                .collect(Collectors.toMap(
                    randomEnumValues::get,
                    i -> new ValueEntry<>(values.get(i))
                ))
            )
        );
    };
    
};
