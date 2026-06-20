package com.petrolpark.compat.create.shared.content.redstone.programmer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.petrolpark.compat.create.registry.PetrolparkIcon;
import com.petrolpark.compat.create.shared.content.redstone.programmer.RedstoneProgrammerMenu.DummyRedstoneProgram;
import com.petrolpark.compat.create.shared.registry.SharedCreateBlocks;
import com.petrolpark.config.PetrolparkConfigs;
import com.petrolpark.util.Lang;
import com.petrolpark.util.codec.CodecHelper;
import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;

import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.data.Couple;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;

public abstract class RedstoneProgram {

    public static final <PROGRAM extends RedstoneProgram> Codec<PROGRAM> codec(Factory<PROGRAM> factory) {
        return RecordCodecBuilder.create(instance -> instance.group(
            PlayMode.CODEC.optionalFieldOf("mode", PlayMode.MANUAL).forGetter(RedstoneProgram::getMode),
            CodecHelper.POS_INT.optionalFieldOf("length", 20).forGetter(RedstoneProgram::getLength),
            CodecHelper.POS_INT.optionalFieldOf("playtime", 0).forGetter(RedstoneProgram::getPlaytime),
            CodecHelper.POS_INT.optionalFieldOf("ticks_to_next_beat", 2).forGetter(RedstoneProgram::getTicksToNextBeat),
            Codec.BOOL.optionalFieldOf("paused", true).forGetter(RedstoneProgram::isPaused),
            Codec.BOOL.optionalFieldOf("was_paused", false).forGetter(RedstoneProgram::wasPausedLastTick),
            Codec.BOOL.optionalFieldOf("was_powered", false).forGetter(RedstoneProgram::wasPoweredLastTick),
            Codec.list(ChannelData.CODEC).optionalFieldOf("channels",Collections.emptyList()).forGetter(RedstoneProgram::getChannelData),
            CodecHelper.POS_INT.optionalFieldOf("ticks_per_beat", 2).forGetter(RedstoneProgram::getTicksPerBeat),
            CodecHelper.POS_INT.optionalFieldOf("beats_per_line", 2).forGetter(RedstoneProgram::getBeatsPerLine),
            CodecHelper.POS_INT.optionalFieldOf("lines_per_bar", 4).forGetter(RedstoneProgram::getLinesPerBar)
        ).apply(instance, factory::create));
    };

    public static final <PROGRAM extends RedstoneProgram> StreamCodec<RegistryFriendlyByteBuf, PROGRAM> streamCodec(Factory<PROGRAM> factory) {
        return CodecHelper.compositeStreamCodec(
            CatnipStreamCodecBuilders.ofEnum(PlayMode.class), RedstoneProgram::getMode,
            ByteBufCodecs.INT, RedstoneProgram::getLength,
            ByteBufCodecs.INT, RedstoneProgram::getPlaytime,
            ByteBufCodecs.INT, RedstoneProgram::getTicksToNextBeat,
            ByteBufCodecs.BOOL, RedstoneProgram::isPaused,
            ByteBufCodecs.BOOL, RedstoneProgram::wasPausedLastTick,
            ByteBufCodecs.BOOL, RedstoneProgram::wasPoweredLastTick,
            ChannelData.STREAM_CODEC.apply(ByteBufCodecs.list()), RedstoneProgram::getChannelData,
            ByteBufCodecs.INT, RedstoneProgram::getTicksPerBeat,
            ByteBufCodecs.INT, RedstoneProgram::getBeatsPerLine,
            ByteBufCodecs.INT, RedstoneProgram::getLinesPerBar,
            factory::create
        );
    };

    public static final StreamEncoder<RegistryFriendlyByteBuf, RedstoneProgram> ENCODER = streamCodec(DummyRedstoneProgram::new);

