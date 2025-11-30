package com.petrolpark.core.inventory.extended;

import java.util.Collection;

import com.petrolpark.PetrolparkClient;

import mezz.jei.api.gui.handlers.IGlobalGuiHandler;
import net.minecraft.client.renderer.Rect2i;

public class ExtendedInventoryJeiGuiHandler implements IGlobalGuiHandler {
    
    @Override
    public Collection<Rect2i> getGuiExtraAreas() {
        return PetrolparkClient.EXTENDED_INVENTORY_HANDLER.getGuiExtraAreas();
    };
};
