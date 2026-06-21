package petrolpark.mc.library.core.badge;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.registry.PetrolparkCriteriaTriggers;
import petrolpark.mc.library.registry.PetrolparkRegistries;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

public class ReceiveBadgeCriterionTrigger extends SimpleCriterionTrigger<ReceiveBadgeCriterionTrigger.Instance> {

    @Override
    public Codec<Instance> codec() {
        return ReceiveBadgeCriterionTrigger.Instance.CODEC;
    };

    public void trigger(ServerPlayer player, Badge badge) {
        trigger(player, instance -> instance.badge() == badge);
    };

    public static final Criterion<ReceiveBadgeCriterionTrigger.Instance> criterion(Badge badge) {
        return PetrolparkCriteriaTriggers.RECEIVE_BADGE.get().createCriterion(new ReceiveBadgeCriterionTrigger.Instance(Optional.empty(), badge));
    };

    public static record Instance(Optional<ContextAwarePredicate> player, Badge badge) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<ReceiveBadgeCriterionTrigger.Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(ReceiveBadgeCriterionTrigger.Instance::player),
            PetrolparkRegistries.BADGES.byNameCodec().fieldOf("badge").forGetter(ReceiveBadgeCriterionTrigger.Instance::badge)
        ).apply(instance, ReceiveBadgeCriterionTrigger.Instance::new));

    };
    
};
