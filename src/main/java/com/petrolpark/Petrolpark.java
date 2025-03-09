package com.petrolpark;

import org.slf4j.Logger;

import java.util.function.Supplier;

import com.mojang.logging.LogUtils;
import com.petrolpark.badge.Badges;
import com.petrolpark.compat.CompatMods;
import com.petrolpark.compat.create.Create;
import com.petrolpark.compat.curios.Curios;
import com.petrolpark.compat.jei.category.ITickableCategory;
import com.petrolpark.data.loot.PetrolparkGlobalLootModifierSerializers;
import com.petrolpark.data.loot.PetrolparkLootConditionTypes;
import com.petrolpark.data.loot.PetrolparkLootEntityNumberProviderTypes;
import com.petrolpark.data.loot.PetrolparkLootItemStackNumberProviderTypes;
import com.petrolpark.data.loot.PetrolparkLootNumberProviderTypes;
import com.petrolpark.data.loot.PetrolparkLootTeamNumberProviders;
import com.petrolpark.data.reward.RewardGeneratorTypes;
import com.petrolpark.data.reward.RewardTypes;
import com.petrolpark.item.decay.DecayingItemHandler;
import com.petrolpark.mobeffects.PetrolparkMobEffects;
import com.petrolpark.network.PetrolparkMessages;
import com.petrolpark.recipe.IPetrolparkRecipeTypes;
import com.petrolpark.recipe.ingredient.modifier.IngredientModifierTypes;
import com.petrolpark.recipe.ingredient.randomizer.IngredientRandomizerTypes;
import com.petrolpark.registrate.PetrolparkRegistrate;
import com.petrolpark.team.TeamTypes;
import com.petrolpark.team.data.TeamDataTypes;
import com.petrolpark.team.scoreboard.ScoreboardTeamManager;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Petrolpark.MOD_ID)
public class Petrolpark {

    public static final String MOD_ID = "petrolpark";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final PetrolparkRegistrate REGISTRATE = new PetrolparkRegistrate(MOD_ID);
    public static final PetrolparkRegistrate DESTROY_REGISTRATE = CompatMods.DESTROY.registrate();

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    };

    public static final ThreadLocal<DecayingItemHandler> DECAYING_ITEM_HANDLER = ThreadLocal.withInitial(() -> DecayingItemHandler.DUMMY);
    public static final ScoreboardTeamManager SCOREBOARD_TEAMS = new ScoreboardTeamManager();

    static {
        PetrolparkItemDisplayContexts.register();
    };

    public Petrolpark(IEventBus modEventBus, ModContainer modContainer) {
        IEventBus neoEventBus = NeoForge.EVENT_BUS;

        REGISTRATE.registerEventListeners(modEventBus);
        DESTROY_REGISTRATE.registerEventListeners(modEventBus);

        // Config
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, PetrolparkConfig.serverSpec);

        // Registration
        PetrolparkRegistries.register();
        Badges.register();
        IPetrolparkRecipeTypes.register(modEventBus);
        PetrolparkItems.register();
        PetrolparkMobEffects.register();
        TeamTypes.register();
        TeamDataTypes.register();
        // Registration - loot
        PetrolparkLootConditionTypes.register();
        PetrolparkLootNumberProviderTypes.register();
        PetrolparkLootItemStackNumberProviderTypes.register();
        PetrolparkLootEntityNumberProviderTypes.register();
        PetrolparkLootTeamNumberProviders.register();
        PetrolparkGlobalLootModifierSerializers.register();
        RewardGeneratorTypes.register();
        RewardTypes.register();
        IngredientModifierTypes.register();
        IngredientRandomizerTypes.register();

        // Client
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PetrolparkClient.clientCtor(modEventBus, forgeEventBus));

        // Register ourselves for server and other game events we are interested in
        neoEventBus.register(this);
    
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::init);

        // Compat
        if (CompatMods.JEI.isLoading()) neoEventBus.register(ITickableCategory.ClientEvents.class);
        CompatMods.CREATE.executeIfInstalled(() -> () -> Create.ctor(modEventBus, neoEventBus));
        CompatMods.CURIOS.executeIfInstalled(() -> () -> Curios.ctor(modEventBus, neoEventBus));
    };

    private void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            PetrolparkMessages.register();
        });
    };

};
