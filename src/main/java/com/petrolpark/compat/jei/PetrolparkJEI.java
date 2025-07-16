package com.petrolpark.compat.jei;

import javax.annotation.Nonnull;

import com.petrolpark.Petrolpark;
import com.petrolpark.core.extendedinventory.ExtendedInventoryJeiGuiHandler;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class PetrolparkJEI implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return Petrolpark.asResource("jei");
    };

    @Override
	public void registerGuiHandlers(@Nonnull IGuiHandlerRegistration registration) {
        registration.addGlobalGuiHandler(new ExtendedInventoryJeiGuiHandler());
	};
    
};
