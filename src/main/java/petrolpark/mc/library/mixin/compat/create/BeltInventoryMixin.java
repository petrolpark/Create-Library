package petrolpark.mc.library.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import petrolpark.mc.library.compat.create.core.world.item.directional.DirectionalTransportedItemStack;
import petrolpark.mc.library.compat.create.core.world.item.directional.IDirectionalOnBelt;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

@Mixin(BeltInventory.class)
public abstract class BeltInventoryMixin {
    
    @WrapMethod(
        method = "Lcom/simibubi/create/content/kinetics/belt/transport/BeltInventory;insert(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;)V",
        remap = false
    )
    public void petrolpark$makeDirectionalStack(TransportedItemStack stack, Operation<Void> original) {
        if (!(stack instanceof DirectionalTransportedItemStack) && stack.stack.getItem() instanceof IDirectionalOnBelt directionalItem) {
            stack = directionalItem.makeDirectionalTransportedItemStack(stack);
        };
        original.call(stack);
    };
};
