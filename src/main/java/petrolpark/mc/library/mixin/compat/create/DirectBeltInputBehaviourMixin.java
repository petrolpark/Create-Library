package petrolpark.mc.library.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import petrolpark.mc.library.compat.create.core.world.item.transported.ISpecialBeltItem;
import petrolpark.mc.library.compat.create.core.world.item.transported.SpecialTransportedItemStack;

@Mixin(DirectBeltInputBehaviour.class)
public class DirectBeltInputBehaviourMixin {
    
    @WrapMethod(
        method = "Lcom/simibubi/create/content/kinetics/belt/behaviour/DirectBeltInputBehaviour;handleInsertion(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Lnet/minecraft/core/Direction;Z)Lnet/minecraft/world/item/ItemStack;",
        remap = false
    )
    public ItemStack petrolpark$insertDirectional(TransportedItemStack stack, Direction side, boolean simulate, Operation<ItemStack> original) {
        if (!(stack instanceof SpecialTransportedItemStack) && stack.stack.getItem() instanceof ISpecialBeltItem specialBeltItem) { // If not already cast to a Directional transported stack
           stack = specialBeltItem.makeTransportedItemStack(stack);
        };
        return original.call(stack, side, simulate);
    };
};
