package petrolpark.mc.library.core.world.restaurant.serving;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import petrolpark.mc.library.core.world.restaurant.customer.ICustomer;

public interface IServingBlockEntity {
    
    public Stream<? extends IServingBlockEntity.Serving> streamServings();

    /**
     * @param simulate
     * @param stack Fulfills the {@link Serving#customer customer's} order
     * @return Remainder item stack
     */
    @Nullable
    public IServingBlockEntity.Serving serve(boolean simulate, ItemStack stack);

    public sealed interface Serving permits IServingBlockEntity.Serving.Satisfied, IServingBlockEntity.Serving.Unsatisfied {

        public ICustomer customer();

        public non-sealed interface Satisfied extends IServingBlockEntity.Serving {

            public boolean isStillSatisfied();

            public ItemStack serving();

            public boolean eat(int ticksElapsed, float eatingProgress);
        };

        public record Unsatisfied(ICustomer customer) implements IServingBlockEntity.Serving {};
    };
};
