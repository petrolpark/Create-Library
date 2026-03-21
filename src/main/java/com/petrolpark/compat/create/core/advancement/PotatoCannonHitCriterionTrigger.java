package com.petrolpark.compat.create.core.advancement;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.util.AdvancementHelper;

import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class PotatoCannonHitCriterionTrigger extends SimpleCriterionTrigger<PotatoCannonHitCriterionTrigger.Instance> {

    public void trigger(ServerPlayer player, ItemStack projectile, Entity target) {
        trigger(player, instance -> instance.matches(player, projectile, target));
    };
    
    public record Instance(Optional<ContextAwarePredicate> player, List<ItemPredicate> projectile, Optional<EntityPredicate> target) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<PotatoCannonHitCriterionTrigger.Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ContextAwarePredicate.CODEC.optionalFieldOf("player").forGetter(PotatoCannonHitCriterionTrigger.Instance::player),
            ItemPredicate.CODEC.listOf().optionalFieldOf("projectile", Collections.emptyList()).forGetter(PotatoCannonHitCriterionTrigger.Instance::projectile),
            EntityPredicate.CODEC.optionalFieldOf("target").forGetter(PotatoCannonHitCriterionTrigger.Instance::target)
        ).apply(instance, PotatoCannonHitCriterionTrigger.Instance::new));

        boolean matches(ServerPlayer player, ItemStack projectile, Entity target) {
            return AdvancementHelper.testItems(projectile(), Collections.singletonList(projectile))
                && AdvancementHelper.testEntity(player, target(), target);
        };
    };

    @Override
    public Codec<PotatoCannonHitCriterionTrigger.Instance> codec() {
        return PotatoCannonHitCriterionTrigger.Instance.CODEC;
    };
};
