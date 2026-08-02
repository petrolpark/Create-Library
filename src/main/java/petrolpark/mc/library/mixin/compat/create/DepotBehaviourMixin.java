package petrolpark.mc.library.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.world.item.ItemStack;
import petrolpark.mc.library.compat.create.core.world.item.transported.DirectionalTransportedItemStack;
import petrolpark.mc.library.compat.create.core.world.item.transported.ISpecialBeltItem;
import petrolpark.mc.library.compat.create.core.world.item.transported.SpecialTransportedItemStack;

@Mixin(value = DepotBehaviour.class, remap = false)
public abstract class DepotBehaviourMixin extends BlockEntityBehaviour {

    @Shadow
    TransportedItemStack heldItem;
    
    public DepotBehaviourMixin(SmartBlockEntity be) {
        super(be);
        throw new AssertionError();
    };

    @WrapMethod(
        method = "insert(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;Z)Lnet/minecraft/world/item/ItemStack;",
        remap = false
    )
    public ItemStack petrolpark$insertSpecialTransportedItemStack(TransportedItemStack heldItem, boolean simulate, Operation<ItemStack> original) {
        if (heldItem != null && !(heldItem instanceof SpecialTransportedItemStack) && heldItem.stack.getItem() instanceof ISpecialBeltItem specialBeltItem) {
            heldItem = specialBeltItem.makeTransportedItemStack(heldItem);
        };
        return original.call(heldItem, simulate);
    };

    @WrapMethod(
        method = "setHeldItem"
    )
    public void petrolpark$setSpecialTransportedItemStack(TransportedItemStack newItem, Operation<Void> original) {
        if (newItem != null && !(newItem instanceof SpecialTransportedItemStack) && newItem.stack.getItem() instanceof ISpecialBeltItem specialBeltItem) {
            newItem = specialBeltItem.makeTransportedItemStack(newItem);
            if (this.heldItem != null && this.heldItem instanceof DirectionalTransportedItemStack directionalExistingItem && newItem instanceof DirectionalTransportedItemStack directionalNewItemStack) {
                directionalNewItemStack.setRotation(directionalExistingItem.getRotation());
            };
        };
        original.call(newItem);
    };

    @Inject(
        method = "Lcom/simibubi/create/content/logistics/depot/DepotBehaviour;tick(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;)Z",
        at = @At("RETURN"),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION,
        remap = false
    )
    public void petrolpark$refreshDirectionalStackAngle(TransportedItemStack heldItem, CallbackInfoReturnable<Boolean> ci, float diff) {
        if (heldItem instanceof DirectionalTransportedItemStack directionalStack) directionalStack.refreshAngle();
    };

    @Inject(
        method = "tick(Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;)Z",
        at = @At("HEAD")
    )
    public void petrolpark$tickSpecialTransportedItemStacks(TransportedItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack instanceof SpecialTransportedItemStack specialStack) specialStack.tick(getWorld());
    };

    @Accessor("heldItem")
    public abstract TransportedItemStack getHeldItem();
};
