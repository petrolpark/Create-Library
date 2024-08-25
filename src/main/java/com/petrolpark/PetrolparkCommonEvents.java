package com.petrolpark;

import com.petrolpark.badge.BadgesCapability;
import com.petrolpark.itemdecay.DecayingItemHandler.ServerDecayingItemHandler;
import com.petrolpark.recipe.advancedprocessing.firsttimelucky.FirstTimeLuckyRecipesCapability;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class PetrolparkCommonEvents {

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player player) {
            // Add Badge Capability
            if (!player.getCapability(BadgesCapability.Provider.PLAYER_BADGES).isPresent()) {
                event.addCapability(Petrolpark.asResource("badges"), new BadgesCapability.Provider());
            };
            // Add Lucky first recipe Capability
            if (!player.getCapability(FirstTimeLuckyRecipesCapability.Provider.PLAYER_LUCKY_FIRST_RECIPES).isPresent()) {
                event.addCapability(Petrolpark.asResource("lucky_first_recipes"), new FirstTimeLuckyRecipesCapability.Provider());
            };
        };
    };

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {

            // Copy Badge data
            event.getOriginal().getCapability(BadgesCapability.Provider.PLAYER_BADGES).ifPresent(oldStore -> {
                event.getEntity().getCapability(BadgesCapability.Provider.PLAYER_BADGES).ifPresent(newStore -> {
                    newStore.setBadges(oldStore.getBadges());
                });
            });
            // Copy Lucky First Recipe data
            event.getOriginal().getCapability(FirstTimeLuckyRecipesCapability.Provider.PLAYER_LUCKY_FIRST_RECIPES).ifPresent(oldStore -> {
                event.getEntity().getCapability(FirstTimeLuckyRecipesCapability.Provider.PLAYER_LUCKY_FIRST_RECIPES).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        };
    };

    @SubscribeEvent
    public static void onTick(LevelTickEvent event) {
        // Decaying Items
        if (event.phase == LevelTickEvent.Phase.END && !event.level.isClientSide() && event.level.getServer().overworld() == event.level) ((ServerDecayingItemHandler)Petrolpark.DECAYING_ITEM_HANDLER.get()).gameTime++;
    };
};
