package petrolpark.mc.library.compat.create.core.world.block.tube;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.compat.create.RequiresCreate;
import petrolpark.mc.library.compat.create.registry.PetrolparkCreateBlockEntityTypes;
import petrolpark.mc.library.compat.create.shared.registry.SharedCreateBlocks;
import petrolpark.mc.library.util.BigItemStack;
import petrolpark.mc.library.util.BlockFace;
import petrolpark.mc.library.util.ItemHelper;
import petrolpark.mc.library.util.NBTHelper;

@RequiresCreate
public class TubeBehaviour extends BlockEntityBehaviour {

    public static final BehaviourType<TubeBehaviour> TYPE = new BehaviourType<>();

    protected final ITubeBlockEntity tubeBlockEntity;

    protected boolean controller = false;
    protected int initializationTicks = 0;
    private List<Vec3> middleControlPoints = new ArrayList<>(); // Used when reading from save while Level is unavailable only. Use getSpline().getMiddleControlPoints() instead
    protected TubeSpline spline = null;
    protected BlockPos otherEndPos = null;

    protected boolean disconnecting = false;

    public <BE extends SmartBlockEntity & ITubeBlockEntity> TubeBehaviour(BE be) {
        super(be);
        tubeBlockEntity = be;
    };

    public boolean isController() {
        return controller;
    };

    @Override
    public void initialize() {
        if (getSpline() != null) connect(getSpline()); // Make sure structural blocks are there
    };

    /**
     * Get the {@link TubeSpline} for this pair of Tube BEs.
     * @return Optional containing the Spline if it exists
     */
    public Optional<TubeSpline> getSplineOptional() {
        return isController() ? Optional.ofNullable(getSpline()) : get(getWorld(), otherEndPos).map(TubeBehaviour::getSpline);
    };

    /**
     * Get the {@link TubeSpline spline}.
     * @return The Spline, if it exists and this is the {@link TubeBehaviour#isController() controller}, or {@code null} otherwise
     * @see TubeBehaviour#getSplineOptional()
     */
    public TubeSpline getSpline() {
        if (spline == null && controller) {
            BlockState thisState = blockEntity.getBlockState();
            if (!(thisState.getBlock() instanceof ITubeBlock tubeBlock)) return null;
            BlockState endState = getWorld().getBlockState(otherEndPos);
            if (endState.getBlock() != tubeBlock) return null;
            spline = new TubeSpline(BlockFace.of(getPos(), tubeBlock.getTubeConnectingFace(getWorld(), getPos(), thisState)), BlockFace.of(otherEndPos, tubeBlock.getTubeConnectingFace(getWorld(), otherEndPos, endState)), middleControlPoints, tubeBlock.getTubeMaxAngle(), tubeBlock.getTubeSegmentLength(), tubeBlock.getTubeSegmentRadius());
            tubeBlockEntity.invalidateTubeRenderBoundingBox();
        };
        return spline;
    };

    public BlockPos getOtherEndPos() {
        return otherEndPos;
    };

    public void connect(TubeSpline spline) {
        if (!spline.start.getPos().equals(getPos())) throw new IllegalStateException("Mismatch in tube spline start and controller position.");
        controller = true;
        disconnecting = false;
        otherEndPos = spline.end.getPos();
        this.spline = spline;
        middleControlPoints = getSpline().getMiddleControlPoints();
        for (BlockPos pos : getSpline().getBlockedPositions()) {
            getWorld().setBlock(pos, SharedCreateBlocks.TUBE_STRUCTURE.getDefaultState(), 3);
        };
        initializationTicks = 3; // Delay to link structural blocks to the controller
        get(getWorld(), otherEndPos).ifPresent(tube -> {
            tube.otherEndPos = getPos();
            tube.disconnecting = false;
        });
        playSound(false);
        tubeBlockEntity.afterTubeConnect();
        blockEntity.notifyUpdate();
    };

    public void disconnect() {
        disconnect(TubeBehaviour::dropItemsAlongSpline);
    };

    /**
     * Remove the Tube connecting the two end Blocks.
     * @param leftoverItemsConsumer What to do with the Items left over. This accepts the {@link TubeBehaviour#isController() controller} TubeBehaviour, which you should use if you need to access the {@link TubeBehaviour#getSpline() Spline}.
     * @see TubeBehaviour#disconnect() Default behaviour
     */
    public void disconnect(BiConsumer<TubeBehaviour, BigItemStack> leftoverItemsConsumer) {
        if (disconnecting) return;
        disconnecting = true;
        if (controller) {
            tubeBlockEntity.beforeTubeDisconnect();
            // Create Items
            leftoverItemsConsumer.accept(this, getRequiredStack());
            // Disconnect other end
            get(getWorld(), otherEndPos).ifPresent(tube -> {
                tube.tubeBlockEntity.beforeTubeDisconnect();
                tube.otherEndPos = null;
                tube.blockEntity.notifyUpdate();
            });
            // Remove tube structural blocks
            if (getSpline() != null) for (BlockPos pos : getSpline().getBlockedPositions()) {
                getWorld().destroyBlock(pos, false);
            };
            sendDestroyTubeParticles();
            playSound(true);
            // Disconnect this end
            controller = false;
            otherEndPos = null;
            spline = null;
            middleControlPoints = Collections.emptyList();
            blockEntity.notifyUpdate();
        } else { // If the other block is the controller, disconnect that
            get(getWorld(), otherEndPos).ifPresent(tube -> tube.disconnect(leftoverItemsConsumer));
        };
    };

