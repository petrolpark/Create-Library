package com.petrolpark.mixin;

import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.core.block.entity.IShulkerBoxBlockEntityDuck;
import com.petrolpark.core.contamination.Contaminant;
import com.petrolpark.core.contamination.GenericContamination;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
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
    public void contaminateAll(Stream<Holder<Contaminant>> contaminants) {
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
    public void inLoadFromTag(CompoundTag tag, HolderLookup.Provider levelRegistry, CallbackInfo ci) {
        contamination = new GenericContamination().readNBT(tag.get("Contamination"), levelRegistry);
    };

    @Inject(
        method = "saveAdditional",
        at = @At("HEAD")
    )
    public void inSaveAdditional(CompoundTag tag, HolderLookup.Provider levelRegistry, CallbackInfo ci) {
        tag.put("Contamination", contamination.writeNBT(levelRegistry));
    };
    
};
