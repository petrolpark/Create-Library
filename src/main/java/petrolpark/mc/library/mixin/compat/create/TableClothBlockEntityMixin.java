package petrolpark.mc.library.mixin.compat.create;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.logistics.tableCloth.TableClothBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import petrolpark.mc.library.core.world.restaurant.serving.IServingBlockEntity;
import petrolpark.mc.library.core.world.restaurant.serving.TableClothServing;

@Mixin(TableClothBlockEntity.class)
public abstract class TableClothBlockEntityMixin extends SmartBlockEntity implements IServingBlockEntity {

    public TableClothBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        throw new AssertionError();
    };
    
    @Inject(
        method = "use",
        at = @At("TAIL")
    )
    public void petrolpark$notifyCustomers(Player player, BlockHitResult ray, CallbackInfoReturnable<ItemInteractionResult> cir) {
        streamServings().map(IServingBlockEntity.Serving::customer)
            .forEach(customer -> customer.notifyOfServing(this));
    };

    @Override
    public Stream<? extends IServingBlockEntity.Serving> streamServings() {
        return TableClothServing.stream((TableClothBlockEntity)(SmartBlockEntity)this);
    };

    @Override
    @Nullable
    public IServingBlockEntity.Serving serve(boolean simulate, ItemStack stack) {
        return TableClothServing.serve((TableClothBlockEntity)(SmartBlockEntity)this, simulate, stack);
    };
    
};
