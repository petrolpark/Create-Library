package petrolpark.mc.library.core.flags;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import petrolpark.mc.library.PetrolparkTags;
import petrolpark.mc.library.core.flags.Flaggable.GenericFlaggable;
import petrolpark.mc.library.registry.PetrolparkDataMapTypes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class Flaggables {
  
    protected static final List<Flaggable<?, ?>> FLAGGABLES = new ArrayList<>();

    public static void register(Flaggable<?, ?> flaggable) {
        FLAGGABLES.add(flaggable);
    };

    public static Stream<Flaggable<?, ?>> streamFlaggables() {
        return FLAGGABLES.stream();
    };

    public static final GenericFlaggable GENERIC = new GenericFlaggable();

    public static final GenericFlaggable NOT = new GenericFlaggable() {

        @Override
        public UnflaggableFlagPole getFlagPole(Object stack) {
            return UnflaggableFlagPole.INSTANCE;
        };
    }; 

    public static final Flaggable<Item, ItemStack> ITEM = new BuiltInRegistryFlaggable<>(BuiltInRegistries.ITEM, PetrolparkDataMapTypes.ITEM_INTRINSIC_FLAGS, PetrolparkDataMapTypes.ITEM_SHOWN_IF_ABSENT_FLAGS) {
        
        @Override
        public boolean isFlaggable(Item object) {
            return PetrolparkTags.Items.FLAGGABLE.matches(object);
        };

        @Override
        public boolean isFlaggableStack(ItemStack stack) {
            return !stack.isEmpty() && isFlaggable(stack.getItem());
        };

        @Override
        public ItemFlagPole getFlagPole(Object stack) {
            if (stack instanceof ItemStack itemStack && isFlaggableStack(itemStack)) return new ItemFlagPole(itemStack);
            return null;
        };
        
    };

    public static final Flaggable<Fluid, FluidStack> FLUID = new BuiltInRegistryFlaggable<>(BuiltInRegistries.FLUID, PetrolparkDataMapTypes.FLUID_INTRINSIC_FLAGS, PetrolparkDataMapTypes.FLUID_SHOWN_IF_ABSENT_FLAGS) {

        
        @Override
        public boolean isFlaggable(Fluid object) {
            return PetrolparkTags.Fluids.FLAGGABLE.matches(object);
        };

        @Override
        public boolean isFlaggableStack(FluidStack stack) {
            return !stack.isEmpty() && isFlaggable(stack.getFluid());
        };

        @Override
        public FluidFlagPole getFlagPole(Object stack) {
            if (stack instanceof FluidStack fluidStack && isFlaggableStack(fluidStack)) return new FluidFlagPole(fluidStack);
            return null;
        };
        
    };

    static {
        register(GENERIC);
        register(NOT);
        register(ITEM);
        register(FLUID);
    };
};
