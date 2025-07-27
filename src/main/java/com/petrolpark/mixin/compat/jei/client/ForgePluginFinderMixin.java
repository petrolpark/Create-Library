package com.petrolpark.mixin.compat.jei.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.petrolpark.compat.jei.PetrolparkCreateJEI;

import mezz.jei.api.IModPlugin;
import mezz.jei.neoforge.startup.ForgePluginFinder;

@Mixin(ForgePluginFinder.class)
public class ForgePluginFinderMixin {
    
    @WrapMethod(
        method = "Lmezz/jei/neoforge/startup/ForgePluginFinder;getModPlugins()Ljava/util/List;"
    )
    private static List<IModPlugin> wrapGetModPlugins(Operation<List<IModPlugin>> original) {
        List<IModPlugin> plugins = original.call();
        plugins.add(new PetrolparkCreateJEI());
        return plugins;
    };
};
