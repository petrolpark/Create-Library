package com.petrolpark.compat.create.block.multi;

import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

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
    @SuppressWarnings("deprecation")
    public void read(CompoundTag nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        setWrappedBlockState(NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), nbt));
    };

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        super.write(nbt, clientPacket);
        NbtUtils.writeBlockState(getWrappedBlockState());
    };
    
};
