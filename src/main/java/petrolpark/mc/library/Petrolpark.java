package petrolpark.mc.library;

import java.util.function.Supplier;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

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
import petrolpark.mc.library.compat.Mods;
import petrolpark.mc.library.compat.create.PetrolparkCreate;
import petrolpark.mc.library.compat.curios.PetrolparkCurios;
import petrolpark.mc.library.compat.jei.category.ITickableCategory;
import petrolpark.mc.library.config.PetrolparkConfigs;
import petrolpark.mc.library.core.badge.Badges;
import petrolpark.mc.library.core.data.recipe.IPetrolparkRecipeTypes;
import petrolpark.mc.library.core.data.recipe.bogglePattern.BogglePattern;
import petrolpark.mc.library.core.data.recipe.compat.CompatRecipeManager;
import petrolpark.mc.library.core.registrate.AbstractPetrolparkRegistrate;
import petrolpark.mc.library.core.world.entity.player.team.scoreboard.ScoreboardTeamManager;
import petrolpark.mc.library.registry.PetrolparkAdvancedIngredientTypes;
import petrolpark.mc.library.registry.PetrolparkAttachmentTypes;
import petrolpark.mc.library.registry.PetrolparkAttributes;
import petrolpark.mc.library.registry.PetrolparkBogglePatternGeneratorTypes;
import petrolpark.mc.library.registry.PetrolparkCriteriaTriggers;
import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;
import petrolpark.mc.library.registry.PetrolparkDataLoadingConditions;
import petrolpark.mc.library.registry.PetrolparkDataSubPredicates;
import petrolpark.mc.library.registry.PetrolparkDecayProductTypes;
import petrolpark.mc.library.registry.PetrolparkFeatureTypes;
import petrolpark.mc.library.registry.PetrolparkGlobalLootModifierSerializers;
import petrolpark.mc.library.registry.PetrolparkIngredientRandomizerTypes;
import petrolpark.mc.library.registry.PetrolparkItems;
import petrolpark.mc.library.registry.PetrolparkLootConditionTypes;
import petrolpark.mc.library.registry.PetrolparkLootItemFunctions;
import petrolpark.mc.library.registry.PetrolparkLootModifierTypes;
import petrolpark.mc.library.registry.PetrolparkNeoForgeIngredientTypes;
import petrolpark.mc.library.registry.PetrolparkNumberProviderTypes;
import petrolpark.mc.library.registry.PetrolparkPackets;
import petrolpark.mc.library.registry.PetrolparkRecipeSerializers;
import petrolpark.mc.library.registry.PetrolparkRecipeTypes;
import petrolpark.mc.library.registry.PetrolparkRegistrateProviderTypes;
import petrolpark.mc.library.registry.PetrolparkRewardGeneratorTypes;
import petrolpark.mc.library.registry.PetrolparkRewardTypes;
import petrolpark.mc.library.registry.PetrolparkTeamProviderTypes;
import petrolpark.mc.library.registry.PetrolparkTradeListingReferenceTypes;
import petrolpark.mc.library.shared.GetPetrolparkSharedFeatures;
import petrolpark.mc.library.shared.Shared;
import petrolpark.mc.library.shared.SharedFeatureFlag;

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

        Shared.ctor(modEventBus, modContainer);

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
        //return new SharedFeatureFlag[]{};
        return new SharedFeatureFlag[]{SharedFeatureFlag.ROLLING_PIN};
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
