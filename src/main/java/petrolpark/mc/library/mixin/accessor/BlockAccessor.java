package petrolpark.mc.library.mixin.accessor;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Block;

@Mixin(Block.class)
public interface BlockAccessor {
    
    @Invoker(
        value = "beginCapturingDrops"
    )
    public static void callBeginCapturingDrops() {
        throw new AssertionError();
    };

    @Invoker(
        value = "stopCapturingDrops"
    )
    public static List<ItemEntity> callStopCapturingDrops() {
        throw new AssertionError();
    };
};
