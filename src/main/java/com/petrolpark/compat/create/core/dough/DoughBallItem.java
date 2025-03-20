package com.petrolpark.compat.create.core.dough;

import com.petrolpark.compat.create.core.item.directional.DirectionalTransportedItemStack;
import com.petrolpark.compat.create.core.item.directional.IDirectionalOnBelt;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.world.item.Item;

public class DoughBallItem extends Item implements IDirectionalOnBelt {

    public DoughBallItem(Properties properties) {
        super(properties);
    };

    @Override
    public DirectionalTransportedItemStack makeDirectionalTransportedItemStack(TransportedItemStack transportedItemStack) {
        return IDirectionalOnBelt.super.makeDirectionalTransportedItemStack(transportedItemStack);
    };
    
};
