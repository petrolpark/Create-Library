package petrolpark.mc.library.registry;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.badge.Badge;
import petrolpark.mc.library.core.data.loot.modifier.LootPoolEntryModifierType;
import petrolpark.mc.library.core.data.loot.modifier.LootTableModification;
import petrolpark.mc.library.core.data.loot.modifier.LootTableModifierType;
import petrolpark.mc.library.core.data.numberProvider.entity.LootEntityNumberProviderType;
import petrolpark.mc.library.core.data.numberProvider.itemStack.LootItemStackNumberProviderType;
import petrolpark.mc.library.core.data.numberProvider.team.LootTeamNumberProviderType;
import petrolpark.mc.library.core.data.recipe.bogglePattern.BogglePattern;
import petrolpark.mc.library.core.data.recipe.bogglePattern.generator.BogglePatternGeneratorType;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredientType;
import petrolpark.mc.library.core.data.recipe.ingredient.randomizer.IngredientRandomizerType;
import petrolpark.mc.library.core.data.reward.IReward;
import petrolpark.mc.library.core.data.reward.entity.IEntityReward;
import petrolpark.mc.library.core.data.reward.generator.IRewardGenerator;
import petrolpark.mc.library.core.data.reward.generator.RewardGeneratorType;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.core.data.reward.team.ITeamReward;
import petrolpark.mc.library.core.data.stringProvider.StringProviderType;
import petrolpark.mc.library.core.flags.Flag;
import petrolpark.mc.library.core.scratch.classes.IScratchClassType;
import petrolpark.mc.library.core.scratch.environment.IScratchEnvironment;
import petrolpark.mc.library.core.scratch.environment.variable.IScratchScope;
import petrolpark.mc.library.core.scratch.symbol.block.IScratchBlock;
import petrolpark.mc.library.core.scratch.symbol.expression.IScratchExpression;
import petrolpark.mc.library.core.world.entity.animal.mood.AnimalMoodModifier;
import petrolpark.mc.library.core.world.entity.player.team.ITeam;
import petrolpark.mc.library.core.world.item.crafting.pocket.crafter.IPocketCrafter;
import petrolpark.mc.library.core.world.item.decay.product.DecayProductType;
import petrolpark.mc.library.core.world.restaurant.Restaurant;
import petrolpark.mc.library.core.world.restaurant.order.RestaurantOrderGenerator;
import petrolpark.mc.library.experimental.trade.ITradeListingReference;

@EventBusSubscriber
public class PetrolparkRegistries {

    /**
     * <b>Only call during gameplay, not during world loading or before.</b>
     */
    @Deprecated
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
    @Deprecated
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
    @Deprecated
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

    private static final Set<Registry<?>> REGISTRIES = new HashSet<>(15);

    // Core
    public static final Registry<DecayProductType> DECAY_PRODUCT_TYPES = simple(Keys.DECAY_PRODUCT_TYPE);
    public static final Registry<ITeam.ProviderType> TEAM_PROVIDER_TYPES = simple(Keys.TEAM_PROVIDER_TYPE);
    public static final Registry<Badge> BADGES = simple(Keys.BADGE);
    public static final Registry<ITradeListingReference.Type> TRADE_LISTING_REFERENCE_TYPES = simple(Keys.TRADE_LISTING_REFERENCE_TYPE);
    public static final Registry<IPocketCrafter<?>> POCKET_CRAFTERS = simple(Keys.POCKET_CRAFTER);

    // Scratch
    public static final Registry<IScratchClassType> SCRATCH_CLASSES = simple(Keys.SCRATCH_CLASS_TYPE);
    public static final Registry<IScratchEnvironment.Type<?>> SCRATCH_ENVIRONMENT_TYPES = simple(Keys.SCRATCH_ENVIRONMENT_TYPE);
    public static final Registry<IScratchBlock.Type<?>> SCRATCH_BLOCK_TYPES = simple(Keys.SCRATCH_BLOCK_TYPE);
    public static final Registry<IScratchExpression.Type<?>> SCRATCH_EXPRESSION_TYPES = simple(Keys.SCRATCH_EXPRESSION_TYPE);
    public static final Registry<IScratchScope> SCRATCH_SCOPES = simple(Keys.SCRATCH_SCOPE);

