package com.petrolpark.core.world.item.restaurant;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.core.world.item.restaurant.offer.RestaurantOffer;
import com.petrolpark.core.world.item.restaurant.offer.RestaurantOfferGenerator;
import com.petrolpark.core.world.item.restaurant.offer.order.RestaurantOrderModifierEntry;
import com.petrolpark.registry.PetrolparkRegistries;

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
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class Restaurant {

    public static final Codec<Restaurant> DIRECT_CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> 
        instance.group(
            Codec.STRING.fieldOf("name").forGetter(Restaurant::getTranslationKey),
            Codec.list(OfferGeneratorEntry.CODEC).fieldOf("offer_generators").forGetter(Restaurant::getOfferGeneratorEntries),
            Codec.list(RestaurantOrderModifierEntry.CODEC).optionalFieldOf("global_order_modifiers", Collections.emptyList()).forGetter(Restaurant::getGlobalOrderModifierEntries),
            EntityPredicate.CODEC.optionalFieldOf("customers", null).forGetter(Restaurant::getCustomerEntities)
        ).apply(instance, Restaurant::new)
    ));

    public static final Codec<Holder<Restaurant>> CODEC = RegistryFileCodec.create(PetrolparkRegistries.Keys.RESTAURANT, DIRECT_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Restaurant>> STREAM_CODEC = ByteBufCodecs.holderRegistry(PetrolparkRegistries.Keys.RESTAURANT);
    
    protected final String translationKey;
    public final List<OfferGeneratorEntry> offerGeneratorEntries;
    protected final List<RestaurantOrderModifierEntry> globalOrderModifierEntries;

    public final EntityPredicate customerEntities;

    public Restaurant(String translationKey, List<OfferGeneratorEntry> offerGeneratorEntries, List<RestaurantOrderModifierEntry> globalOrderModifierEntries, EntityPredicate customerEntities) {
        this.translationKey = translationKey;
        this.offerGeneratorEntries = offerGeneratorEntries;
        this.globalOrderModifierEntries = globalOrderModifierEntries;
        this.customerEntities = customerEntities;
    };

    public String getTranslationKey() {
        return translationKey;
    };

    @OnlyIn(Dist.CLIENT)
    public Component getName() {
        return Component.translatable(getTranslationKey());
    };

    public List<OfferGeneratorEntry> getOfferGeneratorEntries() {
        return offerGeneratorEntries;
    };

    public List<RestaurantOrderModifierEntry> getGlobalOrderModifierEntries() {
        return globalOrderModifierEntries;
    };

    public EntityPredicate getCustomerEntities() {
        return customerEntities;
    };

    public RestaurantOffer generateOffer(LootContext context) {
        float totalWeight = 0f;
        float[] weights = new float[offerGeneratorEntries.size()];
        for (int i = 0; i < offerGeneratorEntries.size(); i++) {
            OfferGeneratorEntry generator = offerGeneratorEntries.get(i);
            weights[i] = totalWeight;
            totalWeight += generator.weight.getFloat(context);
        };
        float roll = context.getRandom().nextFloat() * totalWeight;
        for (int i = 0; i < offerGeneratorEntries.size(); i++) {
            if (roll > weights[i]) return offerGeneratorEntries.get(i).generator.generate(context, this);
        };
        return RestaurantOffer.EMPTY;
    };

    public boolean canServe(ServerPlayer player, Entity entity) {
        return customerEntities.matches(player, entity);
    };

    public static record OfferGeneratorEntry(RestaurantOfferGenerator generator, NumberProvider weight) implements LootContextUser {

        public static final Codec<OfferGeneratorEntry> CODEC = RecordCodecBuilder.create(instance -> 
            instance.group(
                RestaurantOfferGenerator.DIRECT_CODEC.fieldOf("generator").forGetter(OfferGeneratorEntry::generator),
                NumberProviders.CODEC.fieldOf("weight").forGetter(OfferGeneratorEntry::weight)
            ).apply(instance, OfferGeneratorEntry::new)
        );

        @Override
        public Set<LootContextParam<?>> getReferencedContextParams() {
            return Sets.union(generator.getReferencedContextParams(), weight.getReferencedContextParams());
        };
    };
};
