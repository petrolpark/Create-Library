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
import com.petrolpark.core.codec.ContextualMapCodec;
import com.petrolpark.core.codec.ContextualStreamCodec;
import com.petrolpark.core.data.loot.modifier.ILootPoolEntryModifier;
import com.petrolpark.core.data.loot.modifier.ILootTableModifier;
import com.petrolpark.core.data.loot.modifier.LootPoolEntryModifierType;
import com.petrolpark.core.data.loot.modifier.LootTableModifierType;
import com.petrolpark.core.data.loot.numberprovider.FunctionNumberProvider;
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
import com.petrolpark.core.recipe.SharedRecipeType;
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
import com.petrolpark.core.registrate.WoodSetEntry;
import com.petrolpark.core.registrate.builder.MobEffectBuilder;
import com.petrolpark.core.registrate.builder.PetrolparkBlockBuilder;
import com.petrolpark.core.registrate.builder.PetrolparkBlockEntityBuilder;
import com.petrolpark.core.registrate.builder.PetrolparkItemBuilder;
import com.petrolpark.core.registrate.builder.SharedBlockBuilder;
import com.petrolpark.core.registrate.builder.SharedBlockEntityBuilder;
import com.petrolpark.core.registrate.builder.SharedItemBuilder;
import com.petrolpark.core.registrate.builder.SharedMobEffectBuilder;
import com.petrolpark.core.scratch.classes.BooleanScratchClass;
import com.petrolpark.core.scratch.classes.IScratchClass;
import com.petrolpark.core.scratch.classes.IScratchClassType;
import com.petrolpark.core.scratch.classes.ScratchClassType;
import com.petrolpark.core.scratch.classes.SimpleScratchClass;
import com.petrolpark.core.scratch.environment.IScratchEnvironment;
import com.petrolpark.core.scratch.symbol.block.FlexibleEnvironmentScratchBlockType;
import com.petrolpark.core.scratch.symbol.block.GenericInstantBlock;
import com.petrolpark.core.scratch.symbol.block.IScratchBlock;
import com.petrolpark.core.scratch.symbol.expression.GenericExpression;
import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;
import com.petrolpark.core.scratch.symbol.expression.ScratchExpressionType;
import com.petrolpark.core.scratch.symbol.expression.SimpleExpressionType;
import com.petrolpark.core.team.ITeam;
import com.petrolpark.core.trade.ITradeListingReference;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BlockEntityBuilder.BlockEntityFactory;
import com.tterrag.registrate.builders.Builder;
import com.tterrag.registrate.builders.BuilderCallback;
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
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.vehicle.Boat;
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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.WoodType;
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

public abstract class AbstractPetrolparkRegistrate<R extends AbstractPetrolparkRegistrate<R>> extends AbstractRegistrate<R> {

    protected AbstractPetrolparkRegistrate(String modid) {
        super(modid);
    };

    // Builders

    @Override
    public <T extends Block, P> PetrolparkBlockBuilder<T, P> block(@Nonnull P parent, @Nonnull String name, @Nonnull NonNullFunction<BlockBehaviour.Properties, T> factory) {
        return (PetrolparkBlockBuilder<T, P>)entry(name, callback -> PetrolparkBlockBuilder.create(this, parent, name, callback, factory));
    };

    @Override
    public <T extends Item> PetrolparkItemBuilder<T, R> item(@Nonnull String name, @Nonnull NonNullFunction<Properties, T> factory) {
        return item(self(), name, factory);
    };

    @Override
    public <T extends Item, P> PetrolparkItemBuilder<T, P> item(@Nonnull P parent, @Nonnull String name, @Nonnull NonNullFunction<Properties, T> factory) {
        return (PetrolparkItemBuilder<T, P>)entry(name, callback -> PetrolparkItemBuilder.create(this, parent, name, callback, factory));
    };

    @Override
    public <T extends BlockEntity> PetrolparkBlockEntityBuilder<T, R> blockEntity(@Nonnull String name, @Nonnull BlockEntityFactory<T> factory) {
        return blockEntity(self(), name, factory);
    };

    @Override
    public <T extends BlockEntity, P> PetrolparkBlockEntityBuilder<T, P> blockEntity(@Nonnull P parent, @Nonnull String name, @Nonnull BlockEntityFactory<T> factory) {
        return (PetrolparkBlockEntityBuilder<T, P>)entry(name, callback -> PetrolparkBlockEntityBuilder.create(this, parent, name, callback, factory));
    };

