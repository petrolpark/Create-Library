package com.petrolpark.itemdecay;

import com.petrolpark.Petrolpark;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

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

    @EventBusSubscriber
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

        @SubscribeEvent
        public static void onLoadWorld(LevelEvent.Load event) {
            LevelAccessor level = event.getLevel();
            if (!level.isClientSide() && level.getServer().overworld() == level && level instanceof ServerLevel serverLevel) {
                ServerDecayingItemHandler decayHandler = new ServerDecayingItemHandler();
                decayHandler.gameTime = serverLevel.getGameTime();
                Petrolpark.DECAYING_ITEM_HANDLER.set(decayHandler);  
        };
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
