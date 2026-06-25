package petrolpark.mc.library.compat.create.core.world.block.tube;

import java.util.List;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import petrolpark.mc.library.compat.create.RequiresCreate;

@RequiresCreate
public class TubeStructuralBlockEntity extends SmartBlockEntity implements Clearable {

    protected BlockPos controllerPos;

    public TubeStructuralBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {};

    @Override
    public void destroy() {
        if (controllerPos != null) TubeBehaviour.get(getLevel(), controllerPos).ifPresent(TubeBehaviour::disconnect);
        super.destroy();
    };

    public void setController(BlockPos controllerPos) {
        this.controllerPos = controllerPos;
    };

    /**
     * When about to be removed, e.g. as part of a Simulated Contraption, don't spew tubes everywhere
     */
    @Override
    public void clearContent() {
        TubeBehaviour.get(getLevel(), controllerPos).ifPresent(behaviour -> behaviour.disconnect((s, l) -> {}));
    };

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        if (tag.contains("ControllerPos", Tag.TAG_INT_ARRAY)) controllerPos = NbtUtils.readBlockPos(tag, "ControllerPos").orElse(BlockPos.ZERO).offset(getBlockPos());
        super.read(tag, registries, clientPacket);
    };

    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        if (controllerPos != null) tag.put("ControllerPos", NbtUtils.writeBlockPos(controllerPos.subtract(getBlockPos())));
        super.write(tag, registries, clientPacket);
    };
    
};
