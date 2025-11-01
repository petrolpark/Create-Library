package com.petrolpark.mixin;

import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.block.entity.IShulkerBoxBlockEntityDuck;
import com.petrolpark.contamination.Contaminant;
import com.petrolpark.contamination.GenericContamination;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class ShulkerBoxBlockEntityMixin extends RandomizableContainerBlockEntity implements IShulkerBoxBlockEntityDuck {

    @Unique
    private GenericContamination contamination;

    protected ShulkerBoxBlockEntityMixin(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        throw new AssertionError();
    };

    @Override
    public GenericContamination getContamination() {
        return contamination;
    };

    @Override
    public void contaminateAll(Stream<Contaminant> contaminants) {
        contamination.contaminateAll(contaminants);
    };

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    public void inInit(CallbackInfo ci) {
        contamination = new GenericContamination();
    };

    @Inject(
        method = "loadFromTag",
        at = @At("HEAD")
    )
    public void inLoadFromTag(CompoundTag tag, CallbackInfo ci) {
        contamination = new GenericContamination(tag.getList("Contamination", Tag.TAG_STRING));
    };

    @Inject(
        method = "saveAdditional",
        at = @At("HEAD")
    )
    public void inSaveAdditional(CompoundTag tag, CallbackInfo ci) {
        tag.put("Contamination", contamination.writeNBT(getLevel().registryAccess()));
    };
    
};
