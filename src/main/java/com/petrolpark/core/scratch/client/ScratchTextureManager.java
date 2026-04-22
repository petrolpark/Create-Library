package com.petrolpark.core.scratch.client;

import javax.annotation.ParametersAreNonnullByDefault;

import com.petrolpark.Petrolpark;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;

@EventBusSubscriber(Dist.CLIENT)
@ParametersAreNonnullByDefault
public class ScratchTextureManager extends TextureAtlasHolder {

    private static ScratchTextureManager INSTANCE;

    private ScratchTextureManager(TextureManager textureManager) {
        super(textureManager, Petrolpark.asResource("textures/atlas/scratch.png"), Petrolpark.asResource("scratch"));
    };

    public static final ScratchTextureManager getInstance() {
        if (INSTANCE == null) INSTANCE = new ScratchTextureManager(Minecraft.getInstance().getTextureManager());
        return INSTANCE;
    };

    @Override
	public TextureAtlasSprite getSprite(ResourceLocation location) {
		return super.getSprite(location);
	};

    @SubscribeEvent
    public static final void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(getInstance());
    };
    
};
