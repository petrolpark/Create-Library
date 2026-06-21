package petrolpark.mc.library.compat.create.core.world.block.chainConveyor;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import petrolpark.mc.library.compat.create.registry.PetrolparkArmInteractionPointTypes;
import petrolpark.mc.library.config.PetrolparkConfigs;
import petrolpark.mc.library.mixin.compat.create.accessor.client.ChainConveyorOBBAccessor;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.util.Pair;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity.ConnectedPort;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorInteractionHandler;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorShape;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.simibubi.create.content.logistics.box.PackageItem;

import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;

public class ChainConveyorArmInteractionPoint extends ArmInteractionPoint {

    public static final boolean isEnabled() {
        return SharedFeatureFlag.ARMS_TARGET_CHAIN_CONVEYORS.enabled() && PetrolparkConfigs.server().createArmsTargetChainConveyors.get();
    };

    private static final BlockPos getDummyAnchor(Level level) {
        return new BlockPos(0, level.getMinBuildHeight() - 1, 0);
    };

    public static final void registerAll(ArmBlockEntity arm) {
        for (ArmInteractionPoint point : ((IArmBlockEntityDuck)arm).getInputs()) if (point instanceof ChainConveyorArmInteractionPoint chainPoint) chainPoint.register(arm);
        for (ArmInteractionPoint point : ((IArmBlockEntityDuck)arm).getOutputs()) if (point instanceof ChainConveyorArmInteractionPoint chainPoint) chainPoint.register(arm);
    };

    public static final void deregisterAll(ArmBlockEntity arm) {
        for (ArmInteractionPoint point : ((IArmBlockEntityDuck)arm).getInputs()) if (point instanceof ChainConveyorArmInteractionPoint chainPoint) chainPoint.deregister(arm);
        for (ArmInteractionPoint point : ((IArmBlockEntityDuck)arm).getOutputs()) if (point instanceof ChainConveyorArmInteractionPoint chainPoint) chainPoint.deregister(arm);
    };

    protected BlockPos chainConveyorPos;
    protected ChainConveyorBlockEntity.ConnectedPort connectedPort;
    protected Vec3 targetPosition = null;

    protected Pair<ChainConveyorBlockEntity, ChainConveyorPackage> nextConveyorAndBox = null;

    /**
     * 
     * @param level
     * @param liftPos Absolute position of Chain Conveyor Block Entity
     * @param chainPos If {@code connectionPos} is {@code null}, the angle around the Chain Conveyor Block Entity, otherwise the distance from the Chain Conveyor to the next
     * @param connectionPos {@code null} if connected directly to the Chain Conveyor, otherwise the position of the next Chain Conveyor relative to {@code liftPos}
     * @param filter
     */
    protected ChainConveyorArmInteractionPoint(Level level, BlockPos liftPos, float chainPos, @Nullable BlockPos connectionPos, String filter) {
        super(PetrolparkArmInteractionPointTypes.CHAIN_CONVEYOR.get(), level, getDummyAnchor(level), Blocks.VOID_AIR.defaultBlockState());

        this.chainConveyorPos = liftPos;
        this.connectedPort = new ChainConveyorBlockEntity.ConnectedPort(chainPos, connectionPos, filter);
    };

    public void register(ArmBlockEntity arm) {
        if (level.getBlockEntity(chainConveyorPos) instanceof ChainConveyorBlockEntity ccbe) {
            ccbe.routingTable.receivePortInfo(connectedPort.filter(), connectedPort.connection() == null ? BlockPos.ZERO : connectedPort.connection());
            final Map<BlockPos, ConnectedPort> portMap = connectedPort.connection() == null ? ccbe.loopPorts : ccbe.travelPorts;
			portMap.put(arm.getBlockPos().subtract(chainConveyorPos), connectedPort);
        };
    };

    public void deregister(ArmBlockEntity arm) {
        if (level.getBlockEntity(chainConveyorPos) instanceof ChainConveyorBlockEntity ccbe) {
            final BlockPos relativePos = arm.getBlockPos().subtract(chainConveyorPos);
            ccbe.loopPorts.remove(relativePos);
            ccbe.travelPorts.remove(relativePos);
            // Let Routing Table Entry time out
        };
    };

