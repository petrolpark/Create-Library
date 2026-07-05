package petrolpark.mc.library.compat.create.core.world.dough;

import javax.annotation.Nonnull;

import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import petrolpark.mc.library.compat.create.core.world.item.transported.IDirectionalBeltItem;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateDataComponentTypes;
import petrolpark.mc.library.core.world.block.IPickUpPutDownBlock;

public class DoughItem extends BlockItem implements IDirectionalBeltItem<DoughTransportedItemStack> {

    public DoughItem(Block block, Item.Properties properties) {
        super(block, properties);
    };

    @Override
    public InteractionResult place(@Nonnull BlockPlaceContext context) {
        return IPickUpPutDownBlock.removeItemFromInventory(context, super.place(context));
    };

    @Override
    @SuppressWarnings("null")
    public Component getName(@Nonnull ItemStack stack) {
        if (stack.has(PetrolparkCreateDataComponentTypes.DOUGH)) return stack.get(PetrolparkCreateDataComponentTypes.DOUGH).dough().name();
        return super.getName(stack);
    }

    @Override
    public DoughTransportedItemStack makeTransportedItemStack(TransportedItemStack transportedItemStack) {
        return DoughTransportedItemStack.copyFully(transportedItemStack, DoughTransportedItemStack::new);
    };

    
    
};
