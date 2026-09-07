package petrolpark.mc.library.core.world.restaurant.order;

import java.util.Optional;

import com.mojang.serialization.Codec;

import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import petrolpark.mc.library.core.data.advancement.criterion.AdvancedCriterionTrigger;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.core.world.restaurant.Restaurant;
import petrolpark.mc.library.core.world.restaurant.customer.ICustomer;
import petrolpark.mc.library.registry.PetrolparkCriteriaTriggers;
import petrolpark.mc.library.registry.PetrolparkLootContextParamSets;
import petrolpark.mc.library.registry.PetrolparkLootContextParams;

public class TakeEntityRestaurantOrderTrigger extends AdvancedCriterionTrigger<TakeEntityRestaurantOrderTrigger.Instance> {
    
    public void trigger(ServerPlayer player, Holder<Restaurant> restaurant, ITeam team, Entity customerEntity, ICustomer customer) {
        trigger(player, $ -> true, params -> params
            .withParameter(PetrolparkLootContextParams.RESTAURANT, restaurant)
            .withParameter(PetrolparkLootContextParams.TEAM, team)
            .withParameter(PetrolparkLootContextParams.CUSTOMER, customer)
            .withParameter(PetrolparkLootContextParams.CUSTOMER_ENTITY, customerEntity)
        );
    };

    @Override
    public Codec<TakeEntityRestaurantOrderTrigger.Instance> codec() {
        return TakeEntityRestaurantOrderTrigger.Instance.CODEC;
    };

    @Override
    public LootContextParamSet paramSet() {
        return PetrolparkLootContextParamSets.RESTAURANT_ENTITY_ADVANCEMENT;
    };

    public record Instance(Optional<ContextAwarePredicate> player) implements AdvancedCriterionTrigger.AdvancedInstance {

        public static final Codec<TakeEntityRestaurantOrderTrigger.Instance> CODEC = simpleCodec(TakeEntityRestaurantOrderTrigger.Instance::new);

        @Override
        public AdvancedCriterionTrigger<?> trigger() {
            return PetrolparkCriteriaTriggers.TAKE_ENTITY_RESTAURANT_ORDER.get();
        };

    };
};
