package com.petrolpark.compat.create.block.multi;

import java.util.Optional;

import com.petrolpark.util.NBTHelper;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;

public abstract class MultiPartBehaviour<M extends IMulti<? super M>> extends MultiBehaviour<M> {

    protected Optional<BlockPos> relativeControllerPos;

    public MultiPartBehaviour(SmartBlockEntity be) {
        super(be);
    };

    void setRelativeControllerPos(BlockPos pos) {
        this.relativeControllerPos = Optional.of(pos);
        blockEntity.notifyUpdate();
    };

    @Override
    public final Optional<IMulti<? extends M>> getOptionalMulti() {
        return relativeControllerPos.map(getPos()::offset).map(pos -> BlockEntityBehaviour.get(getWorld(), pos, getType())).filter(MultiBehaviour::isMultiController).flatMap(MultiBehaviour::getOptionalMulti);
    };

    @Override
    public final boolean isMultiController() {
        return false;
    };

    @Override
    public void multiDisassembled() {};

    @Override
    public void transform(StructureTransform transform) {
        relativeControllerPos = relativeControllerPos.map(transform::apply); //TODO check this works
    };

    public static final String CONTROLLER_POS_TAG_KEY = "ControllerPos";

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        if (nbt.contains(CONTROLLER_POS_TAG_KEY, Tag.TAG_COMPOUND)) relativeControllerPos = Optional.of(NbtUtils.readBlockPos(nbt.getCompound(CONTROLLER_POS_TAG_KEY)));
        if (getOptionalMulti().isEmpty()) relativeControllerPos = Optional.empty(); // Remove reference if there is no Multi Controller where there should be
    };

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        super.write(nbt, clientPacket);
        relativeControllerPos.map(NbtUtils::writeBlockPos).ifPresent(NBTHelper.writeAt(nbt, CONTROLLER_POS_TAG_KEY));
    };
    
};
