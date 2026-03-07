package com.petrolpark.core.registrate;

import javax.annotation.Nonnull;

import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.SharedFeatureFlag;
import com.simibubi.create.foundation.data.CreateBlockEntityBuilder;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

@RequiresCreate
public class SharedCreateBlockEntityBuilder<T extends BlockEntity, P> extends CreateBlockEntityBuilder<T, P> {

    public final SharedFeatureFlag featureFlag;

    public static <T extends BlockEntity, P> SharedCreateBlockEntityBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, SharedFeatureFlag featureFlag, String name, BuilderCallback callback, BlockEntityFactory<T> factory) {
		final SharedCreateBlockEntityBuilder<T, P> builder = new SharedCreateBlockEntityBuilder<>(owner, parent, featureFlag, name, callback, factory);
        builder.asOptional();
        return builder;
	};

    protected SharedCreateBlockEntityBuilder(AbstractRegistrate<?> owner, P parent, SharedFeatureFlag featureFlag, String name, BuilderCallback callback, BlockEntityFactory<T> factory) {
        super(owner, parent, name, callback, factory);
        this.featureFlag = featureFlag;
    };

    @Override
    public SharedCreateBlockEntityBuilder<T, P> renderer(@Nonnull NonNullSupplier<NonNullFunction<BlockEntityRendererProvider.Context, BlockEntityRenderer<? super T>>> renderer) {
        if (featureFlag.enabled())
            super.renderer(renderer);
        return this;
    };

    @Override
    public BlockEntityBuilder<T, P> onRegister(@Nonnull NonNullConsumer<? super BlockEntityType<T>> callback) {
        if (!featureFlag.enabled()) return this;
        return super.onRegister(callback);
    };

    @Override
    protected void registerVisualizer() {
        if (featureFlag.enabled()) super.registerVisualizer();
    };
    
};
