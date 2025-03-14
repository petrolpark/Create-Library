package com.petrolpark.event;

import java.util.stream.Stream;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkConfig;
import com.petrolpark.PetrolparkTags;
import com.petrolpark.badge.PlayerBadges;
import com.petrolpark.command.ContaminateHeldItemCommand;
import com.petrolpark.contamination.Contaminant;
import com.petrolpark.contamination.ItemContamination;
import com.petrolpark.item.decay.DecayingItemHandler.ServerDecayingItemHandler;
import com.petrolpark.item.decay.IDecayingItem;
import com.petrolpark.shop.customer.EntityCustomer;
import com.petrolpark.team.SinglePlayerTeam;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.brewing.PotionBrewEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber
public class CommonEvents {

    // CORE/REGISTRATION
    
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        ContaminateHeldItemCommand.register(event.getDispatcher(), event.getBuildContext());
    };

    @SubscribeEvent
    public static void onTickLevel(LevelTickEvent.Post event) {
        // Decaying Items
        if (event.getLevel().isClientSide()) return;
        MinecraftServer server = event.getLevel().getServer();
        if (server != null && server.overworld() == event.getLevel()) ((ServerDecayingItemHandler)Petrolpark.DECAYING_ITEM_HANDLER.get()).gameTime++;
        
    };

    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new Contaminant.ReloadListener(event.getRegistryAccess()));
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
            IDecayingItem.startDecay(potion);
            if (PetrolparkConfig.SERVER.brewingPropagatesContaminants.get()) ItemContamination.perpetuateSingle(Stream.of(event.getItem(3), potion).dropWhile(s -> PetrolparkConfig.SERVER.brewingWaterBottleContaminantsIgnored.get() && PotionUtil.getPotion(s) == Potions.WATER), potion);
        };
    };
    
};
