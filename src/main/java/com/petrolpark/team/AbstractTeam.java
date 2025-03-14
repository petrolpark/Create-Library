package com.petrolpark.team;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

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

    @Override
    public <T> @Nullable T set(@Nonnull DataComponentType<? super T> componentType, @Nonnull T value) {
        return components.set(componentType, value);
    };

    @Override
    public <T> @Nullable T remove(@Nonnull DataComponentType<? extends T> componentType) {
        return components.remove(componentType);
    };

    @Override
    public void applyComponents(@Nonnull DataComponentPatch patch) {
        components.applyPatch(patch);
    };

    @Override
    public void applyComponents(@Nonnull DataComponentMap components) {
        this.components.setAll(components);
    };

    @Override
    public DataComponentMap getComponents() {
        return components;
    };
};
