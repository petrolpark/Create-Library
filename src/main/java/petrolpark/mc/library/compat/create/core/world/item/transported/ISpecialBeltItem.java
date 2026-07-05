package petrolpark.mc.library.compat.create.core.world.item.transported;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.core.Direction;

public interface ISpecialBeltItem<STACK extends SpecialTransportedItemStack> {
    
    /**
     * This function should take the (non-directional) TransportedItemStack generated and turn it into a special one
     * @param transportedItemStack
     */
    public STACK makeTransportedItemStack(TransportedItemStack transportedItemStack);

    public default void onLaunchedByWeightedEjector(STACK stack, Direction launchDirection) {

    };
};