package petrolpark.mc.library.compat.create;

import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import petrolpark.mc.library.compat.create.core.client.offGridTiling.OffGridTilingMetadataSection;
import petrolpark.mc.library.compat.create.core.client.ponder.PetrolparkCreatePonderPlugin;
import petrolpark.mc.library.compat.create.core.data.recipe.RecipeBookProviderHighlighter;
import petrolpark.mc.library.compat.create.core.event.CreateClientEvents;
import petrolpark.mc.library.compat.create.core.event.CreateClientModEvents;
import petrolpark.mc.library.compat.create.core.world.block.chainConveyor.ChainConveyorArmInteractionClientHandler;
import petrolpark.mc.library.compat.create.core.world.block.tube.ClientTubePlacementHandler;
import petrolpark.mc.library.compat.create.shared.registry.SharedPartialModels;
import petrolpark.mc.library.core.client.outline.Outliner;

public class PetrolparkCreateClient {

    public static final Outliner OUTLINER = new Outliner();
    public static final RecipeBookProviderHighlighter RECIPE_BOOK_PROVIDER_HIGHLIGHTER = new RecipeBookProviderHighlighter();
    
    public static final void clientCtor(IEventBus modEventBus, IEventBus mainEventBus) {

        // Event Bus Subscribers
        mainEventBus.register(CreateClientEvents.class);
        mainEventBus.register(ClientTubePlacementHandler.class);
        mainEventBus.register(ChainConveyorArmInteractionClientHandler.class);
        mainEventBus.register(RECIPE_BOOK_PROVIDER_HIGHLIGHTER);
        modEventBus.register(CreateClientModEvents.class);
        modEventBus.addListener(PetrolparkCreateClient::clientInit);

        OffGridTilingMetadataSection.init();
        SharedPartialModels.register();
    };

    public static final void clientInit(final FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new PetrolparkCreatePonderPlugin());
    };
};
