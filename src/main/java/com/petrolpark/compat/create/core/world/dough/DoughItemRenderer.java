package com.petrolpark.compat.create.core.world.dough;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.petrolpark.compat.create.registry.PetrolparkCreateDataComponentTypes;
import com.petrolpark.compat.create.shared.registry.SharedCreateBlocks;

import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(Dist.CLIENT)
public class DoughItemRenderer extends BlockEntityWithoutLevelRenderer implements IClientItemExtensions {

    protected static final Cache<DoughData, DoughBlockEntity> CACHE = CacheBuilder.newBuilder()
        .expireAfterAccess(Duration.ofSeconds(30))
        .maximumSize(512)
        .build();

    protected final BlockEntityRenderDispatcher blockEntityRenderDispatcher;

    public DoughItemRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher, null);
        this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
    };

    @Override
    public DoughItemRenderer getCustomRenderer() {
        return this;
    };

    @Override
    public void renderByItem(@Nonnull ItemStack stack, @Nonnull ItemDisplayContext displayContext, @Nonnull PoseStack ms, @Nonnull MultiBufferSource buffer, int packedLight, int packedOverlay) {
        final DoughData data = stack.get(PetrolparkCreateDataComponentTypes.DOUGH);
        if (data == null) return;
        final DoughBlockEntity be;
        try {
            be = CACHE.get(data, () -> new DoughBlockEntity(data));
        } catch (ExecutionException e) {
            return;
        };

        ms.pushPose();

        if (displayContext == ItemDisplayContext.GUI) {
            float scale = DoughData.MAX_WIDTH / Math.max(data.width(), data.length());
            scale = Mth.sqrt(scale);
            TransformStack.of(ms)
                .center()
                .translateY(scale  * 0.375f)
                .scale(scale)
                .uncenter();
        };

        blockEntityRenderDispatcher.renderItem(be, ms, buffer, packedLight, packedOverlay);
        ms.popPose();
    };

    @SubscribeEvent
    public static final void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        final Minecraft mc = Minecraft.getInstance();
        event.registerItem(new DoughItemRenderer(mc.getBlockEntityRenderDispatcher()), SharedCreateBlocks.DOUGH.get().asItem());
    };
    
};
