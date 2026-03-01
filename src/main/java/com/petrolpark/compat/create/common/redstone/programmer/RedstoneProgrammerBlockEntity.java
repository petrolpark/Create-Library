package com.petrolpark.compat.create.common.redstone.programmer;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import com.petrolpark.compat.create.PetrolparkCreateDataComponentTypes;
import com.petrolpark.compat.create.common.redstone.programmer.RedstoneProgrammerBlockItem.ItemStackRedstoneProgram;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneProgrammerBlockEntity extends SmartBlockEntity {

    public RedstoneProgrammerBehaviour programmer;

    public RedstoneProgrammerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        programmer = new RedstoneProgrammerBehaviour(this, () -> getBlockState().getValue(RedstoneProgrammerBlock.POWERED));
        behaviours.add(programmer);
    };

    @Override
    protected void collectImplicitComponents(@Nonnull DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(PetrolparkCreateDataComponentTypes.REDSTONE_PROGRAM, new ItemStackRedstoneProgram().copyFrom(programmer.program));
    };

    @Override
    protected void applyImplicitComponents(@Nonnull DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        Optional.ofNullable(componentInput.get(PetrolparkCreateDataComponentTypes.REDSTONE_PROGRAM)).ifPresent(program -> {
            program.unload();
            programmer.program.copyFrom(program);
            programmer.program.load();
        });
    };
    
};
