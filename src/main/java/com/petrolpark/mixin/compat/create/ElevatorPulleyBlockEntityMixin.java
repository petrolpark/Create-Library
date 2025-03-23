package com.petrolpark.mixin.compat.create;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.petrolpark.PetrolparkTags;
import com.petrolpark.compat.create.block.entity.behaviour.ContaminationBehaviour;
import com.simibubi.create.content.contraptions.elevator.ElevatorPulleyBlockEntity;
import com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ElevatorPulleyBlockEntity.class)
public abstract class ElevatorPulleyBlockEntityMixin extends PulleyBlockEntity {

    public ElevatorPulleyBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        throw new AssertionError();
    };

    @Inject(
        method = "Lcom/simibubi/create/content/contraptions/elevator/ElevatorPulleyBlockEntity;addBehaviours(Ljava/util/List;)V",
        at = @At("HEAD"),
        remap = false
    )
    public void inAddBehaviours(List<BlockEntityBehaviour> behaviours, CallbackInfo ci) {
        if (PetrolparkTags.BlockEntityTypes.CONTAMINABLE_KINETIC.matches(getType())) behaviours.add(new ContaminationBehaviour(this));
    };
    
};