    public boolean inRange(Vec3i armPos) {
        if (connectedPort.connection() == null) return armPos.closerThan(chainConveyorPos, ArmBlockEntity.getRange() + 1);
        else return Vec3.atCenterOf(chainConveyorPos).add(Vec3.atCenterOf(connectedPort.connection()).normalize().scale(connectedPort.chainPosition())).closerThan(Vec3.atCenterOf(armPos), ArmBlockEntity.getRange() + 2f);
    };

    @Override
    public boolean isValid() {
        if (!(level.getBlockEntity(chainConveyorPos) instanceof ChainConveyorBlockEntity ccbe)) return false;
        if (connectedPort.connection() != null && !ccbe.connections.contains(connectedPort.connection())) return false;
        if (
            nextConveyorAndBox != null && !(connectedPort.connection() == null 
                ? ((IChainConveyorBlockEntityDuck)ccbe).getLoopingPackages()
                : ((IChainConveyorBlockEntityDuck)ccbe).getTravellingPackages().get(connectedPort.connection())
            ).contains(nextConveyorAndBox.getSecond())
        ) {
            nextConveyorAndBox = null;
            return false;
        };
        return true;
    };

    @Override
    public Vec3 getInteractionPositionVector() {
        if (!level.isClientSide()) return super.getInteractionPositionVector();
        if (targetPosition == null) targetPosition = createInteractionPositionVector();
        return targetPosition;
    };

    // Client only
    protected Vec3 createInteractionPositionVector() {
        final List<ChainConveyorShape> shapes = ChainConveyorInteractionHandler.loadedChains.get(level).getIfPresent(chainConveyorPos);
        if (shapes != null) for (ChainConveyorShape shape : shapes) {
            if (connectedPort.connection() == null && shape instanceof ChainConveyorShape.ChainConveyorBB) return shape.getVec(chainConveyorPos, connectedPort.chainPosition());
            if (shape instanceof ChainConveyorShape.ChainConveyorOBB obb && Objects.equals(connectedPort.connection(), ((ChainConveyorOBBAccessor)obb).getConnection())) return obb.getVec(chainConveyorPos, connectedPort.chainPosition());
        };
        return super.getInteractionPositionVector();
    };

    @Override
    protected @Nullable IItemHandler getHandler(ArmBlockEntity armBlockEntity) {
        return null;
    };

