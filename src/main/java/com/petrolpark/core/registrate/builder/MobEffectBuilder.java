package com.petrolpark.core.registrate.builder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.petrolpark.PetrolparkRegistrate;
import com.petrolpark.PetrolparkRegistrateProviderTypes;
import com.petrolpark.core.registrate.MobEffectEntry;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import com.tterrag.registrate.util.nullness.NonnullType;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.registries.DeferredHolder;

public class MobEffectBuilder<T extends MobEffect, P> extends AbstractBuilder<MobEffect, T, P, MobEffectBuilder<T, P>> {

    protected final PetrolparkRegistrate petrolparkOwner;

    protected final Factory<T> factory;

    protected MobEffectCategory category = MobEffectCategory.NEUTRAL;
    protected int color = 0xFF000000;
    protected NonNullBiConsumer<T, ResourceLocation> mobEffectCallback = (e, id) -> {};

    public MobEffectBuilder(PetrolparkRegistrate owner, P parent, String name, BuilderCallback callback, Factory<T> factory) {
        super(owner, parent, name, callback, Registries.MOB_EFFECT);
        this.petrolparkOwner = owner;
        this.factory = factory;
    };

    public MobEffectBuilder<T, P> category(MobEffectCategory category) {
        this.category = category;
        return this;  
    };

    public MobEffectBuilder<T, P> color(int color) {
        this.color = color;
        return this;
    };

    public MobEffectBuilder<T, P> attributes(NonNullBiConsumer<T, ResourceLocation> function) {
        this.mobEffectCallback = function;
        return this;
    };

    public MobEffectBuilder<T, P> lang(String lang) {
        return lang(MobEffect::getDescriptionId, lang);
    };   

    public MobEffectBuilder<T, P> defaultLang() {
        return lang(MobEffect::getDescriptionId);
    };

    public MobEffectBuilder<T, P> description(String description) {
        getOwner().addDataGenerator(ProviderType.LANG, prov -> prov.add(getEntry().getDescriptionId() + ".description", description));
        return this;
    };

    public PotionBuilder<MobEffectBuilder<T, P>> potion(int duration) {
        return potion(b -> b.duration(duration));
    };

    public PotionBuilder<MobEffectBuilder<T, P>> potion(NonNullUnaryOperator<MobEffectBuilder.Instance> builderTransformer) {
        return potion(getName(), builderTransformer);
    };

    public PotionBuilder<MobEffectBuilder<T, P>> potion(String potionName, NonNullUnaryOperator<MobEffectBuilder.Instance> builderTransformer) {
        return getOwner().entry(getName(), callback -> PotionBuilder.create(petrolparkOwner, this, potionName, getName(), callback))
            .effect(builderTransformer.apply(new MobEffectBuilder.Instance(() -> get().getDelegate())));
    };

    @SafeVarargs
    public final MobEffectBuilder<T, P> tag(TagKey<MobEffect> ... tags) {
        return tag(PetrolparkRegistrateProviderTypes.MOB_EFFECT_TAGS, tags);
    };

    @Override
    protected @NonnullType T createEntry() {
        final T mobEffect = factory.create(category, color);
        mobEffectCallback.accept(mobEffect, ResourceLocation.fromNamespaceAndPath(getOwner().getModid(), "effect." + getName()));
        return mobEffect;
    };

    @Override
    protected RegistryEntry<MobEffect, T> createEntryWrapper(@Nonnull DeferredHolder<MobEffect, T> delegate) {
        return new MobEffectEntry<>(getOwner(), delegate);
    };

    @Override
    public MobEffectEntry<T> register() {
        return (MobEffectEntry<T>) super.register();
    };

    @FunctionalInterface
    public static interface Factory<T extends MobEffect> {

        public T create(MobEffectCategory category, int color);
    };

    public static class Instance {

        protected final NonNullSupplier<Holder<MobEffect>> effect;
        protected int duration = 600;
        protected int amplifier = 0;
        protected boolean ambient = false;
        protected boolean visible = true;
        protected boolean showIcon = true;
        protected @Nullable MobEffectBuilder.Instance hidden = null;

        public Instance(MobEffectBuilder.Instance builder) {
            this(builder.effect);
            copyFrom(builder);
        };

        public Instance(NonNullSupplier<Holder<MobEffect>> effect) {
            this.effect = effect;
        };

        public Instance copyFrom(Instance builder) {
            this.duration = builder.duration;
            this.amplifier = builder.amplifier;
            this.ambient = builder.ambient;
            this.visible = builder.visible;
            this.showIcon = builder.showIcon;
            this.hidden = builder.hidden;
            return this;
        };

        public NonNullSupplier<Holder<MobEffect>> effect() {
            return effect;
        };

        public int duration() {
            return duration;
        };

        public MobEffectBuilder.Instance duration(int duration) {
            this.duration = duration;
            return this;
        };

        public int amplifier() {
            return amplifier;
        };

        public MobEffectBuilder.Instance amplifier(int amplifier) {
            this.amplifier = amplifier;
            return this;
        };

        public boolean ambient() {
            return ambient;
        };

        public MobEffectBuilder.Instance ambient(boolean ambient) {
            this.ambient = ambient;
            return this;
        };

        public boolean visible() {
            return visible;
        };

        public MobEffectBuilder.Instance visible(boolean visible) {
            this.visible = visible;
            return this;
        };

        public boolean showIcon() {
            return showIcon;
        };

        public MobEffectBuilder.Instance showIcon(boolean showIcon) {
            this.showIcon = showIcon;
            return this;
        };

        public MobEffectBuilder.Instance hidden() {
            return hidden;
        };

        public MobEffectBuilder.Instance hidden(Instance hidden) {
            this.hidden = hidden;
            return this;
        };

        public MobEffectBuilder.Instance copy() {
            return new MobEffectBuilder.Instance(effect()).copyFrom(this);
        };

        public MobEffectInstance build() {
            return new MobEffectInstance(effect.get(), duration, amplifier, ambient, visible, showIcon, hidden == null ? null : hidden.build());
        };
    };
    
};
