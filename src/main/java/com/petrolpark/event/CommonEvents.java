package com.petrolpark.event;

import java.util.stream.Stream;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkConfig;
import com.petrolpark.PetrolparkTags;
import com.petrolpark.badge.BadgesCapability;
import com.petrolpark.command.ContaminateCommand;
import com.petrolpark.contamination.Contaminant;
import com.petrolpark.contamination.ItemContamination;
import com.petrolpark.item.decay.DecayingItemHandler.ServerDecayingItemHandler;
import com.petrolpark.item.decay.IDecayingItem;
import com.petrolpark.shop.customer.EntityCustomer;
import com.petrolpark.team.SinglePlayerTeam;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class CommonEvents {

    // CORE/REGISTRATION
    
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        ContaminateCommand.register(event.getDispatcher(), event.getBuildContext());
    };

    @SubscribeEvent
    public static void onTickLevel(LevelTickEvent event) {
        // Decaying Items
        if (event.phase == LevelTickEvent.Phase.END) {
            if (!event.level.isClientSide() && event.level.getServer().overworld() == event.level && Petrolpark.DECAYING_ITEM_HANDLER.get() instanceof ServerDecayingItemHandler decayingItemHandler) decayingItemHandler.gameTime++;
        };
        
    };

    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new Contaminant.ReloadListener(event.getRegistryAccess()));
    };

    @SubscribeEvent
    public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof final Player player) {
            // Add Badge Capability
            if (!player.getCapability(BadgesCapability.Provider.PLAYER_BADGES).isPresent()) event.addCapability(Petrolpark.asResource("badges"), new BadgesCapability.Provider());
            // Add Team Capability
            if (!player.getCapability(SinglePlayerTeam.CAPABILITY).isPresent()) event.addCapability(Petrolpark.asResource("team"), new SinglePlayerTeam(player));
        } else {
            // Add Shop Customer capability
            if (!entity.getCapability(EntityCustomer.CAPABILITY).isPresent()) event.addCapability(Petrolpark.asResource("customer"), new EntityCustomer(entity));
        };
    };

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // Copy Badge data
            event.getOriginal().getCapability(BadgesCapability.Provider.PLAYER_BADGES).ifPresent(oldStore -> {
                event.getEntity().getCapability(BadgesCapability.Provider.PLAYER_BADGES).ifPresent(newStore -> newStore.setBadges(oldStore.getBadges()));
            });
            // Copy (some) Team Data
            event.getOriginal().getCapability(SinglePlayerTeam.CAPABILITY).ifPresent(oldCap -> {
                event.getEntity().getCapability(SinglePlayerTeam.CAPABILITY).ifPresent(newCap -> newCap.copyTeamData(event.getEntity().level(), oldCap, PetrolparkTags.TeamDataTypes.LOST_ON_PLAYER_DEATH::matches));
            });
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
            IDecayingItem.startDecay(potion);
            if (PetrolparkConfig.SERVER.brewingPropagatesContaminants.get()) ItemContamination.perpetuateSingle(Stream.of(event.getItem(3), potion).dropWhile(s -> PetrolparkConfig.SERVER.brewingWaterBottleContaminantsIgnored.get() && PotionUtils.getPotion(s) == Potions.WATER), potion);
        };
    };
    
};
