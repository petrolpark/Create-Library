package com.petrolpark.core.contamination;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.util.CodecHelper;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public abstract class Contamination<OBJECT, OBJECT_STACK> implements IContamination<OBJECT, OBJECT_STACK> {

    public static final Codec<List<Holder<Contaminant>>> ORPHAN_HOLDER_LIST_CODEC = Codec.list(Contaminant.CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, List<Holder<Contaminant>>> ORPHAN_HOLDER_LIST_STREAM_CODEC = CodecHelper.listStream(Contaminant.STREAM_CODEC);

    protected final OBJECT_STACK stack;
    
    /**
     * Extrinsic {@link Contaminant}s that do not have a parent (if one exists) in this Contamination.
     */
    protected final SortedSet<Contaminant> orphanContaminants = new TreeSet<>(Contaminant::compareTo);
    /**
     * All extrinsic {@link Contaminant}s, whether added themselves or by parental proxy.
     */
    protected final Set<Contaminant> contaminants = new HashSet<>();

    protected Contamination(OBJECT_STACK stack) {
        this.stack = stack;
    };

    @Override
    public final boolean has(Contaminant contaminant) {
        return IntrinsicContaminants.get(this).contains(contaminant) || contaminants.contains(contaminant);
    };

    @Override
    public final boolean hasAnyContaminant() {
        return !IntrinsicContaminants.get(this).isEmpty() || hasAnyExtrinsicContaminant();
    };

    @Override
    public final boolean hasAnyExtrinsicContaminant() {
        return !contaminants.isEmpty();
    };

    @Override
    public final Stream<Contaminant> streamAllContaminants() {
        return Stream.concat(IntrinsicContaminants.get(this).stream(), contaminants.stream());
    };

    @Override
    public final Stream<Contaminant> streamOrphanExtrinsicContaminants() {
        return orphanContaminants.stream();
    };

    @Override
    public final boolean contaminate(final HolderLookup.Provider registries, Contaminant contaminant) {
        if (IntrinsicContaminants.get(this).contains(contaminant)) return false;
        if (!contaminants.add(contaminant)) return false;
        orphanContaminants.removeAll(contaminant.getChildren());
        orphanContaminants.add(contaminant);
        contaminants.addAll(contaminant.getChildren());
        save(registries);
        return true;
    };

    @Override
    public final boolean contaminateAll(final HolderLookup.Provider registries, Stream<Contaminant> contaminantsStream) {
        boolean changed = !contaminantsStream
            .dropWhile(IntrinsicContaminants.get(this)::contains) // Don't include intrinsic Contaminants
            .filter(contaminants::add) // Only include Contaminants whose (parents) are not already here
            .map(contaminant -> {
                orphanContaminants.removeAll(contaminant.getChildren()); // Children of this Contaminant are no longer orphans
                orphanContaminants.add(contaminant); // Add all reminaing Contaminants (they don't have existing parents)
                contaminants.addAll(contaminant.getChildren());
                return contaminant;
            }).toList().isEmpty(); // Need to collect in a List to ensure the map is executed for every element
        if (changed) save(registries);
        return changed;
    };

    @Override
    public final boolean decontaminate(final HolderLookup.Provider registries, Contaminant contaminant) {
        if (IntrinsicContaminants.get(this).contains(contaminant)) return false;
        if (!orphanContaminants.remove(contaminant)) return false;
        contaminants.remove(contaminant);
        for (Contaminant child : contaminant.getChildren()) {
            if (Collections.disjoint(contaminants, child.getParents())) contaminants.remove(child);
        };
        save(registries);
        return true;
    };

    @Override
    public final boolean decontaminateOnly(final HolderLookup.Provider registries, Contaminant contaminant) {
        if (IntrinsicContaminants.get(this).contains(contaminant)) return false;
        if (!orphanContaminants.remove(contaminant)) return false;
        contaminants.remove(contaminant);
        for (Contaminant child : contaminant.getChildren()) {
            if (Collections.disjoint(contaminants, child.getParents())) orphanContaminants.add(child);
        };
        save(registries);
        return true;
    };

    @Override
    public final boolean fullyDecontaminate(final HolderLookup.Provider registries) {
        if (orphanContaminants.isEmpty()) return false;
        orphanContaminants.clear();
        contaminants.clear();
        save(registries);
        return true;
    };

    protected List<Holder<Contaminant>> getOrphanHolderList(final HolderLookup.Provider registries) {
        return orphanContaminants.stream()
            .map(PetrolparkRegistries.holderGetOrThrow(registries, PetrolparkRegistries.Keys.CONTAMINANT))
            .dropWhile(Optional::isEmpty)
            .map(Optional::get)
            .map(h -> (Holder<Contaminant>)h)
            .toList();
    };
};

