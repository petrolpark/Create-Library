package com.petrolpark;

import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.core.badge.Badge;
import com.petrolpark.core.badge.BadgeRegistrateBuilder;
import com.petrolpark.core.data.loot.numberprovider.entity.EntityNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.entity.LootEntityNumberProviderType;
import com.petrolpark.core.data.loot.numberprovider.itemstack.ItemStackNumberProvider;
import com.petrolpark.core.data.loot.numberprovider.itemstack.LootItemStackNumberProviderType;
import com.petrolpark.core.data.loot.numberprovider.team.LootTeamNumberProviderType;
import com.petrolpark.core.data.loot.numberprovider.team.TeamNumberProvider;
import com.petrolpark.core.data.reward.IReward;
import com.petrolpark.core.data.reward.RewardType;
import com.petrolpark.core.data.reward.entity.EntityRewardType;
import com.petrolpark.core.data.reward.entity.IEntityReward;
import com.petrolpark.core.data.reward.generator.IRewardGenerator;
import com.petrolpark.core.data.reward.generator.RewardGeneratorType;
import com.petrolpark.core.data.reward.team.ITeamReward;
import com.petrolpark.core.data.reward.team.TeamRewardType;
import com.petrolpark.core.item.decay.product.DecayProductType;
import com.petrolpark.core.item.decay.product.IDecayProduct;
import com.petrolpark.core.recipe.bogglepattern.BogglePatternGeneratorType;
import com.petrolpark.core.recipe.bogglepattern.IBogglePatternGenerator;
import com.petrolpark.core.recipe.ingredient.modifier.FluidIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.GenericIngredientModifierType;
import com.petrolpark.core.recipe.ingredient.modifier.IIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.IIngredientModifierType;
import com.petrolpark.core.recipe.ingredient.modifier.INamedIngredientModifierType;
import com.petrolpark.core.recipe.ingredient.modifier.ITypelessIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.ItemIngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.NamedIngredientModifierType;
import com.petrolpark.core.recipe.ingredient.randomizer.IngredientRandomizer;
import com.petrolpark.core.recipe.ingredient.randomizer.IngredientRandomizerType;
import com.petrolpark.core.registrate.SharedBlockBuilder;
import com.petrolpark.core.registrate.SharedBlockEntityBuilder;
import com.petrolpark.core.registrate.SharedItemBuilder;
import com.petrolpark.core.team.ITeam;
import com.petrolpark.core.trade.ITradeListingReference;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.builders.BlockEntityBuilder.BlockEntityFactory;
import com.tterrag.registrate.builders.Builder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.ItemBuilder;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.Util;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredientType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class PetrolparkRegistrate extends AbstractRegistrate<PetrolparkRegistrate> {

    public PetrolparkRegistrate(String modid) {
        super(modid);
    };

    // Builders

    public BadgeRegistrateBuilder<Badge, PetrolparkRegistrate> badge(String name) {
        return badge(name, Badge::new);  
    };

    public <T extends Badge> BadgeRegistrateBuilder<T, PetrolparkRegistrate> badge(String name, NonNullSupplier<T> factory) {
		return (BadgeRegistrateBuilder<T, PetrolparkRegistrate>) entry(name, c -> BadgeRegistrateBuilder.create(this, this, name, c, factory));
	};

    // Simple registered objects

    public <C extends ICondition> RegistryEntry<MapCodec<? extends ICondition>, MapCodec<C>> dataLoadingCondition(String name, MapCodec<C> codec) {
        return simple(name, NeoForgeRegistries.Keys.CONDITION_CODECS, () -> codec);
    };

    public RegistryEntry<DecayProductType, DecayProductType> decayProductType(String name, MapCodec<? extends IDecayProduct> codec, StreamCodec<RegistryFriendlyByteBuf, ? extends IDecayProduct> streamCodec) {
        return simple(name, PetrolparkRegistries.Keys.DECAY_PRODUCT_TYPE, () -> new DecayProductType(codec, streamCodec));
    };

    public RegistryEntry<ITeam.ProviderType, ITeam.ProviderType> teamProviderType(String name, MapCodec<? extends ITeam.Provider> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends ITeam.Provider> streamCodec) {
        return simple(name, PetrolparkRegistries.Keys.TEAM_PROVIDER_TYPE, () -> new ITeam.ProviderType(codec, streamCodec));
    };

    public RegistryEntry<ITradeListingReference.Type, ITradeListingReference.Type> tradeListingReferenceType(String name, MapCodec<? extends ITradeListingReference> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends ITradeListingReference> streamCodec) {
        return simple(name, PetrolparkRegistries.Keys.TRADE_LISTING_REFERENCE_TYPE, () -> new ITradeListingReference.Type(codec, streamCodec));
    };

    public RegistryEntry<Attribute, Attribute> attribute(String name, NonNullSupplier<Attribute> factory) {
        return simple(name, Registries.ATTRIBUTE, factory);
    };

    public <C extends CriterionTrigger<?>> RegistryEntry<CriterionTrigger<?>, C> criterionTrigger(String name, NonNullSupplier<C> triggerFactory) {
        return simple(name, Registries.TRIGGER_TYPE, triggerFactory);
    };

    public <PREDICATE extends ItemSubPredicate> RegistryEntry<ItemSubPredicate.Type<?>, ItemSubPredicate.Type<PREDICATE>> itemSubPredicateType(String name, Codec<PREDICATE> codec) {
        return simple(name, Registries.ITEM_SUB_PREDICATE_TYPE, () -> new ItemSubPredicate.Type<>(codec));
    };

    public <PREDICATE extends EntitySubPredicate> RegistryEntry<MapCodec<? extends EntitySubPredicate>, MapCodec<PREDICATE>> entitySubPredicateType(String name, MapCodec<PREDICATE> codec) {
        return simple(name, Registries.ENTITY_SUB_PREDICATE_TYPE, () -> codec);
    };

    public LootContextParamSet lootContextParamSet(String name, Consumer<LootContextParamSet.Builder> builderConsumer) {
        LootContextParamSet.Builder builder = new LootContextParamSet.Builder();
        builderConsumer.accept(builder);
        LootContextParamSet paramSet = builder.build();
        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(getModid(), name);
        return LootContextParamSets.REGISTRY.put(rl, paramSet);
    };

    public RegistryEntry<LootItemConditionType, LootItemConditionType> lootConditionType(String name, MapCodec<? extends LootItemCondition> codec) {
        return simple(name, Registries.LOOT_CONDITION_TYPE, () -> new LootItemConditionType(codec));
    };

    public <T extends LootItemFunction> RegistryEntry<LootItemFunctionType<?>, LootItemFunctionType<T>> lootItemFunctionType(String name, MapCodec<T> codec) {
        return simple(name, Registries.LOOT_FUNCTION_TYPE, () -> new LootItemFunctionType<>(codec));
    };

    public RegistryEntry<LootNumberProviderType, LootNumberProviderType> lootNumberProviderType(String name, MapCodec<? extends NumberProvider> codec) {
        return simple(name, Registries.LOOT_NUMBER_PROVIDER_TYPE, () -> new LootNumberProviderType(codec));
    };

    public <GLM extends IGlobalLootModifier, CODEC extends MapCodec<GLM>> RegistryEntry<MapCodec<? extends IGlobalLootModifier>, CODEC> globalLootModifierSerializer(String name, CODEC codec) {
        return simple(name, NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, () -> codec);
    };

    public RegistryEntry<LootItemStackNumberProviderType, LootItemStackNumberProviderType> lootItemStackNumberProviderType(String name, MapCodec<? extends ItemStackNumberProvider> codec) {
        return simple(name, PetrolparkRegistries.Keys.LOOT_ITEM_STACK_NUMBER_PROVIDER_TYPE, () -> new LootItemStackNumberProviderType(codec));
    };
    
    public RegistryEntry<LootEntityNumberProviderType, LootEntityNumberProviderType> lootEntityNumberProviderType(String name, MapCodec<? extends EntityNumberProvider> codec) {
        return simple(name, PetrolparkRegistries.Keys.LOOT_ENTITY_NUMBER_PROVIDER_TYPE, () -> new LootEntityNumberProviderType(codec));
    };
    
    public RegistryEntry<LootTeamNumberProviderType, LootTeamNumberProviderType> lootTeamNumberProviderType(String name, MapCodec<? extends TeamNumberProvider> codec) {
        return simple(name, PetrolparkRegistries.Keys.LOOT_TEAM_NUMBER_PROVIDER_TYPE, () -> new LootTeamNumberProviderType(codec));
    };

    public <I extends ICustomIngredient> RegistryEntry<IngredientType<?>, IngredientType<I>> ingredientType(String name, MapCodec<I> codec, StreamCodec<? super RegistryFriendlyByteBuf, I> streamCodec) {
        return simple(name, NeoForgeRegistries.Keys.INGREDIENT_TYPES, () -> new IngredientType<>(codec, streamCodec));
    };

    public <I extends FluidIngredient> RegistryEntry<FluidIngredientType<?>, FluidIngredientType<I>> fluidIngredientType(String name, MapCodec<I> codec, StreamCodec<? super RegistryFriendlyByteBuf, I> streamCodec) {
        return simple(name, NeoForgeRegistries.Keys.FLUID_INGREDIENT_TYPES, () -> new FluidIngredientType<>(codec, streamCodec));
    };

    public RegistryEntry<IngredientRandomizerType, IngredientRandomizerType> ingredientRandomizerType(String name, MapCodec<? extends IngredientRandomizer> serializer) {
        return simple(name, PetrolparkRegistries.Keys.INGREDIENT_RANDOMIZER_TYPE, () -> new IngredientRandomizerType(serializer));
    };

    protected <STACK, TYPELESS_MODIFIER extends ITypelessIngredientModifier<STACK>> RegistryEntry<IIngredientModifierType<? super STACK>, GenericIngredientModifierType<STACK, TYPELESS_MODIFIER>> genericIngredientModifierType(
        ResourceKey<Registry<IIngredientModifierType<? super STACK>>> registryKey,
        Codec<IIngredientModifier<? super STACK>> typeCodec,
        StreamCodec<RegistryFriendlyByteBuf, IIngredientModifier<? super STACK>> typeStreamCodec,
        String name,
        Function<Codec<IIngredientModifier<? super STACK>>, MapCodec<TYPELESS_MODIFIER>> codecFactory,
        Function<StreamCodec<RegistryFriendlyByteBuf, IIngredientModifier<? super STACK>>, StreamCodec<? super RegistryFriendlyByteBuf, TYPELESS_MODIFIER>> streamCodecFactory
    ) {
        return simple(name, registryKey, () -> new GenericIngredientModifierType<>(codecFactory.apply(typeCodec), streamCodecFactory.apply(typeStreamCodec)));
    };

    public RegistryEntry<IIngredientModifierType<? super ItemStack>, NamedIngredientModifierType<ItemStack>> itemIngredientModifierType(String name, MapCodec<? extends ItemIngredientModifier> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends ItemIngredientModifier> streamCodec) {
        return simple(name, PetrolparkRegistries.Keys.INGREDIENT_MODIFIER_TYPE, () -> new NamedIngredientModifierType<>(Util.makeDescriptionId("ingredient_modifier", ResourceLocation.fromNamespaceAndPath(getModid(), name)), codec, streamCodec));
    };

    public RegistryEntry<IIngredientModifierType<? super ItemStack>, IIngredientModifierType<? super ItemStack>> itemIngredientModifierType(String name, IIngredientModifierType<? super ItemStack> type) {
        return simple(name, PetrolparkRegistries.Keys.INGREDIENT_MODIFIER_TYPE, () -> type);
    };

    public RegistryEntry<IIngredientModifierType<? super ItemStack>, INamedIngredientModifierType<ItemStack>> itemIngredientModifierType(String name, NonNullFunction<String, INamedIngredientModifierType<ItemStack>> typeFactory) {
        return simple(name, PetrolparkRegistries.Keys.INGREDIENT_MODIFIER_TYPE, () -> typeFactory.apply(Util.makeDescriptionId("ingredient_modifier", ResourceLocation.fromNamespaceAndPath(getModid(), name))));
    };

    public <TYPELESS_MODIFIER extends ITypelessIngredientModifier<ItemStack>> RegistryEntry<IIngredientModifierType<? super ItemStack>, GenericIngredientModifierType<ItemStack, TYPELESS_MODIFIER>> itemIngredientModifierType(String name, Function<Codec<IIngredientModifier<? super ItemStack>>, MapCodec<TYPELESS_MODIFIER>> codecFactory, Function<StreamCodec<RegistryFriendlyByteBuf, IIngredientModifier<? super ItemStack>>, StreamCodec<? super RegistryFriendlyByteBuf, TYPELESS_MODIFIER>> streamCodecFactory) {
        return genericIngredientModifierType(PetrolparkRegistries.Keys.INGREDIENT_MODIFIER_TYPE, ItemIngredientModifier.CODEC, ItemIngredientModifier.STREAM_CODEC, name, codecFactory, streamCodecFactory);
    };

    public RegistryEntry<IIngredientModifierType<? super FluidStack>, NamedIngredientModifierType<FluidStack>> fluidIngredientModifierType(String name, MapCodec<? extends FluidIngredientModifier> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends FluidIngredientModifier> streamCodec) {
        return simple(name, PetrolparkRegistries.Keys.FLUID_INGREDIENT_MODIFIER_TYPE, () -> new NamedIngredientModifierType<>(Util.makeDescriptionId("ingredient_modifier", ResourceLocation.fromNamespaceAndPath(getModid(), name)), codec, streamCodec));
    };

    public RegistryEntry<IIngredientModifierType<? super FluidStack>, IIngredientModifierType<? super FluidStack>> fluidIngredientModifierType(String name, IIngredientModifierType<? super FluidStack> type) {
        return simple(name, PetrolparkRegistries.Keys.FLUID_INGREDIENT_MODIFIER_TYPE, () -> type);
    };

    public <TYPELESS_MODIFIER extends ITypelessIngredientModifier<FluidStack>> RegistryEntry<IIngredientModifierType<? super FluidStack>, GenericIngredientModifierType<FluidStack, TYPELESS_MODIFIER>> fluidIngredientModifierType(String name, Function<Codec<IIngredientModifier<? super FluidStack>>, MapCodec<TYPELESS_MODIFIER>> codecFactory, Function<StreamCodec<RegistryFriendlyByteBuf, IIngredientModifier<? super FluidStack>>, StreamCodec<? super RegistryFriendlyByteBuf, TYPELESS_MODIFIER>> streamCodecFactory) {
        return genericIngredientModifierType(PetrolparkRegistries.Keys.FLUID_INGREDIENT_MODIFIER_TYPE, FluidIngredientModifier.CODEC, FluidIngredientModifier.STREAM_CODEC, name, codecFactory, streamCodecFactory);
    };

    public RegistryEntry<RewardGeneratorType, RewardGeneratorType> rewardGeneratorType(String name, MapCodec<? extends IRewardGenerator> codec) {
        return simple(name, PetrolparkRegistries.Keys.REWARD_GENERATOR_TYPE, () -> new RewardGeneratorType(codec));
    };

    public RegistryEntry<RewardType, RewardType> rewardType(String name, MapCodec<? extends IReward> codec) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(getModid(), name);
        return simple(name, PetrolparkRegistries.Keys.REWARD_TYPE, () -> new RewardType(Util.makeDescriptionId("reward", id), codec));
    };

    public RegistryEntry<EntityRewardType, EntityRewardType> entityRewardType(String name, MapCodec<? extends IEntityReward> codec) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(getModid(), name);
        return simple(name, PetrolparkRegistries.Keys.ENTITY_REWARD_TYPE, () -> new EntityRewardType(Util.makeDescriptionId("entity_reward", id), codec));
    };

    public RegistryEntry<TeamRewardType, TeamRewardType> teamRewardType(String name, MapCodec<? extends ITeamReward> codec) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(getModid(), name);
        return simple(name, PetrolparkRegistries.Keys.TEAM_REWARD_TYPE, () -> new TeamRewardType(Util.makeDescriptionId("team_reward", id), codec));
    };

    public RegistryEntry<BogglePatternGeneratorType, BogglePatternGeneratorType> bogglePatternGeneratorType(String name, MapCodec<? extends IBogglePatternGenerator> codec, MapCodec<? extends IBogglePatternGenerator> directCodec) {
        return simple(name, PetrolparkRegistries.Keys.BOGGLE_PATTERN_GENERATOR_TYPE, () -> new BogglePatternGeneratorType(codec, directCodec));
    };

    public RegistryEntry<BogglePatternGeneratorType, BogglePatternGeneratorType> bogglePatternGeneratorType(String name, NonNullSupplier<? extends IBogglePatternGenerator> unitFactory) {
        MapCodec<? extends IBogglePatternGenerator> codec = MapCodec.unit(unitFactory);
        return simple(name, PetrolparkRegistries.Keys.BOGGLE_PATTERN_GENERATOR_TYPE, () -> new BogglePatternGeneratorType(codec, codec));
    };

    public <O extends ParticleOptions, T extends ParticleType<O>> RegistryEntry<ParticleType<?>, T> particleType(String name, NonNullSupplier<T> factory) {
        return simple(name, Registries.PARTICLE_TYPE, factory);
    };
    
    // Shared features

    public class SharedFeatureBuilderCallback implements BuilderCallback {

        protected final SharedFeatureFlag featureFlag;

        public SharedFeatureBuilderCallback(SharedFeatureFlag featureFlag) {
            this.featureFlag = featureFlag;
        };

        @Override
        public <R, T extends R> RegistryEntry<R, T> accept(@Nonnull String name, @Nonnull ResourceKey<? extends Registry<R>> type, @Nonnull Builder<R, T, ?, ?> builder, @Nonnull NonNullSupplier<? extends T> factory, @Nonnull NonNullFunction<DeferredHolder<R, T>, ? extends RegistryEntry<R, T>> entryFactory) {
            if (featureFlag.enabled()) return PetrolparkRegistrate.super.accept(name, type, builder, factory, entryFactory);
            return entryFactory.apply(DeferredHolder.create(type, ResourceLocation.fromNamespaceAndPath(getModid(), name))); // Create entry but do not register it
        };

    };

    public <R, T extends R, P, S2 extends Builder<R, T, P, S2>> S2 sharedEntry(SharedFeatureFlag featureFlag, @Nonnull String name, @Nonnull NonNullFunction<BuilderCallback, S2> factory) {
        return factory.apply(new SharedFeatureBuilderCallback(featureFlag));
    };

    public <T extends BlockEntity> BlockEntityBuilder<T, PetrolparkRegistrate> sharedBlockEntity(SharedFeatureFlag featureFlag, String name, BlockEntityFactory<T> factory) {
        return sharedEntry(featureFlag, name, callback -> SharedBlockEntityBuilder.create(this, this, featureFlag, name, callback, factory));
    };

    public <T extends Block, P> BlockBuilder<T, PetrolparkRegistrate> sharedBlock(SharedFeatureFlag featureFlag, @Nonnull String name, @Nonnull NonNullFunction<BlockBehaviour.Properties, T> factory) {
        return sharedEntry(featureFlag, name, callback -> SharedBlockBuilder.create(this, this, featureFlag, name, callback, factory));
    };

    public <T extends Item, P> ItemBuilder<T, P> sharedItem(P parent, @Nonnull SharedFeatureFlag featureFlag, String name, NonNullFunction<Item.Properties, T> factory) {
        return sharedEntry(featureFlag, name, callback -> new SharedItemBuilder<>(this, parent, featureFlag, name, callback, factory));
    };

    public <T extends Item> ItemBuilder<T, PetrolparkRegistrate> sharedItem(@Nonnull SharedFeatureFlag featureFlag, String name, NonNullBiFunction<Item.Properties, SharedFeatureFlag, T> factory) {
        return sharedEntry(featureFlag, name, callback -> new SharedItemBuilder<>(this, this, featureFlag, name, callback, properties -> factory.apply(properties, featureFlag)));
    };
    
};
