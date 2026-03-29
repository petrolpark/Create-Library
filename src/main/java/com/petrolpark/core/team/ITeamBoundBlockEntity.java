package com.petrolpark.core.team;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A Block Entity which can be linked to a {@link ITeam}.
 * @see <a href="https://github.com/petrolpark/Create-Library/wiki/Teams#accessing-teams">Usage</a>
 */
@ApiStatus.Experimental
public interface ITeamBoundBlockEntity {
  
    public void bind(ITeam.Provider team, Player player, BlockHitResult hit);
};
