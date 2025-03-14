package com.petrolpark.team;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public interface ITeamBoundBlockEntity {
  
    public void bind(ITeam team, Player player, BlockHitResult hit);
};
