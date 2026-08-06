package petrolpark.mc.library.compat.create.shared.content.redstone.programmer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.redstone.link.LinkBehaviour;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;

import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateDataComponentTypes;
import petrolpark.mc.library.core.world.block.IPickUpPutDownBlock;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;

@ParametersAreNonnullByDefault
public class RedstoneProgrammerBlockItem extends BlockItem implements ISharedFeature {

    public RedstoneProgrammerBlockItem(RedstoneProgrammerBlock block, Properties properties) {
        super(block, properties);
        properties.stacksTo(1);
    };

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        final Player player = context.getPlayer();
        if (player == null || player.isShiftKeyDown()) return super.onItemUseFirst(stack, context);
        
        // Copy Frequency from Link
        final LinkBehaviour linkBehaviour = BlockEntityBehaviour.get(context.getLevel(), context.getClickedPos(), LinkBehaviour.TYPE);
        if (linkBehaviour != null) {
            final Couple<Frequency> frequency = linkBehaviour.getNetworkKey();
            if (!frequency.both(freq -> freq.getStack().isEmpty())) {
                final Optional<ItemStackRedstoneProgram> programOp = getProgram(stack, player.level(), player);
                if (programOp.isPresent()) return programOp.get().tryAddNewChannel(frequency, player, false) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
            };
        };
        
        // Edit program
        openScreen(stack, context.getLevel(), player);
        return InteractionResult.SUCCESS;
    };

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        return IPickUpPutDownBlock.removeItemFromInventory(context, super.place(context));
    };

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        openScreen(stack, level, player);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, player.getItemInHand(usedHand));
    };

    public static final void openScreen(ItemStack stack, Level level, Player player) {
        getProgram(stack, level, player).ifPresent(program -> {
            if (!level.isClientSide()) player.openMenu(new ItemStackRedstoneProgramMenuOpener(program), program.writeToMenu());
        });
    };

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (entity instanceof LivingEntity player) {
            getProgram(stack, level, player).ifPresent(program -> {
                if (!level.isClientSide()) program.load(); // This is a Set so we're safe to repeatedly load
                program.tick();
                stack.set(PetrolparkCreateDataComponentTypes.REDSTONE_PROGRAM, program);
            });
        };
    };

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack from, ItemStack to, boolean slotChanged) {
        return !(from.getItem() instanceof RedstoneProgrammerBlockItem && to.getItem() instanceof RedstoneProgrammerBlockItem);
    };

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack from, ItemStack to) {
        return !(from.getItem() instanceof RedstoneProgrammerBlockItem && to.getItem() instanceof RedstoneProgrammerBlockItem);
    };

    /**
     * Get the Program associated with this Redstone Programmer.
     * @param stack The tag of this may be changed
     * @param level
     * @param player
     * @return An Optional which should almost always contain a Redstone Program
     */
    public static Optional<ItemStackRedstoneProgram> getProgram(ItemStack stack, @Nullable LevelAccessor level, @Nullable LivingEntity player) {
        if (!(stack.getItem() instanceof RedstoneProgrammerBlockItem) || level == null || player == null) return Optional.empty();

        final UUID uuid;
        if (stack.has(PetrolparkCreateDataComponentTypes.REDSTONE_PROGRAM_UUID)) {
            uuid = stack.get(PetrolparkCreateDataComponentTypes.REDSTONE_PROGRAM_UUID);
        } else {
            uuid = UUID.randomUUID();
            stack.set(PetrolparkCreateDataComponentTypes.REDSTONE_PROGRAM_UUID, uuid);
        };

        final ItemStackRedstoneProgram program;

        if (level.isClientSide()) {
            if (stack.has(PetrolparkCreateDataComponentTypes.REDSTONE_PROGRAM)) {
                program = stack.get(PetrolparkCreateDataComponentTypes.REDSTONE_PROGRAM);
            } else {
                program = new ItemStackRedstoneProgram();
            };
        } else {
            program = RedstoneProgrammerItemHandler.PROGRAMS.get(level).computeIfAbsent(uuid, u -> {
                if (stack.has(PetrolparkCreateDataComponentTypes.REDSTONE_PROGRAM)) return stack.get(PetrolparkCreateDataComponentTypes.REDSTONE_PROGRAM);
                return new ItemStackRedstoneProgram();
            });
        };

        if (program != null) program.withPlayer(player);
        return Optional.ofNullable(program);
    };

    public static class ItemStackRedstoneProgram extends RedstoneProgram {

        public static final Codec<ItemStackRedstoneProgram> CODEC = RedstoneProgram.codec(ItemStackRedstoneProgram::new);

        public int ttl;
        protected UUID uuid;
        protected LivingEntity player = null;

        public ItemStackRedstoneProgram() {
            super();
        };

        public ItemStackRedstoneProgram(PlayMode mode, int length, int playtime, int ticksToNextBeat, boolean paused, boolean pausedLastTick, boolean poweredLastTick, List<ChannelData> channels, int ticksPerBeat, int beatsPerLine, int linesPerBar) {
            super(mode, length, playtime, ticksToNextBeat, paused, pausedLastTick, poweredLastTick, channels, ticksPerBeat, beatsPerLine, linesPerBar);
        };

        public ItemStackRedstoneProgram withPlayer(LivingEntity player) {
            this.player = player;
            return this;
        };

        @Override
        public void load() {
            if (player != null && player.getOnPos() != null) super.load();
        };

        @Override
        public void tick() {
            ttl = RedstoneProgrammerItemHandler.TIMEOUT; // This tick is only called for programmers in a Player's inventory, so if the Item is no longer in an inventory, it will die
            super.tick();
        };

        @Override
        public boolean hasPower() {
            return false;
        };

        @Override
        public BlockPos getBlockPos() {
            return player.getOnPos();
        };

        @Override
        public boolean shouldTransmit() {
            return ttl > 0;
        };

        @Override
        public LevelAccessor getWorld() {
            return player.level();
        };

        @Override
        public ItemStackRedstoneProgram copyFrom(RedstoneProgram otherProgram) {
            super.copyFrom(otherProgram);
            return this;
        };

    };

    public static record ItemStackRedstoneProgramMenuOpener(RedstoneProgram program) implements MenuProvider {

        @Override
        public RedstoneProgrammerMenu createMenu(int id, Inventory inv, Player player) {
            return RedstoneProgrammerMenu.create(id, inv, program);
        };

        @Override
        public Component getDisplayName() {
            return Component.empty();
        };

    };

    @Override
    @OnlyIn(Dist.CLIENT)
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(SimpleCustomRenderer.create(this, new RedstoneProgrammerItemRenderer()));
	};

    @Override
    public SharedFeatureFlag getSharedFeatureFlag() {
        return SharedFeatureFlag.REDSTONE_PROGRAMMER;
    };
    
};