    @Override
    public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
        if (level.getBlockEntity(chainConveyorPos, AllBlockEntityTypes.CHAIN_CONVEYOR.get()).map(ccbe -> {
            if (ccbe.getSpeed() != 0f && ccbe.canAcceptPackagesFor(connectedPort.connection())) {
                final ChainConveyorPackage box = new ChainConveyorPackage(connectedPort.chainPosition(), stack);
                if (PackageItem.isPackage(stack)) {
                    if (!simulate) {
                        if (connectedPort.connection() == null) {
                            ccbe.addLoopingPackage(box);
                        } else {
                            ccbe.addTravellingPackage(box, connectedPort.connection());
                        };
                    };
                    return true;
                } else {
                    return ChainConveyorItemEvent.canAdd(level, stack, ccbe, connectedPort.connection(), connectedPort.chainPosition(), simulate);
                }
            };
            return false;
        }).orElse(false)) {
            return stack.copyWithCount(stack.getCount() - 1);
        } else {
            return stack;
        }
    };

    @Override
    public ItemStack extract(ArmBlockEntity armBlockEntity, int slot, boolean simulate) {
        if (simulate) {
            if (nextConveyorAndBox == null) return ItemStack.EMPTY;
            final ChainConveyorItemEvent.Remove event = ChainConveyorItemEvent.getRemoved(level, nextConveyorAndBox.getFirst(), connectedPort.connection(), nextConveyorAndBox.getSecond(), true);
            if (event.isAllowed()) return event.getStack();
        };
        return ItemStack.EMPTY; // Non-simulated extraction done manually
    };

    @Override
    public ItemStack extract(ArmBlockEntity armBlockEntity, int slot, int amount, boolean simulate) {
        return amount == 0 ? ItemStack.EMPTY : extract(armBlockEntity, slot, simulate); // Can only ever extract 1 at a time
    };
    
    @Override
    public int getSlotCount(ArmBlockEntity armBlockEntity) {
        return 1;
    };

    @Override
    protected void serialize(CompoundTag nbt, BlockPos anchor) {
        super.serialize(nbt, anchor);
        nbt.put("ChainConveyorPos", NbtUtils.writeBlockPos(chainConveyorPos));
        nbt.putFloat("ChainPosition", connectedPort.chainPosition());
        if (connectedPort.connection() != null) nbt.put("ConnectionPos", NbtUtils.writeBlockPos(connectedPort.connection()));
        nbt.putString("Filter", connectedPort.filter());
    };

    @Override
    protected void deserialize(CompoundTag nbt, BlockPos anchor) {
        super.deserialize(nbt, anchor);
        chainConveyorPos = NbtUtils.readBlockPos(nbt, "ChainConveyorPos").orElse(BlockPos.ZERO);
        connectedPort = new ConnectedPort(nbt.getFloat("ChainPosition"), nbt.contains("ConnectionPos") ? NbtUtils.readBlockPos(nbt, "ConnectionPos").orElse(null) : null, nbt.getString("Filter"));
        targetPosition = null;
    };

    public static class Type extends ArmInteractionPointType {

        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return pos.equals(getDummyAnchor(level)); // Should never be called normally. Handled manually in ChainConveyorArmInteractionHandler
        };

        @Override
        public @Nullable ChainConveyorArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return pos.equals(getDummyAnchor(level)) ? new ChainConveyorArmInteractionPoint(level, BlockPos.ZERO, 0f, null, "") : null;
        };

    };

    public static final void notifyArmToAnticipate(ChainConveyorBlockEntity ccbe, ChainConveyorBlockEntity.ConnectedPort connectedPort, ArmBlockEntity arm, ChainConveyorPackage box) {
        if (arm.getSpeed() != 0f && ((IArmBlockEntityDuck)arm).getPhase() == ArmBlockEntity.Phase.SEARCH_INPUTS) {
            for (ArmInteractionPoint point : ((IArmBlockEntityDuck)arm).getInputs()) {
                if (point instanceof ChainConveyorArmInteractionPoint chainPoint && chainPoint.connectedPort == connectedPort) {
                    ((IArmBlockEntityDuck)arm).setPhase(ArmBlockEntity.Phase.SEARCH_INPUTS);
                    chainPoint.nextConveyorAndBox = Pair.of(ccbe, box);
                    break;
                };
            };
            ((IArmBlockEntityDuck)arm).invokeSearchForItem(); // Search again now we've added the next Box
            if (((IArmBlockEntityDuck)arm).invokeGetTargetedInteractionPoint() instanceof ChainConveyorArmInteractionPoint chainPoint && chainPoint.connectedPort == connectedPort) {
                //((IArmBlockEntityDuck)arm).setChasedPointProgress(1f); // Move to the position ASAP
            };
        };
    };

    public static final boolean exportToArm(ChainConveyorBlockEntity ccbe, ChainConveyorBlockEntity.ConnectedPort connectedPort, ArmBlockEntity arm, ChainConveyorPackage box) {
        if (((IArmBlockEntityDuck)arm).invokeGetTargetedInteractionPoint() instanceof ChainConveyorArmInteractionPoint chainPoint 
            && chainPoint.connectedPort == connectedPort // Check if Arm is targeting this Chain Conveyor bit
            && ((IArmBlockEntityDuck)arm).getChasedPointProgress() == 1f
        ) { 
            final ChainConveyorItemEvent.Remove event = ChainConveyorItemEvent.getRemoved(ccbe.getLevel(), ccbe, connectedPort.connection(), box, false);
            if (!event.isAllowed()) return false;
            ((IArmBlockEntityDuck)arm).setHeldItem(event.getStack());
            chainPoint.nextConveyorAndBox = null;
            
            // Reset
            ((IArmBlockEntityDuck)arm).setPhase(ArmBlockEntity.Phase.SEARCH_OUTPUTS);
            ((IArmBlockEntityDuck)arm).setChasedPointProgress(0f);
            ((IArmBlockEntityDuck)arm).setChasedPointIndex(-1);
            arm.sendData();
            arm.setChanged();
            if (!(arm.getLevel() instanceof PonderLevel)) ((IArmBlockEntityDuck)arm).invokeSearchForDestination();

            return true;
        };
        return false;
    };
    
};
