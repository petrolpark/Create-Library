package petrolpark.mc.library.core.world.restaurant;

import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import petrolpark.mc.library.core.data.advancement.criterion.AdvancedCriterionTrigger;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.registry.PetrolparkCriteriaTriggers;
import petrolpark.mc.library.registry.PetrolparkLootContextParamSets;
import petrolpark.mc.library.registry.PetrolparkLootContextParams;

public class RestaurantChangedCriterionTrigger extends AdvancedCriterionTrigger<RestaurantChangedCriterionTrigger.Instance> {
    
    public void trigger(ServerPlayer player, Holder<Restaurant> restaurant, ITeam team) {
        trigger(player, $ -> true, params -> params
            .withParameter(PetrolparkLootContextParams.RESTAURANT, restaurant)
            .withParameter(PetrolparkLootContextParams.TEAM, team)
        );
    };

    @Override
    public Codec<RestaurantChangedCriterionTrigger.Instance> codec() {
        return RestaurantChangedCriterionTrigger.Instance.CODEC;
    };

    @Override
    public LootContextParamSet paramSet() {
        return PetrolparkLootContextParamSets.RESTAURANT_ADVANCEMENT;
    };

    public record Instance(
        Optional<ContextAwarePredicate> player
    ) implements AdvancedCriterionTrigger.AdvancedInstance {

        public static final Codec<RestaurantChangedCriterionTrigger.Instance> CODEC = simpleCodec(RestaurantChangedCriterionTrigger.Instance::new);

        @Override
        public AdvancedCriterionTrigger<?> trigger() {
            return PetrolparkCriteriaTriggers.RESTAURANT_CHANGED.get();
        };

    };
};
