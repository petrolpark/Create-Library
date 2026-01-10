package com.petrolpark.event;

import java.util.List;
import java.util.stream.Stream;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkTags;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.core.contamination.Contaminant;
import com.petrolpark.core.contamination.ContaminateHeldItemCommand;
import com.petrolpark.core.contamination.ItemContamination;
import com.petrolpark.core.item.decay.ItemDecay;
import com.petrolpark.core.recipe.bogglepattern.BogglePatternCommand;
import com.petrolpark.core.world.effect.shader.IShaderEffect;
import com.petrolpark.core.world.effect.shader.packet.RemoveAllEffectShadersPacket;
import com.petrolpark.util.mixininterfaces.IGameRendererMixin;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.brewing.PotionBrewEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.network.PacketDistributor;

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

    /**
     * Cleans residual shader effects on disconnection
     * @param event
     */
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onPlayerLogOut(ClientPlayerNetworkEvent.LoggingOut event) {
        IGameRendererMixin gameRenderer = ((IGameRendererMixin) Minecraft.getInstance().gameRenderer);
        gameRenderer.petrolpark$cleanShaderEffects();
    };

    /**
     * Gets rid of shader effects on effect removal.
     * @param event
     */
    @SubscribeEvent
    public static void onMobEffectRemoved(MobEffectEvent.Remove event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance == null) return;

        Holder<MobEffect> effectHolder = effectInstance.getEffect();
        if (effectHolder.value() instanceof IShaderEffect shaderEffect && event.getEntity() instanceof ServerPlayer serverPlayer) {
            shaderEffect.cleanupShader(serverPlayer, effectHolder);
        };
    };

    /**
     * Gets rid of shader effects on effect expiration
     * @param event
     */
    @SubscribeEvent
    public static void onMobEffectExpired(MobEffectEvent.Expired event) {
        MobEffectInstance effectInstance = event.getEffectInstance();
        if (effectInstance == null) return;

        Holder<MobEffect> effectHolder = effectInstance.getEffect();
        if (effectHolder.value() instanceof IShaderEffect shaderEffect && event.getEntity() instanceof ServerPlayer serverPlayer) {
            shaderEffect.cleanupShader(serverPlayer, effectHolder);
        };
    }

    /**
     * Gets rid of shader effects on Player death.
     * @param event
     */
    @SubscribeEvent
    public static void onEntityDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new RemoveAllEffectShadersPacket(true));
        };
    };

    /**
     * Prevent a Mob from breeding if it is infertile due to an Effect.
     * @param event
     */
    @SubscribeEvent
    public static final void onBabyEntitySpawn(BabyEntitySpawnEvent event) {
        if (event.getParentA().getActiveEffects().stream().anyMatch(PetrolparkTags.MobEffects.CAUSES_INFERTILITY::matches) || event.getParentB().getActiveEffects().stream().anyMatch(PetrolparkTags.MobEffects.CAUSES_INFERTILITY::matches)) failToBreed(event);
    };

    public static final void failToBreed(BabyEntitySpawnEvent event) {
        if (event.getParentA().level() instanceof ServerLevel serverLevel) {
            final RandomSource random = serverLevel.getRandom();
            for (Mob parent : List.of(event.getParentA(), event.getParentB())) for (int i = 0; i < 7; i++) serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER, parent.getRandomX(1d), parent.getRandomY() + 0.5d, parent.getRandomZ(1d), 1, random.nextGaussian() * 0.5d, random.nextGaussian() * 0.5d, random.nextGaussian() * 0.5d, 0.02d);
        };
        event.setCanceled(true);
    };
};
