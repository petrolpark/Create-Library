package petrolpark.mc.library.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.content.logistics.depot.EjectorBlockEntity;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import petrolpark.mc.library.compat.create.core.world.item.transported.ISpecialBeltItem;
import petrolpark.mc.library.compat.create.core.world.item.transported.SpecialTransportedItemStack;
import petrolpark.mc.library.mixin.compat.create.accessor.DepotBehaviourAccessor;

@Mixin(value = EjectorBlockEntity.class, remap = false)
public abstract class EjectorBlockEntityMixin {
    
    @Inject(
        method = "launchItems()V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/logistics/depot/EjectorBlockEntity;addToLaunchedItems(Lnet/minecraft/world/item/ItemStack;)Z",
            ordinal = 0
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT,
        remap = false
    )
    @SuppressWarnings("unchecked")
    public void petrolpark$launchDirectional(CallbackInfo ci, ItemStack heldItemStack, Direction funnelFacing) {
        TransportedItemStack stack = ((DepotBehaviourAccessor)getDepotBehaviour()).getHeldItem();
        if (stack instanceof SpecialTransportedItemStack specialStack && specialStack.stack.getItem() instanceof ISpecialBeltItem item) item.onLaunchedByWeightedEjector(specialStack, funnelFacing.getOpposite());
    };

    @Accessor("depotBehaviour")
    public abstract DepotBehaviour getDepotBehaviour();
};
