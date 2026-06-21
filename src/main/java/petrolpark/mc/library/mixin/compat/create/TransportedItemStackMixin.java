package petrolpark.mc.library.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.compat.create.core.world.item.directional.DirectionalTransportedItemStack;
import petrolpark.mc.library.compat.create.core.world.item.directional.IDirectionalOnBelt;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Rotation;

@RequiresCreate
@Mixin(TransportedItemStack.class)
public class TransportedItemStackMixin {
    
    @WrapMethod(
        method = "Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;read(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)Lcom/simibubi/create/content/kinetics/belt/transport/TransportedItemStack;",
        remap = false
    )
    private static TransportedItemStack petrolpark$readDirectional(CompoundTag nbt, HolderLookup.Provider registries, Operation<TransportedItemStack> original) {
        TransportedItemStack stack = original.call(nbt, registries);
        if (stack.stack.getItem() instanceof IDirectionalOnBelt directionalItem) {
            DirectionalTransportedItemStack directionalStack = directionalItem.makeDirectionalTransportedItemStack(stack);
            if (nbt.contains("Rotation", Tag.TAG_INT)) {
                directionalStack.setRotation(Rotation.values()[nbt.getInt("Rotation")]);
            };
            return directionalStack;
        };
        return stack;
    };
};
