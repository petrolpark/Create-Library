package petrolpark.mc.library.compat.create.core.world.item.transported;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Rotation;
import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;

@RequiresCreate
public interface IDirectionalBeltItem<STACK extends DirectionalTransportedItemStack> extends ISpecialBeltItem<STACK> {

    /**
     * Get the rotation an Item Stack should have when placed on a Belt, Depot, etc.
     * @param stack This may be mutated in this method
     * @return A rotation from north. May be null, in which case it will be ignored
     */
    @Nullable
    public default Rotation rotationForPlacement(ItemStack stack) {
        return stack.getOrDefault(PetrolparkDataComponentTypes.ROTATION_WHILE_FLYING, Rotation.NONE);
    };

    /**
     * This function is enacted on Items just before they are thrown by a Weighted Ejector.
     * @param stack
     * @param launchDirection
     */
    @Override
    public default void onLaunchedByWeightedEjector(STACK stack, Direction launchDirection) {
        stack.stack.set(PetrolparkDataComponentTypes.ROTATION_WHILE_FLYING, stack.rotation);
    };
};
