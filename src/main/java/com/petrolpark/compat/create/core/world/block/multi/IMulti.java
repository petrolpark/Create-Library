package com.petrolpark.compat.create.core.world.block.multi;

import java.util.Optional;

import com.petrolpark.util.BlockHelper;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;

public interface IMulti<M extends IMulti<? super M>> extends INBTSerializable<CompoundTag> {
    
    public IMultiType<? extends M> getMultiType();

    public Level getLevel();

    void setLevel(Level level);

    void setMultiControllerAbsolutePos(BlockPos pos);

    public BlockPos getMultiControllerAbsolutePos();

    void setMultiAbsoluteLowerOuterCornerPos(BlockPos pos);

    public BlockPos getMultiAbsoluteLowerOuterCornerPos();

    void setMultiAbsoluteUpperOuterCornerPos(BlockPos pos);

    public BlockPos getMultiAbsoluteUpperOuterCornerPos();

    public default BlockPos getMultiAbsoluteLowerInnerCornerPos() {
        return getMultiAbsoluteLowerOuterCornerPos().offset(BlockHelper.UNIT);
    };

    public default BlockPos getMultiAbsoluteUpperInnerCornerPos() {
        return getMultiAbsoluteUpperOuterCornerPos().subtract(BlockHelper.UNIT);
    };

    public void markMultiDisassembling();

    public boolean isMultiDisassembling();

    public default void beforeMultiDisassembly() {};

    public default void afterMultiDisassembly() {};

    public static <M extends IMulti<? super M>> void disassemble(IMulti<?> multi) {
        if (multi.isMultiDisassembling()) return;
        multi.markMultiDisassembling();
        multi.beforeMultiDisassembly();
        BlockHelper.betweenClosedExcludingEdges(multi.getMultiAbsoluteLowerOuterCornerPos(), multi.getMultiAbsoluteUpperOuterCornerPos()).forEach(pos -> {
            Optional.ofNullable(BlockEntityBehaviour.get(multi.getLevel(), pos, multi.getMultiType().getBehaviourType()))
                //.filter(mb -> mb.getOptionalMulti().filter(multi::equals).isPresent()) // Don't accidentally dissasemble parts belonging to a different Multi (however that would even happen)
                .ifPresent(MultiBehaviour::multiDisassembled);
        });
        multi.afterMultiDisassembly();
    };

    default void transform(StructureTransform transform, BlockPos newAbsoluteControllerPos) {
        BlockPos controllerMotion = getMultiControllerAbsolutePos().subtract(newAbsoluteControllerPos);
        setMultiControllerAbsolutePos(newAbsoluteControllerPos);
        BlockPos upperCornerPosition = transform.apply(getMultiAbsoluteUpperOuterCornerPos().offset(controllerMotion));
        BlockPos lowerCornerPosition = transform.apply(getMultiAbsoluteLowerOuterCornerPos().offset(controllerMotion));
        BlockPos newUpperCornerPosition = new BlockPos(Math.max(upperCornerPosition.getX(), lowerCornerPosition.getX()), Math.max(upperCornerPosition.getY(), lowerCornerPosition.getY()), Math.max(upperCornerPosition.getZ(), lowerCornerPosition.getZ()));
        BlockPos newLowerCornerPosition = new BlockPos(Math.min(upperCornerPosition.getX(), lowerCornerPosition.getX()), Math.min(upperCornerPosition.getY(), lowerCornerPosition.getY()), Math.min(upperCornerPosition.getZ(), lowerCornerPosition.getZ()));
        setMultiAbsoluteUpperOuterCornerPos(newUpperCornerPosition);
        setMultiAbsoluteLowerOuterCornerPos(newLowerCornerPosition);
    };
};
