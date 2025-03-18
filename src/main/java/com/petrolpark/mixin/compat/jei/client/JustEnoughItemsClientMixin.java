package com.petrolpark.mixin.compat.jei.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.petrolpark.compat.jei.PetrolparkJEI;

import mezz.jei.api.IModPlugin;
import mezz.jei.common.network.IConnectionToServer;
import mezz.jei.gui.config.InternalKeyMappings;
import mezz.jei.neoforge.JustEnoughItemsClient;
import mezz.jei.neoforge.events.PermanentEventSubscriptions;
import mezz.jei.neoforge.network.NetworkHandler;

@Mixin(JustEnoughItemsClient.class)
public class JustEnoughItemsClientMixin {
    
    @Inject(
        method = "<init>",
        at = @At("RETURN"),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    public void inInit(
        NetworkHandler networkHandler,
        PermanentEventSubscriptions subscriptions,
        CallbackInfo ci,
        InternalKeyMappings keyMappings,
        IConnectionToServer serverConnection,
        List<IModPlugin> plugins
    ) {
        plugins.add(new PetrolparkJEI());
    };
};
