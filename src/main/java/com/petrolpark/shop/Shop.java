package com.petrolpark.shop;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.network.GsonSerializableCodecs;
import com.petrolpark.shop.offer.ShopOffer;
import com.petrolpark.shop.offer.ShopOfferGenerator;
import com.petrolpark.shop.offer.ShopOrderModifierEntry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Shop {

    public static final Codec<Shop> CODEC = RecordCodecBuilder.create(instance -> 
        instance.group(
            Codec.STRING.fieldOf("name").forGetter(Shop::getTranslationKey),
            Codec.list(OfferGeneratorEntry.CODEC).fieldOf("offerGenerators").forGetter(Shop::getOfferGeneratorEntries),
            Codec.list(ShopOrderModifierEntry.CODEC).optionalFieldOf("globalOrderModifiers", Collections.emptyList()).forGetter(Shop::getGlobalOrderModifierEntries),
            TagKey.codec(Registries.ENTITY_TYPE).optionalFieldOf("customerEntityTypes").forGetter(Shop::getCustomerEntityTypes)
        ).apply(instance, Shop::new)
    );
    
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
                ShopOfferGenerator.CODEC.fieldOf("generator").forGetter(OfferGeneratorEntry::generator),
                GsonSerializableCodecs.NUMBER_PROVIDER.fieldOf("weight").forGetter(OfferGeneratorEntry::weight)
            ).apply(instance, OfferGeneratorEntry::new)
        );

        @Override
        public Set<LootContextParam<?>> getReferencedContextParams() {
            return Sets.union(generator.getReferencedContextParams(), weight.getReferencedContextParams());
        };
    };
};
