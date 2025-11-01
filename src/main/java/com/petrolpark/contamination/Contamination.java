package com.petrolpark.contamination;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

public abstract class Contamination<OBJECT, OBJECT_STACK> implements IContamination<OBJECT, OBJECT_STACK> {

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
    public final boolean contaminate(Contaminant contaminant) {
        if (IntrinsicContaminants.get(this).contains(contaminant)) return false;
        if (!contaminants.add(contaminant)) return false;
        orphanContaminants.removeAll(contaminant.getChildren());
        orphanContaminants.add(contaminant);
        contaminants.addAll(contaminant.getChildren());
        save();
        return true;
    };

    @Override
    public final boolean contaminateAll(Stream<Contaminant> contaminantsStream) {
        boolean changed = !contaminantsStream
            .dropWhile(IntrinsicContaminants.get(this)::contains) // Don't include intrinsic Contaminants
            .filter(contaminants::add) // Only include Contaminants whose (parents) are not already here
            .map(contaminant -> {
                orphanContaminants.removeAll(contaminant.getChildren()); // Children of this Contaminant are no longer orphans
                orphanContaminants.add(contaminant); // Add all reminaing Contaminants (they don't have existing parents)
                contaminants.addAll(contaminant.getChildren());
                return contaminant;
            }).toList().isEmpty(); // Need to collect in a List to ensure the map is executed for every element
        if (changed) save();
        return changed;
    };

    @Override
    public final boolean decontaminate(Contaminant contaminant) {
        if (IntrinsicContaminants.get(this).contains(contaminant)) return false;
        if (!orphanContaminants.remove(contaminant)) return false;
        contaminants.remove(contaminant);
        for (Contaminant child : contaminant.getChildren()) {
            if (Collections.disjoint(contaminants, child.getParents())) contaminants.remove(child);
        };
        save();
        return true;
    };

    @Override
    public final boolean decontaminateOnly(Contaminant contaminant) {
        if (IntrinsicContaminants.get(this).contains(contaminant)) return false;
        if (!orphanContaminants.remove(contaminant)) return false;
        contaminants.remove(contaminant);
        for (Contaminant child : contaminant.getChildren()) {
            if (Collections.disjoint(contaminants, child.getParents())) orphanContaminants.add(child);
        };
        save();
        return true;
    };

    @Override
    public final boolean fullyDecontaminate() {
        if (orphanContaminants.isEmpty()) return false;
        orphanContaminants.clear();
        contaminants.clear();
        save();
        return true;
    };

    /**
     * Prefer {@link #readNBT(RegistryAccess, ListTag)} whenever the Level is accessible.
     */
    @Deprecated
    public void readNBT(ListTag contaminationTag) {
        contaminateAll(contaminationTag.stream().map(Tag::getAsString).map(ResourceLocation::new).map(Contaminant::get));
    };

    public void readNBT(RegistryAccess registryAccess, ListTag contaminationTag) {
        contaminateAll(contaminationTag.stream().map(Tag::getAsString).map(ResourceLocation::new).map(rl -> Contaminant.get(registryAccess, rl)));
    };

    /**
     * Prefer {@link #writeNBT(RegistryAccess)} whenever the Level is accessible.
     */
    @Deprecated
    public ListTag writeNBT() {
        ListTag tag = new ListTag();
        orphanContaminants.forEach(c -> tag.add(StringTag.valueOf(c.getLocation().toString())));
        return tag;
    };

    public ListTag writeNBT(RegistryAccess registryAccess) {
        ListTag tag = new ListTag();
        orphanContaminants.forEach(c -> tag.add(StringTag.valueOf(c.getLocation(registryAccess).toString())));
        return tag;
    };
};