    public void dropItemsAlongSpline(BigItemStack stack) {
        if (getWorld().isClientSide() || !getWorld().getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) return;
        int points = getSpline().getPoints().size();
        int items = (int)stack.getCount();
        if (items / points > 0) for (Vec3 point : getSpline().getPoints()) ItemHelper.pop(getWorld(), point, stack.copyStackWithCount(items / points));
        for (int i = 0; i < items % points; i++) ItemHelper.pop(getWorld(), getSpline().getPoints().get(i), stack.getSingleItemStack());
    };

    public void sendDestroyTubeParticles() {
        if (!(getWorld() instanceof ServerLevel level)) return;
        BlockParticleOption data = new BlockParticleOption(ParticleTypes.BLOCK, blockEntity.getBlockState());
        for (Vec3 point : spline.getPoints()) level.sendParticles(data, point.x, point.y, point.z, 1, 0, 0, 0, 0);
    };

    @SuppressWarnings("null")
    public void playSound(boolean destroy) {
        BlockState state = blockEntity.getBlockState();
        SoundType soundType = state.getSoundType(getWorld(), getPos(), null);
        getWorld().playSound(null, getPos(), destroy ? soundType.getBreakSound() : soundType.getPlaceSound(), SoundSource.BLOCKS, soundType.getVolume(), soundType.getPitch());
    };

    public boolean reconnect(Player player, boolean tryOtherIfNotController) {
        if (!controller) {
            if (tryOtherIfNotController) return get(getWorld(), otherEndPos).map(tube -> tube.reconnect(player, false)).orElse(false);
            return false;
        };
        TubeSpline oldSpline = getSpline();
        if (oldSpline == null) return false;
        ItemStack stackForConstruction = getRequiredStack().getSingleItemStack();
        disconnect((controller, stack) -> {if (!player.getAbilities().instabuild) stack.getAsStacks().forEach(player.getInventory()::placeItemBackInInventory);});
        Petrolpark.unsafeRunClient(() -> () -> reconnectClient(oldSpline, stackForConstruction));
        return true;
    };

    @OnlyIn(Dist.CLIENT)
    public void reconnectClient(TubeSpline oldSpline, ItemStack stack) {
        ClientTubePlacementHandler.cancel();
        if (!(blockEntity.getBlockState().getBlock() instanceof ITubeBlock tubeBlock)) return;
        Minecraft mc = Minecraft.getInstance();
        ClientTubePlacementHandler.tryConnect(oldSpline.start, stack, tubeBlock, false);
        ClientTubePlacementHandler.tryConnect(oldSpline.end, stack, tubeBlock, false);
        if (ClientTubePlacementHandler.active()) for (Vec3 controlPoint : oldSpline.getMiddleControlPoints()) {
            ClientTubePlacementHandler.addControlPointWithoutRevalidating(controlPoint);
        };
        ClientTubePlacementHandler.revalidateSpline(mc);
    };

    public BigItemStack getRequiredStack() {
        Block block = blockEntity.getBlockState().getBlock();
        if (getSpline() == null || !(block instanceof ITubeBlock tubeBlock)) return BigItemStack.EMPTY;
        return new BigItemStack(block, tubeBlock.getItemsForTubeLength(getSpline().getLength()));
    };

    public static Optional<TubeBehaviour> get(Level level, BlockPos pos) {
        if (pos == null) return Optional.empty();
        return Optional.ofNullable(BlockEntityBehaviour.get(level, pos, TYPE));
    };

    @Override
    public void tick() {
        super.tick();
        if (initializationTicks > 0) {
            initializationTicks--;
            if (controller && initializationTicks == 1 && getSpline() != null) {
                for (BlockPos pos : getSpline().getBlockedPositions()) {
                    getWorld().getBlockEntity(pos, PetrolparkCreateBlockEntityTypes.TUBE_STRUCTURE.get()).ifPresent(be -> be.setController(getPos()));
                };
            };
            tubeBlockEntity.invalidateTubeRenderBoundingBox();
        };
    };

    @Override
    public void destroy() {
        super.destroy();
        disconnect();
    };

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);

        boolean hadSpline = spline != null;

        // Reset when client recieves the packet in case the tube has been destroyed
        controller = false;
        middleControlPoints = Collections.emptyList();
        spline = null;
        
        if (nbt.contains("OtherEndPos")) otherEndPos = getPos().offset(NbtUtils.readBlockPos(nbt, "OtherEndPos").orElse(BlockPos.ZERO));
        if (nbt.contains("Points", Tag.TAG_LIST)) {
            controller = true;
            initializationTicks = nbt.getInt("InitializationTicks");
            middleControlPoints = nbt.getList("Points", Tag.TAG_LIST).stream().map(t -> NBTHelper.readVec3((ListTag)t, getPos())).toList();
        };
        if (blockEntity.hasLevel() && hadSpline == (getSpline() == null)) tubeBlockEntity.invalidateTubeRenderBoundingBox();
    };

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        if (otherEndPos != null) nbt.put("OtherEndPos", NbtUtils.writeBlockPos(otherEndPos.subtract(getPos())));
        if (!controller || getSpline() == null) return;
        if (initializationTicks > 0) nbt.putInt("InitializationTicks", initializationTicks);
        ListTag pointsTag = new ListTag();
        getSpline().getMiddleControlPoints().forEach(p -> pointsTag.add(NBTHelper.writeVec3(p, getPos())));
        nbt.put("Points", pointsTag);
    };

    @Override
    public BehaviourType<TubeBehaviour> getType() {
        return TYPE;
    };
    
};
