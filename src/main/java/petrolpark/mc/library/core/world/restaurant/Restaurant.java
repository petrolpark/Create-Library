package petrolpark.mc.library.core.world.restaurant;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.core.world.restaurant.order.RestaurantOrderGenerator;
import petrolpark.mc.library.core.world.restaurant.order.ServerRestaurantOrder;
import petrolpark.mc.library.registry.PetrolparkLootContextParamSets;
import petrolpark.mc.library.registry.PetrolparkLootContextParams;
import petrolpark.mc.library.registry.PetrolparkRegistries;
import petrolpark.mc.library.util.DataValidationHelper;

@ParametersAreNonnullByDefault
public record Restaurant(
    String translationKey,
    List<OrderGeneratorEntry> orderGeneratorEntries,
    List<RestaurantOrderGenerator.ModifierEntry> globalOrderModifierEntries,
    EntityPredicate customers,
    NumberProvider xpRequiredForLevel
) {

    public static final Codec<Restaurant> DIRECT_CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> 
        instance.group(
            Codec.STRING.fieldOf("name").forGetter(Restaurant::translationKey),
            Codec.list(OrderGeneratorEntry.CODEC).fieldOf("order_generators").forGetter(Restaurant::orderGeneratorEntries),
            Codec.list(RestaurantOrderGenerator.ModifierEntry.CODEC).optionalFieldOf("global_order_modifiers", Collections.emptyList()).forGetter(Restaurant::globalOrderModifierEntries),
            EntityPredicate.CODEC.optionalFieldOf("customers", EntityPredicate.Builder.entity().build()).forGetter(Restaurant::customers),
            NumberProviders.CODEC
                .validate(DataValidationHelper.validateParamSet(PetrolparkLootContextParamSets.RESTAURANT_XP_CALCULATION, "XP calculator"))
                .fieldOf("xp_required_for_level").forGetter(Restaurant::xpRequiredForLevel)
        ).apply(instance, Restaurant::new)
    ));  

    public static final Codec<Holder<Restaurant>> CODEC = RegistryFileCodec.create(PetrolparkRegistries.Keys.RESTAURANT, DIRECT_CODEC, false);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Restaurant>> STREAM_CODEC = ByteBufCodecs.holderRegistry(PetrolparkRegistries.Keys.RESTAURANT);

    @OnlyIn(Dist.CLIENT)
    public Component getName() {
        return Component.translatable(translationKey());
    };

    public boolean canServe(ServerPlayer player, Entity entity) {
        return customers().matches(player, entity);
    };

    public static final Optional<ServerRestaurantOrder> generateOrder(ServerPlayer player, Holder<Restaurant> restaurantHolder, ITeam team, @Nullable Entity customer) {
        if (restaurantHolder.value().orderGeneratorEntries().isEmpty()) return Optional.empty();
        if (team.isNone()) return Optional.empty();
        
        final LootContext context = new LootContext.Builder(new LootParams.Builder(player.serverLevel())
                .withParameter(PetrolparkLootContextParams.RESTAURANT, restaurantHolder)
                .withParameter(PetrolparkLootContextParams.TEAM, team)
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withOptionalParameter(PetrolparkLootContextParams.CUSTOMER_ENTITY, customer)
                .create(PetrolparkLootContextParamSets.RESTAURANT_ORDER_GENERATION)
            ).create(Optional.empty());

        // Increment number of orders taken
        final AtomicInteger id = new AtomicInteger();
        RestaurantsData.modify(team, data -> id.set(data.getOrCreate(restaurantHolder).totalOrdersTaken++));

        // Choose the generator to use
        final RestaurantOrderGenerator generator;
        chooseGenerator: if (restaurantHolder.value().orderGeneratorEntries().size() == 1) {
            generator = restaurantHolder.value().orderGeneratorEntries().get(0).generator().value();
        } else {
            record WeightedGenerator(RestaurantOrderGenerator generator, float weight) {};

            final List<WeightedGenerator> generators = restaurantHolder.value().orderGeneratorEntries().stream()
                .map(entry -> new WeightedGenerator(entry.generator().value(), entry.weight().getFloat(context)))
                .toList();
            
            final double sum = generators.stream().mapToDouble(WeightedGenerator::weight).sum();
            double value = context.getRandom().nextDouble() * sum;

            for (WeightedGenerator weightedGenerator : generators) {
                value -= weightedGenerator.weight();
                if (value <= 0f) {
                    generator = weightedGenerator.generator();
                    break chooseGenerator;
                };
            };

            return Optional.empty();
        };

        // Generate the order
        return Optional.of(generator.generate(context, id.get(), restaurantHolder.value().globalOrderModifierEntries().stream()));
    };

    public record OrderGeneratorEntry(Holder<RestaurantOrderGenerator> generator, NumberProvider weight) implements LootContextUser {

        public static final Codec<OrderGeneratorEntry> CODEC = RecordCodecBuilder.create(instance -> 
            instance.group(
                RestaurantOrderGenerator.CODEC.fieldOf("generator").forGetter(OrderGeneratorEntry::generator),
                NumberProviders.CODEC.fieldOf("weight").forGetter(OrderGeneratorEntry::weight)
            ).apply(instance, OrderGeneratorEntry::new)
        );

        @Override
        public void validate(ValidationContext context) {
            LootContextUser.super.validate(context);
            DataValidationHelper.validateHolder(generator(), context, "generator");
            weight().validate(context.forChild(".weight"));
        };
    };
};
