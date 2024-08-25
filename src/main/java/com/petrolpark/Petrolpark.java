package com.petrolpark;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.petrolpark.badge.Badge;
import com.petrolpark.badge.Badges;
import com.petrolpark.compat.CompatMods;
import com.petrolpark.compat.curios.Curios;
import com.petrolpark.compat.jei.category.ITickableCategory;
import com.petrolpark.itemdecay.DecayingItemHandler;
import com.petrolpark.recipe.PetrolparkRecipeTypes;
import com.petrolpark.registrate.PetrolparkRegistrate;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryBuilder;

@Mod(Petrolpark.MOD_ID)
public class Petrolpark {

    public static final String MOD_ID = "petrolpark";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final PetrolparkRegistrate REGISTRATE = new PetrolparkRegistrate(MOD_ID);
    public static final PetrolparkRegistrate DESTROY_REGISTRATE = CompatMods.DESTROY.registrate();

    // Registries
    public static final ResourceKey<Registry<Badge>> BADGE_REGISTRY_KEY = REGISTRATE.makeRegistry("badge", RegistryBuilder::new);

    public static ResourceLocation asResource(String path) {
        return new ResourceLocation(MOD_ID, path);
    };

    public static final ThreadLocal<DecayingItemHandler> DECAYING_ITEM_HANDLER = ThreadLocal.withInitial(() -> DecayingItemHandler.DUMMY);

    public Petrolpark() {
        //ModLoadingContext modLoadingContext = ModLoadingContext.get();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        REGISTRATE.registerEventListeners(modEventBus);
        DESTROY_REGISTRATE.registerEventListeners(modEventBus);

        Badges.register();
        PetrolparkRecipeTypes.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::init);

        // JEI compat
        if (CompatMods.JEI.isLoading()) {
            forgeEventBus.register(ITickableCategory.ClientEvents.class);
        };

        CompatMods.CURIOS.executeIfInstalled(() -> () -> Curios.init(modEventBus, forgeEventBus));
    };

    private void init(final FMLCommonSetupEvent event) {
        
    };

};
