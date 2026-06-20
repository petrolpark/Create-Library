package com.petrolpark.core.data.predicate.entity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import com.petrolpark.util.codec.CodecHelper;

import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public record OrEntitySubPredicate(List<EntitySubPredicate> predicates) implements EntitySubPredicate {
    
    public static final MapCodec<OrEntitySubPredicate> CODEC = CodecHelper.singleFieldMap(EntitySubPredicate.CODEC.listOf(), "predicates", OrEntitySubPredicate::predicates, OrEntitySubPredicate::new);

    @Override
    public MapCodec<OrEntitySubPredicate> codec() {
        return CODEC;
    };

    @Override
    public boolean matches(@Nonnull Entity entity, @Nonnull ServerLevel level, @Nullable Vec3 position) {
        return predicates().stream().anyMatch(p -> p.matches(entity, level, position));
    };


};
