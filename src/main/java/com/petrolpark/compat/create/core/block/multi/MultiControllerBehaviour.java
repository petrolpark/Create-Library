// package com.petrolpark.compat.create.core.block.multi;

// import java.util.Optional;

// import com.simibubi.create.content.contraptions.StructureTransform;
// import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;

// import net.minecraft.core.HolderLookup;
// import net.minecraft.nbt.CompoundTag;
// import net.minecraft.nbt.NbtUtils;

// public abstract class MultiControllerBehaviour<M extends IMulti<? super M>> extends MultiBehaviour<M> implements IMultiSideBehaviour<M> {

//     protected IMulti<? extends M> multi;

//     public MultiControllerBehaviour(SmartBlockEntity be) {
//         super(be);
//     };

//     @Override
//     public final Optional<IMulti<? extends M>> getOptionalMulti() {
//         return Optional.ofNullable(multi);
//     };

//     @Override
//     public final boolean isMultiController() {
//         return true;
//     };

//     @Override
//     public final void multiDisassembled() {};

//     @Override
//     public void transform(StructureTransform transform) {
//         getOptionalMulti().ifPresent(multi -> multi.transform(transform, getPos()));
//     };

//     @Override
//     public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
//         // TODO Auto-generated method stub
//         super.read(nbt, registries, clientPacket);
//     };

//     public static final String LOWER_POS_TAG_KEY = "LowerPos";
//     public static final String UPPER_POS_TAG_KEY = "UpperPos";

//     @Override
//     public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
//         getOptionalMulti().ifPresent(multi -> {
//             nbt.put(LOWER_POS_TAG_KEY, NbtUtils.writeBlockPos(multi.getMultiAbsoluteLowerOuterCornerPos().subtract(getPos())));
//             nbt.put(UPPER_POS_TAG_KEY, NbtUtils.writeBlockPos(multi.getMultiAbsoluteUpperOuterCornerPos().subtract(getPos())));
//         });
//     };
    
// };
