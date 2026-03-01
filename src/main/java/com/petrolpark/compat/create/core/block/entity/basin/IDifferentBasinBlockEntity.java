package com.petrolpark.compat.create.core.block.entity.basin;

import java.util.Optional;

import com.petrolpark.mixin.compat.create.accessor.BasinBlockEntityAccessor;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;

import net.minecraft.world.item.crafting.Recipe;

public interface IDifferentBasinBlockEntity {
    
    public boolean matchStaticFilters(Recipe<?> recipe);

    public default Optional<BasinOperatingBlockEntity> getBasinOperator() {
        return ((BasinBlockEntityAccessor)this).callGetOperator();
    };

    public default boolean haveContentsChanged() {
        return ((BasinBlockEntityAccessor)this).getContentsChanged();
    };
};