    /**
     * See {@link RedstoneProgram.PlayMode PlayMode}.
     */
    public PlayMode mode;
    /**
     * Length of the program in beats.
     */
    protected int length;
    /**
     * How far through the program we are, in beats.
     */
    protected int playtime;
    /**
     * How many ticks until the beat gets incremented.
     */
    protected int ticksToNextBeat;
    /**
     * If we are paused.
     */
    public boolean paused;
    /**
     * Whether we were paused last tick.
     */
    protected boolean pausedLastTick;
    /**
     * Whether we were powered last tick.
     */
    protected boolean poweredLastTick;
    /**
     * Each pair of Frequencies, and the list of strengths associated with it.
     */
    protected List<Channel> channels;

    /**
     * Channel sequences may not change strength more often than this if edited in the GUI.
     */
    protected int ticksPerBeat;
    /**
     * Purely visual, informs where lines should be drawn in the GUI.
     */
    public int beatsPerLine;
    /**
     * Purely visual, informs which lines drawn in the GUI should be thicker.
     */
    public int linesPerBar;
    /**
     * Whether a the Redstone Link network was notified of a change in the last tick and it needn't be notified again.
     */
    protected boolean notifiedChange;

    public RedstoneProgram(PlayMode mode, int length, int playtime, int ticksToNextBeat, boolean paused, boolean pausedLastTick, boolean poweredLastTick, List<ChannelData> channels, int ticksPerBeat, int beatsPerLine, int linesPerBar) {
        copyFrom(mode, length, playtime, ticksToNextBeat, paused, pausedLastTick, poweredLastTick, channels, ticksPerBeat, beatsPerLine, linesPerBar);
    };

    public RedstoneProgram() {
        mode = PlayMode.MANUAL;
        ticksPerBeat = PetrolparkConfigs.server().redstoneProgrammerMinTicksPerBeat.get();
        length = 20;
        playtime = 0;
        ticksToNextBeat = ticksPerBeat;
        paused = true;
        pausedLastTick = false;
        poweredLastTick = false;
        channels = new ArrayList<>();
        beatsPerLine = 2;
        linesPerBar = 4;
        notifiedChange = false;
    };

    public PlayMode getMode() {
        return mode;
    };

    public int getLength() {
        return length;
    };

    public int getPlaytime() {
        return playtime;
    };

    public int getTicksToNextBeat() {
        return ticksToNextBeat;
    };

    public boolean isPaused() {
        return paused;
    };

    public boolean wasPausedLastTick() {
        return pausedLastTick;
    };

    public boolean wasPoweredLastTick() {
        return poweredLastTick;
    };

    public List<ChannelData> getChannelData() {
        return getChannels().stream().map(Channel::asData).toList();
    };

    public int getTicksPerBeat() {
        return ticksPerBeat;
    };

    public int getBeatsPerLine() {
        return beatsPerLine;
    };

    public int getLinesPerBar() {
        return linesPerBar;
    };

    /**
     * The number of ticks this program has been playing for.
     */
    public int getAbsolutePlaytime() {
        return getTicksPerBeat() * getPlaytime() + (getTicksPerBeat() - getTicksToNextBeat());
    };

    public void setTicksPerBeat(int value) {
        ticksPerBeat = Math.max(PetrolparkConfigs.server().redstoneProgrammerMinTicksPerBeat.get(), value);
    };

    public void tick() {
        boolean powered = hasPower();

        if (mode.powerRequired) paused = !powered; // If we need power to run, make sure we're doing the right thing

        if (powered && !poweredLastTick) { // If we've been pulsed
            if (mode == PlayMode.SWITCH_ON_PULSE) {
                paused = !paused;
            } else if (mode == PlayMode.RESTART_ON_PULSE) {
                paused = false;
                playtime = 0;
            };
        };

        if (!powered && mode == PlayMode.LOOP_WITH_POWER) playtime = 0; // Restart if we should

        if (!paused) { // Play if not paused
            notifiedChange = false;
            if (ticksPerBeat == 1) {
                playtime++;
            } else {
                ticksToNextBeat--;
                if (ticksToNextBeat <= 0) {
                    ticksToNextBeat = ticksPerBeat;
                    playtime++;
                } else {
                    notifiedChange = true; // If nothing can have changed, there is no need to update again
                };
            };
        };

        if (paused != pausedLastTick) notifiedChange = false; // If we've unpaused, we need to start telling the Redstone Link network about changes again

        if (playtime >= length) { // Restart if we've reached the end
            playtime = 0;
            if (mode.pausesWhenFinished) paused = true; // If we shouldn't loop, don't
        };

        if (!notifiedChange) { // If we need to notify the Redstone Link network of our power change
            channels.forEach(Channel::updateNetwork);
            if (paused) notifiedChange = true; // If we're paused, don't notify next tick too
        };

        poweredLastTick = powered;
        pausedLastTick = paused;
    };

