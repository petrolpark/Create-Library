package com.petrolpark.core.data.predicate.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.util.CodecHelper;

import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public record PermissionsEntitySubPredicate(int level) implements EntitySubPredicate {

    public static final MapCodec<PermissionsEntitySubPredicate> CODEC = CodecHelper.singleFieldMap(Codec.intRange(0, 4), "level", PermissionsEntitySubPredicate::level, PermissionsEntitySubPredicate::new);

    @Override
    public MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    };

    @Override
    public boolean matches(@Nonnull Entity entity, @Nonnull ServerLevel level, @Nullable Vec3 position) {
        return entity.hasPermissions(this.level);
    };
    
};
