package petrolpark.mc.library.mixin.compat.create;

import java.util.List;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import petrolpark.mc.library.PetrolparkTags;
import petrolpark.mc.library.compat.create.core.world.block.composite.CompositeKineticBlockEntity;
import petrolpark.mc.library.compat.create.core.world.block.entity.IKineticBlockEntityDuck;
import petrolpark.mc.library.compat.create.core.world.block.entity.IOverridableKineticBlockEntity;
import petrolpark.mc.library.compat.create.core.world.block.entity.behaviour.FlagPoleBehaviour;

@Mixin(KineticBlockEntity.class)
public abstract class KineticBlockEntityMixin extends SmartBlockEntity implements IKineticBlockEntityDuck {

    @Unique
    @Nullable
    public Integer sourceIndex;

    @Unique
    protected boolean sourceOverridable;

    @Shadow
    public abstract void setNetwork(@Nullable Long networkIn);

    @Shadow
    protected abstract void copySequenceContextFrom(KineticBlockEntity sourceBE);
    
    public KineticBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        throw new AssertionError();
    };

    @Override
    @Nullable
    public Integer getSourceIndex() {
        return sourceIndex;
    };

    @Override
    public void setSourceIndex(@Nullable Integer sourceIndex) {
        this.sourceIndex = sourceIndex;
    };

    @Override
    public boolean isSourceOverridable() {
        return sourceOverridable;
    };

    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;addBehaviours(Ljava/util/List;)V",
        at = @At("HEAD"),
        remap = false
    )
    public void petrolpark$addFlagsBehaviour(List<BlockEntityBehaviour> behaviours, CallbackInfo ci) {
        if (PetrolparkTags.Items.FLAGGABLE.matches(getBlockState().getBlock().asItem())) behaviours.add(new FlagPoleBehaviour(this));
    };

    @Inject(
        method = "Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;validateKinetics()V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/base/KineticBlockEntity;removeSource()V",
            ordinal = 1
        ),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true
    )
    public void petrolpark$allowCompositeBlockEntitiesAsSourcesInValidation(CallbackInfo ci, BlockEntity blockEntity, KineticBlockEntity sourceBE) {
        if (blockEntity instanceof CompositeKineticBlockEntity) ci.cancel();
    };

    /**
     * If this being called from {@link RotationPropagator}, then {@link KineticBlockEntityMixin#setSourceIndex(Integer)} will have just been called.
     */
    @ModifyExpressionValue(
        method = "setSource",
        at = @At(
            value = "INVOKE",
            target = "getBlockEntity"
        )
    )
    public BlockEntity petrolpark$setCompositeKineticBlockEntityPartSource(BlockEntity original) {
        final Integer sourceIndex = this.sourceIndex;
        return (
            original instanceof CompositeKineticBlockEntity composite &&
            sourceIndex != null &&
            sourceIndex >= 0 &&
            sourceIndex < composite.getParts().size()
        )
            ? composite.getParts().get(sourceIndex) 
            : original;
    };

    @Inject(
        method = "setSource",
        at = @At("TAIL"),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void petrolpark$setSourceOverridable(BlockPos source, CallbackInfo ci, BlockEntity be, KineticBlockEntity sourceBE) {
        sourceOverridable = IOverridableKineticBlockEntity.isSourceOverridable(sourceBE);
    };

    @Inject(
        method = "removeSource",
        at = @At("HEAD")
    )
    public void petrolpark$removeSourceIndex(CallbackInfo ci) {
        sourceIndex = null;
    };

    @Inject(
        method = "clearKineticInformation",
        at = @At("HEAD")
    )
    public void petrolpark$clearSourceIndexInformation(CallbackInfo ci) {
        sourceIndex = null;
    };

    @Inject(
        method = "read",
        at = @At(
            value = "INVOKE",
            target = "readBlockPos"
        )
    )
    protected void petrolpark$readSourceIndex(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (compound.contains("SourceIndex", Tag.TAG_INT)) sourceIndex = compound.getInt("SourceIndex");
        else sourceIndex = null;
        if (compound.contains("SourceOverridable", Tag.TAG_BYTE)) sourceOverridable = true;
    };

    @Inject(
        method = "write",
        at = @At(
            value = "INVOKE",
            target = "writeBlockPos"
        )
    )
    protected void petrolpark$writeSourceIndex(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        if (sourceIndex != null) compound.putInt("SourceIndex", sourceIndex);
        if (sourceOverridable) compound.putBoolean("SourceOverridable", true);
    };
};
