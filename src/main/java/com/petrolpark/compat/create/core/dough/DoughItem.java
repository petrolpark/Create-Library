package com.petrolpark.compat.create.core.dough;

import javax.annotation.Nonnull;

import com.petrolpark.compat.create.PetrolparkCreateDataComponentTypes;
import com.petrolpark.compat.create.core.item.directional.DirectionalTransportedItemStack;
import com.petrolpark.compat.create.core.item.directional.IDirectionalOnBelt;
import com.petrolpark.core.world.block.IPickUpPutDownBlock;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

public class DoughItem extends BlockItem implements IDirectionalOnBelt {

    public DoughItem(Block block, Item.Properties properties) {
        super(block, properties);
    };

    @Override
    public InteractionResult place(@Nonnull BlockPlaceContext context) {
        return IPickUpPutDownBlock.removeItemFromInventory(context, super.place(context));
    };

    @Override
    @SuppressWarnings("null")
    public Component getName(@Nonnull ItemStack stack) {
        if (stack.has(PetrolparkCreateDataComponentTypes.DOUGH)) return stack.get(PetrolparkCreateDataComponentTypes.DOUGH).dough().name();
        return super.getName(stack);
    };

    @Override
    public DirectionalTransportedItemStack makeDirectionalTransportedItemStack(TransportedItemStack transportedItemStack) {
        return IDirectionalOnBelt.super.makeDirectionalTransportedItemStack(transportedItemStack);
    };
    
};
