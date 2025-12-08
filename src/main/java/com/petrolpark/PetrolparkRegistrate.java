package com.petrolpark;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.core.badge.Badge;
import com.petrolpark.core.badge.BadgeRegistrateBuilder;
import com.petrolpark.core.data.loot.modifier.ILootPoolEntryModifier;
import com.petrolpark.core.data.loot.modifier.ILootTableModifier;
import com.petrolpark.core.data.loot.modifier.LootPoolEntryModifierType;
import com.petrolpark.core.data.loot.modifier.LootTableModifierType;
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
import com.petrolpark.core.recipe.bogglepattern.generator.BogglePatternGeneratorType;
import com.petrolpark.core.recipe.bogglepattern.generator.IBogglePatternGenerator;
import com.petrolpark.core.recipe.ingredient.advanced.FluidAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.GenericAdvancedIngredientType;
import com.petrolpark.core.recipe.ingredient.advanced.IAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.IAdvancedIngredientType;
import com.petrolpark.core.recipe.ingredient.advanced.INamedAdvancedIngredientType;
import com.petrolpark.core.recipe.ingredient.advanced.ITypelessAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.ItemAdvancedIngredient;
import com.petrolpark.core.recipe.ingredient.advanced.NamedAdvancedIngredientType;
import com.petrolpark.core.recipe.ingredient.randomizer.IngredientRandomizer;
import com.petrolpark.core.recipe.ingredient.randomizer.IngredientRandomizerType;
import com.petrolpark.core.registrate.PetrolparkBlockBuilder;
import com.petrolpark.core.registrate.PetrolparkItemBuilder;
import com.petrolpark.core.registrate.SharedBlockBuilder;
import com.petrolpark.core.registrate.SharedBlockEntityBuilder;
import com.petrolpark.core.registrate.SharedItemBuilder;
import com.petrolpark.core.scratch.IScratchClass;
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
import net.minecraft.advancements.critereon.EntitySubPredicates;
import net.minecraft.advancements.critereon.EntitySubPredicates.EntityVariantPredicateType;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
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

    @Override
    public <T extends Block, P> BlockBuilder<T, P> block(@Nonnull P parent, @Nonnull String name, @Nonnull NonNullFunction<BlockBehaviour.Properties, T> factory) {
        return entry(name, callback -> PetrolparkBlockBuilder.create(this, parent, name, callback, factory));
    };

    @Override
    public <T extends Item, P> ItemBuilder<T, P> item(@Nonnull P parent, @Nonnull String name, @Nonnull NonNullFunction<Properties, T> factory) {
        return entry(name, callback -> PetrolparkItemBuilder.create(this, parent, name, callback, factory));
    };

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

    public <VARIANT> RegistryEntry<MapCodec<? extends EntitySubPredicate>, MapCodec<EntitySubPredicates.EntityVariantPredicateType<VARIANT>.Instance>> entityVariantPredicateType(String name, Codec<VARIANT> variantCodec, Function<Entity, Optional<VARIANT>> variantGetter) {
        EntitySubPredicates.EntityVariantPredicateType<VARIANT> predicateType = EntityVariantPredicateType.<VARIANT>create(variantCodec, variantGetter);
        return entitySubPredicateType(name, predicateType.codec);
    };

    public <VARIANT> RegistryEntry<MapCodec<? extends EntitySubPredicate>, MapCodec<EntitySubPredicates.EntityVariantPredicateType<VARIANT>.Instance>> entityVariantPredicateType(String name, Registry<VARIANT> variantRegistry, Function<Entity, Optional<VARIANT>> variantGetter) {
        EntitySubPredicates.EntityVariantPredicateType<VARIANT> predicateType = EntityVariantPredicateType.<VARIANT>create(variantRegistry, variantGetter);
        return entitySubPredicateType(name, predicateType.codec);
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

    public RegistryEntry<LootTableModifierType, LootTableModifierType> lootTableModifierType(String name, MapCodec<? extends ILootTableModifier> codec) {
        return simple(name, PetrolparkRegistries.Keys.LOOT_TABLE_MODIFIER_TYPE, () -> new LootTableModifierType(codec));  
    };

    public RegistryEntry<LootPoolEntryModifierType, LootPoolEntryModifierType> lootPoolEntryModifierType(String name, MapCodec<? extends ILootPoolEntryModifier> codec) {
        return simple(name, PetrolparkRegistries.Keys.LOOT_POOL_ENTRY_MODIFIER_TYPE, () -> new LootPoolEntryModifierType(codec));
    };

    public <I extends RecipeInput, R extends Recipe<? extends I>> RegistryEntry<RecipeType<?>, RecipeType<R>> recipeType(String name) {
        return simple(name, Registries.RECIPE_TYPE, () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(getModid(), name)));
    };

    public <I extends RecipeInput, R extends Recipe<? extends I>, S extends RecipeSerializer<? extends R>> RegistryEntry<RecipeSerializer<?>, S> recipeSerializer(String name, NonNullSupplier<S> factory) {
        return simple(name, Registries.RECIPE_SERIALIZER, factory);
    };

    public <R extends CraftingRecipe> RegistryEntry<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<R>> recipeSerializer(String name, SimpleCraftingRecipeSerializer.Factory<R> constructor) {
        return recipeSerializer(name, () -> new SimpleCraftingRecipeSerializer<>(constructor));
    };

    public <I extends RecipeInput, R extends Recipe<? extends I>> RegistryEntry<RecipeSerializer<?>, RecipeSerializer<R>> recipeSerializer(String name, MapCodec<R> codec, StreamCodec<RegistryFriendlyByteBuf, R> streamCodec) {
        return recipeSerializer(name, () -> new RecipeSerializer<R>() {
            
            @Override
            public MapCodec<R> codec() {
                return codec;
            };

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
                return streamCodec;
            };
        });
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

    protected <STACK, TYPELESS_INGREDIENT extends ITypelessAdvancedIngredient<STACK>> RegistryEntry<IAdvancedIngredientType<? super STACK>, GenericAdvancedIngredientType<STACK, TYPELESS_INGREDIENT>> genericAdvancedIngredientType(
        ResourceKey<Registry<IAdvancedIngredientType<? super STACK>>> registryKey,
        Codec<IAdvancedIngredient<? super STACK>> typeCodec,
        StreamCodec<RegistryFriendlyByteBuf, IAdvancedIngredient<? super STACK>> typeStreamCodec,
        String name,
        Function<Codec<IAdvancedIngredient<? super STACK>>, MapCodec<TYPELESS_INGREDIENT>> codecFactory,
        Function<StreamCodec<RegistryFriendlyByteBuf, IAdvancedIngredient<? super STACK>>, StreamCodec<? super RegistryFriendlyByteBuf, TYPELESS_INGREDIENT>> streamCodecFactory
    ) {
        return simple(name, registryKey, () -> new GenericAdvancedIngredientType<>(codecFactory.apply(typeCodec), streamCodecFactory.apply(typeStreamCodec)));
    };

    public RegistryEntry<IAdvancedIngredientType<? super ItemStack>, NamedAdvancedIngredientType<ItemStack>> itemAdvancedIngredientType(String name, MapCodec<? extends ItemAdvancedIngredient> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends ItemAdvancedIngredient> streamCodec) {
        return simple(name, PetrolparkRegistries.Keys.ADVANCED_ITEM_INGREDIENT_TYPE, () -> new NamedAdvancedIngredientType<>(Util.makeDescriptionId("advanced_ingredient", ResourceLocation.fromNamespaceAndPath(getModid(), name)), codec, streamCodec));
    };

    public RegistryEntry<IAdvancedIngredientType<? super ItemStack>, IAdvancedIngredientType<? super ItemStack>> itemAdvancedIngredientType(String name, IAdvancedIngredientType<? super ItemStack> type) {
        return simple(name, PetrolparkRegistries.Keys.ADVANCED_ITEM_INGREDIENT_TYPE, () -> type);
    };

    public RegistryEntry<IAdvancedIngredientType<? super ItemStack>, INamedAdvancedIngredientType<ItemStack>> itemAdvancedIngredientType(String name, NonNullFunction<String, INamedAdvancedIngredientType<ItemStack>> typeFactory) {
        return simple(name, PetrolparkRegistries.Keys.ADVANCED_ITEM_INGREDIENT_TYPE, () -> typeFactory.apply(Util.makeDescriptionId("advanced_ingredient", ResourceLocation.fromNamespaceAndPath(getModid(), name))));
    };

    public <TYPELESS_INGREDIENT extends ITypelessAdvancedIngredient<ItemStack>> RegistryEntry<IAdvancedIngredientType<? super ItemStack>, GenericAdvancedIngredientType<ItemStack, TYPELESS_INGREDIENT>> itemAdvancedIngredientType(String name, Function<Codec<IAdvancedIngredient<? super ItemStack>>, MapCodec<TYPELESS_INGREDIENT>> codecFactory, Function<StreamCodec<RegistryFriendlyByteBuf, IAdvancedIngredient<? super ItemStack>>, StreamCodec<? super RegistryFriendlyByteBuf, TYPELESS_INGREDIENT>> streamCodecFactory) {
        return genericAdvancedIngredientType(PetrolparkRegistries.Keys.ADVANCED_ITEM_INGREDIENT_TYPE, ItemAdvancedIngredient.CODEC, ItemAdvancedIngredient.STREAM_CODEC, name, codecFactory, streamCodecFactory);
    };

    public RegistryEntry<IAdvancedIngredientType<? super FluidStack>, NamedAdvancedIngredientType<FluidStack>> fluidAdvancedIngredientType(String name, MapCodec<? extends FluidAdvancedIngredient> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends FluidAdvancedIngredient> streamCodec) {
        return simple(name, PetrolparkRegistries.Keys.ADVANCED_FLUID_INGREDIENT_TYPE, () -> new NamedAdvancedIngredientType<>(Util.makeDescriptionId("advanced_ingredient", ResourceLocation.fromNamespaceAndPath(getModid(), name)), codec, streamCodec));
    };

    public RegistryEntry<IAdvancedIngredientType<? super FluidStack>, IAdvancedIngredientType<? super FluidStack>> fluidAdvancedIngredientType(String name, IAdvancedIngredientType<? super FluidStack> type) {
        return simple(name, PetrolparkRegistries.Keys.ADVANCED_FLUID_INGREDIENT_TYPE, () -> type);
    };

    public <TYPELESS_INGREDIENT extends ITypelessAdvancedIngredient<FluidStack>> RegistryEntry<IAdvancedIngredientType<? super FluidStack>, GenericAdvancedIngredientType<FluidStack, TYPELESS_INGREDIENT>> fluidAdvancedIngredientType(String name, Function<Codec<IAdvancedIngredient<? super FluidStack>>, MapCodec<TYPELESS_INGREDIENT>> codecFactory, Function<StreamCodec<RegistryFriendlyByteBuf, IAdvancedIngredient<? super FluidStack>>, StreamCodec<? super RegistryFriendlyByteBuf, TYPELESS_INGREDIENT>> streamCodecFactory) {
        return genericAdvancedIngredientType(PetrolparkRegistries.Keys.ADVANCED_FLUID_INGREDIENT_TYPE, FluidAdvancedIngredient.CODEC, FluidAdvancedIngredient.STREAM_CODEC, name, codecFactory, streamCodecFactory);
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

    public <T, SCRATCH_CLASS extends IScratchClass<T, ?>> RegistryEntry<IScratchClass<?, ?>, SCRATCH_CLASS> scratchClass(String name, NonNullSupplier<SCRATCH_CLASS> factory) {
        return simple(name, PetrolparkRegistries.Keys.SCRATCH_CLASS, factory);
    };

    // public <T> RegistryEntry<IScratchType<?>, SimpleScratchType<T>> scratchType(String name, Class<T> clazz) {
    //     return scratchType(name, () -> new SimpleScratchType<>(clazz));
    // };

    // public <SYMBOL extends IScratchSymbol<?, ?>, SYMBOL_TYPE extends IScratchSymbolType<SYMBOL>> RegistryEntry<IScratchSymbolType<?>, SYMBOL_TYPE> scratchSymbolType(String name, NonNullSupplier<SYMBOL_TYPE> typeFactory) {
    //     return simple(name, PetrolparkRegistries.Keys.SCRATCH_SYMBOL_TYPE, typeFactory);
    // };

    // public <EXPRESSION extends IScratchExpression<?, ?, ?>> RegistryEntry<IScratchSymbolType<?>, SimpleScratchExpressionType<EXPRESSION>> simpleScratchExpressionType(String name, NonNullSupplier<EXPRESSION> expressionFactory) {
    //     final EXPRESSION expressionUnit = expressionFactory.asParameters();
    //     return scratchSymbolType(name, () -> new SimpleScratchExpressionType<>(Codec.unit(expressionUnit), StreamCodec.unit(expressionUnit)));
    // };
    
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
