package com.petrolpark.core.client.texts;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ServerTextsManager {
    
    protected static final Map<ServerPlayer, WeakReference<Consumer<String>>> awaitingTexts = new HashMap<>();

    public static final void getText(ResourceLocation id, ServerPlayer serverPlayer, Consumer<String> consumer) {
        awaitingTexts.put(serverPlayer, new WeakReference<>(consumer));
        CatnipServices.NETWORK.sendToClient(serverPlayer, new RequestTextPacket(id));
    };
};
