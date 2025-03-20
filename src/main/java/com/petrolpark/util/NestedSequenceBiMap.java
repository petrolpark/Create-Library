package com.petrolpark.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.petrolpark.util.ISequenceBiMap.IEntry;

public abstract class NestedSequenceBiMap<K, V> implements ISequenceBiMap<K, V, List<K>, NestedSequenceBiMap.Entry<K, V>, NestedSequenceBiMap<K, V>> {

    protected abstract Map<K, Entry<K, V>> getEntryMap();

    @Override
    public final Entry<K, V> get(K key) {
        return getEntryMap().get(key);
    };

    @Override
    public final Entry<K, V> get(List<K> sequence) {
        Iterator<K> iterator = sequence.iterator();
        NestedSequenceBiMap<K, V> map = this;
        Entry<K, V> entry = null;
        while (iterator.hasNext()) {
            K key = iterator.next();
            entry = map.get(key);
            if (entry != null && entry.asMap().isPresent()) map = entry.asMap().get();
            else if (iterator.hasNext()) return null;
        };
        return entry;
    };

    @Override
    public final V follow(List<K> sequence) {
        Iterator<K> iterator = sequence.iterator();
        NestedSequenceBiMap<K, V> map = this;
        while (iterator.hasNext()) {
            K key = iterator.next();
            iterator.remove();
            Entry<K, V> entry = map.get(key);
            if (entry == null) return null;
            if (entry.asValue().isPresent()) return entry.asValue().get();
            if (entry.asMap().isPresent()) map = entry.asMap().get();
            throw new IllegalStateException("Entry is neither a value nor a sub-map");
        };
        return null;
    };

    @Override
    public final List<K> getKeySequence(V value) {
        return getKeySequenceMap().get(value);
    };

    @Override
    public final Collection<V> values() {
        return getKeySequenceMap().keySet();
    };

    public sealed interface Entry<K, V> extends IEntry<K, V, List<K>, Entry<K, V>, NestedSequenceBiMap<K, V>> permits Entry.ValueEntry, Entry.SubMapEntry {

        public record ValueEntry<K, V>(V value) implements Entry<K, V> {

            @Override
            public Optional<? extends V> asValue() {
                return Optional.ofNullable(value);
            };

            @Override
            public Optional<NestedSequenceBiMap<K, V>> asMap() {
                return Optional.empty();
            };

        };

        public record SubMapEntry<K, V>(NestedSequenceBiMap<K, V> subMap) implements Entry<K, V> {

            @Override
            public Optional<? extends V> asValue() {
                return Optional.empty();
            };

            @Override
            public Optional<NestedSequenceBiMap<K, V>> asMap() {
                return Optional.ofNullable(subMap);
            };

        };
    };

    protected static <K, V> void populateSequenceMap(Map<V, List<K>> sequenceMap, Entry<K, V> entry, List<K> sequence) {
        entry.asValue().ifPresent(value -> sequenceMap.put(value, sequence));
        entry.asMap().ifPresent(map -> {
            map.getEntryMap().forEach((key, subEntry) -> {
                List<K> subSequence = new ArrayList<>(sequence.size() + 1);
                subSequence.addAll(sequence);
                subSequence.add(key);
                populateSequenceMap(sequenceMap, subEntry, subSequence);
            });
        });
    };
};
