package com.petrolpark.registrate;

import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.badge.Badge;
import com.petrolpark.data.loot.numberprovider.entity.EntityNumberProvider;
import com.petrolpark.data.loot.numberprovider.entity.LootEntityNumberProviderType;
import com.petrolpark.data.loot.numberprovider.itemstack.ItemStackNumberProvider;
import com.petrolpark.data.loot.numberprovider.itemstack.LootItemStackNumberProviderType;
import com.petrolpark.data.loot.numberprovider.team.LootTeamNumberProviderType;
import com.petrolpark.data.loot.numberprovider.team.TeamNumberProvider;
import com.petrolpark.data.reward.IReward;
import com.petrolpark.data.reward.RewardType;
import com.petrolpark.data.reward.entity.EntityRewardType;
import com.petrolpark.data.reward.entity.IEntityReward;
import com.petrolpark.data.reward.generator.IRewardGenerator;
import com.petrolpark.data.reward.generator.RewardGeneratorType;
import com.petrolpark.data.reward.team.ITeamReward;
import com.petrolpark.data.reward.team.TeamRewardType;
import com.petrolpark.recipe.ingredient.modifier.IngredientModifier;
import com.petrolpark.recipe.ingredient.modifier.IngredientModifierType;
import com.petrolpark.recipe.ingredient.randomizer.IngredientRandomizer;
import com.petrolpark.recipe.ingredient.randomizer.IngredientRandomizerType;
import com.petrolpark.team.ITeam;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class PetrolparkRegistrate extends AbstractRegistrate<PetrolparkRegistrate> {

    public PetrolparkRegistrate(String modid) {
        super(modid);
    };

    public BadgeBuilder<Badge, PetrolparkRegistrate> badge(String name) {
        return badge(name, Badge::new);  
    };

    public <T extends Badge> BadgeBuilder<T, PetrolparkRegistrate> badge(String name, NonNullSupplier<T> factory) {
		return (BadgeBuilder<T, PetrolparkRegistrate>) entry(name, c -> BadgeBuilder.create(this, this, name, c, factory));
	};

    public RegistryEntry<ITeam.ProviderType, ITeam.ProviderType> teamProviderType(String name, MapCodec<? extends ITeam.Provider> codec, StreamCodec<? super RegistryFriendlyByteBuf, ? extends ITeam.Provider> streamCodec) {
        return simple(name, PetrolparkRegistries.Keys.TEAM_PROVIDER_TYPE, () -> new ITeam.ProviderType(codec, streamCodec));
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
    
};
