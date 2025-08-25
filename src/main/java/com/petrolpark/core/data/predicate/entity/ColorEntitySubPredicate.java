package com.petrolpark.core.data.predicate.entity;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import com.petrolpark.util.CodecHelper;

import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;

public record ColorEntitySubPredicate(DyeColor color) implements EntitySubPredicate {

    public static final MapCodec<ColorEntitySubPredicate> CODEC = CodecHelper.singleFieldMap(DyeColor.CODEC, "color", ColorEntitySubPredicate::color, ColorEntitySubPredicate::new);

    @Override
    public MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    };

    @Override
    public boolean matches(@Nonnull Entity entity, @Nonnull ServerLevel level, @Nullable Vec3 position) {
        if (entity instanceof Sheep sheep) return sheep.getColor() == color;
        if (entity instanceof Shulker shulker) return Objects.equals(color, shulker.getColor());
        return false;
    };
    
};
