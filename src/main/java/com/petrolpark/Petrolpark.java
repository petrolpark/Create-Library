package com.petrolpark;

import java.util.function.Supplier;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.petrolpark.badge.Badges;
import com.petrolpark.compat.Mods;
import com.petrolpark.compat.create.Create;
import com.petrolpark.compat.curios.Curios;
import com.petrolpark.compat.jei.category.ITickableCategory;
import com.petrolpark.data.reward.PetrolparkRewardGeneratorTypes;
import com.petrolpark.data.reward.PetrolparkRewardTypes;
import com.petrolpark.item.decay.DecayingItemHandler;
import com.petrolpark.mobeffects.PetrolparkMobEffects;
import com.petrolpark.network.PetrolparkMessages;
import com.petrolpark.recipe.IPetrolparkRecipeTypes;
import com.petrolpark.recipe.ingredient.modifier.IngredientModifierTypes;
import com.petrolpark.recipe.ingredient.randomizer.IngredientRandomizerTypes;
import com.petrolpark.registrate.PetrolparkRegistrate;
import com.petrolpark.team.PetrolparkTeamProviderTypes;
import com.petrolpark.team.data.TeamDataTypes;
import com.petrolpark.team.scoreboard.ScoreboardTeamManager;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
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
    public static final PetrolparkRegistrate DESTROY_REGISTRATE = Mods.DESTROY.registrate();

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    };

    public static final ThreadLocal<DecayingItemHandler> DECAYING_ITEM_HANDLER = ThreadLocal.withInitial(() -> DecayingItemHandler.DUMMY);
    public static final ScoreboardTeamManager SCOREBOARD_TEAMS = new ScoreboardTeamManager();

    static {
        PetrolparkItemDisplayContexts.register();
    };

    public Petrolpark(IEventBus modEventBus, ModContainer modContainer) {

        REGISTRATE.registerEventListeners(modEventBus);
        DESTROY_REGISTRATE.registerEventListeners(modEventBus);

        // Config
        modContainer.registerConfig(ModConfig.Type.SERVER, PetrolparkConfig.serverSpec);

        // Registration
        PetrolparkRegistries.register();
        PetrolparkDataComponents.register(modEventBus);
        PetrolparkAttachmentTypes.register(modEventBus);
        Badges.register();
        IPetrolparkRecipeTypes.register(modEventBus);
        PetrolparkItems.register();
        PetrolparkMobEffects.register();
        PetrolparkTeamProviderTypes.register();
        TeamDataTypes.register();
        // Registration - loot
        PetrolparkLootConditionTypes.register();
        PetrolparkNumberProviderTypes.register();
        PetrolparkGlobalLootModifierSerializers.register();
        PetrolparkRewardGeneratorTypes.register();
        PetrolparkRewardTypes.register();
        IngredientModifierTypes.register();
        IngredientRandomizerTypes.register();

        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);
    
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::init);

        // Compat
        if (Mods.JEI.isLoading()) NeoForge.EVENT_BUS.register(ITickableCategory.ClientEvents.class);
        Mods.CREATE.executeIfInstalled(() -> () -> Create.ctor(modEventBus, NeoForge.EVENT_BUS));
        Mods.CURIOS.executeIfInstalled(() -> () -> Curios.ctor(modEventBus, NeoForge.EVENT_BUS));
    };

    private void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            PetrolparkMessages.register();
        });
    };

    public static final <T> T runForDist(Supplier<Supplier<T>> clientSupplier, Supplier<Supplier<T>> serverSupplier) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            return clientSupplier.get().get();
        } else {
            return serverSupplier.get().get();
        }
    };

    public static final <T> T unsafeCallClient(Supplier<Supplier<T>> supplier) {
        try {
            if (FMLEnvironment.dist == Dist.CLIENT) supplier.get().get();
        } catch (Exception e) {
            throw new RuntimeException();
        };
        return null;
    };

    public static final void unsafeRunClient(Supplier<Runnable> supplier) {
        try {
            if (FMLEnvironment.dist == Dist.CLIENT) supplier.get().run();
        } catch (Exception e) {
            throw new RuntimeException();
        };
    };

};
