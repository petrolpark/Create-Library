package petrolpark.mc.library.compat.create.core.world.block.entity.basin;

import java.util.Optional;

import petrolpark.mc.library.mixin.compat.create.accessor.BasinBlockEntityAccessor;
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
