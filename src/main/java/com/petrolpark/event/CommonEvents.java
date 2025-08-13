package com.petrolpark.event;

import java.util.stream.Stream;

import com.petrolpark.Petrolpark;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.contamination.Contaminant;
import com.petrolpark.core.contamination.ContaminateHeldItemCommand;
import com.petrolpark.core.contamination.ItemContamination;
import com.petrolpark.core.item.decay.ItemDecay;
import com.petrolpark.core.recipe.bogglepattern.BogglePatternCommand;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.brewing.PotionBrewEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;

@EventBusSubscriber
public class CommonEvents {

    // CORE/REGISTRATION
    
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        ContaminateHeldItemCommand.register(event.getDispatcher(), event.getBuildContext());
        BogglePatternCommand.register(event.getDispatcher(), event.getBuildContext());
    };

    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new Contaminant.ReloadListener(event.getRegistryAccess()));
    };

    public static final ResourceLocation PLAYER_JOINS_FUNCTION_TAG = Petrolpark.asResource("player_joins");

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            MinecraftServer server = player.getServer();
            if (server != null) server.getFunctions().getTag(PLAYER_JOINS_FUNCTION_TAG).forEach(function -> 
                server.getFunctions().execute(function, player.createCommandSourceStack().withPermission(server.getFunctionCompilationLevel()))
            );
        };
    };

    // GAMEPLAY

    /**
     * Preserve Contaminants of Potions, and start decaying newly brewed Potions.
     * @param event
     */
    @SubscribeEvent
    public static void onPotionBrewed(PotionBrewEvent.Post event) {
        for (int slot = 0; slot < 3; slot++) {
            ItemStack potion = event.getItem(slot);
            ItemDecay.startDecay(potion);
            if (PetrolparkConfigs.server().brewingPropagatesContaminants.get()) ItemContamination.perpetuateSingle(
                Stream.of(event.getItem(3), potion)
                .dropWhile(s -> 
                    PetrolparkConfigs.server().brewingWaterBottleContaminantsIgnored.get()
                    && s.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion()
                        .map(Potions.WATER::equals)
                        .orElse(false)
                ), potion
            );
        };
    };
    
};