    public void restart() {
        playtime = 0;
        ticksToNextBeat = ticksPerBeat;
    };

    public abstract boolean hasPower();

    public abstract BlockPos getBlockPos();

    public abstract boolean shouldTransmit();

    public abstract LevelAccessor getWorld();

    public void whenChanged() {};

    public Object getHashSalt() {
        return 0;
    };

    public ImmutableList<Channel> getChannels() {
        return ImmutableList.copyOf(channels);
    };

    public boolean tryAddNewChannel(Couple<Frequency> frequency, Player player, boolean simulate) {
        if (frequency.getFirst().getStack().isEmpty() && frequency.getSecond().getStack().isEmpty()) return false;
        if (channels.size() >= PetrolparkConfigs.server().redstoneProgrammerMaxChannels.get()) {
            if (!simulate) player.displayClientMessage(translate("add_frequency.failure.full").withStyle(ChatFormatting.RED), true);
        }  else if (channels.stream().map(Channel::getNetworkKey).anyMatch(frequency::equals)) {
            if (!simulate) player.displayClientMessage(translate("add_frequency.failure.exists").withStyle(ChatFormatting.RED), true);
        } else {
            if (!simulate) {
                player.displayClientMessage(translate("add_frequency.success", frequency.getFirst().getStack().getHoverName(), frequency.getSecond().getStack().getHoverName()), true);
                addBlankChannel(frequency);
            };
            return true;
        };
        return false;
    };

    public void addBlankChannel(Couple<Frequency> frequencies) {
        final Channel channel = new Channel(frequencies, new int[length]);
        channels.add(channel);
        if (isValidWorld(getWorld())) getHandler().addToNetwork(getWorld(), channel);
    };

    public boolean remove(Channel channel) {
        boolean removed = channels.remove(channel);
        if (removed && isValidWorld(getWorld())) getHandler().removeFromNetwork(getWorld(), channel);
        return removed;
    };

    public void swap(Channel channel1, Channel channel2) {
        if (channels.contains(channel1) && channels.contains(channel2)) Collections.swap(channels, channels.indexOf(channel1), channels.indexOf(channel2));
    };

    public void load() {
        if (!isValidWorld(getWorld()) || getBlockPos() == null) return;
        channels.forEach(channel -> getHandler().addToNetwork(getWorld(), channel));
        notifiedChange = false;
    };

    public void unload() {
        if (!isValidWorld(getWorld())) return;
        channels.forEach(channel -> getHandler().removeFromNetwork(getWorld(), channel));
    };

    public void setDuration(int duration) {
        length = duration;
        for (Channel channel : channels) {
            channel.sequence = Arrays.copyOf(channel.sequence, duration);
        };
    };

    public RedstoneProgram copyFrom(PlayMode mode, int length, int playtime, int ticksToNextBeat, boolean paused, boolean pausedLastTick, boolean poweredLastTick, List<ChannelData> channels, int ticksPerBeat, int beatsPerLine, int linesPerBar) {
        this.mode = mode;
        this.length = length;
        this.playtime = playtime;
        this.ticksToNextBeat = ticksToNextBeat;
        this.paused = paused;
        this.pausedLastTick = pausedLastTick;
        this.poweredLastTick = poweredLastTick;
        this.channels = channels.stream().map(Channel::new).collect(Collectors.toCollection(ArrayList::new));
        this.ticksPerBeat = ticksPerBeat;
        this.beatsPerLine = beatsPerLine;
        this.linesPerBar = linesPerBar;
        notifiedChange = false;
        return this;
    };

