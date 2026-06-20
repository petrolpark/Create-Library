package com.petrolpark.compat.create.registry;

import com.petrolpark.compat.create.PetrolparkCreatePonderPlugin;
import com.petrolpark.compat.create.core.client.offGridTiling.OffGridTilingMetadataSection;
import com.petrolpark.compat.create.core.data.recipe.RecipeBookProviderHighlighter;
import com.petrolpark.compat.create.core.event.CreateClientEvents;
import com.petrolpark.compat.create.core.event.CreateClientModEvents;
import com.petrolpark.compat.create.core.world.block.tube.ClientTubePlacementHandler;
import com.petrolpark.compat.create.shared.registry.SharedPartialModels;
import com.petrolpark.core.client.outline.Outliner;

import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class PetrolparkCreateClient {

    public static final Outliner OUTLINER = new Outliner();
    public static final RecipeBookProviderHighlighter RECIPE_BOOK_PROVIDER_HIGHLIGHTER = new RecipeBookProviderHighlighter();
    
    public static final void clientCtor(IEventBus modEventBus, IEventBus mainEventBus) {

        // Event Bus Subscribers
        modEventBus.register(CreateClientModEvents.class);
        mainEventBus.register(CreateClientEvents.class);
        mainEventBus.register(ClientTubePlacementHandler.class);
        mainEventBus.register(RECIPE_BOOK_PROVIDER_HIGHLIGHTER);
        modEventBus.addListener(PetrolparkCreateClient::clientInit);

        OffGridTilingMetadataSection.init();
        SharedPartialModels.register();
    };

    public static final void clientInit(final FMLClientSetupEvent event) {
        PonderIndex.addPlugin(new PetrolparkCreatePonderPlugin());
    };
};
