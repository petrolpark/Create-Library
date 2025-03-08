package com.petrolpark.registrate;

import java.util.function.Supplier;

import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.badge.Badge;
import com.petrolpark.data.loot.numberprovider.entity.EntityNumberProvider;
import com.petrolpark.data.loot.numberprovider.entity.LootEntityNumberProviderType;
import com.petrolpark.data.loot.numberprovider.itemstack.ItemStackNumberProvider;
import com.petrolpark.data.loot.numberprovider.itemstack.LootItemStackNumberProviderType;
import com.petrolpark.data.loot.numberprovider.team.LootTeamNumberProviderType;
import com.petrolpark.data.loot.numberprovider.team.TeamNumberProvider;
import com.petrolpark.data.reward.RewardType;
import com.petrolpark.data.reward.IReward;
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

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraftforge.eventbus.api.IEventBus;
import org.jetbrains.annotations.NotNull;

public class PetrolparkRegistrate extends AbstractRegistrate<PetrolparkRegistrate> {

    public PetrolparkRegistrate(String modid) {
        super(modid);
    };

    @Override
	public @NotNull PetrolparkRegistrate registerEventListeners(@NotNull IEventBus bus) {
		return super.registerEventListeners(bus);
	};

    public BadgeBuilder<Badge, PetrolparkRegistrate> badge(String name) {
        return badge(name, Badge::new);  
    };

    public <T extends Badge> BadgeBuilder<T, PetrolparkRegistrate> badge(String name, NonNullSupplier<T> factory) {
		return (BadgeBuilder<T, PetrolparkRegistrate>) entry(name, c -> BadgeBuilder.create(this, this, name, c, factory));
	};

    public RegistryEntry<LootItemConditionType> lootConditionType(String name, net.minecraft.world.level.storage.loot.Serializer<? extends LootItemCondition> serializer) {
        return simple(name, Registries.LOOT_CONDITION_TYPE, () -> new LootItemConditionType(serializer));
    };

    public RegistryEntry<LootNumberProviderType> lootNumberProviderType(String name, net.minecraft.world.level.storage.loot.Serializer<? extends NumberProvider> serializer) {
        return simple(name, Registries.LOOT_NUMBER_PROVIDER_TYPE, () -> new LootNumberProviderType(serializer));
    };

    public RegistryEntry<LootItemStackNumberProviderType> lootItemStackNumberProviderType(String name, net.minecraft.world.level.storage.loot.Serializer<? extends ItemStackNumberProvider> serializer) {
        return simple(name, PetrolparkRegistries.Keys.LOOT_ITEM_STACK_NUMBER_PROVIDER_TYPE, () -> new LootItemStackNumberProviderType(serializer));
    };
    
    public RegistryEntry<LootItemStackNumberProviderType> lootItemStackNumberProviderType(String name, Supplier<? extends ItemStackNumberProvider> simpleFactory) {
        return simple(name, PetrolparkRegistries.Keys.LOOT_ITEM_STACK_NUMBER_PROVIDER_TYPE, () -> new LootItemStackNumberProviderType(simpleFactory));
    };

    public RegistryEntry<LootEntityNumberProviderType> lootEntityNumberProviderType(String name, net.minecraft.world.level.storage.loot.Serializer<? extends EntityNumberProvider> serializer) {
        return simple(name, PetrolparkRegistries.Keys.LOOT_ENTITY_NUMBER_PROVIDER_TYPE, () -> new LootEntityNumberProviderType(serializer));
    };
    
    public RegistryEntry<LootEntityNumberProviderType> lootEntityNumberProviderType(String name, Supplier<? extends EntityNumberProvider> simpleFactory) {
        return simple(name, PetrolparkRegistries.Keys.LOOT_ENTITY_NUMBER_PROVIDER_TYPE, () -> new LootEntityNumberProviderType(simpleFactory));
    };

    public RegistryEntry<LootTeamNumberProviderType> lootTeamNumberProviderType(String name, net.minecraft.world.level.storage.loot.Serializer<? extends TeamNumberProvider> serializer) {
        return simple(name, PetrolparkRegistries.Keys.LOOT_TEAM_NUMBER_PROVIDER_TYPE, () -> new LootTeamNumberProviderType(serializer));
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

    public <DATA> RegistryEntry<ITeamDataType<DATA>> teamDataType(String name, NonNullSupplier<ITeamDataType<DATA>> supplier) {
        return generic(name, PetrolparkRegistries.Keys.TEAM_DATA_TYPE, supplier).register();
    };
    
};
