package com.petrolpark.compat.create;

import com.petrolpark.client.outline.Outliner;
import com.petrolpark.compat.create.client.offgridtiling.OffGridTilingMetadataSection;
import com.petrolpark.compat.create.core.recipe.RecipeBookProviderHighlighter;
import com.petrolpark.compat.create.core.tube.ClientTubePlacementHandler;
import com.petrolpark.compat.create.event.CreateClientEvents;
import com.petrolpark.compat.create.event.CreateClientModEvents;

import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class CreateClient {

    public static final Outliner OUTLINER = new Outliner();
    public static final RecipeBookProviderHighlighter RECIPE_BOOK_PROVIDER_HIGHLIGHTER = new RecipeBookProviderHighlighter();
    
    public static final void clientCtor(IEventBus modEventBus, IEventBus mainEventBus) {

        // Event Bus Subscribers
        modEventBus.register(CreateClientModEvents.class);
        mainEventBus.register(CreateClientEvents.class);
        mainEventBus.register(ClientTubePlacementHandler.class);
        mainEventBus.register(RECIPE_BOOK_PROVIDER_HIGHLIGHTER);
        modEventBus.addListener(CreateClient::clientInit);

        OffGridTilingMetadataSection.init();
        PetrolparkPartialModels.register();
    };

    public static final void clientInit(final FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new PetrolparkCreatePonderPlugin());
    };
};
