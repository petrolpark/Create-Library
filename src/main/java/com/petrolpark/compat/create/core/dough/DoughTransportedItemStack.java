package com.petrolpark.compat.create.core.dough;

import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.core.item.directional.DirectionalTransportedItemStack;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.world.item.ItemStack;

@RequiresCreate
public class DoughTransportedItemStack extends DirectionalTransportedItemStack {

    public final DoughBall doughBall;

    public DoughTransportedItemStack(ItemStack stack) {
        this(stack, DoughBall.get(stack));
    };

    protected DoughTransportedItemStack(ItemStack stack, DoughBall doughBall) {
        super(stack);
        this.doughBall = doughBall;
    };

    @Override
    public TransportedItemStack copy() {
        return copy(this, s -> new DoughTransportedItemStack(s, doughBall));
    };
    
};