    public RedstoneProgram copyFrom(@Nonnull RedstoneProgram otherProgram) {
        return copyFrom(otherProgram.getMode(), otherProgram.getLength(), otherProgram.getPlaytime(), otherProgram.getTicksToNextBeat(), otherProgram.isPaused(), otherProgram.wasPausedLastTick(), otherProgram.wasPoweredLastTick(), otherProgram.getChannelData(), otherProgram.getTicksPerBeat(), otherProgram.getBeatsPerLine(), otherProgram.getLinesPerBar());
    };

    public boolean hasPowerChanged() {
        return hasPower() != poweredLastTick;
    };

    protected static RedstoneLinkNetworkHandler getHandler() {
        return Create.REDSTONE_LINK_NETWORK_HANDLER;
    };

    protected static boolean isValidWorld(LevelAccessor level) {
        return level != null && !level.isClientSide();
    };

    public Consumer<RegistryFriendlyByteBuf> writeToMenu() {
        return buffer -> {
            ENCODER.encode(buffer, this);
            buffer.writeBoolean(hasPower());
        };
    };

    public record ChannelData(Couple<Frequency> networkKey, int[] sequence) {
        
        public static final Codec<ChannelData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Couple.codec(ItemStack.OPTIONAL_CODEC.xmap(Frequency::of, Frequency::getStack)).fieldOf("frequency").forGetter(ChannelData::networkKey),
            Codec.INT_STREAM.xmap(IntStream::toArray, Arrays::stream).fieldOf("sequence").forGetter(ChannelData::sequence)
        ).apply(instance, ChannelData::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, ChannelData> STREAM_CODEC = StreamCodec.composite(
            Couple.streamCodec(ItemStack.OPTIONAL_STREAM_CODEC.map(Frequency::of, Frequency::getStack)), ChannelData::networkKey,
            StreamCodec.of(FriendlyByteBuf::writeVarIntArray, FriendlyByteBuf::readVarIntArray), ChannelData::sequence,
            ChannelData::new
        );
    };

    public class Channel implements IRedstoneLinkable {

        public final Couple<Frequency> networkKey;
        protected int[] sequence;

        protected Channel(ChannelData data) {
            this(data.networkKey(), data.sequence());
        };

        protected Channel(Couple<Frequency> networkKey, int[] sequence) {
            this.networkKey = networkKey;
            this.sequence = sequence;
        };

        public ChannelData asData() {
            return new ChannelData(networkKey, sequence);
        };

        protected void updateNetwork() {
            if (!isValidWorld(getWorld())) return;
            if (playtime == 0 || sequence[playtime] != sequence[playtime - 1]) getHandler().updateNetworkOf(getWorld(), this); // If we've changed signal, update the Network
        };

        public int getStrength(int position) {
            if (position >= sequence.length || position < 0) return 0;
            return sequence[position];
        };

        public void setStrength(int position, int strength) {
            if (position < length) {
                if (strength >= 16 || strength < 0) strength = 0;
                sequence[position] = strength;  
            };
        };

        public void clear() {
            sequence = new int[length];
        };

        @Override
        public int getTransmittedStrength() {
            if (playtime >= length) return 0;
            return sequence[playtime];
        };

        @Override
        public void setReceivedStrength(int power) {
            // Do nothing
        };

        @Override
        public boolean isListening() {
            return false;
        };

        @Override
        public boolean isAlive() {
            return shouldTransmit();
        };

        @Override
        public Couple<Frequency> getNetworkKey() {
            return networkKey;
        };

