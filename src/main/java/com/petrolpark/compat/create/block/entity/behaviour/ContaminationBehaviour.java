package com.petrolpark.compat.create.block.entity.behaviour;

import com.petrolpark.contamination.GenericContamination;
import com.petrolpark.contamination.ItemContamination;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * Behaviour for Create Block Entities which can hold the Contaminants of the ItemStack used to place them.
 * 
 * <p>Implementers must handle transferring those Contaminants themselves, probably {@link Block#setPlacedBy(net.minecraft.world.level.Level, net.minecraft.core.BlockPos, net.minecraft.world.level.block.state.BlockState, net.minecraft.world.entity.LivingEntity, ItemStack) here},
 * and the BlockItem must be tagged with {@code petrolpark:contaminable_blocks}. They must also handle transferring the Contaminants back to the Item when mined.</p>
 * 
 * <p>This is however all done automatically (in addition to this Behaviour being added) for any instances of a {@link KineticBlock} with a {@link KineticBlockEntity} whose BlockEntityType is in the tag {@code petrolpark:contaminable_kinetic}.</p>
 */
public class ContaminationBehaviour extends BlockEntityBehaviour {

    public static final BehaviourType<ContaminationBehaviour> TYPE = new BehaviourType<>();

    protected ListTag contaminationTag = null;
    protected boolean updateFromTag = false;
    protected final GenericContamination contamination;

    public ContaminationBehaviour(SmartBlockEntity be) {
        super(be);
        contamination = new GenericContamination(this::contaminationUpdated);
    };

    public void contaminationUpdated() {
        contaminationTag = contamination.writeNBT(getWorld().registryAccess());
        blockEntity.notifyUpdate();
    };

    public GenericContamination getContamination() {
        if (updateFromTag && contaminationTag != null) {
            contamination.readNBT(getWorld().registryAccess(), contaminationTag);
            updateFromTag = false;
        };
        return contamination;
    };

    public void setFromItem(ItemStack stack) {
        contamination.contaminateAll(ItemContamination.get(stack).streamAllContaminants());
    };

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        contaminationTag = nbt.getList("Contamination", Tag.TAG_STRING);
        updateFromTag = true;
    };

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        super.write(nbt, clientPacket);
        if (contaminationTag != null) nbt.put("Contamination", contaminationTag);
    };

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    };
    
};
