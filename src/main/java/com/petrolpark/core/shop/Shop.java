package com.petrolpark.core.shop;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.core.shop.offer.ShopOffer;
import com.petrolpark.core.shop.offer.ShopOfferGenerator;
import com.petrolpark.core.shop.offer.order.ShopOrderModifierEntry;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class Shop {

    public static final Codec<Shop> DIRECT_CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> 
        instance.group(
            Codec.STRING.fieldOf("name").forGetter(Shop::getTranslationKey),
            Codec.list(OfferGeneratorEntry.CODEC).fieldOf("offerGenerators").forGetter(Shop::getOfferGeneratorEntries),
            Codec.list(ShopOrderModifierEntry.CODEC).optionalFieldOf("globalOrderModifiers", Collections.emptyList()).forGetter(Shop::getGlobalOrderModifierEntries),
            TagKey.codec(Registries.ENTITY_TYPE).optionalFieldOf("customerEntityTypes").forGetter(Shop::getCustomerEntityTypes)
        ).apply(instance, Shop::new)
    ));

    public static final Codec<Holder<Shop>> CODEC = RegistryFileCodec.create(PetrolparkRegistries.Keys.SHOP, DIRECT_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Shop>> STREAM_CODEC = ByteBufCodecs.holderRegistry(PetrolparkRegistries.Keys.SHOP);
    
    protected final String translationKey;
    public final List<OfferGeneratorEntry> offerGeneratorEntries;
    protected final List<ShopOrderModifierEntry> globalOrderModifierEntries;

    public final Optional<TagKey<EntityType<?>>> customerEntityTypes;

    public Shop(String translationKey, List<OfferGeneratorEntry> offerGeneratorEntries, List<ShopOrderModifierEntry> globalOrderModifierEntries, Optional<TagKey<EntityType<?>>> customerEntityTypes) {
        this.translationKey = translationKey;
        this.offerGeneratorEntries = offerGeneratorEntries;
        this.globalOrderModifierEntries = globalOrderModifierEntries;

        this.customerEntityTypes = customerEntityTypes;
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

    public List<ShopOrderModifierEntry> getGlobalOrderModifierEntries() {
        return globalOrderModifierEntries;
    };

    public Optional<TagKey<EntityType<?>>> getCustomerEntityTypes() {
        return customerEntityTypes;
    };

    public ShopOffer generateOffer(LootContext context) {
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
        return ShopOffer.EMPTY;
    };

    public boolean canServe(Entity entity) {
        return customerEntityTypes.map(entity.getType()::is).orElse(false);
    };

    public static record OfferGeneratorEntry(ShopOfferGenerator generator, NumberProvider weight) implements LootContextUser {

        public static final Codec<OfferGeneratorEntry> CODEC = RecordCodecBuilder.create(instance -> 
            instance.group(
                ShopOfferGenerator.DIRECT_CODEC.fieldOf("generator").forGetter(OfferGeneratorEntry::generator),
                NumberProviders.CODEC.fieldOf("weight").forGetter(OfferGeneratorEntry::weight)
            ).apply(instance, OfferGeneratorEntry::new)
        );

        @Override
        public Set<LootContextParam<?>> getReferencedContextParams() {
            return Sets.union(generator.getReferencedContextParams(), weight.getReferencedContextParams());
        };
    };
};