    // Loot/Data
    public static final Registry<LootItemStackNumberProviderType> LOOT_ITEM_STACK_NUMBER_PROVIDER_TYPES = simple(Keys.LOOT_ITEM_STACK_NUMBER_PROVIDER_TYPE);
    public static final Registry<LootEntityNumberProviderType> LOOT_ENTITY_NUMBER_PROVIDER_TYPES = simple(Keys.LOOT_ENTITY_NUMBER_PROVIDER_TYPE);
    public static final Registry<LootTeamNumberProviderType> LOOT_TEAM_NUMBER_PROVIDER_TYPES = simple(Keys.LOOT_TEAM_NUMBER_PROVIDER_TYPE);
    public static final Registry<LootTableModifierType> LOOT_TABLE_MODIFIER_TYPES = simple(Keys.LOOT_TABLE_MODIFIER_TYPE);
    public static final Registry<LootPoolEntryModifierType> LOOT_POOL_ENTRY_MODIFIER_TYPES = simple(Keys.LOOT_POOL_ENTRY_MODIFIER_TYPE);
    public static final Registry<StringProviderType> STRING_PROVIDER_TYPES = simple(Keys.STRING_PROVIDER_TYPE);

    // Generated Ingredients
    public static final Registry<IngredientRandomizerType> INGREDIENT_RANDOMIZER_TYPES = simple(Keys.INGREDIENT_RANDOMIZER_TYPE);
    public static final Registry<IAdvancedIngredientType<? super ItemStack>> ADVANCED_ITEM_INGREDIENT_TYPES = simple(Keys.ADVANCED_ITEM_INGREDIENT_TYPE);
    public static final Registry<IAdvancedIngredientType<? super FluidStack>> ADVANCED_FLUID_INGREDIENT_TYPES = simple(Keys.ADVANCED_FLUID_INGREDIENT_TYPE);

    // Rewards
    public static final Registry<RewardGeneratorType> REWARD_GENERATOR_TYPES = simple(Keys.REWARD_GENERATOR_TYPE);
    public static final Registry<IReward.Type> REWARD_TYPES = simple(Keys.REWARD_TYPE);
    public static final Registry<IRewardInfo.Type> REWARD_INFO_TYPES = simple(Keys.REWARD_INFO_TYPE);
    public static final Registry<IEntityReward.Type> ENTITY_REWARD_TYPES = simple(Keys.ENTITY_REWARD_TYPE);
    public static final Registry<ITeamReward.Type> TEAM_REWARD_TYPES = simple(Keys.TEAM_REWARD_TYPE);

    // Misc
    public static final Registry<BogglePatternGeneratorType> BOGGLE_PATTERN_GENERATOR_TYPES = simple(Keys.BOGGLE_PATTERN_GENERATOR_TYPE);

    @ApiStatus.Internal
    public static <T> Registry<T> simple(ResourceKey<Registry<T>> key) {
        return register(key, false);
    };

    @ApiStatus.Internal
    @SuppressWarnings("deprecation")
	public static <T> Registry<T> register(ResourceKey<Registry<T>> key, boolean hasIntrusiveHolders) {
		RegistryBuilder<T> builder = new RegistryBuilder<>(key).sync(true);

		if (hasIntrusiveHolders) builder.withIntrusiveHolders();

		Registry<T> registry = builder.create();
		REGISTRIES.add(registry);

		return registry;
	};

    @SubscribeEvent
    public static final void onNewRegistries(NewRegistryEvent event) {
        REGISTRIES.forEach(event::register);
    };

	@ApiStatus.Internal
	public static void init() {
		// make sure the class is loaded.
		// this method is called at the tail of BuiltInRegistries, injected by BuiltInRegistriesMixin.
	};
    
    public static class Keys {
        // Core
        public static final ResourceKey<Registry<Flag>> FLAG = key("flag");
        public static final ResourceKey<Registry<DecayProductType>> DECAY_PRODUCT_TYPE = key("decay_product_type");
        public static final ResourceKey<Registry<ITeam.ProviderType>> TEAM_PROVIDER_TYPE = key("team_provider_type");
        public static final ResourceKey<Registry<Badge>> BADGE = key("badge");
        public static final ResourceKey<Registry<ITradeListingReference.Type>> TRADE_LISTING_REFERENCE_TYPE = key("trade_listing_reference_type");
        public static final ResourceKey<Registry<IPocketCrafter<?>>> POCKET_CRAFTER = key("pocket_crafter");

