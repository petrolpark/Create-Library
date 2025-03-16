package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import java.util.Optional;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.petrolpark.badge.Badge;
import com.petrolpark.compat.create.dough.Dough;
import com.petrolpark.compat.create.dough.DoughCut;
import com.petrolpark.contamination.Contaminant;
import com.petrolpark.data.loot.numberprovider.entity.LootEntityNumberProviderType;
import com.petrolpark.data.loot.numberprovider.itemstack.LootItemStackNumberProviderType;
import com.petrolpark.data.loot.numberprovider.team.LootTeamNumberProviderType;
import com.petrolpark.data.reward.RewardType;
import com.petrolpark.data.reward.entity.EntityRewardType;
import com.petrolpark.data.reward.generator.RewardGeneratorType;
import com.petrolpark.data.reward.team.TeamRewardType;
import com.petrolpark.recipe.ingredient.modifier.IngredientModifierType;
import com.petrolpark.recipe.ingredient.randomizer.IngredientRandomizerType;
import com.petrolpark.shop.Shop;
import com.petrolpark.shop.offer.ShopOfferGenerator;
import com.petrolpark.team.ITeam;
import com.petrolpark.team.data.ITeamDataType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class PetrolparkRegistries {

    public static <OBJECT> Registry<OBJECT> getRegistry(ResourceKey<Registry<OBJECT>> key) {
        return Petrolpark.runForDist(() -> () -> {
            ClientPacketListener connection = Minecraft.getInstance().getConnection();
            if (connection == null) return null;
            return connection.registryAccess();
        }, () -> () -> ServerLifecycleHooks.getCurrentServer().registryAccess()).registryOrThrow(key);
    };

    public static <OBJECT> Optional<Holder.Reference<OBJECT>> getHolder(ResourceKey<Registry<OBJECT>> registryKey, OBJECT object) {
        return getHolder(getRegistry(registryKey), object);
    };

    public static <OBJECT> Optional<Holder.Reference<OBJECT>> getHolder(Registry<OBJECT> registry, OBJECT object) {
        ResourceKey<OBJECT> key = registry.getResourceKey(object).orElseThrow();
        return registry.getHolder(key);
    };

    // Core
    public static final Registry<Badge> BADGES = simple(Keys.BADGE);
    public static final Registry<ITeam.ProviderType> TEAM_PROVIDER_TYPES = simple(Keys.TEAM_PROVIDER_TYPE);

    // Loot/Data
    public static final Registry<LootItemStackNumberProviderType> LOOT_ITEM_STACK_NUMBER_PROVIDER_TYPES = simple(Keys.LOOT_ITEM_STACK_NUMBER_PROVIDER_TYPE);
    public static final Registry<LootEntityNumberProviderType> LOOT_ENTITY_NUMBER_PROVIDER_TYPES = simple(Keys.LOOT_ENTITY_NUMBER_PROVIDER_TYPE);
    public static final Registry<LootTeamNumberProviderType> LOOT_TEAM_NUMBER_PROVIDER_TYPES = simple(Keys.LOOT_TEAM_NUMBER_PROVIDER_TYPE);

    // Rewards
    public static final Registry<RewardGeneratorType> REWARD_GENERATOR_TYPES = simple(Keys.REWARD_GENERATOR_TYPE);
    public static final Registry<RewardType> REWARD_TYPES = simple(Keys.REWARD_TYPE);
    public static final Registry<EntityRewardType> ENTITY_REWARD_TYPES = simple(Keys.ENTITY_REWARD_TYPE);
    public static final Registry<TeamRewardType> TEAM_REWARD_TYPES = simple(Keys.TEAM_REWARD_TYPE);

    // Shops
    public static final Registry<Shop> SHOP = simple(Keys.SHOP);
    public static final Registry<ShopOfferGenerator> SHOP_OFFER_GENERATOR = simple(Keys.SHOP_OFFER_GENERATOR);

    private static <T> Registry<T> simple(ResourceKey<Registry<T>> key) {
        return register(key, false);
    };

    @SuppressWarnings({"deprecation", "unchecked", "rawtypes"})
	private static <T> Registry<T> register(ResourceKey<Registry<T>> key, boolean hasIntrusiveHolders) {
		RegistryBuilder<T> builder = new RegistryBuilder<>(key).sync(true);

		if (hasIntrusiveHolders) builder.withIntrusiveHolders();

		Registry<T> registry = builder.create();
		((WritableRegistry) BuiltInRegistries.REGISTRY).register(key, registry, RegistrationInfo.BUILT_IN);
		return registry;
	};

	@Internal
	public static void init() {
		// make sure the class is loaded.
		// this method is called at the tail of BuiltInRegistries, injected by BuiltInRegistriesMixin.
	};
    
    public static class Keys {
        // Core
        public static final ResourceKey<Registry<Contaminant>> CONTAMINANT = REGISTRATE.makeDatapackRegistry("contaminant", Contaminant.DIRECT_CODEC, Contaminant.DIRECT_CODEC);
        public static final ResourceKey<Registry<ITeam.ProviderType>> TEAM_PROVIDER_TYPE = REGISTRATE.makeRegistry("team_provider_type", RegistryBuilder::new); 
        public static final ResourceKey<Registry<ITeamDataType<?>>> TEAM_DATA_TYPE = REGISTRATE.makeRegistry("team_data_type", RegistryBuilder::new);
        public static final ResourceKey<Registry<Badge>> BADGE = REGISTRATE.makeRegistry("badge", RegistryBuilder::new);

        // Loot/Data
        public static final ResourceKey<Registry<LootItemStackNumberProviderType>> LOOT_ITEM_STACK_NUMBER_PROVIDER_TYPE = REGISTRATE.makeRegistry("loot_item_stack_number_provider_type", RegistryBuilder::new);
        public static final ResourceKey<Registry<LootEntityNumberProviderType>> LOOT_ENTITY_NUMBER_PROVIDER_TYPE = REGISTRATE.makeRegistry("loot_entity_number_provider_type", RegistryBuilder::new);
        public static final ResourceKey<Registry<LootTeamNumberProviderType>> LOOT_TEAM_NUMBER_PROVIDER_TYPE = REGISTRATE.makeRegistry("loot_team_number_provider_type", RegistryBuilder::new);
        // Generated ingredients
        public static final ResourceKey<Registry<IngredientRandomizerType>> INGREDIENT_RANDOMIZER_TYPE = REGISTRATE.makeRegistry("ingredient_randomizer_type", RegistryBuilder::new);
        public static final ResourceKey<Registry<IngredientModifierType>> INGREDIENT_MODIFIER_TYPE = REGISTRATE.makeRegistry("ingredient_modifier_type", RegistryBuilder::new);
        // Rewards
        public static final ResourceKey<Registry<RewardGeneratorType>> REWARD_GENERATOR_TYPE = REGISTRATE.makeRegistry("reward_generator_type", RegistryBuilder::new);
        public static final ResourceKey<Registry<RewardType>> REWARD_TYPE = REGISTRATE.makeRegistry("reward_type", RegistryBuilder::new);
        public static final ResourceKey<Registry<EntityRewardType>> ENTITY_REWARD_TYPE = REGISTRATE.makeRegistry("entity_reward_type", RegistryBuilder::new);
        public static final ResourceKey<Registry<TeamRewardType>> TEAM_REWARD_TYPE = REGISTRATE.makeRegistry("team_reward_type", RegistryBuilder::new);

        // Shops
        public static final ResourceKey<Registry<Shop>> SHOP = REGISTRATE.makeDatapackRegistry("shop", Shop.DIRECT_CODEC, Shop.DIRECT_CODEC);
        public static final ResourceKey<Registry<ShopOfferGenerator>> SHOP_OFFER_GENERATOR = REGISTRATE.makeDatapackRegistry("shop_offer_generator", ShopOfferGenerator.DIRECT_CODEC, ShopOfferGenerator.DIRECT_CODEC);

        // Dough
        //TODO move to Create compat directory
        public static final ResourceKey<Registry<Dough>> DOUGH = REGISTRATE.makeRegistry("dough", RegistryBuilder::new); // Data
        public static final ResourceKey<Registry<DoughCut>> DOUGH_CUT = REGISTRATE.makeRegistry("dough_cut", RegistryBuilder::new); // Data

    };

    public static final void register() {};
};
