package com.petrolpark.core.contamination;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

/**
 * A {@link Contamination} not tied to any specific object. When these are used, the developer will have to manage loading and saving them themselves.
 */
public class GenericContamination extends Contamination<Object, Object> {

    private final Runnable onSave;

    public GenericContamination() {
        this(() -> {});
    };

    public GenericContamination(Runnable onSave) {
        super(new Object());
        this.onSave = onSave;
    };

    public GenericContamination readNBT(Tag tag, HolderLookup.Provider registries) {
        orphanContaminants.clear();
        ORPHAN_HOLDER_LIST_CODEC.parse(NbtOps.INSTANCE, tag).ifSuccess(ls -> ls.stream().map(orphanContaminants::add));
        return this;
    };

    public Tag writeNBT(HolderLookup.Provider registries) {
        return ORPHAN_HOLDER_LIST_CODEC.encodeStart(NbtOps.INSTANCE, getOrphanHolderList()).getOrThrow();
    };

    @Override
    @Deprecated
    public Contaminable<Object, Object> getContaminable() {
        return Contaminables.GENERIC;
    };

    @Override
    @Deprecated
    public Object getType() {
        return stack;
    };

    @Override
    @Deprecated
    public double getAmount() {
        return 1d;
    };

    /**
     * @deprecated Generic Contaminations must be saved externally.
     */
    @Override
    @Deprecated
    public final void save() {
        onSave.run();
    };
    
};