        // Scratch
        public static final ResourceKey<Registry<IScratchClassType>> SCRATCH_CLASS_TYPE = key("scratch_class_type");
        public static final ResourceKey<Registry<IScratchEnvironment.Type<?>>> SCRATCH_ENVIRONMENT_TYPE = key("scratch_environment_type");
        public static final ResourceKey<Registry<IScratchBlock.Type<?>>> SCRATCH_BLOCK_TYPE = key("scratch_block_type");
        public static final ResourceKey<Registry<IScratchExpression.Type<?>>> SCRATCH_EXPRESSION_TYPE = key("scratch_expression_type");
        public static final ResourceKey<Registry<IScratchScope>> SCRATCH_SCOPE = key("scratch_scope");

        // Loot/Data
        public static final ResourceKey<Registry<LootItemStackNumberProviderType>> LOOT_ITEM_STACK_NUMBER_PROVIDER_TYPE = key("loot_item_stack_number_provider_type");
        public static final ResourceKey<Registry<LootEntityNumberProviderType>> LOOT_ENTITY_NUMBER_PROVIDER_TYPE = key("loot_entity_number_provider_type");
        public static final ResourceKey<Registry<LootTeamNumberProviderType>> LOOT_TEAM_NUMBER_PROVIDER_TYPE = key("loot_team_number_provider_type");
        public static final ResourceKey<Registry<LootTableModifierType>> LOOT_TABLE_MODIFIER_TYPE = key("loot_table_modifier_type");
        public static final ResourceKey<Registry<LootPoolEntryModifierType>> LOOT_POOL_ENTRY_MODIFIER_TYPE = key("loot_pool_entry_modifier_type");
        public static final ResourceKey<Registry<LootTableModification>> LOOT_TABLE_MODIFICATION = key("loot_table_modification");
        public static final ResourceKey<Registry<StringProviderType>> STRING_PROVIDER_TYPE= key("string_provider_type");

        // Generated ingredients
        public static final ResourceKey<Registry<IngredientRandomizerType>> INGREDIENT_RANDOMIZER_TYPE = key("ingredient_randomizer_type");
        public static final ResourceKey<Registry<IAdvancedIngredientType<? super ItemStack>>> ADVANCED_ITEM_INGREDIENT_TYPE = key("advanced_ingredient_type");
        public static final ResourceKey<Registry<IAdvancedIngredientType<? super FluidStack>>> ADVANCED_FLUID_INGREDIENT_TYPE = key("advanced_fluid_ingredient_type");
        
        // Rewards
        public static final ResourceKey<Registry<RewardGeneratorType>> REWARD_GENERATOR_TYPE = key("reward_generator_type");
        public static final ResourceKey<Registry<IReward.Type>> REWARD_TYPE = key("reward_type");
        public static final ResourceKey<Registry<IRewardInfo.Type>> REWARD_INFO_TYPE = key("reward_info_type");
        public static final ResourceKey<Registry<IEntityReward.Type>> ENTITY_REWARD_TYPE = key("entity_reward_type");
        public static final ResourceKey<Registry<ITeamReward.Type>> TEAM_REWARD_TYPE = key("team_reward_type");
        public static final ResourceKey<Registry<IRewardGenerator>> REWARD_GENERATOR = key("reward_generator");
        public static final ResourceKey<Registry<IReward>> REWARD = key("reward");

        // Restaurants
        public static final ResourceKey<Registry<Restaurant>> RESTAURANT = key("restaurant");
        public static final ResourceKey<Registry<RestaurantOrderGenerator>> RESTAURANT_ORDER_GENERATOR = key("restaurant/offer_generator");

        // Misc
        public static final ResourceKey<Registry<AnimalMoodModifier>> ANIMAL_MOOD_MODIFIER = key("animal_mood_modifier");
        public static final ResourceKey<Registry<BogglePatternGeneratorType>> BOGGLE_PATTERN_GENERATOR_TYPE = key("boggle_pattern_generator_type");
        public static final ResourceKey<Registry<BogglePattern>> BOGGLE_PATTERN = key("boggle_pattern");

        private static <T> ResourceKey<Registry<T>> key(String name) {
		    return ResourceKey.createRegistryKey(Petrolpark.asResource(name));
	    };
    };

    
};
