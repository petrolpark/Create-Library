package petrolpark.mc.library.compat.create.core.world.block.entity.behaviour;

import petrolpark.mc.library.core.flags.GenericFlagPole;
import petrolpark.mc.library.core.flags.ItemFlagPole;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * Behaviour for Create Block Entities which can hold the Flags of the ItemStack used to place them.
 * 
 * <p>Implementers must handle transferring those Flags themselves, probably {@link Block#setPlacedBy(net.minecraft.world.level.Level, net.minecraft.core.BlockPos, net.minecraft.world.level.block.state.BlockState, net.minecraft.world.entity.LivingEntity, ItemStack) here},
 * and the BlockItem must be tagged with {@code petrolpark:flaggable_blocks}. They must also handle transferring the Flags back to the Item when mined.</p>
 * 
 * <p>This is however all done automatically (in addition to this Behaviour being added) for any instances of a {@link KineticBlock} with a {@link KineticBlockEntity} whose BlockEntityType is in the tag {@code petrolpark:flaggable_kinetic}.</p>
 */
public class FlagPoleBehaviour extends BlockEntityBehaviour {

    public static final BehaviourType<FlagPoleBehaviour> TYPE = new BehaviourType<>();

    protected final GenericFlagPole flagPole;

    public FlagPoleBehaviour(SmartBlockEntity be) {
        super(be);
        flagPole = new GenericFlagPole(blockEntity::notifyUpdate);
    };

    public GenericFlagPole getFlagPole() {
        return flagPole;
    };

    public void setFromItem(ItemStack stack) {
        flagPole.flagAll(ItemFlagPole.get(stack).streamAllFlags());
    };

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        flagPole.readNBT(nbt.getList("Flags", Tag.TAG_STRING), registries);
    };

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        nbt.put("Flags", flagPole.writeNBT(registries));
    };

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    };
    
};