    public <T extends MobEffect> MobEffectBuilder<T, R> mobEffect(@Nonnull String name, @Nonnull MobEffectBuilder.Factory<T> factory) {
        return mobEffect(self(), name, factory);
    };

    public <T extends MobEffect, P> MobEffectBuilder<T, P> mobEffect(@Nonnull P parent, @Nonnull String name, @Nonnull MobEffectBuilder.Factory<T> factory) {
        return entry(name, callback -> new MobEffectBuilder<>(this, parent, name, callback, factory));
    };

    public BadgeRegistrateBuilder<Badge, R> badge(String name) {
        return badge(name, Badge::new);  
    };

    public <T extends Badge> BadgeRegistrateBuilder<T, R> badge(String name, NonNullSupplier<T> factory) {
		return (BadgeRegistrateBuilder<T, R>) entry(name, c -> BadgeRegistrateBuilder.create(self(), self(), name, c, factory));
	};

    public WoodSetEntry.Builder<R> woodSet(WoodType woodType, TreeGrower treeGrower, Boat.Type boatType) {
        return new WoodSetEntry.Builder<R>(self(), woodType, treeGrower, boatType);
    };

    // Simple registered objects

    public <B extends BlockEntity, T extends BlockEntityType<B>> RegistryEntry<BlockEntityType<?>, T> blockEntityType(String name, NonNullSupplier<T> factory) {
        return simple(name, Registries.BLOCK_ENTITY_TYPE, factory);
    };

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

    public RegistryEntry<LootNumberProviderType, LootNumberProviderType> functionLootNumberProviderType(String name, FunctionNumberProvider.Factory<?> constructor) {
        final LootNumberProviderType type = new LootNumberProviderType(FunctionNumberProvider.codec(constructor));
        FunctionNumberProvider.register(type, constructor);
        return simple(name, Registries.LOOT_NUMBER_PROVIDER_TYPE, () -> type);
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

    public RegistryEntry<SoundEvent, SoundEvent> soundEvent(String name, float range) {
        return simple(name, Registries.SOUND_EVENT, () -> SoundEvent.createFixedRangeEvent(ResourceLocation.fromNamespaceAndPath(getModid(), name), range));
    };

    public RegistryEntry<SoundEvent, SoundEvent> soundEvent(String name) {
        return simple(name, Registries.SOUND_EVENT, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(getModid(), name)));
    };

