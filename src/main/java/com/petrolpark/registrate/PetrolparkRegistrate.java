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
import com.petrolpark.data.reward.generator.IRewardGenerator;
import com.petrolpark.data.reward.generator.RewardGeneratorType;
import com.petrolpark.recipe.ingredient.modifier.IngredientModifier;
import com.petrolpark.recipe.ingredient.modifier.IngredientModifierType;
import com.petrolpark.recipe.ingredient.randomizer.IngredientRandomizer;
import com.petrolpark.recipe.ingredient.randomizer.IngredientRandomizerType;
import com.petrolpark.team.data.ITeamDataType;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import net.minecraft.core.registries.Registries;
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

    @Override
	public @Nonnull PetrolparkRegistrate registerEventListeners(@Nonnull IEventBus bus) {
		return super.registerEventListeners(bus);
	};

    public BadgeBuilder<Badge, PetrolparkRegistrate> badge(String name) {
        return badge(name, Badge::new);  
    };

    public <T extends Badge> BadgeBuilder<T, PetrolparkRegistrate> badge(String name, NonNullSupplier<T> factory) {
		return (BadgeBuilder<T, PetrolparkRegistrate>) entry(name, c -> BadgeBuilder.create(this, this, name, c, factory));
	};

    public RegistryEntry<LootItemConditionType, LootItemConditionType> lootConditionType(String name, MapCodec<? extends LootItemCondition> codec) {
        return simple(name, Registries.LOOT_CONDITION_TYPE, () -> new LootItemConditionType(codec));
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
    
    public RegistryEntry<LootItemStackNumberProviderType> lootItemStackNumberProviderType(String name, Supplier<? extends ItemStackNumberProvider> simpleFactory) {
        return simple(name, PetrolparkRegistries.Keys.LOOT_ITEM_STACK_NUMBER_PROVIDER_TYPE, () -> new LootItemStackNumberProviderType(simpleFactory));
    };
    
    public RegistryEntry<LootEntityNumberProviderType, LootEntityNumberProviderType> lootEntityNumberProviderType(String name, MapCodec<? extends EntityNumberProvider> codec) {
        return simple(name, PetrolparkRegistries.Keys.LOOT_ENTITY_NUMBER_PROVIDER_TYPE, () -> new LootEntityNumberProviderType(codec));
    };
    
    public RegistryEntry<LootTeamNumberProviderType> lootTeamNumberProviderType(String name, Supplier<? extends TeamNumberProvider> simpleFactory) {
        return simple(name, PetrolparkRegistries.Keys.LOOT_TEAM_NUMBER_PROVIDER_TYPE, () -> new LootTeamNumberProviderType(simpleFactory));
    };

    public RegistryEntry<IngredientRandomizerType> ingredientRandomizerType(String name, net.minecraft.world.level.storage.loot.Serializer<? extends IngredientRandomizer> serializer) {
        return simple(name, PetrolparkRegistries.Keys.INGREDIENT_RANDOMIZER_TYPE, () -> new IngredientRandomizerType(serializer));
    };

    public RegistryEntry<IngredientModifierType> ingredientModifierType(String name, net.minecraft.world.level.storage.loot.Serializer<? extends IngredientModifier> serializer) {
        return simple(name, PetrolparkRegistries.Keys.INGREDIENT_MODIFIER_TYPE, () -> new IngredientModifierType(serializer));
    };

    public RegistryEntry<RewardGeneratorType> rewardGeneratorType(String name, net.minecraft.world.level.storage.loot.Serializer<? extends IRewardGenerator> serializer) {
        return simple(name, PetrolparkRegistries.Keys.REWARD_GENERATOR_TYPE, () -> new RewardGeneratorType(serializer));
    };

    public RegistryEntry<RewardType> rewardType(String name, net.minecraft.world.level.storage.loot.Serializer<? extends IReward> serializer) {
        return simple(name, PetrolparkRegistries.Keys.REWARD_TYPE, () -> new RewardType(serializer));
    };

    public <DATA> RegistryEntry<ITeamDataType<?>, ITeamDataType<DATA>> teamDataType(String name, NonNullSupplier<ITeamDataType<DATA>> supplier) {
        return generic(name, PetrolparkRegistries.Keys.TEAM_DATA_TYPE, supplier).register();
    };
    
};
