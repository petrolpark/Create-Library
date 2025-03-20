package com.petrolpark.core.team;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public interface ITeamBoundBlockEntity {
  
    public void bind(ITeam.Provider team, Player player, BlockHitResult hit);
};
