package com.petrolpark.compat.create.common.redstone.programmer;

import java.util.List;
import java.util.function.BooleanSupplier;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NotNull;

import com.mojang.serialization.Codec;
import com.petrolpark.util.NBTHelper;
import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class RedstoneProgrammerBehaviour extends BlockEntityBehaviour implements ClipboardCloneable, MenuProvider {

    public static final BehaviourType<RedstoneProgrammerBehaviour> TYPE = new BehaviourType<>();

    protected final BooleanSupplier powerChecker;
    public final BehaviourRedstoneProgram program;
    public final Codec<BehaviourRedstoneProgram> programCodec;

    public RedstoneProgrammerBehaviour(SmartBlockEntity be, BooleanSupplier powerChecker) {
        super(be);
        this.powerChecker = powerChecker;
        program = new BehaviourRedstoneProgram();
        programCodec = RedstoneProgram.codec(program::copyFrom);
    };

    @Override
	public void initialize() {
		super.initialize();
		if (getWorld().isClientSide()) return;
		program.load();
	};

    @Override
	public void unload() {
		super.unload();
		if (getWorld().isClientSide()) return;
		program.unload();
	};

    @Override
    public void tick() {
        program.tick();
        super.tick();
    };

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        NBTHelper.read(registries, programCodec, nbt.get("Program"));
    };

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        nbt.put("Program", NBTHelper.write(registries, programCodec, program));
    };

    public class BehaviourRedstoneProgram extends RedstoneProgram {

        @Override
        public boolean hasPower() {
            return powerChecker.getAsBoolean();
        };

        @Override
        public BlockPos getBlockPos() {
            return getPos();
        };

        /**
         * Copied from the {@link com.simibubi.create.content.redstone.link.LinkBehaviour Create source code}.
         */
        @Override
        public boolean shouldTransmit() {
            Level level = RedstoneProgrammerBehaviour.super.getWorld();
            BlockPos pos = getPos();
            if (blockEntity.isChunkUnloaded()) return false;
            if (blockEntity.isRemoved()) return false;
            if (!level.isLoaded(pos)) return false;
            return level.getBlockEntity(pos) == blockEntity;
        };

        @Override
        public LevelAccessor getWorld() {
            return RedstoneProgrammerBehaviour.super.getWorld();
        };

        @Override
        public void whenChanged() {
            blockEntity.notifyUpdate();
            program.load();
        };

        @Override
        public void tick() {
            super.tick();
        };

        @Override
        public BehaviourRedstoneProgram copyFrom(PlayMode mode, int length, int playtime, int ticksToNextBeat, boolean paused, boolean pausedLastTick, boolean poweredLastTick, List<ChannelData> channels, int ticksPerBeat, int beatsPerLine, int linesPerBar) {
            super.copyFrom(mode, length, playtime, ticksToNextBeat, paused, pausedLastTick, poweredLastTick, channels, ticksPerBeat, beatsPerLine, linesPerBar);
            return this;
        };

        @Override
        public Object getHashSalt() { // Needed so when the ItemStackRedstoneProgram removes its Channels upon unloading, this one doesn't too
            return getBlockPos();
        };
        
    };

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    @Override
    public String getClipboardKey() {
        return "Frequencies";
    };

    @Override
    public boolean writeToClipboard(@NotNull Provider registries, CompoundTag tag, Direction side) {
        tag.put("RedstoneProgram", NBTHelper.write(registries, programCodec, program));
        return true;
    };

    @Override
    public boolean readFromClipboard(@NotNull Provider registries, CompoundTag tag, Player player, Direction side, boolean simulate) {
        if (tag.contains("RedstoneProgram")) {
            if (!simulate) NBTHelper.read(registries, programCodec, tag);
            return true;
        };
        if (tag.contains("First") || !tag.contains("Last")) {
            final Couple<Frequency> frequencies = Couple.create(Frequency.of(ItemStack.parseOptional(registries, tag.getCompound("FrequencyFirst"))), Frequency.of(ItemStack.parseOptional(registries, tag.getCompound("FrequencyLast"))));
            if (program.getChannels().stream().anyMatch(channel -> channel.networkKey.equals(frequencies))) return false;
            if (!simulate) program.addBlankChannel(frequencies);
            return true;
        };
        return false;
    };

    @Override
    public AbstractContainerMenu createMenu(int containerId, @Nonnull Inventory playerInventory, @Nonnull Player player) {
        return RedstoneProgrammerMenu.create(containerId, playerInventory, program);
    };

    @Override
    public Component getDisplayName() {
        return Component.empty();
    };
    
};
