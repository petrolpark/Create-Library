package com.petrolpark.core.data.predicate.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import com.petrolpark.registry.PetrolparkDataSubPredicates;

import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.phys.Vec3;

public final class IsNeutralPredicate implements EntitySubPredicate {

    public static final IsNeutralPredicate INSTANCE = new IsNeutralPredicate();

    public static final MapCodec<IsNeutralPredicate> CODEC = MapCodec.unit(INSTANCE);

    private IsNeutralPredicate() {};

    @Override
    public MapCodec<? extends EntitySubPredicate> codec() {
        return PetrolparkDataSubPredicates.ENTITY_NEUTRAL.get();
    };

    @Override
    public boolean matches(@Nonnull Entity entity, @Nonnull ServerLevel level, @Nullable Vec3 position) {
        return entity instanceof NeutralMob
            || (entity instanceof Mob mob && mob.goalSelector.getAvailableGoals().stream().map(WrappedGoal::getGoal).anyMatch(goal -> goal instanceof HurtByTargetGoal) && (!(mob instanceof Panda panda) || panda.isAggressive()))
            || (entity instanceof LivingEntity livingEntity && livingEntity.getBrain().getMemoryInternal(MemoryModuleType.ANGRY_AT) != null);
    };
    
};
