package com.petrolpark;

import java.util.Optional;
import java.util.function.Function;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.petrolpark.compat.create.core.dough.Dough;
import com.petrolpark.compat.create.core.dough.DoughCut;
import com.petrolpark.core.badge.Badge;
import com.petrolpark.core.contamination.Contaminant;
import com.petrolpark.core.data.loot.numberprovider.entity.LootEntityNumberProviderType;
import com.petrolpark.core.data.loot.numberprovider.itemstack.LootItemStackNumberProviderType;
import com.petrolpark.core.data.loot.numberprovider.team.LootTeamNumberProviderType;
import com.petrolpark.core.data.reward.RewardType;
import com.petrolpark.core.data.reward.entity.EntityRewardType;
import com.petrolpark.core.data.reward.generator.RewardGeneratorType;
import com.petrolpark.core.data.reward.team.TeamRewardType;
import com.petrolpark.core.item.decay.product.DecayProductType;
import com.petrolpark.core.recipe.ingredient.modifier.IIngredientModifierType;
import com.petrolpark.core.recipe.ingredient.randomizer.IngredientRandomizerType;
import com.petrolpark.core.shop.Shop;
import com.petrolpark.core.shop.offer.ShopOfferGenerator;
import com.petrolpark.core.team.ITeam;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class PetrolparkRegistries {

    /**
     * <b>Only call during gameplay, not during world loading or before.</b>
     */
    public static final RegistryAccess registryAccess() {
        return Petrolpark.runForDist(() -> () -> {
            ClientPacketListener connection = Minecraft.getInstance().getConnection();
            if (connection == null) return null;
            return connection.registryAccess();
        }, () -> () -> ServerLifecycleHooks.getCurrentServer().registryAccess());
    };

    /**
     * Fetch the (Datapack) Registry with the given key.
     * <b>Only call during gameplay, not during world loading or before.</b>
     * @param <OBJECT> Type of objects in the Registry
     * @param key
     */
    public static <OBJECT> Optional<Registry<OBJECT>> getRegistry(ResourceKey<Registry<OBJECT>> key) {
        return registryAccess().registry(key);
    };

    /**
     * Fetch the Holder of the given object in the Registry with the given key.
     * <b>Only call during gameplay, not during world loading or before.</b>
     * @param <OBJECT> Type of objects in the Registry
     * @see PetrolparkRegistries#getHolder(Registry, Object)
     * @see PetrolparkRegistries#getHolder(net.minecraft.core.HolderLookup.Provider, ResourceKey, Object)
     */
    public static <OBJECT> Optional<Holder.Reference<OBJECT>> getHolder(ResourceKey<Registry<OBJECT>> registryKey, OBJECT object) {
        return getRegistry(registryKey).flatMap(reg -> getHolder(reg, object));
    };

    public static <OBJECT> Optional<Holder.Reference<OBJECT>> getHolder(Registry<OBJECT> registry, OBJECT object) {
        ResourceKey<OBJECT> key = registry.getResourceKey(object).orElseThrow();
        return registry.getHolder(key);
    };

    public static <OBJECT> Optional<Holder.Reference<OBJECT>> getHolder(HolderLookup.Provider provider, ResourceKey<Registry<OBJECT>> registryKey, OBJECT object) {
        if (provider instanceof RegistryAccess registryAccess) return getHolder(registryAccess.registryOrThrow(registryKey), object);
        return provider.lookupOrThrow(registryKey).listElements().filter(h -> h.value() == object).findAny();
    };

    public static <OBJECT> Function<OBJECT, Optional<Holder.Reference<OBJECT>>> holderGetOrThrow(HolderLookup.Provider provider, ResourceKey<Registry<OBJECT>> registryKey) {
        if (provider instanceof RegistryAccess registryAccess) return object -> getHolder(registryAccess.registryOrThrow(registryKey), object);
        return object -> provider.lookupOrThrow(registryKey).listElements().filter(h -> h.value() == object).findAny();
    };

    // Core
    public static final Registry<DecayProductType> DECAY_PRODUCT_TYPES = simple(Keys.DECAY_PRODUCT_TYPE);
    public static final Registry<Badge> BADGES = simple(Keys.BADGE);
    public static final Registry<ITeam.ProviderType> TEAM_PROVIDER_TYPES = simple(Keys.TEAM_PROVIDER_TYPE);

    // Loot/Data
    public static final Registry<LootItemStackNumberProviderType> LOOT_ITEM_STACK_NUMBER_PROVIDER_TYPES = simple(Keys.LOOT_ITEM_STACK_NUMBER_PROVIDER_TYPE);
    public static final Registry<LootEntityNumberProviderType> LOOT_ENTITY_NUMBER_PROVIDER_TYPES = simple(Keys.LOOT_ENTITY_NUMBER_PROVIDER_TYPE);
    public static final Registry<LootTeamNumberProviderType> LOOT_TEAM_NUMBER_PROVIDER_TYPES = simple(Keys.LOOT_TEAM_NUMBER_PROVIDER_TYPE);

    // Generated Ingredients
    public static final Registry<IngredientRandomizerType> INGREDIENT_RANDOMIZER_TYPES = simple(Keys.INGREDIENT_RANDOMIZER_TYPE);
    public static final Registry<IIngredientModifierType<? super ItemStack>> INGREDIENT_MODIFIER_TYPES = simple(Keys.INGREDIENT_MODIFIER_TYPE);
    public static final Registry<IIngredientModifierType<? super FluidStack>> FLUID_INGREDIENT_MODIFIER_TYPES = simple(Keys.FLUID_INGREDIENT_MODIFIER_TYPE);

    // Rewards
    public static final Registry<RewardGeneratorType> REWARD_GENERATOR_TYPES = simple(Keys.REWARD_GENERATOR_TYPE);
    public static final Registry<RewardType> REWARD_TYPES = simple(Keys.REWARD_TYPE);
    public static final Registry<EntityRewardType> ENTITY_REWARD_TYPES = simple(Keys.ENTITY_REWARD_TYPE);
    public static final Registry<TeamRewardType> TEAM_REWARD_TYPES = simple(Keys.TEAM_REWARD_TYPE);

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
        public static final ResourceKey<Registry<Contaminant>> CONTAMINANT = key("contaminant");
        public static final ResourceKey<Registry<DecayProductType>> DECAY_PRODUCT_TYPE = key("decay_product_type");
        public static final ResourceKey<Registry<ITeam.ProviderType>> TEAM_PROVIDER_TYPE = key("team_provider_type");
        public static final ResourceKey<Registry<Badge>> BADGE = key("badge");

        // Loot/Data
        public static final ResourceKey<Registry<LootItemStackNumberProviderType>> LOOT_ITEM_STACK_NUMBER_PROVIDER_TYPE = key("loot_item_stack_number_provider_type");
        public static final ResourceKey<Registry<LootEntityNumberProviderType>> LOOT_ENTITY_NUMBER_PROVIDER_TYPE = key("loot_entity_number_provider_type");
        public static final ResourceKey<Registry<LootTeamNumberProviderType>> LOOT_TEAM_NUMBER_PROVIDER_TYPE = key("loot_team_number_provider_type");
        
        // Generated ingredients
        public static final ResourceKey<Registry<IngredientRandomizerType>> INGREDIENT_RANDOMIZER_TYPE = key("ingredient_randomizer_type");
        public static final ResourceKey<Registry<IIngredientModifierType<? super ItemStack>>> INGREDIENT_MODIFIER_TYPE = key("ingredient_modifier_type");
        public static final ResourceKey<Registry<IIngredientModifierType<? super FluidStack>>> FLUID_INGREDIENT_MODIFIER_TYPE = key("fluid_ingredient_modifier_type");
        
        // Rewards
        public static final ResourceKey<Registry<RewardGeneratorType>> REWARD_GENERATOR_TYPE = key("reward_generator_type");
        public static final ResourceKey<Registry<RewardType>> REWARD_TYPE = key("reward_type");
        public static final ResourceKey<Registry<EntityRewardType>> ENTITY_REWARD_TYPE = key("entity_reward_type");
        public static final ResourceKey<Registry<TeamRewardType>> TEAM_REWARD_TYPE = key("team_reward_type");

        // Shops
        public static final ResourceKey<Registry<Shop>> SHOP = key("shop");
        public static final ResourceKey<Registry<ShopOfferGenerator>> SHOP_OFFER_GENERATOR = key("shop_offer_generator");

        // Dough
        //TODO move to Create compat directory
        public static final ResourceKey<Registry<Dough>> DOUGH = key("dough"); // Data
        public static final ResourceKey<Registry<DoughCut>> DOUGH_CUT = key("dough_cut"); // Data

        private static <T> ResourceKey<Registry<T>> key(String name) {
		    return ResourceKey.createRegistryKey(Petrolpark.asResource(name));
	    };
    };

    
};
