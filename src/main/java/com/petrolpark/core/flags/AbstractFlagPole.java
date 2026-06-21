package com.petrolpark.core.flags;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.petrolpark.util.codec.CodecHelper;

import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public abstract class AbstractFlagPole<OBJECT, OBJECT_STACK> implements IFlagPole<OBJECT, OBJECT_STACK> {

    public static final Codec<List<Holder<Flag>>> ORPHAN_HOLDER_LIST_CODEC = Codec.list(Flag.CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, List<Holder<Flag>>> ORPHAN_HOLDER_LIST_STREAM_CODEC = CodecHelper.listStream(Flag.STREAM_CODEC);

    protected final OBJECT_STACK stack;
    
    /**
     * Extrinsic {@link Flag}s that do not have a parent (if one exists) in this Flags.
     */
    protected final SortedSet<Holder<Flag>> orphanFlags = new TreeSet<>(Flag::compareHolders);
    /**
     * All extrinsic {@link Flag}s, whether added themselves or by parental proxy.
     */
    protected final Set<Holder<Flag>> flags = new HashSet<>();

    protected AbstractFlagPole(OBJECT_STACK stack) {
        this.stack = stack;
    };

    @Override
    public final boolean has(Holder<Flag> flag) {
        return isIntrinsic(flag) || flags.contains(flag);
    };

    @Override
    public final boolean hasAnyFlag() {
        return !getFlaggable().getIntrinsicFlags(getType()).isEmpty()|| hasAnyExtrinsicFlag();
    };

    @Override
    public final boolean hasAnyExtrinsicFlag() {
        return !flags.isEmpty();
    };

    @Override
    public final Stream<Holder<Flag>> streamAllFlags() {
        return Stream.concat(streamIntrinsicFlags(), flags.stream());
    };

    @Override
    public final Stream<Holder<Flag>> streamOrphanExtrinsicFlags() {
        return orphanFlags.stream();
    };

    @Override
    public final boolean flag(Holder<Flag> flag) {
        if (isIntrinsic(flag)) return false;
        if (!flags.add(flag)) return false;
        orphanFlags.removeAll(flag.value().getChildren());
        orphanFlags.add(flag);
        flags.addAll(flag.value().getChildren());
        save();
        return true;
    };

    @Override
    public final boolean flagAll(Stream<Holder<Flag>> flagsStream) {
        boolean changed = !flagsStream
            .dropWhile(this::isIntrinsic) // Don't include intrinsic Flags
            .filter(flags::add) // Only include Flags whose (parents) are not already here
            .map(flag -> {
                orphanFlags.removeAll(flag.value().getChildren()); // Children of this Flag are no longer orphans
                orphanFlags.add(flag); // Add all reminaing Flags (they don't have existing parents)
                flags.addAll(flag.value().getChildren());
                return flag;
            }).toList().isEmpty(); // Need to collect in a List to ensure the map is executed for every element
        if (changed) save();
        return changed;
    };

    @Override
    public final boolean unflag(Holder<Flag> flag) {
        if (isIntrinsic(flag)) return false;
        if (!orphanFlags.remove(flag)) return false;
        flags.remove(flag);
        for (Holder<Flag> child : flag.value().getChildren()) {
            if (Collections.disjoint(flags, child.value().getParents())) flags.remove(child);
        };
        save();
        return true;
    };

    @Override
    public final boolean unflagOnly(Holder<Flag> flag) {
        if (isIntrinsic(flag)) return false;
        if (!orphanFlags.remove(flag)) return false;
        flags.remove(flag);
        for (Holder<Flag> child : flag.value().getChildren()) {
            if (Collections.disjoint(flags, child.value().getParents())) orphanFlags.add(child);
        };
        save();
        return true;
    };

    @Override
    public final boolean clearFlags() {
        if (orphanFlags.isEmpty()) return false;
        orphanFlags.clear();
        flags.clear();
        save();
        return true;
    };

    protected List<Holder<Flag>> getOrphanHolderList() {
        return orphanFlags.stream()
            //.map(PetrolparkRegistries.holderGetOrThrow(registries, PetrolparkRegistries.Keys.FLAG))
            //.dropWhile(Optional::isEmpty)
            //.map(Optional::get)
            //.map(h -> (Holder<Flag>)h)
            .toList();
    };

    @Override
    public Stream<Holder<Flag>> streamIntrinsicFlags() {
        return getFlaggable().getIntrinsicFlags(getType()).stream();
    };

    @Override
    public Stream<Holder<Flag>> streamShownIfAbsentFlags() {
        return getFlaggable().getShownIfAbsentFlags(getType()).stream();
    };

    @Override
    public boolean isIntrinsic(Holder<Flag> flagHolder) {
        return getFlaggable().getIntrinsicFlags(getType()).contains(flagHolder);
    };
};

