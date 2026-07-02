package petrolpark.mc.library.compat.create.core.world.dough.rollingPin;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateItems;
import petrolpark.mc.library.shared.SharedFeatureFlag;

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

    public static final void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        if (SharedFeatureFlag.ROLLING_PIN.enabled()) event.registerItem(new RollingPinItemRenderer(), SharedCreateItems.ROLLING_PIN.get());
    };
};
