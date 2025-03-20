package com.petrolpark.compat.create.core.block.multi;

import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

public abstract class WrappedMultiSidePartBehaviour<M extends IMulti<? super M>> extends MultiSidePartBehaviour<M> implements IWrappedMultiPartBehaviour<M> {

    public WrappedMultiSidePartBehaviour(SmartBlockEntity be) {
        super(be);
    };

    @Override
    public void multiDisassembled() {
        getWorld().setBlockAndUpdate(getPos(), getWrappedBlockState());
    };

    @Override
    public void transform(StructureTransform transform) {
        super.transform(transform);
        transformWrappedBlockState(transform);
    };

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        setWrappedBlockState(NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), nbt));
    };

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        NbtUtils.writeBlockState(getWrappedBlockState());
    };
    
};
