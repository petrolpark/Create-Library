package petrolpark.mc.library.compat.create.shared.content.processing.extrusion;

import java.util.List;

import petrolpark.mc.library.compat.create.core.world.block.entity.behaviour.AdvancementBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ExtrusionDieBlockEntity extends SmartBlockEntity {

    public ExtrusionDieBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new AdvancementBehaviour(this));
    };
    
};
