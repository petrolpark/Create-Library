package petrolpark.mc.library.core.world.restaurant.serving;

import java.util.stream.Stream;

import net.minecraft.world.item.ItemStack;

public interface IMobServingBlockEntity {
    
    public Stream<MobServing> streamServings();

    public interface MobServing {

        /**
         * Remove the served ItemStack
         */
        public ItemStack serve(boolean simulate);
    };
};
