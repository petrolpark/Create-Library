package com.petrolpark;

import com.petrolpark.compat.Mods;
import com.petrolpark.compat.create.CreateClient;
import com.petrolpark.compat.curios.CuriosClient;
import com.petrolpark.compat.jei.PetrolparkJEI;
import com.petrolpark.core.inventory.extended.ExtendedInventoryClientHandler;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = Petrolpark.MOD_ID, dist = Dist.CLIENT)
public class PetrolparkClient {

    public static final ExtendedInventoryClientHandler EXTENDED_INVENTORY_HANDLER = new ExtendedInventoryClientHandler();

    public PetrolparkClient(IEventBus modEventBus) {
		clientCtor(modEventBus, NeoForge.EVENT_BUS);

        Mods.CREATE.executeIfInstalled(() -> () -> CreateClient.clientCtor(modEventBus, NeoForge.EVENT_BUS));
        Mods.CURIOS.executeIfInstalled(() -> () -> CuriosClient.clientCtor(modEventBus, NeoForge.EVENT_BUS));
        Mods.JEI.executeIfInstalled(() -> () -> PetrolparkJEI.ctor(modEventBus, NeoForge.EVENT_BUS));
	};

    public final void clientCtor(IEventBus modEventBus, IEventBus neoEventBus) {
        modEventBus.addListener(PetrolparkClient::clientInit);
        neoEventBus.register(EXTENDED_INVENTORY_HANDLER);
    };
    
    public static final void clientInit(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> { // Work which must be done on main thread
            
        });
    };
};
