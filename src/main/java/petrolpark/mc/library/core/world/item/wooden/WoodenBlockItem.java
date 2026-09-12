package petrolpark.mc.library.core.world.item.wooden;

import javax.annotation.Nonnull;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;
import petrolpark.mc.library.util.WoodHelper;

public class WoodenBlockItem extends BlockItem {

    protected String itemTranslationKey = null;

    public WoodenBlockItem(Block block, Item.Properties properties) {
        super(block, properties.component(PetrolparkDataComponentTypes.WOOD, WoodHelper.OAK));
    };
    
    @Override
    public String getDescriptionId() {
        return getOrCreateDescriptionId();
    };

    @Override
    public Component getName(@Nonnull ItemStack stack) {
        return Component.translatable(getDescriptionId(), WoodHelper.getName(stack.get(PetrolparkDataComponentTypes.WOOD)));
    };
    
};
