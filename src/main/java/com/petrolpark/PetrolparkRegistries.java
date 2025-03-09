package com.petrolpark;

import static com.petrolpark.Petrolpark.REGISTRATE;

import com.petrolpark.badge.Badge;
import com.petrolpark.compat.create.dough.Dough;
import com.petrolpark.compat.create.dough.DoughCut;
import com.petrolpark.contamination.Contaminant;
import com.petrolpark.data.loot.numberprovider.entity.LootEntityNumberProviderType;
import com.petrolpark.data.loot.numberprovider.itemstack.LootItemStackNumberProviderType;
import com.petrolpark.data.loot.numberprovider.team.LootTeamNumberProviderType;
import com.petrolpark.data.reward.RewardType;
import com.petrolpark.data.reward.generator.RewardGeneratorType;
import com.petrolpark.recipe.ingredient.modifier.IngredientModifierType;
import com.petrolpark.recipe.ingredient.randomizer.IngredientRandomizerType;
import com.petrolpark.shop.Shop;
import com.petrolpark.shop.offer.ShopOfferGenerator;
import com.petrolpark.team.ITeam.ITeamType;
import com.petrolpark.team.data.ITeamDataType;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryManager;
import net.minecraftforge.server.ServerLifecycleHooks;

public class PetrolparkRegistries {

    public static <OBJECT> ForgeRegistry<OBJECT> getRegistry(ResourceKey<Registry<OBJECT>> key) {
        return RegistryManager.ACTIVE.getRegistry(key);
    };

    public static <OBJECT> Registry<OBJECT> getDataRegistry(ResourceKey<Registry<OBJECT>> key) {
        return DistExecutor.unsafeRunForDist(() -> () -> Minecraft.getInstance().getConnection().registryAccess(), () -> () -> ServerLifecycleHooks.getCurrentServer().registryAccess()).registryOrThrow(key);
    };
    
    public static class Keys {
        // Core
        public static final ResourceKey<Registry<Contaminant>> CONTAMINANT = REGISTRATE.makeRegistry("contaminant", RegistryBuilder::new); // Data
        public static final ResourceKey<Registry<ITeamType<?>>> TEAM_TYPE = REGISTRATE.makeRegistry("team_type", RegistryBuilder::new); 
        public static final ResourceKey<Registry<ITeamDataType<?>>> TEAM_DATA_TYPE = REGISTRATE.makeRegistry("team_data_type", RegistryBuilder::new);
        public static final ResourceKey<Registry<Badge>> BADGE = REGISTRATE.makeRegistry("badge", RegistryBuilder::new);

        // Loot
        public static final ResourceKey<Registry<LootItemStackNumberProviderType>> LOOT_ITEM_STACK_NUMBER_PROVIDER_TYPE = REGISTRATE.makeRegistry("loot_item_stack_number_provider_type", RegistryBuilder::new);
        public static final ResourceKey<Registry<LootEntityNumberProviderType>> LOOT_ENTITY_NUMBER_PROVIDER_TYPE = REGISTRATE.makeRegistry("loot_entity_number_provider_type", RegistryBuilder::new);
        public static final ResourceKey<Registry<LootTeamNumberProviderType>> LOOT_TEAM_NUMBER_PROVIDER_TYPE = REGISTRATE.makeRegistry("loot_team_number_provider_type", RegistryBuilder::new);
        // Generated ingredients
        public static final ResourceKey<Registry<IngredientRandomizerType>> INGREDIENT_RANDOMIZER_TYPE = REGISTRATE.makeRegistry("ingredient_randomizer_type", RegistryBuilder::new);
        public static final ResourceKey<Registry<IngredientModifierType>> INGREDIENT_MODIFIER_TYPE = REGISTRATE.makeRegistry("ingredient_modifier_type", RegistryBuilder::new);
        // Rewards
        public static final ResourceKey<Registry<RewardGeneratorType>> REWARD_GENERATOR_TYPE = REGISTRATE.makeRegistry("reward_generator_type", RegistryBuilder::new);
        public static final ResourceKey<Registry<RewardType>> REWARD_TYPE = REGISTRATE.makeRegistry("reward_type", RegistryBuilder::new);

        // Shops
        public static final ResourceKey<Registry<Shop>> SHOP = REGISTRATE.makeRegistry("shop", RegistryBuilder::new); // Data
        public static final ResourceKey<Registry<ShopOfferGenerator>> SHOP_OFFER_GENERATOR = REGISTRATE.makeRegistry("shop_offer_generator", RegistryBuilder::new); // Data

        // Dough
        //TODO move to Create compat directory
        public static final ResourceKey<Registry<Dough>> DOUGH = REGISTRATE.makeRegistry("dough", RegistryBuilder::new); // Data
        public static final ResourceKey<Registry<DoughCut>> DOUGH_CUT = REGISTRATE.makeRegistry("dough_cut", RegistryBuilder::new); // Data

    };

    public static final void register() {};
};
