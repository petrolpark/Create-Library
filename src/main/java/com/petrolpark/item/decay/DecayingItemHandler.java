package com.petrolpark.item.decay;

import com.petrolpark.Petrolpark;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

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
            MinecraftServer server = level.getServer();
            if (!level.isClientSide() && server != null && server.overworld() == level && level instanceof ServerLevel serverLevel) {
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
            ClientLevel level = minecraft.level;
            if (level == null) return 0l;
            return level.getGameTime();
        };

        @Override
        public boolean isClientSide() {
            return true;
        };

    };
};