    public <I extends RecipeInput, T extends Recipe<? extends I>> RegistryEntry<RecipeType<?>, RecipeType<T>> recipeType(String name) {
        return simple(name, Registries.RECIPE_TYPE, () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(getModid(), name)));
    };

    public <I extends RecipeInput, T extends Recipe<? extends I>, S extends RecipeSerializer<? extends T>> RegistryEntry<RecipeSerializer<?>, S> recipeSerializer(String name, NonNullSupplier<S> factory) {
        return simple(name, Registries.RECIPE_SERIALIZER, factory);
    };

    public <T extends CraftingRecipe> RegistryEntry<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<T>> recipeSerializer(String name, SimpleCraftingRecipeSerializer.Factory<T> constructor) {
        return recipeSerializer(name, () -> new SimpleCraftingRecipeSerializer<>(constructor));
    };

    public <I extends RecipeInput, T extends Recipe<? extends I>> RegistryEntry<RecipeSerializer<?>, RecipeSerializer<T>> recipeSerializer(String name, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return recipeSerializer(name, () -> new RecipeSerializer<T>() {
            
            @Override
            public MapCodec<T> codec() {
                return codec;
            };

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
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

    // Simple Registered Objects - Scratch

    public <SCRATCH_CLASS extends IScratchClass<?, ?>> RegistryEntry<IScratchClassType, ScratchClassType<SCRATCH_CLASS>> scratchClassType(String name, MapCodec<SCRATCH_CLASS> codec, StreamCodec<? super RegistryFriendlyByteBuf, SCRATCH_CLASS> streamCodec) {
        return simple(name, PetrolparkRegistries.Keys.SCRATCH_CLASS_TYPE, () -> new ScratchClassType<>(codec, streamCodec));
    };

    public <TYPE, SCRATCH_CLASS extends SimpleScratchClass<TYPE, ?>> RegistryEntry<IScratchClassType, SCRATCH_CLASS> simpleScratchClass(String name, NonNullSupplier<SCRATCH_CLASS> factory) {
        return simple(name, PetrolparkRegistries.Keys.SCRATCH_CLASS_TYPE, factory);
    };

    public <ENVIRONMENT extends IScratchEnvironment, TYPE extends IScratchEnvironment.Type<ENVIRONMENT>> RegistryEntry<IScratchEnvironment.Type<?>, TYPE> scratchEnvironmentType(String name, NonNullSupplier<TYPE> factory) {
        return simple(name, PetrolparkRegistries.Keys.SCRATCH_ENVIRONMENT_TYPE, factory);
    };

    public <BLOCK extends IScratchBlock<?, ?, ?>, TYPE extends IScratchBlock.Type<BLOCK>> RegistryEntry<IScratchBlock.Type<?>, TYPE> scratchBlockType(String name, NonNullSupplier<TYPE> factory) {
        return simple(name, PetrolparkRegistries.Keys.SCRATCH_BLOCK_TYPE, factory);
    };

    public <BLOCK extends GenericInstantBlock<?, ?, ?, ?, ?>> RegistryEntry<IScratchBlock.Type<?>, GenericInstantBlock.Type<BLOCK>> genericScratchBlockType(String name, Function<IScratchClass<?, ?>, BLOCK> blockFactory) {
        return scratchBlockType(name, () -> new GenericInstantBlock.Type<>(blockFactory));
    };

    public <BASE_ENVIRONMENT extends IScratchEnvironment, BLOCK extends IScratchBlock<?, ?, ?>> RegistryEntry<IScratchBlock.Type<?>, FlexibleEnvironmentScratchBlockType<BASE_ENVIRONMENT, BLOCK>> flexibleEnvironmentScratchBlockType(String name, Class<BASE_ENVIRONMENT> environmentClass, Function<IScratchEnvironment.Type<?>, BLOCK> factory) {
        return scratchBlockType(name, () -> new FlexibleEnvironmentScratchBlockType<>(environmentClass, factory));
    };

    public <EXPRESSION extends IScratchExpression<?, ?, ?, ?>, TYPE extends IScratchExpression.Type<EXPRESSION>> RegistryEntry<IScratchExpression.Type<?>, TYPE> scratchExpressionType(String name, NonNullSupplier<TYPE> expressionTypeFactory) {
        return simple(name, PetrolparkRegistries.Keys.SCRATCH_EXPRESSION_TYPE, expressionTypeFactory);
    };

    public <EXPRESSION extends IScratchExpression<?, ?, ?, ?>> RegistryEntry<IScratchExpression.Type<?>, ScratchExpressionType<EXPRESSION>> environmentDepedendentScratchExpressionType(String name, ContextualMapCodec<IScratchEnvironment.Type<?>, EXPRESSION> codec, ContextualStreamCodec<? super RegistryFriendlyByteBuf, IScratchEnvironment.Type<?>, EXPRESSION> streamCodec) {
        return scratchExpressionType(name, () -> new ScratchExpressionType<>(codec, streamCodec));
    };

    public <EXPRESSION extends IScratchExpression<?, ?, ?, ?>> RegistryEntry<IScratchExpression.Type<?>, ScratchExpressionType<EXPRESSION>> scratchExpressionType(String name, MapCodec<EXPRESSION> codec, StreamCodec<? super RegistryFriendlyByteBuf, EXPRESSION> streamCodec) {
        return scratchExpressionType(name, () -> new ScratchExpressionType<>(codec, streamCodec));
    };

    public <EXPRESSION extends GenericExpression<?, ?, ?, ?, ?, ?>> RegistryEntry<IScratchExpression.Type<?>, GenericExpression.Type<EXPRESSION>> genericScratchExpressionType(String name, Function<IScratchClass<?, ?>, EXPRESSION> expressionFactory) {
        return scratchExpressionType(name, () -> new GenericExpression.Type<>(expressionFactory));
    };

    public <TYPE extends SimpleExpressionType<?, ?, ?, ?, TYPE>> RegistryEntry<IScratchExpression.Type<?>, TYPE> booleanScratchExpression(String name, Function<BooleanScratchClass, TYPE> expressionFactory) {
        return scratchExpressionType(name, () -> expressionFactory.apply(PetrolparkScratchClasses.BOOLEAN.get()));
    };
    
    // Shared Features

    public class SharedFeatureBuilderCallback implements BuilderCallback {

        protected final SharedFeatureFlag featureFlag;

        public SharedFeatureBuilderCallback(SharedFeatureFlag featureFlag) {
            this.featureFlag = featureFlag;
        };

        @Override
        public <R2, T extends R2> RegistryEntry<R2, T> accept(@Nonnull String name, @Nonnull ResourceKey<? extends Registry<R2>> type, @Nonnull Builder<R2, T, ?, ?> builder, @Nonnull NonNullSupplier<? extends T> factory, @Nonnull NonNullFunction<DeferredHolder<R2, T>, ? extends RegistryEntry<R2, T>> entryFactory) {
            if (featureFlag.enabled()) return AbstractPetrolparkRegistrate.super.accept(name, type, builder, factory, entryFactory);
            return entryFactory.apply(DeferredHolder.create(type, ResourceLocation.fromNamespaceAndPath(getModid(), name))); // Create entry but do not register it
        };

    };

    public <R2, T extends R2, P, BUILDER extends AbstractBuilder<R2, T, P, BUILDER>> BUILDER sharedEntry(SharedFeatureFlag featureFlag, @Nonnull String name, @Nonnull NonNullFunction<BuilderCallback, BUILDER> factory) {
        return factory.apply(new SharedFeatureBuilderCallback(featureFlag)).asOptional();
    };

    public <T extends BlockEntity> SharedBlockEntityBuilder<T, R> sharedBlockEntity(SharedFeatureFlag featureFlag, String name, BlockEntityFactory<T> factory) {
        return (SharedBlockEntityBuilder<T, R>)sharedEntry(featureFlag, name, callback -> SharedBlockEntityBuilder.create(self(), self(), featureFlag, name, callback, factory));
    };

    public <T extends Block, P> SharedBlockBuilder<T, R> sharedBlock(SharedFeatureFlag featureFlag, @Nonnull String name, @Nonnull NonNullFunction<BlockBehaviour.Properties, T> factory) {
        return (SharedBlockBuilder<T, R>)sharedEntry(featureFlag, name, callback -> SharedBlockBuilder.create(self(), self(), featureFlag, name, callback, factory));
    };

    public <T extends Block> SharedBlockBuilder<T, R> sharedBlock(@Nonnull SharedFeatureFlag featureFlag, String name, NonNullBiFunction<BlockBehaviour.Properties, SharedFeatureFlag, T> factory) {
        return sharedBlock(featureFlag, name, properties -> factory.apply(properties, featureFlag));
    };

    public <T extends Item, P> SharedItemBuilder<T, P> sharedItem(P parent, @Nonnull SharedFeatureFlag featureFlag, String name, NonNullFunction<Item.Properties, T> factory) {
        return (SharedItemBuilder<T, P>)sharedEntry(featureFlag, name, callback -> new SharedItemBuilder<>(this, parent, featureFlag, name, callback, factory));
    };

    public <T extends Item> SharedItemBuilder<T, R> sharedItem(@Nonnull SharedFeatureFlag featureFlag, String name, NonNullFunction<Item.Properties, T> factory) {
        return sharedItem(self(), featureFlag, name, factory);
    };
    
    public <T extends Item> SharedItemBuilder<T, R> sharedItem(@Nonnull SharedFeatureFlag featureFlag, String name, NonNullBiFunction<Item.Properties, SharedFeatureFlag, T> factory) {
        return sharedItem(featureFlag, name, properties -> factory.apply(properties, featureFlag));
    };

    public <T extends MobEffect> SharedMobEffectBuilder<T, R> sharedMobEffect(@Nonnull SharedFeatureFlag featureFlag, String name, MobEffectBuilder.Factory<T> factory) {
        return (SharedMobEffectBuilder<T, R>)sharedEntry(featureFlag, name, callback -> SharedMobEffectBuilder.create(self(), self(), featureFlag, name, callback, factory));
    };

    public <I extends RecipeInput, T extends Recipe<? extends I>> RegistryEntry<RecipeType<?>, SharedRecipeType<T>> sharedRecipeType(SharedFeatureFlag featureFlag, String name) {
        return simple(name, Registries.RECIPE_TYPE, () -> new SharedRecipeType<>(ResourceLocation.fromNamespaceAndPath(getModid(), name), featureFlag));
    };
    
};
