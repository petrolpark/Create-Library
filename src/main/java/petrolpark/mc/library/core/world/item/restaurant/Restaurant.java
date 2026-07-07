package petrolpark.mc.library.core.world.item.restaurant;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
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
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import petrolpark.mc.library.core.world.item.restaurant.order.RestaurantOrderGenerator;
import petrolpark.mc.library.core.world.item.restaurant.order.RestaurantOrderModifier;
import petrolpark.mc.library.registry.PetrolparkRegistries;

public class Restaurant {

    public static final Codec<Restaurant> DIRECT_CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(instance -> 
        instance.group(
            Codec.STRING.fieldOf("name").forGetter(Restaurant::getTranslationKey),
            Codec.list(OrderGeneratorEntry.CODEC).fieldOf("order_generators").forGetter(Restaurant::getOfferGeneratorEntries),
            Codec.list(RestaurantOrderModifier.CODEC).optionalFieldOf("global_order_modifiers", Collections.emptyList()).forGetter(Restaurant::getGlobalOrderModifiers),
            EntityPredicate.CODEC.optionalFieldOf("customers", EntityPredicate.Builder.entity().build()).forGetter(Restaurant::getCustomerEntities),
            NumberProviders.CODEC.fieldOf("xp_required_for_level").forGetter(Restaurant::getXpRequiredForLevel)
        ).apply(instance, Restaurant::new)
    ));  

    public static final Codec<Holder<Restaurant>> ID_CODEC = RegistryFileCodec.create(PetrolparkRegistries.Keys.RESTAURANT, DIRECT_CODEC, false);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Restaurant>> STREAM_CODEC = ByteBufCodecs.holderRegistry(PetrolparkRegistries.Keys.RESTAURANT);
    
    protected final String translationKey;
    public final List<OrderGeneratorEntry> offerGeneratorEntries;
    protected final List<RestaurantOrderModifier> globalOrderModifiers;

    public final EntityPredicate customerEntities;
    protected final NumberProvider xpRequired;

    public Restaurant(String translationKey, List<OrderGeneratorEntry> offerGeneratorEntries, List<RestaurantOrderModifier> globalOrderModifierEntries, EntityPredicate customerEntities, NumberProvider xpRequired) {
        this.translationKey = translationKey;
        this.offerGeneratorEntries = offerGeneratorEntries;
        this.globalOrderModifiers = globalOrderModifierEntries;
        this.customerEntities = customerEntities;
        this.xpRequired = xpRequired;
    };

    public String getTranslationKey() {
        return translationKey;
    };

    @OnlyIn(Dist.CLIENT)
    public Component getName() {
        return Component.translatable(getTranslationKey());
    };

    public List<OrderGeneratorEntry> getOfferGeneratorEntries() {
        return offerGeneratorEntries;
    };

    public List<RestaurantOrderModifier> getGlobalOrderModifiers() {
        return globalOrderModifiers;
    };

    public EntityPredicate getCustomerEntities() {
        return customerEntities;
    };

    public NumberProvider getXpRequiredForLevel() {
        return xpRequired;
    };

    public boolean canServe(ServerPlayer player, Entity entity) {
        return customerEntities.matches(player, entity);
    };

    public static record OrderGeneratorEntry(RestaurantOrderGenerator generator, NumberProvider weight) implements LootContextUser {

        public static final Codec<OrderGeneratorEntry> CODEC = RecordCodecBuilder.create(instance -> 
            instance.group(
                RestaurantOrderGenerator.UNVALIDATED_DIRECT_CODEC.fieldOf("generator").forGetter(OrderGeneratorEntry::generator),
                NumberProviders.CODEC.fieldOf("weight").forGetter(OrderGeneratorEntry::weight)
            ).apply(instance, OrderGeneratorEntry::new)
        );

        @Override
        public Set<LootContextParam<?>> getReferencedContextParams() {
            return Sets.union(generator.getReferencedContextParams(), weight.getReferencedContextParams());
        };
    };
};
