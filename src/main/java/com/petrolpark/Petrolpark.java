package com.petrolpark;

import java.util.function.Supplier;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.petrolpark.compat.Mods;
import com.petrolpark.compat.create.PetrolparkCreate;
import com.petrolpark.compat.curios.PetrolparkCurios;
import com.petrolpark.compat.jei.category.ITickableCategory;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.badge.Badges;
import com.petrolpark.core.data.recipe.IPetrolparkRecipeTypes;
import com.petrolpark.core.data.recipe.bogglePattern.BogglePattern;
import com.petrolpark.core.data.recipe.compat.CompatRecipeManager;
import com.petrolpark.core.registrate.AbstractPetrolparkRegistrate;
import com.petrolpark.core.world.entity.player.team.scoreboard.ScoreboardTeamManager;
import com.petrolpark.registry.PetrolparkAdvancedIngredientTypes;
import com.petrolpark.registry.PetrolparkAttachmentTypes;
import com.petrolpark.registry.PetrolparkAttributes;
import com.petrolpark.registry.PetrolparkBogglePatternGeneratorTypes;
import com.petrolpark.registry.PetrolparkCriteriaTriggers;
import com.petrolpark.registry.PetrolparkDataComponentTypes;
import com.petrolpark.registry.PetrolparkDataLoadingConditions;
import com.petrolpark.registry.PetrolparkDataSubPredicates;
import com.petrolpark.registry.PetrolparkDecayProductTypes;
import com.petrolpark.registry.PetrolparkFeatureTypes;
import com.petrolpark.registry.PetrolparkGlobalLootModifierSerializers;
import com.petrolpark.registry.PetrolparkIngredientRandomizerTypes;
import com.petrolpark.registry.PetrolparkItems;
import com.petrolpark.registry.PetrolparkLootConditionTypes;
import com.petrolpark.registry.PetrolparkLootItemFunctions;
import com.petrolpark.registry.PetrolparkLootModifierTypes;
import com.petrolpark.registry.PetrolparkNeoForgeIngredientTypes;
import com.petrolpark.registry.PetrolparkNumberProviderTypes;
import com.petrolpark.registry.PetrolparkPackets;
import com.petrolpark.registry.PetrolparkRecipeSerializers;
import com.petrolpark.registry.PetrolparkRecipeTypes;
import com.petrolpark.registry.PetrolparkRegistrateProviderTypes;
import com.petrolpark.registry.PetrolparkRewardGeneratorTypes;
import com.petrolpark.registry.PetrolparkRewardTypes;
import com.petrolpark.registry.PetrolparkTeamProviderTypes;
import com.petrolpark.registry.PetrolparkTradeListingReferenceTypes;
import com.petrolpark.shared.GetPetrolparkSharedFeatures;
import com.petrolpark.shared.Shared;
import com.petrolpark.shared.SharedFeatureFlag;

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
import net.neoforged.neoforge.data.loading.DatagenModLoader;

@Mod(Petrolpark.MOD_ID)
public class Petrolpark {

    public static final String MOD_ID = "petrolpark";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final PetrolparkRegistrate REGISTRATE = new PetrolparkRegistrate();
    public static final AbstractPetrolparkRegistrate<?> DESTROY_REGISTRATE = Mods.DESTROY.registrate();

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    };

    public static final String translationKey(String suffix) {
        return MOD_ID + "." + suffix;
    };

    public static final CompatRecipeManager COMPAT_RECIPES = new CompatRecipeManager();
    public static final ScoreboardTeamManager SCOREBOARD_TEAMS = new ScoreboardTeamManager();
    public static final BogglePattern.Manager BOGGLE_PATTERNS = new BogglePattern.Manager();

    public Petrolpark(IEventBus modEventBus, ModContainer modContainer) {
        
        if (DatagenModLoader.isRunningDataGen()) PetrolparkDatagen.prepareDatagen();

        Shared.init(modEventBus, modContainer);

        REGISTRATE.registerEventListeners(modEventBus);
        DESTROY_REGISTRATE.registerEventListeners(modEventBus);

        // Registration
        Badges.register();
        PetrolparkAdvancedIngredientTypes.register();
        PetrolparkAttachmentTypes.register(modEventBus);
        PetrolparkAttributes.register();
        PetrolparkBogglePatternGeneratorTypes.register();
        PetrolparkCriteriaTriggers.register();
        PetrolparkDataComponentTypes.register(modEventBus);
        PetrolparkDataLoadingConditions.register();
        PetrolparkDataSubPredicates.register();
        PetrolparkDecayProductTypes.register();
        PetrolparkFeatureTypes.register();
        PetrolparkGlobalLootModifierSerializers.register();
        PetrolparkIngredientRandomizerTypes.register();
        PetrolparkNeoForgeIngredientTypes.register();
        PetrolparkItems.register();
        PetrolparkLootConditionTypes.register();
        PetrolparkLootItemFunctions.register();
        PetrolparkLootModifierTypes.register();
        PetrolparkNumberProviderTypes.register();
        PetrolparkPackets.register();
        PetrolparkRecipeSerializers.register();
        PetrolparkRecipeTypes.register();
        IPetrolparkRecipeTypes.register(modEventBus);
        PetrolparkRegistrateProviderTypes.register();
        PetrolparkRewardGeneratorTypes.register();
        PetrolparkRewardTypes.register();
        PetrolparkTeamProviderTypes.register();
        PetrolparkTradeListingReferenceTypes.register();
    
        // Events
        modEventBus.addListener(this::init);
        modEventBus.addListener(EventPriority.LOWEST, PetrolparkDatagen::gatherData);
        NeoForge.EVENT_BUS.register(SCOREBOARD_TEAMS);
        NeoForge.EVENT_BUS.register(BOGGLE_PATTERNS);

        // Compat
        if (Mods.JEI.isLoading()) NeoForge.EVENT_BUS.register(ITickableCategory.ClientEvents.class);
        Mods.CREATE.executeIfInstalled(() -> () -> PetrolparkCreate.ctor(modEventBus, NeoForge.EVENT_BUS));
        Mods.CURIOS.executeIfInstalled(() -> () -> PetrolparkCurios.ctor(modEventBus, NeoForge.EVENT_BUS));

        // Config
        PetrolparkConfigs.register(ModLoadingContext.get(), modContainer);
    };

    @GetPetrolparkSharedFeatures
    public static final SharedFeatureFlag[] getEnabledSharedFeatureFlags() {
        return new SharedFeatureFlag[]{};
        //return new SharedFeatureFlag[]{SharedFeatureFlag.HORSE_MILL};
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
