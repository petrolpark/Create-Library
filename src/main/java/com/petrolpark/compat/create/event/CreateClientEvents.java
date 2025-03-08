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
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CreateClientEvents {
    
    @SubscribeEvent
    public static void onTick(ClientTickEvent event) {
        if (!ClientEvents.isGameActive()) return;
        CreateClient.OUTLINER.tickOutlines();
    };

    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
        if (event.getStage() != Stage.AFTER_PARTICLES) return;

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
