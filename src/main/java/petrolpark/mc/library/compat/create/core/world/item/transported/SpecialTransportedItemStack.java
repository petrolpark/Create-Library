package petrolpark.mc.library.compat.create.core.world.item.transported;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * {@link TransportedItemStack} with special behaviour, namely ticking, rendering and de/serialization
 */
public class SpecialTransportedItemStack extends TransportedItemStack {

    public SpecialTransportedItemStack(ItemStack stack) {
        super(stack);
    };

    public void tick(Level level) {};

    public void deserializeNBT(CompoundTag nbt, HolderLookup.Provider registries) {};

    public final boolean renderRotated(PoseStack ms, MultiBufferSource buffer, float partialTicks, int light, int overlay, int angle) {
        ms.pushPose();
        ms.mulPose(Axis.YP.rotationDegrees(angle));
        final boolean skipDefault = render(ms, buffer, partialTicks, light, overlay);
        ms.popPose();
        return skipDefault;
    };

    /**
     * @return {@code true} to skip default rendering
     */
    @OnlyIn(Dist.CLIENT)
    public boolean render(PoseStack ms, MultiBufferSource buffer, float partialTicks, int light, int overlay) {
        return false;
    };
    
};
