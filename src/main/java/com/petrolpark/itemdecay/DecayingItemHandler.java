package com.petrolpark.itemdecay;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface DecayingItemHandler {
    
    public long getGameTime();

    public boolean isClientSide();

    public static DecayingItemHandler DUMMY = new DecayingItemHandler() {

        @Override
        public long getGameTime() {
            return 0l;
        };

        @Override
        public boolean isClientSide() {
            return false;
        };
    };

    public static class ServerDecayingItemHandler implements DecayingItemHandler {
        
        public long gameTime;

        @Override
        public long getGameTime() {
            return gameTime;
        };

        @Override
        public boolean isClientSide() {
            return false;
        };

    };

    @OnlyIn(Dist.CLIENT)
    public static class ClientDecayingItemHandler implements DecayingItemHandler {

        private final Minecraft minecraft = Minecraft.getInstance();

        @Override
        public long getGameTime() {
            return minecraft.level.getGameTime();
        };

        @Override
        public boolean isClientSide() {
            return true;
        };

    };
};
