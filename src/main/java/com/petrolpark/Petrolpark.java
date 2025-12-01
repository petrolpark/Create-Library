package com.petrolpark;

import java.lang.annotation.ElementType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.petrolpark.compat.GetPetrolparkSharedFeatures;
import com.petrolpark.compat.Mods;
import com.petrolpark.compat.SharedFeatureFlag;
import com.petrolpark.compat.create.Create;
import com.petrolpark.compat.curios.Curios;
import com.petrolpark.compat.jei.category.ITickableCategory;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.badge.Badges;
import com.petrolpark.core.recipe.IPetrolparkRecipeTypes;
import com.petrolpark.core.recipe.bogglepattern.BogglePattern;
import com.petrolpark.core.recipe.compat.CompatRecipeManager;
import com.petrolpark.core.team.scoreboard.ScoreboardTeamManager;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforgespi.language.ModFileScanData;

@Mod(Petrolpark.MOD_ID)
public class Petrolpark {

    public static final String MOD_ID = "petrolpark";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final PetrolparkRegistrate REGISTRATE = new PetrolparkRegistrate(MOD_ID);
    public static final PetrolparkRegistrate DESTROY_REGISTRATE = Mods.DESTROY.registrate();

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    };

    public static final CompatRecipeManager COMPAT_RECIPES = new CompatRecipeManager();
    public static final ScoreboardTeamManager SCOREBOARD_TEAMS = new ScoreboardTeamManager();
    public static final BogglePattern.Manager BOGGLE_PATTERNS = new BogglePattern.Manager();

    public Petrolpark(IEventBus modEventBus, ModContainer modContainer) {

        initializeSharedFeatures();

        REGISTRATE.registerEventListeners(modEventBus);
        DESTROY_REGISTRATE.registerEventListeners(modEventBus);

        // Config
        PetrolparkConfigs.register(ModLoadingContext.get(), modContainer);

        // Registration
        Badges.register();
        PetrolparkAdvancedIngredientTypes.register();
        PetrolparkAttachmentTypes.register(modEventBus);
        PetrolparkAttributes.register();
        PetrolparkBlockEntityTypes.register();
        PetrolparkBlocks.register();
        PetrolparkBogglePatternGeneratorTypes.register();
        PetrolparkCriteriaTriggers.register();
        PetrolparkDataComponents.register(modEventBus);
        PetrolparkDataLoadingConditions.register();
        PetrolparkDataSubPredicates.register();
        PetrolparkDecayProductTypes.register();
        PetrolparkGlobalLootModifierSerializers.register();
        PetrolparkIngredientRandomizerTypes.register();
        PetrolparkIngredientTypes.register();
        PetrolparkItems.register();
        PetrolparkLootConditionTypes.register();
        PetrolparkLootItemFunctions.register();
        PetrolparkMobEffects.register();
        PetrolparkNumberProviderTypes.register();
        PetrolparkPackets.register();
        PetrolparkParticleTypes.register();
        PetrolparkRecipeSerializers.register();
        PetrolparkRecipeTypes.register();
        IPetrolparkRecipeTypes.register(modEventBus);
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
        Mods.CREATE.executeIfInstalled(() -> () -> Create.ctor(modEventBus, NeoForge.EVENT_BUS));
        Mods.CURIOS.executeIfInstalled(() -> () -> Curios.ctor(modEventBus, NeoForge.EVENT_BUS));
    };

    @GetPetrolparkSharedFeatures
    public static final SharedFeatureFlag[] getEnabledSharedFeatureFlags() {
        return new SharedFeatureFlag[]{};
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

    private static final void initializeSharedFeatures() {
        Petrolpark.LOGGER.info("Searching for Mods enabling Petrolpark's Shared Features");
        for (final ModFileScanData scanData : ModList.get().getAllScanData()) {

            scanData.getAnnotatedBy(GetPetrolparkSharedFeatures.class, ElementType.METHOD).forEach(data -> {
                final String className = data.clazz().getClassName();
                final String memberName = data.memberName().split("\\(")[0];
                Petrolpark.LOGGER.info("Found suitable method " + memberName + "in class " + className);
                try {
                    final Class<?> clazz = Class.forName(className);
                    final Mod mod = clazz.getAnnotation(Mod.class);
                    if (mod == null) throw new IllegalArgumentException("@InitializeSharedFeatures method must be in @Mod class");
                    Mods compatMod = Mods.LOOKUP.apply(mod.value());
                    if (compatMod == null) compatMod = Mods.PETROLPARK; // Other Mods can enable Shared Features under the Petrolpark name
                    final Method method = clazz.getMethod(memberName);
                    if (Modifier.isStatic(method.getModifiers())) {
                        if (method.invoke(null) instanceof SharedFeatureFlag[] flags) {
                            for (SharedFeatureFlag flag : flags) flag.enable(compatMod);
                        } else {
                            throw new IllegalArgumentException("Must return an array of SharedFeatureFlag");
                        };
                    } else {
                        throw new IllegalArgumentException("@InitializeSharedFeatures method must be static");
                    };
                } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new IllegalArgumentException("Could not initialize Shared Features in class " + className, e);
                };
            });
        };
    };

};
