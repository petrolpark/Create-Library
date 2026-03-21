package com.petrolpark.core.registrate.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.function.TriConsumer;

import com.petrolpark.PetrolparkRegistrate;
import com.petrolpark.PetrolparkRegistrateProviderTypes;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonnullType;

import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

public class PotionBuilder<P> extends AbstractBuilder<Potion, Potion, P, PotionBuilder<P>> {

    public static final <P> PotionBuilder<P> create(PetrolparkRegistrate owner, P parent, String name, String potionName, BuilderCallback callback) {
        return new PotionBuilder<>(owner, parent, name, potionName, callback);
    };

    protected final PetrolparkRegistrate petrolparkOwner;

    protected final String potionName;
    protected final List<MobEffectBuilder.MobEffectInstanceBuilder> effectInstanceBuilders = new ArrayList<>();

    protected PotionBuilder(PetrolparkRegistrate owner, P parent, String name, String potionName, BuilderCallback callback) {
        super(owner, parent, name, callback, Registries.POTION);
        this.petrolparkOwner = owner;
        this.potionName = potionName;
    };

    public PotionBuilder<P> defaultLang() {
        return lang((prov, s) -> RegistrateLangProvider.toEnglishName(s));
    };

    public PotionBuilder<P> lang(String englishName) {
        return lang((prov, s) -> englishName);
    };

    public PotionBuilder<P> lang(NonNullBiFunction<RegistrateLangProvider, String, String> localizedNameProvider) {
        final String effectKey = Util.makeDescriptionId(".effect", ResourceLocation.fromNamespaceAndPath(getOwner().getModid(), potionName));
        
        return setData(ProviderType.LANG, (ctx, prov) -> {
            final String englishName = localizedNameProvider.apply(prov, potionName);

            prov.add(Items.POTION.getDescriptionId() + effectKey, "Potion of " + englishName);
            prov.add(Items.SPLASH_POTION.getDescriptionId() + effectKey, "Splash Potion of " + englishName);
            prov.add(Items.LINGERING_POTION.getDescriptionId() + effectKey, "Lingering Potion of " + englishName);
            prov.add(Items.TIPPED_ARROW.getDescriptionId() + effectKey, "Arrow of " + englishName);
        });
    };

    public PotionBuilder<P> effect(Stream<MobEffectBuilder.MobEffectInstanceBuilder> instanceBuilders) {
        instanceBuilders.forEach(effectInstanceBuilders::add);
        return this;
    };

    public PotionBuilder<P> effect(MobEffectBuilder.MobEffectInstanceBuilder ... instanceBuilders) {
        for (MobEffectBuilder.MobEffectInstanceBuilder builder : instanceBuilders) effectInstanceBuilders.add(builder);
        return this;
    };

    @SuppressWarnings("unchecked")
    public PotionBuilder<P> tag(TagKey<Potion> ... tags) {
        return tag(PetrolparkRegistrateProviderTypes.POTION_TAGS, tags);
    };;

    public PotionBuilder<P> simpleRecipe(ItemLike item) {
        return recipe((r, b, e) -> b.addMix(Potions.AWKWARD, item.asItem(), e.getDelegate()));
    };

    public PotionBuilder<P> recipe(TriConsumer<RegistryAccess, PotionBrewing.Builder, RegistryEntry<Potion, Potion>> consumer) {
        NeoForge.EVENT_BUS.addListener(RegisterBrewingRecipesEvent.class, event -> consumer.accept(event.getRegistryAccess(), event.getBuilder(), get()));
        return this;
    };

    public PotionBuilder<PotionBuilder<P>> potion(String name, String potionName) {
        return getOwner().entry(name, callback -> create(petrolparkOwner, this, name, potionName, callback));
    };

    public PotionBuilder<PotionBuilder<P>> defaultLong(float durationMultiplier) {
        return potion("long_" + getName(), potionName)
            .effect(effectInstanceBuilders.stream().map(b -> b.copy()
                .duration((int)(b.duration() * durationMultiplier))
            )).recipe((r, b, e) -> b.addMix(get().getDelegate(), Items.REDSTONE, e.getDelegate()));
    };

    public PotionBuilder<PotionBuilder<P>> defaultStrong() {
        return potion("strong_" + getName(), potionName)
            .effect(effectInstanceBuilders.stream().map(b -> b.copy()
                .amplifier(b.amplifier() + 1)
                .duration(b.duration() / 2)
            )).recipe((r, b, e) -> b.addMix(get().getDelegate(), Items.GLOWSTONE_DUST, e.getDelegate()));
    };

    @Override
    protected @NonnullType Potion createEntry() {
        return new Potion(getOwner().getModid() + "." + potionName, effectInstanceBuilders.stream().map(MobEffectBuilder.MobEffectInstanceBuilder::build).toArray(MobEffectInstance[]::new));
    };
    
};
