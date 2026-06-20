package com.petrolpark.compat.jei.subtypeInterpreter;

import java.util.Optional;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

import com.petrolpark.compat.create.core.world.dough.DoughData;
import com.petrolpark.compat.create.core.world.dough.IDough;
import com.petrolpark.compat.create.registry.PetrolparkCreateDataComponentTypes;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.world.item.ItemStack;

@ParametersAreNonnullByDefault
public class DoughItemSubtypeInterpreter implements ISubtypeInterpreter<ItemStack> {

    public static final DoughItemSubtypeInterpreter INSTANCE = new DoughItemSubtypeInterpreter();

    @Override
    public @Nullable IDough getSubtypeData(ItemStack ingredient, UidContext context) {
        final DoughData data = ingredient.get(PetrolparkCreateDataComponentTypes.DOUGH);
        return data == null ? null : data.dough();
    };

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
        return ingredient.getDescriptionId() + ".dough." + Optional.ofNullable(getSubtypeData(ingredient, context)).map(IDough::uniqueString).orElse("none");
    };
    
};
