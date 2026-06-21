package petrolpark.mc.library.mixin.compat.create.accessor;

import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;

import net.createmod.catnip.data.IntAttached;
import net.minecraft.world.item.ItemStack;

@Mixin(BasinBlockEntity.class)
public interface BasinBlockEntityAccessor {

    @Invoker
    public Optional<BasinOperatingBlockEntity> callGetOperator();

    @Accessor("contentsChanged")
    public boolean getContentsChanged();
    
    @Accessor("visualizedOutputItems")
    public List<IntAttached<ItemStack>> getVisualziedOutputItems();
};
