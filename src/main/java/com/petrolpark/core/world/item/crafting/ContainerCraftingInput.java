package com.petrolpark.core.world.item.crafting;

import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.crafting.CraftingInput;

public class ContainerCraftingInput extends CraftingInput {

    public final TransientCraftingContainer container;

    public ContainerCraftingInput(TransientCraftingContainer container, CraftingInput craftingInput) {
        super(craftingInput.width(), craftingInput.height(), craftingInput.items());
        this.container = container;
    };
    
};