        @Override
        public BlockPos getLocation() {
            return getBlockPos();
        };

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof Channel channel)) return false;
            return networkKey.equals(channel.networkKey)
                && sequence.equals(channel.sequence);
        };

        @Override
        public int hashCode() {
            return Objects.hash(networkKey, sequence, getHashSalt());
        };

    };
    
    public static enum PlayMode implements StringRepresentable {
        
        /**
         * Manually play, pause, restart, and skip.
         */
        MANUAL(true, false, PetrolparkIcon.REDSTONE_PROGRAMMER_MODE_MANUAL),
        /**
         * If there is a redstone pulse, switch between playing and pausing. Restart if the end is reached.
         */
        SWITCH_ON_PULSE(false, false, PetrolparkIcon.REDSTONE_PROGRAMMER_MODE_SWITCH_ON_PULSE),
        /**
         * If there is a redstone pulse, start the program again, even if already running. Don't loop.
         */
        RESTART_ON_PULSE(true, false, PetrolparkIcon.REDSTONE_PROGRAMMER_MODE_RESTART_ON_PULSE),
        /**
         * If there is power, play. If not, pause. If the end is reached, start again.
         */
        RESUME_WITH_POWER(false, true, PetrolparkIcon.REDSTONE_PROGRAMMER_MODE_RESUME_WITH_POWER),
        /**
         * If there is power, play. If not, go back to the start and pause. Do not loop.
         */
        RESTART_WITH_POWER(true, true, PetrolparkIcon.REDSTONE_PROGRAMMER_MODE_RESTART_WITH_POWER),
        /**
         * If there is power, play. If not, pause and restart. If the end is reached, start again.
         */
        LOOP_WITH_POWER(false, true, PetrolparkIcon.REDSTONE_PROGRAMMER_MODE_LOOP_WITH_POWER),
        /**
         * Play on repeat infinitely.
         */
        LOOP(false, false, PetrolparkIcon.REDSTONE_PROGRAMMER_MODE_LOOP);

        public static final Codec<PlayMode> CODEC = StringRepresentable.fromEnum(PlayMode::values);

        PlayMode(boolean pausesWhenFinished, boolean powerRequired, PetrolparkIcon icon) {
            this.pausesWhenFinished = pausesWhenFinished;
            this.powerRequired = powerRequired;
            this.description = Component.translatable(Util.makeDescriptionId("block", SharedCreateBlocks.REDSTONE_PROGRAMMER.getId()) + ".mode." + Lang.asId(name()));
            this.icon = icon;
        };

        /**
         * Whether we should stop once we reach the end and go back to the beginning, rather than play again.
         */
        public final boolean pausesWhenFinished;
        /**
         * Whether we should pause if we don't have power and play if we do.
         */
        public final boolean powerRequired;

        public final Component description;

        public final PetrolparkIcon icon;

        @Override
        public String getSerializedName() {
            return Lang.asId(name());
        };
    };

    @FunctionalInterface
    public static interface Factory<PROGRAM extends RedstoneProgram> {

        public PROGRAM create(PlayMode mode, int length, int playtime, int ticksToNextBeat, boolean paused, boolean pausedLastTick, boolean poweredLastTick, List<ChannelData> channels, int ticksPerBeat, int beatsPerLine, int linesPerBar);
    
    };

    public static final MutableComponent translate(String suffix, Object ... args) {
        return Component.translatable(SharedCreateBlocks.REDSTONE_PROGRAMMER.get().getDescriptionId() + "." + suffix, args);
    };

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof RedstoneProgram program)) return false;
        return mode == program.mode
            && length == program.length
            && playtime == program.playtime
            && ticksToNextBeat == program.ticksToNextBeat
            && paused == program.paused
            && pausedLastTick == program.pausedLastTick
            && poweredLastTick == program.poweredLastTick
            && ticksPerBeat == program.ticksPerBeat
            && beatsPerLine == program.beatsPerLine
            && linesPerBar == program.linesPerBar
            && channels.equals(program.channels);
    };

    @Override
    public int hashCode() {
        return Objects.hash(mode, length, playtime, ticksToNextBeat, paused, pausedLastTick, poweredLastTick, channels, ticksPerBeat, beatsPerLine, linesPerBar);
    };
};
