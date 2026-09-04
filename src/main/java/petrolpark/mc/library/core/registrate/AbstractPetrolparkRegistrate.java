package petrolpark.mc.library.core.registrate;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
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
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.MobCategory;
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
import petrolpark.mc.library.core.badge.Badge;
import petrolpark.mc.library.core.badge.BadgeRegistrateBuilder;
import petrolpark.mc.library.core.data.loot.modifier.ILootPoolEntryModifier;
import petrolpark.mc.library.core.data.loot.modifier.ILootTableModifier;
import petrolpark.mc.library.core.data.loot.modifier.LootPoolEntryModifierType;
import petrolpark.mc.library.core.data.loot.modifier.LootTableModifierType;
import petrolpark.mc.library.core.data.numberProvider.FunctionNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.entity.EntityNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.entity.LootEntityNumberProviderType;
import petrolpark.mc.library.core.data.numberProvider.itemStack.ItemStackNumberProvider;
import petrolpark.mc.library.core.data.numberProvider.itemStack.LootItemStackNumberProviderType;
import petrolpark.mc.library.core.data.numberProvider.team.LootTeamNumberProviderType;
import petrolpark.mc.library.core.data.numberProvider.team.TeamNumberProvider;
import petrolpark.mc.library.core.data.recipe.bogglePattern.generator.BogglePatternGeneratorType;
import petrolpark.mc.library.core.data.recipe.bogglePattern.generator.IBogglePatternGenerator;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.FluidAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.GenericAdvancedIngredientType;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredientType;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.INamedAdvancedIngredientType;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.ITypelessAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.ItemAdvancedIngredient;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.NamedAdvancedIngredientType;
import petrolpark.mc.library.core.data.recipe.ingredient.randomizer.IngredientRandomizer;
import petrolpark.mc.library.core.data.recipe.ingredient.randomizer.IngredientRandomizerType;
import petrolpark.mc.library.core.data.reward.IReward;
import petrolpark.mc.library.core.data.reward.ISimpleReward;
import petrolpark.mc.library.core.data.reward.RewardAndInfoType;
import petrolpark.mc.library.core.data.reward.StandardRewardType;
import petrolpark.mc.library.core.data.reward.entity.EntityRewardAndInfoType;
import petrolpark.mc.library.core.data.reward.entity.EntityRewardType;
import petrolpark.mc.library.core.data.reward.entity.IEntityReward;
import petrolpark.mc.library.core.data.reward.entity.ISimpleEntityReward;
import petrolpark.mc.library.core.data.reward.generator.IRewardGenerator;
import petrolpark.mc.library.core.data.reward.generator.RewardGeneratorType;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.core.data.reward.info.RewardInfoType;
import petrolpark.mc.library.core.data.reward.info.WrappedRewardInfo;
import petrolpark.mc.library.core.data.reward.team.ITeamReward;
import petrolpark.mc.library.core.data.reward.team.TeamRewardAndInfoType;
import petrolpark.mc.library.core.data.reward.team.TeamRewardType;
import petrolpark.mc.library.core.data.stringProvider.StringProvider;
import petrolpark.mc.library.core.data.stringProvider.StringProviderType;
import petrolpark.mc.library.core.registrate.builder.MobEffectBuilder;
import petrolpark.mc.library.core.registrate.builder.PetrolparkBlockBuilder;
import petrolpark.mc.library.core.registrate.builder.PetrolparkBlockEntityBuilder;
import petrolpark.mc.library.core.registrate.builder.PetrolparkEntityBuilder;
import petrolpark.mc.library.core.registrate.builder.PetrolparkItemBuilder;
import petrolpark.mc.library.core.registrate.builder.shared.SharedBlockBuilder;
import petrolpark.mc.library.core.registrate.builder.shared.SharedBlockEntityBuilder;
import petrolpark.mc.library.core.registrate.builder.shared.SharedItemBuilder;
import petrolpark.mc.library.core.registrate.builder.shared.SharedMobEffectBuilder;
import petrolpark.mc.library.core.scratch.classes.BooleanScratchClass;
import petrolpark.mc.library.core.scratch.classes.IScratchClass;
import petrolpark.mc.library.core.scratch.classes.IScratchClassType;
import petrolpark.mc.library.core.scratch.classes.ScratchClassType;
import petrolpark.mc.library.core.scratch.classes.SimpleScratchClass;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.symbol.block.FlexibleEnvironmentScratchBlockType;
import petrolpark.mc.library.core.scratch.symbol.block.GenericInstantBlock;
import petrolpark.mc.library.core.scratch.symbol.block.IScratchBlock;
import petrolpark.mc.library.core.scratch.symbol.expression.GenericExpression;
import petrolpark.mc.library.core.scratch.symbol.expression.IScratchExpression;
import petrolpark.mc.library.core.scratch.symbol.expression.ScratchExpressionType;
import petrolpark.mc.library.core.scratch.symbol.expression.SimpleExpressionType;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.core.world.entity.player.team.predicate.ITeamPredicate;
import petrolpark.mc.library.core.world.item.decay.product.DecayProductType;
import petrolpark.mc.library.core.world.item.decay.product.IDecayProduct;
import petrolpark.mc.library.core.world.restaurant.customer.ICustomer;
import petrolpark.mc.library.experimental.trade.ITradeListingReference;
import petrolpark.mc.library.registry.PetrolparkRegistries;
import petrolpark.mc.library.registry.scratch.PetrolparkScratchClasses;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.shared.world.item.crafting.SharedRecipeType;
import petrolpark.mc.library.util.codec.ContextualMapCodec;
import petrolpark.mc.library.util.codec.ContextualStreamCodec;

