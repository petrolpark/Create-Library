package com.petrolpark.mixin.compat.create.accessor.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointHandler;

import net.minecraft.world.item.ItemStack;

@Mixin(ArmInteractionPointHandler.class)
public interface ArmInteractionPointHandlerAccessor {

    @Accessor("currentSelection")
    public static List<ArmInteractionPoint> getCurrentSelection() {
        throw new AssertionError();
    };
    
    @Accessor("currentItem")
    public static ItemStack getCurrentItem() {
        throw new AssertionError();
    };

    @Accessor("lastBlockPos")
    public static long getLastBlockPos() {
        throw new AssertionError();
    };
};
