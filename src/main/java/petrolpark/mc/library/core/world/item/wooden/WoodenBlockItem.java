package petrolpark.mc.library.core.world.item.wooden;

import javax.annotation.Nonnull;

import petrolpark.mc.library.registry.PetrolparkDataComponentTypes;
import petrolpark.mc.library.util.WoodHelper;

import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class WoodenBlockItem extends BlockItem {

    protected String itemTranslationKey = null;

    public WoodenBlockItem(Block block, Item.Properties properties) {
        super(block, properties.component(PetrolparkDataComponentTypes.WOOD, WoodHelper.OAK));
    };

    protected String getOrCreateItemTranslationKey() {
        if (itemTranslationKey == null) itemTranslationKey = Util.makeDescriptionId("item", BuiltInRegistries.ITEM.getKey(this));
        return itemTranslationKey;
    };

    @Override
    public Component getName(@Nonnull ItemStack stack) {
        return Component.translatable(getOrCreateItemTranslationKey(), WoodHelper.getName(stack.get(PetrolparkDataComponentTypes.WOOD)));
    };
    
};
