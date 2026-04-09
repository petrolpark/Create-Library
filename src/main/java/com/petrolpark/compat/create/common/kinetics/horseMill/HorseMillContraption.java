package com.petrolpark.compat.create.common.kinetics.horseMill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.petrolpark.compat.create.PetrolparkCreateContraptionTypes;
import com.petrolpark.mixin.compat.create.accessor.ContraptionAccessor;
import com.petrolpark.util.Lang;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;

import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class HorseMillContraption extends BearingContraption {

    protected @Nullable Boolean clockwise = null;

    protected List<BlockPos> harnesses = new ArrayList<>();
	protected Map<UUID, Integer> harnessMapping = new HashMap<>();

    public HorseMillContraption() {};
    
    public HorseMillContraption(Direction facing) {
        super(false, facing);
    };

    public void recalculateSpeedAndStress() {
        //TODO
    };

    @Override
    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        clockwise = null; // Don't know yet
        return super.assemble(world, pos);
    };

    @Override
    public void onEntityInitialize(Level world, AbstractContraptionEntity contraptionEntity) {
        super.onEntityInitialize(world, contraptionEntity);

        if (!(contraptionEntity instanceof HorseMillContraptionEntity horseMillContraptionEntity)) return;

        for (BlockPos harnessPos : getHarnesses()) {
			final Entity passenger = initialPassengers().get(harnessPos);
			if (passenger == null) continue;
			final int harnessIndex = getHarnesses().indexOf(harnessPos);
			if (harnessIndex == -1) continue;
			horseMillContraptionEntity.addHarnessedPassenger(passenger, harnessIndex);
		};
    };

    @Override
    protected boolean moveBlock(Level world, @Nullable Direction forcedDirection, Queue<BlockPos> frontier, Set<BlockPos> visited) throws AssemblyException {
        final BlockPos pos = frontier.peek();
        final boolean moved = super.moveBlock(world, forcedDirection, frontier, visited);
        final BlockState state = world.getBlockState(pos);
        if (moved && state.getBlock() instanceof HarnessBlock) {
            double facing = new Vec3(pos.getZ() - anchor.getZ(), 0f, anchor.getX() - pos.getX()).dot(Vec3.atLowerCornerOf(state.getValue(HarnessBlock.FACING).getNormal()));
            if (facing == 0d) throw harnessesFacingWrongWay(); // Radial
            boolean clockwise = facing > 0d;
            if (this.clockwise == null) {
                this.clockwise = clockwise;
            } else if (clockwise != this.clockwise) throw harnessesFacingWrongWay();
            moveHarness(world, pos); 
        };
        return moved;
    };

    protected void moveHarness(Level world, BlockPos pos) {
		final BlockPos local = toLocalPos(pos);
		getHarnesses().add(local);
		final List<HarnessEntity> harnessEntities = world.getEntitiesOfClass(HarnessEntity.class, new AABB(pos));
		if (!harnessEntities.isEmpty()) {
		    final HarnessEntity harness = harnessEntities.get(0);
			List<Entity> passengers = harness.getPassengers();
			if (!passengers.isEmpty()) initialPassengers().put(local, passengers.get(0));
		};
	};

    @Override
    public void readNBT(Level world, CompoundTag tag, boolean spawnData) {
        super.readNBT(world, tag, spawnData);

        clockwise = tag.contains("Clockwise", Tag.TAG_BYTE) ? null : tag.getBoolean("Clockwise");

        harnesses.clear();
        NBTHelper.iterateCompoundList(tag.getList("Harnesses", Tag.TAG_COMPOUND),
			c -> harnesses.add(c.contains("Pos") ? NBTHelper.readBlockPos(c, "Pos")
				: new BlockPos(c.getInt("X"), c.getInt("Y"), c.getInt("Z"))));

        harnessMapping.clear();
		NBTHelper.iterateCompoundList(tag.getList("HarnessPassengers", Tag.TAG_COMPOUND),
			c -> harnessMapping.put(NbtUtils.loadUUID(NBTHelper.getINBT(c, "Id")), c.getInt("Harness")));
    };

    @Override
    public CompoundTag writeNBT(HolderLookup.Provider registries, boolean spawnPacket) {
        final CompoundTag nbt = super.writeNBT(registries, spawnPacket);

        if (clockwise != null) nbt.putBoolean("Clockwise", clockwise);

        nbt.put("Harnesses", NBTHelper.writeCompoundList(getHarnesses(), pos -> {
			CompoundTag c = new CompoundTag();
			c.put("Pos", NbtUtils.writeBlockPos(pos));
			return c;
		}));
		nbt.put("HarnessPassengers", NBTHelper.writeCompoundList(getHarnessMapping().entrySet(), e -> {
			CompoundTag tag = new CompoundTag();
			tag.put("Id", NbtUtils.createUUID(e.getKey()));
			tag.putInt("Harness", e.getValue());
			return tag;
		}));

        return nbt;
    };

    @Override
    public void addPassengersToWorld(Level world, StructureTransform transform, List<Entity> seatedEntities) {
        super.addPassengersToWorld(world, transform, seatedEntities);
        
        if (getHarnessMapping().isEmpty()) return;

        for (final Entity harnessedEntity : seatedEntities) {
			final Integer harnessIndex = getHarnessMapping().get(harnessedEntity.getUUID());
			if (harnessIndex == null) continue; // It's probably on a Seat
			BlockPos harnessPos = getHarnesses().get(harnessIndex);
			harnessPos = transform.apply(harnessPos);
			if (!(world.getBlockState(harnessPos).getBlock() instanceof HarnessBlock)) continue;
			if (HarnessBlock.isHarnessOccupied(world, harnessPos)) continue;
			HarnessBlock.harness(world, harnessPos, harnessedEntity);
		};
    };

    public Map<UUID, Integer> getHarnessMapping() {
        return harnessMapping;
    };

    public BlockPos getHarnessOf(UUID entityId) {
		if (!getHarnessMapping().containsKey(entityId)) return null;
		final int harnessIndex = getHarnessMapping().get(entityId);
		if (harnessIndex >= getHarnesses().size()) return null;
		return getHarnesses().get(harnessIndex);
	};

    public void setHarnessMapping(Map<UUID, Integer> harnessMapping) {
		this.harnessMapping = harnessMapping;
	}

    public List<BlockPos> getHarnesses() {
        return harnesses;
    };

    protected Map<BlockPos, Entity> initialPassengers() {
        return ((ContraptionAccessor)(Contraption)this).getInitialPassengers();
    };

    @Override
    public ContraptionType getType() {
        return PetrolparkCreateContraptionTypes.HORSE_MILL.get();
    };

    public static final AssemblyException harnessesFacingWrongWay() {
        return new AssemblyException(Lang.translate("gui.assembly.exception.harnessesFaceWrongWay"));
    };
};
