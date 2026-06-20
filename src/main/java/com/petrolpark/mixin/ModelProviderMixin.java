package com.petrolpark.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ModelProvider;

@Mixin(ModelProvider.class)
public class ModelProviderMixin {
    
    @ModifyExpressionValue(
        method = "extendWithFolder",
        at = @At(
            value = "INVOKE",
            target = "Ljava/lang/String;contains(Ljava/lang/CharSequence;)Z"
        )
    )
    public boolean petrolpark$prependBlockFolderToSharedBlockModels(boolean original, ResourceLocation rl) {
        return original && !rl.getPath().startsWith("shared/");
    };
};
