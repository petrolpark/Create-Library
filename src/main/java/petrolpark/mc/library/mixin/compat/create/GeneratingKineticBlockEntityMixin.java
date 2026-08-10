package petrolpark.mc.library.mixin.compat.create;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import petrolpark.mc.library.compat.create.core.world.block.composite.CompositeKineticBlockEntity;
import petrolpark.mc.library.compat.create.core.world.block.entity.IKineticBlockEntityDuck;
import petrolpark.mc.library.compat.create.core.world.block.entity.IOverridableKineticBlockEntity;

@Mixin(GeneratingKineticBlockEntity.class)
public abstract class GeneratingKineticBlockEntityMixin extends KineticBlockEntity {
    
    public GeneratingKineticBlockEntityMixin(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        throw new AssertionError();
    };

    @WrapOperation(
        method = "setSource",
        at = @At(
            value = "INVOKE",
            target = "getBlockEntity"
        )
    )
    public BlockEntity petrolpark$potentiallyGetCompositeKineticBlockEntitySource(Level level, BlockPos pos, Operation<BlockEntity> original) {
        final BlockEntity be = original.call(level, pos);
        return be instanceof CompositeKineticBlockEntity ? IKineticBlockEntityDuck.getSource(this) : be;
    };

    @ModifyExpressionValue(
        method = "applyNewSpeed",
        at = @At(
            value = "INVOKE",
            target = "abs",
            ordinal = 0
        )
    )
    public float petrolpark$overrideSource(float original) {
        if (IOverridableKineticBlockEntity.isSourceOverridable(this)) return 0f;
        return original;
    };
};
