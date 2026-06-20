package com.petrolpark.compat.create.core.world.dough.rollingPin;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.compat.create.shared.registry.SharedCreateItems;
import com.petrolpark.shared.SharedFeatureFlag;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(Dist.CLIENT)
public class RollingPinItemRenderer implements IClientItemExtensions {

    @Override
    public boolean applyForgeHandTransform(@Nonnull PoseStack ms, @Nonnull LocalPlayer player, @Nonnull HumanoidArm arm, @Nonnull ItemStack itemInHand, float partialTick, float equipProcess, float swingProcess) {
        if (player.getUseItemRemainingTicks() <= 0) return false;
        
        final float useTime = itemInHand.getUseDuration(player) - player.getUseItemRemainingTicks() + partialTick - 1f;

        final float movedToMiddle = Math.min(useTime, 4f) / 4f;
        final float rolling = (Mth.clamp(useTime,10f, 20f) - 10f) / 10f;
        TransformStack.of(ms)
            .translate(0.32f * movedToMiddle * (arm == HumanoidArm.RIGHT ? -1f : 1f), -0.8f * movedToMiddle, 0f)
            .rotateZDegrees(90f * movedToMiddle * (arm == HumanoidArm.RIGHT ? 1f : -1f))
            .translateZ(Mth.sin(rolling * Mth.PI) * -0.5f);

        return false;
    };

    @SubscribeEvent
    public static final void onRegisterClientItemExtensions(RegisterClientExtensionsEvent event) {
        if (SharedFeatureFlag.ROLLING_PIN.enabled()) event.registerItem(new RollingPinItemRenderer(), SharedCreateItems.ROLLING_PIN.get());
    };
};
