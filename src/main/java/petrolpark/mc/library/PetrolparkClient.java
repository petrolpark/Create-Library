package petrolpark.mc.library;

import net.createmod.ponder.foundation.PonderIndex;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import petrolpark.mc.library.compat.Mods;
import petrolpark.mc.library.compat.create.PetrolparkCreateClient;
import petrolpark.mc.library.compat.curios.PetrolparkCuriosClient;
import petrolpark.mc.library.compat.jei.PetrolparkJEI;
import petrolpark.mc.library.core.client.ponder.PetrolparkPonderPlugin;
import petrolpark.mc.library.core.client.texts.ClientTextsManager;
import petrolpark.mc.library.core.world.entity.player.extendedInventory.ExtendedInventoryClientHandler;
import petrolpark.mc.library.core.world.item.crafting.pocket.PocketCraftingClientHandler;
import petrolpark.mc.library.util.WoodHelperClient;

@Mod(value = Petrolpark.MOD_ID, dist = Dist.CLIENT)
public class PetrolparkClient {

    public static final ExtendedInventoryClientHandler EXTENDED_INVENTORY_HANDLER = new ExtendedInventoryClientHandler();
    public static final PocketCraftingClientHandler POCKET_CRAFTING_HANDLER = new PocketCraftingClientHandler(Minecraft.getInstance());
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
        neoEventBus.register(POCKET_CRAFTING_HANDLER);
    };
    
    public static final void clientInit(final FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new PetrolparkPonderPlugin());
        
        event.enqueueWork(() -> { // Work which must be done on main thread
            
        });
    };
};
