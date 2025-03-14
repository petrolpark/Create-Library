package com.petrolpark.contamination;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.ListTag;

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

    public GenericContamination(ListTag tag) {
        this();
        if (tag != null) readNBT(tag);
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
    public final void save(RegistryAccess registries) {
        onSave.run();
    };
    
};
