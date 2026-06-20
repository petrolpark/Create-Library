package com.petrolpark;

import com.petrolpark.compat.Mods;
import com.petrolpark.compat.create.registry.PetrolparkCreateClient;
import com.petrolpark.compat.curios.PetrolparkCuriosClient;
import com.petrolpark.compat.jei.PetrolparkJEI;
import com.petrolpark.core.client.ponder.PetrolparkPonderPlugin;
import com.petrolpark.core.client.texts.ClientTextsManager;
import com.petrolpark.core.world.entity.player.extendedInventory.ExtendedInventoryClientHandler;
import com.petrolpark.util.WoodHelperClient;

import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = Petrolpark.MOD_ID, dist = Dist.CLIENT)
public class PetrolparkClient {

    public static final ExtendedInventoryClientHandler EXTENDED_INVENTORY_HANDLER = new ExtendedInventoryClientHandler();
    public static final ClientTextsManager TEXTS = new ClientTextsManager();

    public PetrolparkClient(IEventBus modEventBus) {
		clientCtor(modEventBus, NeoForge.EVENT_BUS);

        Mods.CREATE.executeIfInstalled(() -> () -> PetrolparkCreateClient.clientCtor(modEventBus, NeoForge.EVENT_BUS));
        Mods.CURIOS.executeIfInstalled(() -> () -> PetrolparkCuriosClient.clientCtor(modEventBus, NeoForge.EVENT_BUS));
        Mods.JEI.executeIfInstalled(() -> () -> PetrolparkJEI.ctor(modEventBus, NeoForge.EVENT_BUS));

        WoodHelperClient.init();
	};

    public final void clientCtor(IEventBus modEventBus, IEventBus neoEventBus) {
        modEventBus.addListener(PetrolparkClient::clientInit);
        modEventBus.addListener(TEXTS::registerListener);
        neoEventBus.register(EXTENDED_INVENTORY_HANDLER);
    };
    
    public static final void clientInit(final FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new PetrolparkPonderPlugin());
        
        event.enqueueWork(() -> { // Work which must be done on main thread
            
        });
    };
};
