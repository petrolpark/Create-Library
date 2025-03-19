package com.petrolpark.compat.create.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.compat.create.CreateClient;
import com.petrolpark.event.ClientEvents;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.DefaultSuperRenderTypeBuffer;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public class CreateClientEvents {
    
    @SubscribeEvent
    public static void onTick(ClientTickEvent.Pre event) {
        if (!ClientEvents.isGameActive()) return;
        CreateClient.OUTLINER.tickOutlines();
    };

    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

		PoseStack ms = event.getPoseStack();
		ms.pushPose();
		SuperRenderTypeBuffer buffer = DefaultSuperRenderTypeBuffer.getInstance();
		float partialTicks = AnimationTickHolder.getPartialTicks();
        Minecraft mc = Minecraft.getInstance();
		Vec3 camera = mc.gameRenderer.getMainCamera().getPosition();

        CreateClient.OUTLINER.renderOutlines(ms, buffer, camera, partialTicks);

        buffer.draw();
		RenderSystem.enableCull();
        ms.popPose();
    };
};
