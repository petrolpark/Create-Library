package com.petrolpark;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import com.petrolpark.compat.SharedFeatures;
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
import com.petrolpark.core.recipe.ingredient.modifier.IngredientModifier;
import com.petrolpark.core.recipe.ingredient.modifier.IngredientModifierType;
import com.petrolpark.core.recipe.ingredient.randomizer.IngredientRandomizer;
import com.petrolpark.core.recipe.ingredient.randomizer.IngredientRandomizerType;
import com.petrolpark.core.team.ITeam;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.builders.BlockEntityBuilder.BlockEntityFactory;
import com.tterrag.registrate.builders.Builder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullFunction;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
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

    public RegistryEntry<ITeam.ProviderType, ITeam.ProviderType> teamProviderType(String name, MapCodec<? extends ITeam.Provider> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends ITeam.Provider> streamCodec) {
        return simple(name, PetrolparkRegistries.Keys.TEAM_PROVIDER_TYPE, () -> new ITeam.ProviderType(codec, streamCodec));
    };

    public <C extends CriterionTrigger<?>> RegistryEntry<CriterionTrigger<?>, C> criterionTrigger(String name, NonNullSupplier<C> triggerFactory) {
        return simple(name, Registries.TRIGGER_TYPE, triggerFactory);
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

    public RegistryEntry<IngredientRandomizerType, IngredientRandomizerType> ingredientRandomizerType(String name, MapCodec<? extends IngredientRandomizer> serializer) {
        return simple(name, PetrolparkRegistries.Keys.INGREDIENT_RANDOMIZER_TYPE, () -> new IngredientRandomizerType(serializer));
    };

    public RegistryEntry<IngredientModifierType, IngredientModifierType> ingredientModifierType(String name, MapCodec<? extends IngredientModifier> serializer) {
        return simple(name, PetrolparkRegistries.Keys.INGREDIENT_MODIFIER_TYPE, () -> new IngredientModifierType(serializer));
    };

    public RegistryEntry<RewardGeneratorType, RewardGeneratorType> rewardGeneratorType(String name, MapCodec<? extends IRewardGenerator> codec) {
        return simple(name, PetrolparkRegistries.Keys.REWARD_GENERATOR_TYPE, () -> new RewardGeneratorType(codec));
    };

    public RegistryEntry<RewardType, RewardType> rewardType(String name, MapCodec<? extends IReward> codec) {
        return simple(name, PetrolparkRegistries.Keys.REWARD_TYPE, () -> new RewardType(codec));
    };

    public RegistryEntry<EntityRewardType, EntityRewardType> entityRewardType(String name, MapCodec<? extends IEntityReward> codec) {
        return simple(name, PetrolparkRegistries.Keys.ENTITY_REWARD_TYPE, () -> new EntityRewardType(codec));
    };

    public RegistryEntry<TeamRewardType, TeamRewardType> teamRewardType(String name, MapCodec<? extends ITeamReward> codec) {
        return simple(name, PetrolparkRegistries.Keys.TEAM_REWARD_TYPE, () -> new TeamRewardType(codec));
    };
    
    // Shared features

    protected class SharedFeatureBuilderCallback implements BuilderCallback {

        protected final SharedFeatures feature;

        public SharedFeatureBuilderCallback(SharedFeatures feature) {
            this.feature = feature;
        };

        @Override
        public <R, T extends R> RegistryEntry<R, T> accept(@Nonnull String name, @Nonnull ResourceKey<? extends Registry<R>> type, @Nonnull Builder<R, T, ?, ?> builder, @Nonnull NonNullSupplier<? extends T> factory, @Nonnull NonNullFunction<DeferredHolder<R, T>, ? extends RegistryEntry<R, T>> entryFactory) {
            if (feature.enabled()) return PetrolparkRegistrate.super.accept(name, type, builder, factory, entryFactory);
            return entryFactory.apply(DeferredHolder.create(type, ResourceLocation.fromNamespaceAndPath(getModid(), name))); // Create entry but do not register it
        };

    };

    protected <R, T extends R, P, S2 extends Builder<R, T, P, S2>> S2 sharedEntry(SharedFeatures feature, @Nonnull String name, @Nonnull NonNullFunction<BuilderCallback, S2> factory) {
        return factory.apply(new SharedFeatureBuilderCallback(feature));
    };

    public <T extends BlockEntity> BlockEntityBuilder<T, PetrolparkRegistrate> sharedBlockEntity(SharedFeatures feature, String name, BlockEntityFactory<T> factory) {
        return sharedEntry(feature, name, callback -> BlockEntityBuilder.create(this, this, name, callback, factory));
    };

    public <T extends Block, P> BlockBuilder<T, PetrolparkRegistrate> sharedBlock(SharedFeatures feature, @Nonnull String name, @Nonnull NonNullFunction<BlockBehaviour.Properties, T> factory) {
        return sharedEntry(feature, name, callback -> BlockBuilder.create(this, this, name, callback, factory));
    };
    
};
