package petrolpark.mc.library.core.client.ponder;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import petrolpark.mc.library.util.AdvancementHelper;

public class WatchPonderCriterionTrigger extends SimpleCriterionTrigger<WatchPonderCriterionTrigger.Instance> {
    
    public void trigger(ServerPlayer player, ResourceLocation scene) {
        trigger(player, instance -> instance.matches(scene));
    };

    @Override
    public Codec<WatchPonderCriterionTrigger.Instance> codec() {
        return WatchPonderCriterionTrigger.Instance.CODEC;
    };

    public record Instance(
        Optional<ContextAwarePredicate> player,
        Optional<ResourceLocation> scene
    ) implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<WatchPonderCriterionTrigger.Instance> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(WatchPonderCriterionTrigger.Instance::player),
                ResourceLocation.CODEC.optionalFieldOf("scene").forGetter(WatchPonderCriterionTrigger.Instance::scene)
            ).apply(instance, WatchPonderCriterionTrigger.Instance::new)
        );

        boolean matches(ResourceLocation scene) {
            return AdvancementHelper.test(scene(), scene);
        };

    };
};
