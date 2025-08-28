package com.petrolpark.core.data.predicate.entity;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.util.ColorHelper;

import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;

public record ColorEntitySubPredicate(Optional<DyeColor> color) implements EntitySubPredicate {

    public static final MapCodec<ColorEntitySubPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        DyeColor.CODEC.optionalFieldOf("color").forGetter(ColorEntitySubPredicate::color)
    ).apply(instance, ColorEntitySubPredicate::new));

    @Override
    public MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    };

    @Override
    public boolean matches(@Nonnull Entity entity, @Nonnull ServerLevel level, @Nullable Vec3 position) {
        return entity instanceof LivingEntity livingEntity ? Objects.equals(color.orElse(null), ColorHelper.getColor(livingEntity)) : false;
    };
    
};
