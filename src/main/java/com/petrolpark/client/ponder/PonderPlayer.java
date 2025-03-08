package com.petrolpark.client.ponder;

import com.mojang.authlib.GameProfile;


import net.createmod.catnip.levelWrappers.WrappedClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.level.Level;

public class PonderPlayer extends AbstractClientPlayer {

    public PonderPlayer(Level level, String playername) {
        super(WrappedClientLevel.of(level), new GameProfile(null, playername));
    };
    
};
