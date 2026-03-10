package com.petrolpark.compat.create.core.dough;

import javax.annotation.Nullable;

import com.petrolpark.RequiresCreate;
import com.petrolpark.compat.create.core.item.directional.DirectionalTransportedItemStack;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.world.item.ItemStack;

@RequiresCreate
public class DoughTransportedItemStack extends DirectionalTransportedItemStack {

    @Nullable
    public final DoughData doughBall;

    public DoughTransportedItemStack(ItemStack stack) {
        this(stack, DoughData.get(stack));
    };

    protected DoughTransportedItemStack(ItemStack stack, DoughData doughBall) {
        super(stack);
        this.doughBall = doughBall;
    };

    @Override
    public TransportedItemStack copy() {
        return copy(this, s -> new DoughTransportedItemStack(s, doughBall));
    };
    
};
