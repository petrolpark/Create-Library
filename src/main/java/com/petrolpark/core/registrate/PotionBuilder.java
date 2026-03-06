package com.petrolpark.core.registrate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.function.TriConsumer;

import com.petrolpark.PetrolparkRegistrateProviderTypes;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.OneTimeEventReceiver;
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
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

public class PotionBuilder<P> extends AbstractBuilder<Potion, Potion, P, PotionBuilder<P>> {

    public static final <P> PotionBuilder<P> create(AbstractRegistrate<?> owner, P parent, String name, String potionName, BuilderCallback callback) {
        return new PotionBuilder<>(owner, parent, name, potionName, callback);
    };

    protected final String potionName;
    protected final List<MobEffectBuilder.Instance> effectInstanceBuilders = new ArrayList<>();

    protected PotionBuilder(AbstractRegistrate<?> owner, P parent, String name, String potionName, BuilderCallback callback) {
        super(owner, parent, name, callback, Registries.POTION);
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
            //prov.add(effectKey, englishName); // Actual effect
            prov.add(Items.POTION.getDescriptionId() + effectKey, "Potion of " + englishName);
            prov.add(Items.SPLASH_POTION.getDescriptionId() + effectKey, "Splash Potion of " + englishName);
            prov.add(Items.LINGERING_POTION.getDescriptionId() + effectKey, "Lingering Potion of " + englishName);
            prov.add(Items.TIPPED_ARROW.getDescriptionId() + effectKey, "Arrow of " + englishName);
        });
    };

    public PotionBuilder<P> effect(Stream<MobEffectBuilder.Instance> instanceBuilders) {
        instanceBuilders.forEach(effectInstanceBuilders::add);
        return this;
    };

    public PotionBuilder<P> effect(MobEffectBuilder.Instance ... instanceBuilders) {
        for (MobEffectBuilder.Instance builder : instanceBuilders) effectInstanceBuilders.add(builder);
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
        OneTimeEventReceiver.addForgeListener(RegisterBrewingRecipesEvent.class, event -> consumer.accept(event.getRegistryAccess(), event.getBuilder(), get()));
        return this;
    };

    public PotionBuilder<PotionBuilder<P>> defaultLong(float durationMultiplier) {
        final String longName = "long_" + getName();
        return getOwner().entry(longName, callback -> create(getOwner(), this, longName, potionName, callback))
            .effect(effectInstanceBuilders.stream().map(b -> b.copy()
                .duration((int)(b.duration() * durationMultiplier))
            )).recipe((r, b, e) -> b.addMix(get().getDelegate(), Items.REDSTONE, e.getDelegate()));
    };

    public PotionBuilder<PotionBuilder<P>> defaultStrong() {
        final String strongName = "strong_" + getName();
        return getOwner().entry(strongName, callback -> create(getOwner(), this, strongName, potionName, callback))
            .effect(effectInstanceBuilders.stream().map(b -> b.copy()
                .amplifier(b.amplifier() + 1)
                .duration(b.duration() / 2)
            )).recipe((r, b, e) -> b.addMix(get().getDelegate(), Items.GLOWSTONE_DUST, e.getDelegate()));
    };

    @Override
    protected @NonnullType Potion createEntry() {
        return new Potion(getOwner().getModid() + "." + getName(), effectInstanceBuilders.stream().map(MobEffectBuilder.Instance::build).toArray(MobEffectInstance[]::new));
    };
    
};
