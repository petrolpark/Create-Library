package petrolpark.mc.library.compat.create.shared.content.redstone.programmer;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import petrolpark.mc.library.compat.create.registry.PetrolparkCreateDataComponentTypes;
import petrolpark.mc.library.compat.create.shared.content.redstone.programmer.RedstoneProgrammerBlockItem.ItemStackRedstoneProgram;
import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RedstoneProgrammerBlockEntity extends SmartBlockEntity {

    public RedstoneProgrammerBehaviour programmer;

    public RedstoneProgrammerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    };

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(programmer = new RedstoneProgrammerBehaviour(this, () -> getBlockState().getValue(RedstoneProgrammerBlock.POWERED)));
        behaviours.add(new RedstoneLinkFrequencyCopyingBehaviour());
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

    public class RedstoneLinkFrequencyCopyingBehaviour extends BlockEntityBehaviour implements ClipboardCloneable {

        public static final BehaviourType<RedstoneLinkFrequencyCopyingBehaviour> TYPE = new BehaviourType<>();

        public RedstoneLinkFrequencyCopyingBehaviour() {
            super(RedstoneProgrammerBlockEntity.this);
        };

        @Override
        public BehaviourType<RedstoneLinkFrequencyCopyingBehaviour> getType() {
            return TYPE;
        };

        @Override
        public String getClipboardKey() {
            return "Frequencies";
        };

        @Override
        public boolean writeToClipboard(@NotNull HolderLookup.Provider registries, CompoundTag tag, Direction side) {
            return false;
        };

        @Override
        public boolean readFromClipboard(@NotNull HolderLookup.Provider registries, CompoundTag tag, Player player, Direction side, boolean simulate) {
            if (tag.contains("First") && tag.contains("Last")) {
                final boolean success = programmer.program.tryAddNewChannel(Couple.create(Frequency.of(ItemStack.parseOptional(registries, tag.getCompound("First"))), Frequency.of(ItemStack.parseOptional(registries, tag.getCompound("Last")))), player, simulate);
                if (success) sendData();
                return success;
            };
            return false;
        };
        
    };
    
};
