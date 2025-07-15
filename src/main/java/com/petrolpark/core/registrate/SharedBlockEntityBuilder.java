package com.petrolpark.core.registrate;

import javax.annotation.Nonnull;

import com.petrolpark.compat.SharedFeatureFlag;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SharedBlockEntityBuilder<T extends BlockEntity, P> extends BlockEntityBuilder<T, P> {

    public final SharedFeatureFlag featureFlag;

    public static <T extends BlockEntity, P> BlockEntityBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, SharedFeatureFlag flag, String name, BuilderCallback callback, BlockEntityFactory<T> factory) {
        return new SharedBlockEntityBuilder<>(owner, parent, flag, name, callback, factory);
    };

    protected SharedBlockEntityBuilder(AbstractRegistrate<?> owner, P parent, SharedFeatureFlag featureFlag, String name, BuilderCallback callback, BlockEntityFactory<T> factory) {
        super(owner, parent, name, callback, factory);
        this.featureFlag = featureFlag;
    };

    @Override
    public BlockEntityBuilder<T, P> renderer(@Nonnull NonNullSupplier<NonNullFunction<Context, BlockEntityRenderer<? super T>>> renderer) {
        if (featureFlag.enabled()) return super.renderer(renderer);
        return this;
    };

    @Override
    public BlockEntityBuilder<T, P> onRegister(@Nonnull NonNullConsumer<? super BlockEntityType<T>> callback) {
        if (!featureFlag.enabled()) return this;
        return super.onRegister(callback);
    };
    
};
