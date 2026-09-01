package petrolpark.mc.library.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.BeltInventory;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import petrolpark.mc.library.compat.create.core.world.item.transported.ISpecialBeltItem;
import petrolpark.mc.library.compat.create.core.world.item.transported.SpecialTransportedItemStack;

@Mixin(BeltInventory.class)
public abstract class BeltInventoryMixin {

    @Shadow
    BeltBlockEntity belt;
    
    @WrapMethod(
        method = "Lcom/simibubi/create/content/kinetics/belt/transport/BeltInventory;insert(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;)V",
        remap = false
    )
    public void petrolpark$makeDirectionalStack(TransportedItemStack stack, Operation<Void> original) {
        if (stack != null && !(stack instanceof SpecialTransportedItemStack) && stack.stack.getItem() instanceof ISpecialBeltItem specialBeltItem) {
            stack = specialBeltItem.makeTransportedItemStack(stack);
        };
        original.call(stack);
    };

    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "next",
            shift = Shift.BY,
            by = 5
        ),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void petrolpark$tickSpecialTransportedItemStacks(CallbackInfo ci, TransportedItemStack stackInFront, TransportedItemStack currentItem) {
        if (currentItem instanceof SpecialTransportedItemStack specialStack) specialStack.tick(belt.getLevel());
    };
};
