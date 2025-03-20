package com.petrolpark.util;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface ISequenceBiMap<K, V, S extends Iterable<K>, E extends ISequenceBiMap.IEntry<K, V, S, E, ? super M>, M extends ISequenceBiMap<K, V, S, E, ? super M>> {

    /**
     * Get the {@link IEntry} associated with this key.
     * Equivalently, the {@link IEntry} {@link ISequenceBiMap#get(IKeySequence) associated} with the sequence of length {@code 1} containing this key.
     * @param key
     * @return {@code null} if no {@link IEntry} exists
     */
    public E get(K key);

    /**
     * Get the {@link IEntry} at this exact sequence position.
     * @param sequence Not mutated
     * @return {@link IEntry} or {@code null} if no entry exists with that sequence.
     * @see ISequenceBiMap#follow(Iterable)
     */
    public E get(S sequence);

    /**
     * Follow a sequence of keys until a value is found. A key is taken off the start of the sequence and used to find the {@link IEntry} there.
     * If this {@link IEntry} is a value, this is returned. If it is a sub-map, then the process is repeated with the next key.
     * @param sequence This is shortened by this operation
     * @return The value found by following the sequence of keys, or {@code null} if the sequence reaches a dead end, or the sequence runs out before a value is found.
     * @see ISequenceBiMap#get(Iterable)
     */
    public V follow(S sequence);

    /**
     * The map of values to the sequences by which those values are indexed.
     * @return Non-{@code null} map
     * @see {@link ISequenceBiMap#getKeySequence(Object)}
     */
    public Map<V, S> getKeySequenceMap();

    /**
     * Essentially a shortcut for {@link ISequenceBiMap#getKeySequenceMap()}.{@link Map#get(Object)}.
     * @param value
     * @return The {@code sequence} such that {@code get(sequence) == value}, or {@code null} if that value is not in this Map
     * @see ISequenceBiMap#getKeySequenceMap()
     */
    public S getKeySequence(V value);

    public Collection<V> values();

    public interface IEntry<K, V, S extends Iterable<K>, E extends IEntry<K, V, S, E, M>, M extends ISequenceBiMap<K, V, S, E, M>> {

        public Optional<? extends V> asValue();

        public Optional<M> asMap();
    };
};
