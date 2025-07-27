package com.petrolpark;

import java.util.function.Supplier;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.petrolpark.compat.Mods;
import com.petrolpark.compat.create.Create;
import com.petrolpark.compat.curios.Curios;
import com.petrolpark.compat.jei.category.ITickableCategory;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.badge.Badges;
import com.petrolpark.core.recipe.IPetrolparkRecipeTypes;
import com.petrolpark.core.recipe.bogglepattern.BogglePattern;
import com.petrolpark.core.recipe.ingredient.randomizer.PetrolparkIngredientRandomizerTypes;
import com.petrolpark.core.team.PetrolparkTeamProviderTypes;
import com.petrolpark.core.team.scoreboard.ScoreboardTeamManager;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
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

    public static final ScoreboardTeamManager SCOREBOARD_TEAMS = new ScoreboardTeamManager();
    public static final BogglePattern.Manager BOGGLE_PATTERNS = new BogglePattern.Manager();

    public Petrolpark(IEventBus modEventBus, ModContainer modContainer) {

        REGISTRATE.registerEventListeners(modEventBus);
        DESTROY_REGISTRATE.registerEventListeners(modEventBus);

        // Config
        PetrolparkConfigs.register(ModLoadingContext.get(), modContainer);

        // Registration
        PetrolparkPackets.register();
        PetrolparkDataComponents.register(modEventBus);
        PetrolparkAttributes.register();
        PetrolparkAttachmentTypes.register(modEventBus);
        Badges.register();
        IPetrolparkRecipeTypes.register(modEventBus);
        PetrolparkBlocks.register();
        PetrolparkItems.register();
        PetrolparkMobEffects.register();
        PetrolparkParticleTypes.register();
        PetrolparkTeamProviderTypes.register();
        PetrolparkTradeListingReferenceTypes.register();
        PetrolparkBogglePatternGeneratorTypes.register();

        // Registration - data/loot
        PetrolparkDataLoadingConditions.register();
        PetrolparkCriteriaTriggers.register();
        PetrolparkDataSubPredicates.register();
        PetrolparkLootConditionTypes.register();
        PetrolparkLootItemFunctions.register();
        PetrolparkNumberProviderTypes.register();
        PetrolparkGlobalLootModifierSerializers.register();
        PetrolparkRewardGeneratorTypes.register();
        PetrolparkRewardTypes.register();

        PetrolparkDecayProductTypes.register();
        PetrolparkIngredientTypes.register();
        PetrolparkIngredientModifierTypes.register();
        PetrolparkIngredientRandomizerTypes.register();
    
        modEventBus.addListener(this::init);
        modEventBus.addListener(EventPriority.LOWEST, PetrolparkDatagen::gatherData);

        // Compat
        if (Mods.JEI.isLoading()) NeoForge.EVENT_BUS.register(ITickableCategory.ClientEvents.class);
        Mods.CREATE.executeIfInstalled(() -> () -> Create.ctor(modEventBus, NeoForge.EVENT_BUS));
        Mods.CURIOS.executeIfInstalled(() -> () -> Curios.ctor(modEventBus, NeoForge.EVENT_BUS));
    };

    private void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

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
