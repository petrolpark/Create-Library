package com.petrolpark.compat.create.common.redstone.programmer;

import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.compat.create.PetrolparkPartialModels;
import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgram.Channel;
import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgrammerBlockItem.ItemStackRedstoneProgram;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.math.AngleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RedstoneProgrammerItemRenderer extends CustomRenderedItemModelRenderer {

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer, ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        final Minecraft mc = Minecraft.getInstance();

        renderer.render(model.getOriginalModel(), light);
        final Optional<ItemStackRedstoneProgram> programOptional = RedstoneProgrammerBlockItem.getProgram(stack, mc.level, mc.player);

        final boolean running = transformType != ItemDisplayContext.GROUND && transformType != ItemDisplayContext.FIXED && programOptional.map(program -> !program.paused).orElse(false);
        final float rotation = running ? AnimationTickHolder.getRenderTime() : 0f;
        
        ms.pushPose();
        TransformStack.of(ms)
            .uncenter()
            .translate(0, 6 / 16d, 10 / 16d)
            .rotateXDegrees(rotation)
            .translateBack(0, 6 / 16d, 10 / 16d)
            .center();
        renderer.render(PetrolparkPartialModels.REDSTONE_PROGRAMMER_CYLINDER.get(), light);
        ms.popPose();

        ms.pushPose();
        TransformStack.of(ms)
            .uncenter()
            .translate(0d, 8.5 / 16d, 5.5 / 16d)
            .rotateXDegrees(-2 + 8 * -Mth.sin(4 * AngleHelper.rad(rotation)))
            .translateBack(0d, 8.5 / 16d, 5.5 / 16d)
            .center();
        renderer.render(PetrolparkPartialModels.REDSTONE_PROGRAMMER_NEEDLE.get(), light);
        ms.popPose();

        if (programOptional.isEmpty()) return;
        RedstoneProgram program = programOptional.get();
        ImmutableList<Channel> channels = program.getChannels();
        for (int i = 0; i < 6; i++) {
            if (i >= channels.size()) continue;
            boolean powered = !program.paused && program.getChannels().get(i).getTransmittedStrength() != 0;
            ms.pushPose();
            ms.translate(i % 2 == 0 ? 0f : 15 / 16f, 1 / 16f, (3 + 4.5F * (i / 2)) / 16f);
            renderer.render((powered ? PetrolparkPartialModels.REDSTONE_PROGRAMMER_TRANSMITTER_POWERED : PetrolparkPartialModels.REDSTONE_PROGRAMMER_TRANSMITTER).get(), light);
            ms.popPose();
        };
    };
    
};
