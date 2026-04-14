package com.petrolpark.core.contamination;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.petrolpark.PetrolparkDataMapTypes;
import com.petrolpark.PetrolparkTags;
import com.petrolpark.core.contamination.Contaminable.GenericContaminable;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class Contaminables {
  
    protected static final List<Contaminable<?, ?>> CONTAMINABLES = new ArrayList<>();

    public static void register(Contaminable<?, ?> contaminable) {
        CONTAMINABLES.add(contaminable);
    };

    public static Stream<Contaminable<?, ?>> streamContaminables() {
        return CONTAMINABLES.stream();
    };

    public static final GenericContaminable GENERIC = new GenericContaminable();

    public static final GenericContaminable NOT = new GenericContaminable() {

        @Override
        public IncontaminableContamination getContamination(Object stack) {
            return IncontaminableContamination.INSTANCE;
        };
    }; 

    public static final Contaminable<Item, ItemStack> ITEM = new BuiltInRegistryContaminable<>(BuiltInRegistries.ITEM, PetrolparkDataMapTypes.ITEM_INTRINSIC_CONTAMINANTS, PetrolparkDataMapTypes.ITEM_SHOWN_IF_ABSENT_CONTAMINANTS) {
        
        @Override
        public boolean isContaminable(Item object) {
            return PetrolparkTags.Items.CONTAMINABLE.matches(object);
        };

        @Override
        public boolean isContaminableStack(ItemStack stack) {
            return !stack.isEmpty() && isContaminable(stack.getItem());
        };

        @Override
        public ItemContamination getContamination(Object stack) {
            if (stack instanceof ItemStack itemStack && isContaminableStack(itemStack)) return new ItemContamination(itemStack);
            return null;
        };
        
    };

    public static final Contaminable<Fluid, FluidStack> FLUID = new BuiltInRegistryContaminable<>(BuiltInRegistries.FLUID, PetrolparkDataMapTypes.FLUID_INTRINSIC_CONTAMINANTS, PetrolparkDataMapTypes.FLUID_SHOWN_IF_ABSENT_CONTAMINANTS) {

        
        @Override
        public boolean isContaminable(Fluid object) {
            return PetrolparkTags.Fluids.CONTAMINABLE.matches(object);
        };

        @Override
        public boolean isContaminableStack(FluidStack stack) {
            return !stack.isEmpty() && isContaminable(stack.getFluid());
        };

        @Override
        public FluidContamination getContamination(Object stack) {
            if (stack instanceof FluidStack fluidStack && isContaminableStack(fluidStack)) return new FluidContamination(fluidStack);
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
