package com.petrolpark.block.entity.behaviour;

import java.util.ConcurrentModificationException;
import java.util.UUID;

import javax.annotation.Nullable;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractRememberPlacerBehaviour extends BlockEntityBehaviour {

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

    public static void setPlacedBy(SmartBlockEntity be, Player player) {
        if (player == null) return;
        for (BlockEntityBehaviour behaviour : be.getAllBehaviours()) {
            if (behaviour instanceof AbstractRememberPlacerBehaviour arpb && arpb.shouldRememberPlacer(player)) arpb.setPlayer(player.getUUID());
        };
    };

    @Override
    public void read(CompoundTag nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        if (nbt.contains("Owner")) playerUUID = nbt.getUUID("Owner");
    };

    @Override
    public void write(CompoundTag nbt, boolean clientPacket) {
        super.write(nbt, clientPacket);
        Player player = getPlayer();
        if (!nbt.contains("Owner") && player != null && shouldRememberPlacer(player)) nbt.putUUID("Owner", playerUUID); // Don't record more than once
    };
    
};
