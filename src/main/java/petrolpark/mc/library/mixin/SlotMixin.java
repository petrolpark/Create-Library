package petrolpark.mc.library.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import petrolpark.mc.library.core.world.item.decay.IApplyDecayRecipe;
import petrolpark.mc.library.shared.registry.SharedRecipeTypes;
import petrolpark.mc.library.shared.world.item.crafting.ageing.AgeingContainerWrapper;
import petrolpark.mc.library.shared.world.item.crafting.ageing.AgeingRecipe;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * If an Item is removed from a Barrel, it should stop {@link AgeingRecipe ageing}. This mixin is to ensure that happens even when the Item is quick-swapped to a hotbar slot.
 */
@Mixin(Slot.class)
public abstract class SlotMixin {
    
    @Shadow
    public final Container container;

    public SlotMixin(Container container) {
        this.container = container;
        throw new AssertionError();
    };

    @WrapMethod(
        method = "Lnet/minecraft/world/inventory/Slot;onTake(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)V"
    )
    public void petrolpark$removeAppliedDecay(Player player, ItemStack stack, Operation<Void> operation) {
        if (AgeingContainerWrapper.isAgeingContainer(container)) IApplyDecayRecipe.withAppliedDecayRemoved(player.level(), SharedRecipeTypes.AGEING.get(), stack);
        operation.call(player, stack);
    };
};
