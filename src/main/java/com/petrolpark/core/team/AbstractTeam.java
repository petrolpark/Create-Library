package com.petrolpark.core.team;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.include.com.google.common.base.Objects;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

public abstract class AbstractTeam implements ITeam {
    
    protected final PatchedDataComponentMap components;

    protected AbstractTeam(DataComponentPatch components) {
        this.components = PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, components);
    };

    protected AbstractTeam(PatchedDataComponentMap components) {
        this.components = components;
    };

    public DataComponentPatch getDataComponentPatch() {
        return components.asPatch();
    };

    @Nullable
    public Tag writeDataComponentsTag() {
        return DataComponentPatch.CODEC.encodeStart(NbtOps.INSTANCE, getDataComponentPatch()).getOrThrow();
    };

    @Override
    public final boolean isNone() {
        return false;
    };

    public abstract void setChanged(DataComponentPatch patch);

    @Override
    public final <T> @Nullable T set(@Nonnull DataComponentType<? super T> componentType, @Nonnull T value) {
        T oldValue = components.set(componentType, value);
        if (!Objects.equal(oldValue, value)) setChanged(DataComponentPatch.builder().set(componentType, value).build());
        return oldValue;
    };

    @Override
    public final <T> @Nullable T remove(@Nonnull DataComponentType<? extends T> componentType) {
        T oldValue = components.remove(componentType);
        if (!Objects.equal(oldValue, null)) setChanged(DataComponentPatch.builder().remove(componentType).build());
        return oldValue;
    };

    @Override
    public final void applyComponents(@Nonnull DataComponentPatch patch) {
        components.applyPatch(patch);
        setChanged(patch);
    };

    @Override
    public final void applyComponents(@Nonnull DataComponentMap components) {
        this.components.setAll(components);
        DataComponentPatch.Builder builder = DataComponentPatch.builder();
        components.forEach(builder::set);
        setChanged(builder.build());
    };

    @Override
    public final DataComponentMap getComponents() {
        return components;
    };
};
