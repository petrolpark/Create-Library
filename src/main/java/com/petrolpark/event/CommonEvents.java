package com.petrolpark.event;

import com.petrolpark.Petrolpark;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.contamination.Contaminant;
import com.petrolpark.core.contamination.ContaminateHeldItemCommand;
import com.petrolpark.core.contamination.ItemContamination;
import com.petrolpark.core.item.decay.ItemDecay;
import com.petrolpark.shadereffect.IShaderEffect;
import com.petrolpark.shadereffect.packet.RemoveAllShadersPacket;
import com.petrolpark.util.mixininterfaces.IGameRendererMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.brewing.PotionBrewEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.stream.Stream;

@EventBusSubscriber
public class CommonEvents {

    // CORE/REGISTRATION
    
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        ContaminateHeldItemCommand.register(event.getDispatcher(), event.getBuildContext());
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

    /**
     * Cleans residual shader effects on disconnection
     * @param event
     */
    @SubscribeEvent
    public static void onPlayerLogOut(ClientPlayerNetworkEvent.LoggingOut event) {
        IGameRendererMixin gameRenderer = (( IGameRendererMixin ) Minecraft.getInstance().gameRenderer);
        gameRenderer.cleanShaderEffects();
    }

    /**
     * Gets rid of shader effects on effect removal
     * @param event
     */
    @SubscribeEvent
    public static void onMobEffectRemoved(MobEffectEvent.Remove event) {
        MobEffect effect = event.getEffect().value();
        if (effect instanceof IShaderEffect shaderEffect && event.getEntity() instanceof ServerPlayer serverPlayer) {
            shaderEffect.cleanupShader(serverPlayer, shaderEffect);
        }
    }

    /**
     * Gets rid of shader effects on player death
     * @param event
     */
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new RemoveAllShadersPacket(true));
        }
    }
};
