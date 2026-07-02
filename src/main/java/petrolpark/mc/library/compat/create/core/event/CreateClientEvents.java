package petrolpark.mc.library.compat.create.core.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.render.DefaultSuperRenderTypeBuffer;
import net.createmod.catnip.render.SuperRenderTypeBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.create.PetrolparkCreateClient;
import petrolpark.mc.library.compat.create.core.world.block.tube.ClientTubePlacementHandler;
import petrolpark.mc.library.compat.create.core.world.dough.DoughModel;
import petrolpark.mc.library.compat.create.core.world.dough.rollingPin.RollingPinItemRenderer;
import petrolpark.mc.library.core.event.ClientEvents;

public class CreateClientEvents {

    // REGISTRATION

    @SubscribeEvent
    public static final void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        DoughModel.onRegisterGeometryLoaders(event);
    };

    @SubscribeEvent
    public static final void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        RollingPinItemRenderer.onRegisterClientExtensions(event);
    };

    @SubscribeEvent
    public static final void registerGuiOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, Petrolpark.asResource("tube_info"), ClientTubePlacementHandler.OVERLAY);
    };

    // TICKING/FRAMES
    
    @SubscribeEvent
    public static void onTick(ClientTickEvent.Pre event) {
        if (!ClientEvents.isGameActive()) return;
        PetrolparkCreateClient.OUTLINER.tickOutlines();
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

        PetrolparkCreateClient.OUTLINER.renderOutlines(ms, buffer, camera, partialTicks);

        buffer.draw();
		RenderSystem.enableCull();
        ms.popPose();
    };
};
