package com.petrolpark.compat.create.core.block.entity.behaviour;

import java.util.ConcurrentModificationException;
import java.util.UUID;

import javax.annotation.Nullable;

import com.petrolpark.RequiresCreate;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent.EntityPlaceEvent;

@RequiresCreate
public abstract class AbstractRememberPlacerBehaviour extends BlockEntityBehaviour {

    @SubscribeEvent
    public static final void onPlaceBlock(EntityPlaceEvent event) {
        // Remember-placer behaviours for non-Petrolpark block entities - as the adding of this behaviour is deferred, simply placing the block won't do.
        if (event.getEntity() instanceof LivingEntity player) setPlacedBy(player.level(), event.getPos(), player);
    };

    @Nullable
    public static final Player getPlayer(Level level, CompoundTag blockEntityData) {
        return blockEntityData.contains("Owner") ? level.getPlayerByUUID(blockEntityData.getUUID("Owner")) : null;
    };

    private UUID playerUUID;

    public AbstractRememberPlacerBehaviour(SmartBlockEntity be) {
        super(be);
    };

    public abstract boolean shouldRememberPlacer(Player placer);

    @Nullable
    public Player getPlayer() {
        if (playerUUID == null) return null;
        return getWorld().getPlayerByUUID(playerUUID);
    };

    public void setPlayer(UUID uuid) {
		Player player = getWorld().getPlayerByUUID(uuid);
		if (player == null) return;
		playerUUID = uuid;
		blockEntity.setChanged();
	};

    public static void setPlacedBy(Level level, BlockPos pos, LivingEntity placer) {
        if (placer == null || !(placer instanceof Player player)) return;
        BlockEntity be;
		try {
			be = level.getBlockEntity(pos);
		} catch (ConcurrentModificationException e) {
			be = null;
		};
        if (be == null || !(be instanceof SmartBlockEntity sbe)) return;
        setPlacedBy(sbe, player);
	};

    public static final void setPlacedBy(SmartBlockEntity be, Player player) {
        if (player == null) return;
        for (BlockEntityBehaviour behaviour : be.getAllBehaviours()) {
            if (behaviour instanceof AbstractRememberPlacerBehaviour arpb && arpb.shouldRememberPlacer(player)) arpb.setPlayer(player.getUUID());
        };
    };

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(nbt, registries, clientPacket);
        if (nbt.contains("Owner")) playerUUID = nbt.getUUID("Owner");
    };

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(nbt, registries, clientPacket);
        if (playerUUID == null) return;
        if (blockEntity.hasLevel()) {
            final Player player = getPlayer();
            if (player == null || !shouldRememberPlacer(player)) return;
        };
        if (!nbt.contains("Owner")) nbt.putUUID("Owner", playerUUID); // Don't record more than once
    };
    
};
