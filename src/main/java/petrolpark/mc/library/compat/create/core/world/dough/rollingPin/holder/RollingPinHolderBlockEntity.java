package petrolpark.mc.library.compat.create.core.world.dough.rollingPin.holder;

import java.util.List;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour.ProcessingResult;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.TooltipHelper;

import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.animation.LerpedFloat.Chaser;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import petrolpark.mc.library.compat.create.core.world.dough.DoughData;
import petrolpark.mc.library.compat.create.core.world.dough.DoughTransportedItemStack;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateDataComponentTypes;
import petrolpark.mc.library.util.Lang;

public class RollingPinHolderBlockEntity extends KineticBlockEntity implements Clearable {

    protected BeltProcessingBehaviour beltProcessing;
    protected LerpedFloat armsExtension = LerpedFloat.linear().chase(0d, 0.1d, Chaser.EXP).startWithValue(0d);
    protected int ticksSinceLastProcess = 5;

    protected ItemStack rollingPin = ItemStack.EMPTY;

    public RollingPinHolderBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.add(new BeltProcessingBehaviour(this)
            .whenItemEnters(this::whenItemHeld)
            .whileItemHeld(this::whenItemHeld)
        );
    };

    @Override
    public void tick() {
        super.tick();

        armsExtension.updateChaseSpeed(Math.abs(getSpeed()) / 256);
        
        if (ticksSinceLastProcess <= 0) armsExtension.updateChaseTarget(1f); // For client 
        ticksSinceLastProcess++;
        if (ticksSinceLastProcess >= 20) armsExtension.updateChaseTarget(0f); 

        armsExtension.tickChaser();
    };

    public ProcessingResult whenItemHeld(TransportedItemStack stack, TransportedItemStackHandlerBehaviour handler) {
        if (rollingPin.isEmpty() || getSpeed() == 0f) return ProcessingResult.PASS;
        if (!(stack instanceof DoughTransportedItemStack doughStack)) return ProcessingResult.PASS;
        final DoughData doughData = stack.stack.get(PetrolparkCreateDataComponentTypes.DOUGH);
        if (doughData == null) return ProcessingResult.PASS;
        final boolean lengthwise = getBlockState().getValue(RollingPinHolderBlock.HORIZONTAL_AXIS) == Axis.Z == (doughStack.getRotation() == Rotation.NONE || doughStack.getRotation() == Rotation.CLOCKWISE_180);
        if (!doughData.isRollable(lengthwise)) return ProcessingResult.PASS;

        ticksSinceLastProcess = -1;
        armsExtension.updateChaseTarget(1f);
        notifyUpdate();
        if (armsExtension.getValue() < 0.95d) return ProcessingResult.HOLD; // Wait until (almost) fully extended

        final DoughTransportedItemStack result = doughStack.copy().update(doughData.rolled(lengthwise, false));
        handler.handleProcessingOnItem(doughStack, TransportedResult.convertTo(result));
        return ProcessingResult.PASS;
    };

    @Override
    @SuppressWarnings("null")
    public void destroy() {
        super.destroy();
        Block.popResource(getLevel(), getBlockPos(), rollingPin);
    };

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox()
            .inflate(0.5f)
            .expandTowards(0f, -1f, 0f);
    };

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (compound.contains("rolling_pin")) 
            rollingPin = ItemStack.parseOptional(registries, compound.getCompound("rolling_pin"));
        ticksSinceLastProcess = compound.getInt("ticks_since_last_process");
    };

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        if (!rollingPin.isEmpty()) compound.put("rolling_pin", rollingPin.saveOptional(registries));
        compound.putInt("ticks_since_last_process", ticksSinceLastProcess);
    };

    @Override
    public void clearContent() {
        rollingPin = ItemStack.EMPTY;
    };
    
    // Goggles

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        final boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        if (rollingPin.isEmpty()) {
            Lang.builder().translate("gui.goggles.rolling_pin_holder.no_rolling_pin")
                .style(ChatFormatting.GOLD)
                .forGoggles(tooltip);
            for (Component line : TooltipHelper.cutTextComponent(Lang.translate("gui.goggles.rolling_pin_holder.no_rolling_pin.info"), FontHelper.Palette.GRAY_AND_WHITE)) {
                Lang.builder()
                    .add(line.copy())
                    .forGoggles(tooltip);
            };
            return true;
        };
        return added;
    };

    
};
