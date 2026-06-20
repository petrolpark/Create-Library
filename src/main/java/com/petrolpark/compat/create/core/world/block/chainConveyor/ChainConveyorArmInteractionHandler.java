package com.petrolpark.compat.create.core.world.block.chainConveyor;

import static com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorInteractionHandler.selectedChainPosition;
import static com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorInteractionHandler.selectedConnection;
import static com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorInteractionHandler.selectedLift;

import com.petrolpark.mixin.compat.create.accessor.client.ArmInteractionPointHandlerAccessor;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.outliner.Outliner;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;

@EventBusSubscriber(Dist.CLIENT)
public class ChainConveyorArmInteractionHandler {
    
    @SubscribeEvent(priority = EventPriority.HIGH) // Needs to be before Create's ChainConveyorInteractionHandler
    public static final void onRightClickInput(InputEvent.InteractionKeyMappingTriggered event) {
        if (!ChainConveyorArmInteractionPoint.isEnabled() || selectedLift == null || ArmInteractionPointHandlerAccessor.getCurrentItem() == null) return;
        final Minecraft mc = Minecraft.getInstance();
        if (event.getKeyMapping() != mc.options.keyUse && event.getKeyMapping() != mc.options.keyAttack) return;

        ChainConveyorArmInteractionPoint selectedPoint = null;
        for (ArmInteractionPoint point : ArmInteractionPointHandlerAccessor.getCurrentSelection()) {
            if (point instanceof ChainConveyorArmInteractionPoint chainPoint && selectedLift == chainPoint.chainConveyorPos && selectedChainPosition == chainPoint.connectedPort.chainPosition()) {
                selectedPoint = chainPoint;
                break;
            };
        };

        final LocalPlayer player = mc.player;

        if (event.getKeyMapping() == mc.options.keyUse) { // Create a point

            if (selectedPoint == null) {
                ArmInteractionPointHandlerAccessor.getCurrentSelection().add(selectedPoint = new ChainConveyorArmInteractionPoint(mc.level, selectedLift, selectedChainPosition, selectedConnection, "*"));
            };
            selectedPoint.cycleMode();
            if (player != null) {
                player.swing(InteractionHand.MAIN_HAND);
                final ArmInteractionPoint.Mode mode = selectedPoint.getMode();
                CreateLang.builder()
                    .translate(mode.getTranslationKey(), CreateLang.blockName(AllBlocks.CHAIN_CONVEYOR.getDefaultState()))
                    .style(ChatFormatting.WHITE)
                    .color(mode.getColor())
                    .sendStatus(player);
            };

        } else if (selectedPoint != null) { // Remove a point

            if (player != null) player.swing(InteractionHand.MAIN_HAND);
            ArmInteractionPointHandlerAccessor.getCurrentSelection().remove(selectedPoint);
            event.setCanceled(true);
        };
    };

    @SubscribeEvent
    public static final void onTick(ClientTickEvent.Pre event) {
        final Minecraft mc = Minecraft.getInstance();
        final LocalPlayer player = mc.player;
        if (player == null
            || !ChainConveyorArmInteractionPoint.isEnabled() 
            || !(AllBlocks.MECHANICAL_ARM.isIn(player.getMainHandItem()) || (AllItems.WRENCH.isIn(player.getMainHandItem()) && ArmInteractionPointHandlerAccessor.getLastBlockPos() != -1l))
        ) return;

        for (ArmInteractionPoint point : ArmInteractionPointHandlerAccessor.getCurrentSelection()) {
            if (point instanceof ChainConveyorArmInteractionPoint chainPoint) Outliner.getInstance()
				.chaseAABB(chainPoint, new AABB(chainPoint.getInteractionPositionVector(), chainPoint.getInteractionPositionVector()).inflate(1 / 8f))
				.colored(chainPoint.getMode().getColor())
				.lineWidth(1 / 16f)
				.disableLineNormals();
        };
    };
};
