package petrolpark.mc.library.core.world.item.decay.product;

import net.minecraft.world.item.ItemStack;

public class NoDecayProduct implements IDecayProduct {

    public static final NoDecayProduct INSTANCE = new NoDecayProduct();

    @Override
    public ItemStack get(ItemStack stack) {
        return stack;
    };

    @Override
    public DecayProductType getType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getType'");
    };
    
};