@ParametersAreNonnullByDefault
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

    public <T extends BlockEntity> PetrolparkBlockEntityBuilder<T, R> uninstantiableBlockEntity(String name) {
        return blockEntity(self(), name, (type, pos, state) -> {
            throw new IllegalStateException("Cannot instantiate " + name);
        });
    };

    // No default lang
    @Override
    public <T extends Entity, P> PetrolparkEntityBuilder<T, P> entity(P parent, String name, EntityFactory<T> factory, MobCategory classification) {
        return (PetrolparkEntityBuilder<T, P>)entry(name, callback -> new PetrolparkEntityBuilder<>(this, parent, name, callback, factory, classification));
    };

    public <T extends MobEffect> MobEffectBuilder<T, R> mobEffect(String name, MobEffectBuilder.Factory<T> factory) {
        return mobEffect(self(), name, factory);
    };

    public <T extends MobEffect, P> MobEffectBuilder<T, P> mobEffect(P parent, String name, MobEffectBuilder.Factory<T> factory) {
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

    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>, I extends ArgumentTypeInfo<A, T>> RegistryEntry<ArgumentTypeInfo<?, ?>, I> commandArgumentType(String name, Class<A> infoClass, I argumentTypeInfo) {
        ArgumentTypeInfos.registerByClass(infoClass, argumentTypeInfo);
        return simple(name, Registries.COMMAND_ARGUMENT_TYPE, () -> argumentTypeInfo);
    };

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
        final LootContextParamSet.Builder builder = new LootContextParamSet.Builder();
        builderConsumer.accept(builder);
        final LootContextParamSet paramSet = builder.build();
        final ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(getModid(), name);
        LootContextParamSets.REGISTRY.put(rl, paramSet);
        return paramSet;
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

    public RegistryEntry<StringProviderType, StringProviderType> stringProviderType(String name, MapCodec<? extends StringProvider> codec) {
        return simple(name, PetrolparkRegistries.Keys.STRING_PROVIDER_TYPE, () -> new StringProviderType(codec));
    };

    public RegistryEntry<ITeamPredicate.Type, ITeamPredicate.Type> teamPredicateType(String name, MapCodec<? extends ITeamPredicate> codec) {
        return simple(name, PetrolparkRegistries.Keys.TEAM_PREDICATE_TYPE, () -> new ITeamPredicate.Type(codec));
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

    protected <STACK, TYPELESS_INGREDIENT extends ITypelessAdvancedIngredient<STACK>> RegistryEntry<IAdvancedIngredientType<STACK>, GenericAdvancedIngredientType<STACK, TYPELESS_INGREDIENT>> genericAdvancedIngredientType(
        ResourceKey<Registry<IAdvancedIngredientType<STACK>>> registryKey,
        Codec<IAdvancedIngredient<STACK>> typeCodec,
        StreamCodec<RegistryFriendlyByteBuf, IAdvancedIngredient<STACK>> typeStreamCodec,
        String name,
        Function<Codec<IAdvancedIngredient<STACK>>, MapCodec<TYPELESS_INGREDIENT>> codecFactory,
        Function<StreamCodec<RegistryFriendlyByteBuf, IAdvancedIngredient<STACK>>, StreamCodec<? super RegistryFriendlyByteBuf, TYPELESS_INGREDIENT>> streamCodecFactory
    ) {
        return simple(name, registryKey, () -> new GenericAdvancedIngredientType<>(codecFactory.apply(typeCodec), streamCodecFactory.apply(typeStreamCodec)));
    };

    public RegistryEntry<IAdvancedIngredientType<ItemStack>, NamedAdvancedIngredientType<ItemStack>> itemAdvancedIngredientType(String name, MapCodec<? extends ItemAdvancedIngredient> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends ItemAdvancedIngredient> streamCodec) {
        return itemAdvancedIngredientType(name, () -> new NamedAdvancedIngredientType<>(Util.makeDescriptionId("advancedIngredient", ResourceLocation.fromNamespaceAndPath(getModid(), name)), codec, streamCodec));
    };

    public RegistryEntry<IAdvancedIngredientType<ItemStack>, INamedAdvancedIngredientType<ItemStack>> namedItemAdvancedIngredientType(String name, NonNullFunction<String, INamedAdvancedIngredientType<ItemStack>> typeFactory) {
        return itemAdvancedIngredientType(name, () -> typeFactory.apply(Util.makeDescriptionId("advancedIngredient", ResourceLocation.fromNamespaceAndPath(getModid(), name))));
    };

    public <T extends IAdvancedIngredientType<ItemStack>> RegistryEntry<IAdvancedIngredientType<ItemStack>, T> itemAdvancedIngredientType(String name, NonNullSupplier<T> typeFactory) {
        return simple(name, PetrolparkRegistries.Keys.ADVANCED_ITEM_INGREDIENT_TYPE, typeFactory);
    };

    public <TYPELESS_INGREDIENT extends ITypelessAdvancedIngredient<ItemStack>> RegistryEntry<IAdvancedIngredientType<ItemStack>, GenericAdvancedIngredientType<ItemStack, TYPELESS_INGREDIENT>> itemAdvancedIngredientType(String name, Function<Codec<IAdvancedIngredient<ItemStack>>, MapCodec<TYPELESS_INGREDIENT>> codecFactory, Function<StreamCodec<RegistryFriendlyByteBuf, IAdvancedIngredient<ItemStack>>, StreamCodec<? super RegistryFriendlyByteBuf, TYPELESS_INGREDIENT>> streamCodecFactory) {
        return genericAdvancedIngredientType(PetrolparkRegistries.Keys.ADVANCED_ITEM_INGREDIENT_TYPE, ItemAdvancedIngredient.CODEC, ItemAdvancedIngredient.STREAM_CODEC, name, codecFactory, streamCodecFactory);
    };

    public RegistryEntry<IAdvancedIngredientType<FluidStack>, NamedAdvancedIngredientType<FluidStack>> fluidAdvancedIngredientType(String name, MapCodec<? extends FluidAdvancedIngredient> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends FluidAdvancedIngredient> streamCodec) {
        return fluidAdvancedIngredientType(name, () -> new NamedAdvancedIngredientType<>(Util.makeDescriptionId("advancedIngredient", ResourceLocation.fromNamespaceAndPath(getModid(), name)), codec, streamCodec));
    };

    public <T extends IAdvancedIngredientType<FluidStack>> RegistryEntry<IAdvancedIngredientType<FluidStack>, T> fluidAdvancedIngredientType(String name, NonNullSupplier<T> typeFactory) {
        return simple(name, PetrolparkRegistries.Keys.ADVANCED_FLUID_INGREDIENT_TYPE, typeFactory);
    };

    public <TYPELESS_INGREDIENT extends ITypelessAdvancedIngredient<FluidStack>> RegistryEntry<IAdvancedIngredientType<FluidStack>, GenericAdvancedIngredientType<FluidStack, TYPELESS_INGREDIENT>> fluidAdvancedIngredientType(String name, Function<Codec<IAdvancedIngredient<FluidStack>>, MapCodec<TYPELESS_INGREDIENT>> codecFactory, Function<StreamCodec<RegistryFriendlyByteBuf, IAdvancedIngredient<FluidStack>>, StreamCodec<? super RegistryFriendlyByteBuf, TYPELESS_INGREDIENT>> streamCodecFactory) {
        return genericAdvancedIngredientType(PetrolparkRegistries.Keys.ADVANCED_FLUID_INGREDIENT_TYPE, FluidAdvancedIngredient.CODEC, FluidAdvancedIngredient.STREAM_CODEC, name, codecFactory, streamCodecFactory);
    };

    public RegistryEntry<RewardGeneratorType, RewardGeneratorType> rewardGeneratorType(String name, MapCodec<? extends IRewardGenerator> codec) {
        return simple(name, PetrolparkRegistries.Keys.REWARD_GENERATOR_TYPE, () -> new RewardGeneratorType(codec));
    };

    public <T extends IReward.Type> RegistryEntry<IReward.Type, T> rewardType(String name, NonNullSupplier<T> factory) {
        return simple(name, PetrolparkRegistries.Keys.REWARD_TYPE, factory);
    };

    public RegistryEntry<IReward.Type, StandardRewardType> rewardType(String name, MapCodec<? extends IReward> codec) {
        return rewardType(name, () -> new StandardRewardType(codec));
    };

    public RegistryEntry<IRewardInfo.Type, RewardInfoType> rewardInfoType(String name, MapCodec<? extends IRewardInfo> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends IRewardInfo> streamCodec) {
        return rewardInfoType(name, () -> new RewardInfoType(name, codec, streamCodec));
    };

    public <T extends IRewardInfo.Type> RegistryEntry<IRewardInfo.Type, T> rewardInfoType(String name, NonNullSupplier<T> factory) {
        return simple(name, PetrolparkRegistries.Keys.REWARD_INFO_TYPE, factory);
    };

    public RegistryEntry<IReward.Type, RewardAndInfoType> rewardAndInfoTypes(String name, MapCodec<? extends IReward> rewardCodec, MapCodec<? extends IRewardInfo> infoCodec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends IRewardInfo> infoStreamCodec) {
        final RewardAndInfoType type = new RewardAndInfoType(ResourceLocation.fromNamespaceAndPath(getModid(), name).toLanguageKey("reward"), rewardCodec, infoCodec, infoStreamCodec);
        rewardInfoType(name, () -> type);
        return rewardType(name, () -> type);
    };

    public RegistryEntry<IReward.Type, RewardAndInfoType> simpleRewardType(String name, MapCodec<? extends ISimpleReward> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends ISimpleReward> streamCodec) {
        return rewardAndInfoTypes(name, codec, codec, streamCodec);
    };

    public <I extends WrappedRewardInfo> RegistryEntry<IReward.Type, RewardAndInfoType> rewardAndWrappedInfoTypes(String name, MapCodec<? extends IReward> rewardCodec, NonNullFunction<IRewardInfo, I> infoFactory) {
        return rewardAndInfoTypes(name, rewardCodec, WrappedRewardInfo.codec(infoFactory), WrappedRewardInfo.streamCodec(infoFactory));
    };

    public RegistryEntry<IEntityReward.Type, EntityRewardType> entityRewardType(String name, MapCodec<? extends IEntityReward> codec) {
        return entityRewardType(name, () -> new EntityRewardType(codec));
    };

    public <T extends IEntityReward.Type> RegistryEntry<IEntityReward.Type, T> entityRewardType(String name, NonNullSupplier<T> factory) {
        return simple(name, PetrolparkRegistries.Keys.ENTITY_REWARD_TYPE, factory);
    };

    public RegistryEntry<IEntityReward.Type, EntityRewardAndInfoType> entityRewardAndInfoTypes(String name, MapCodec<? extends IEntityReward> rewardCodec, MapCodec<? extends IRewardInfo> infoCodec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends IRewardInfo> infoStreamCodec) {
        final EntityRewardAndInfoType type = new EntityRewardAndInfoType(ResourceLocation.fromNamespaceAndPath(getModid(), name).toLanguageKey("reward"), rewardCodec, infoCodec, infoStreamCodec);
        rewardInfoType(name, () -> type);
        return entityRewardType(name, () -> type);
    };

    public <I extends WrappedRewardInfo> RegistryEntry<IEntityReward.Type, EntityRewardAndInfoType> entityRewardAndWrappedInfoTypes(String name, MapCodec<? extends IEntityReward> rewardCodec, NonNullFunction<IRewardInfo, I> infoFactory) {
        return entityRewardAndInfoTypes(name, rewardCodec, WrappedRewardInfo.codec(infoFactory), WrappedRewardInfo.streamCodec(infoFactory));
    };

    public RegistryEntry<IEntityReward.Type, EntityRewardAndInfoType> simpleEntityRewardType(String name, MapCodec<? extends ISimpleEntityReward> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends ISimpleEntityReward> streamCodec) {
        return entityRewardAndInfoTypes(name, codec, codec, streamCodec);
    };

    public RegistryEntry<ITeamReward.Type, TeamRewardType> teamRewardType(String name, MapCodec<? extends ITeamReward> codec) {
        return teamRewardType(name, () -> new TeamRewardType(codec));
    };

    public <T extends ITeamReward.Type> RegistryEntry<ITeamReward.Type, T> teamRewardType(String name, NonNullSupplier<T> factory) {
        return simple(name, PetrolparkRegistries.Keys.TEAM_REWARD_TYPE, factory);
    };

    public RegistryEntry<ITeamReward.Type, TeamRewardAndInfoType> teamRewardAndInfoTypes(String name, MapCodec<? extends ITeamReward> rewardCodec, MapCodec<? extends IRewardInfo> infoCodec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends IRewardInfo> infoStreamCodec) {
        final TeamRewardAndInfoType type = new TeamRewardAndInfoType(ResourceLocation.fromNamespaceAndPath(getModid(), name).toLanguageKey("reward"), rewardCodec, infoCodec, infoStreamCodec);
        rewardInfoType(name, () -> type);
        return teamRewardType(name, () -> type);
    };

    public RegistryEntry<ICustomer.ProviderType, ICustomer.ProviderType> customerProviderType(String name, MapCodec<? extends ICustomer.Provider> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends ICustomer.Provider> streamCodec) {
        return simple(name, PetrolparkRegistries.Keys.CUSTOMER_PROVIDER_TYPE, () -> new ICustomer.ProviderType(codec, streamCodec));
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

    public <R2, T extends R2, P, BUILDER extends AbstractBuilder<R2, T, P, BUILDER>> BUILDER sharedEntry(SharedFeatureFlag featureFlag, NonNullFunction<BuilderCallback, BUILDER> factory) {
        return factory.apply(new SharedFeatureBuilderCallback(featureFlag)).asOptional();
    };

    public <T extends BlockEntity> SharedBlockEntityBuilder<T, R> sharedBlockEntity(SharedFeatureFlag featureFlag, String name, BlockEntityFactory<T> factory) {
        return (SharedBlockEntityBuilder<T, R>)sharedEntry(featureFlag, callback -> SharedBlockEntityBuilder.create(self(), self(), featureFlag, name, callback, factory));
    };

    public <T extends Block, P> SharedBlockBuilder<T, R> sharedBlock(SharedFeatureFlag featureFlag, String name, NonNullFunction<BlockBehaviour.Properties, T> factory) {
        return (SharedBlockBuilder<T, R>)sharedEntry(featureFlag, callback -> SharedBlockBuilder.create(self(), self(), featureFlag, getSharedPath(name), callback, factory));
    };

    public <T extends Block> SharedBlockBuilder<T, R> sharedBlock(SharedFeatureFlag featureFlag, String name, NonNullBiFunction<BlockBehaviour.Properties, SharedFeatureFlag, T> factory) {
        return sharedBlock(featureFlag, name, properties -> factory.apply(properties, featureFlag));
    };

    public <T extends Item, P> SharedItemBuilder<T, P> sharedItem(P parent, SharedFeatureFlag featureFlag, String name, NonNullFunction<Item.Properties, T> factory) {
        return (SharedItemBuilder<T, P>)sharedEntry(featureFlag, callback -> new SharedItemBuilder<>(this, parent, featureFlag, getSharedPath(name), callback, factory));
    };

    public <T extends Item> SharedItemBuilder<T, R> sharedItem(SharedFeatureFlag featureFlag, String name, NonNullFunction<Item.Properties, T> factory) {
        return sharedItem(self(), featureFlag, name, factory);
    };
    
    public <T extends Item> SharedItemBuilder<T, R> sharedItem(SharedFeatureFlag featureFlag, String name, NonNullBiFunction<Item.Properties, SharedFeatureFlag, T> factory) {
        return sharedItem(featureFlag, name, properties -> factory.apply(properties, featureFlag));
    };

    public <T extends MobEffect> SharedMobEffectBuilder<T, R> sharedMobEffect(SharedFeatureFlag featureFlag, String name, MobEffectBuilder.Factory<T> factory) {
        return (SharedMobEffectBuilder<T, R>)sharedEntry(featureFlag, callback -> SharedMobEffectBuilder.create(self(), self(), featureFlag, getSharedPath(name), callback, factory));
    };

    public <I extends RecipeInput, T extends Recipe<? extends I>> RegistryEntry<RecipeType<?>, SharedRecipeType<T>> sharedRecipeType(SharedFeatureFlag featureFlag, String name) {
        return simple(name, Registries.RECIPE_TYPE, () -> new SharedRecipeType<>(ResourceLocation.fromNamespaceAndPath(getModid(), name), featureFlag));
    };

    public String getSharedPath(String path) {
        return path.startsWith("shared/") ? path : "shared/" + path;
    };
    
};
