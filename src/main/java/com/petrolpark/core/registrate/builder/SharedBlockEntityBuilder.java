package com.petrolpark.core.registrate.builder;

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
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

public class SharedBlockEntityBuilder<T extends BlockEntity, P> extends PetrolparkBlockEntityBuilder<T, P> {

    public final SharedFeatureFlag featureFlag;

    public static <T extends BlockEntity, P> BlockEntityBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, SharedFeatureFlag flag, String name, BuilderCallback callback, BlockEntityFactory<T> factory) {
        return new SharedBlockEntityBuilder<>(owner, parent, flag, name, callback, factory)
            .asOptional();
    };

    protected SharedBlockEntityBuilder(AbstractRegistrate<?> owner, P parent, SharedFeatureFlag featureFlag, String name, BuilderCallback callback, BlockEntityFactory<T> factory) {
        super(owner, parent, name, callback, factory);
        this.featureFlag = featureFlag;
    };

    @Override
    public <CAP, CTX> SharedBlockEntityBuilder<T, P> registerCapability(BlockCapability<CAP, CTX> capability, ICapabilityProvider<T, CTX, CAP> provider) {
        if (featureFlag.enabled()) super.registerCapability(capability, provider);
        return this;
    };

    @Override
    public SharedBlockEntityBuilder<T, P> renderer(@Nonnull NonNullSupplier<NonNullFunction<Context, BlockEntityRenderer<? super T>>> renderer) {
        if (featureFlag.enabled()) super.renderer(renderer);
        return this;
    };

    @Override
    public SharedBlockEntityBuilder<T, P> onRegister(@Nonnull NonNullConsumer<? super BlockEntityType<T>> callback) {
        if (featureFlag.enabled()) super.onRegister(callback);
        return this;
    };
    
};
