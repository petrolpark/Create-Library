package petrolpark.mc.library.core.world.item.decay;

import petrolpark.mc.library.registry.PetrolparkLootItemFunctions;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class StartDecayLootItemFunction implements LootItemFunction {

    @Override
    public ItemStack apply(ItemStack stack, LootContext context) {
        ItemDecay.startDecay(stack);
        return ItemDecay.checkDecay(stack);
    };

    @Override
    public LootItemFunctionType<? extends LootItemFunction> getType() {
        return PetrolparkLootItemFunctions.START_DECAY.get();
    };
    
};
