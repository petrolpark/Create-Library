package com.petrolpark.core.recipe.bogglepattern;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.util.CodecHelper;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;

@ApiStatus.Experimental
public class BogglePattern {

    public static final Codec<BogglePattern> DIRECT_CODEC = CodecHelper.singleField(IBogglePatternGenerator.CODEC, "generator", BogglePattern::getGenerator, BogglePattern::new);
    public static final Codec<BogglePattern> DIRECT_NETWORK_CODEC = CodecHelper.singleField(IBogglePatternGenerator.NETWORK_CODEC, "generator", BogglePattern::getGenerator, BogglePattern::new);

    protected final IBogglePatternGenerator generator;
    private Integer pattern;

    public BogglePattern(IBogglePatternGenerator generator) {
        this.generator = generator;
    };
    
    public IBogglePatternGenerator getGenerator() {
        return generator;
    };

    public int getPattern(Level level) {
        if (pattern == null) pattern = generator.generate(level.random);
        return pattern;
    };

    public static class Manager {
    
        protected BogglePatternSavedData savedData;

        // public void playerLogin(Player player) {
        //     if (player instanceof ServerPlayer serverPlayer) {
        //         loadSavedData(serverPlayer.getServer());
        //         for (ScoreboardTeam team : teams.values()) {
        //             CatnipServices.NETWORK.sendToClient(serverPlayer, new ScoreboardTeamComponentChangedPacket(team.team.getName(), team.getDataComponentPatch()));
        //         };
        //     }
        // };

        public void playerLogout(Player player) {};

        public void levelLoaded(LevelAccessor level) {
            MinecraftServer server = level.getServer();
            if (server == null || server.overworld() != level) return;
            savedData = null;
            loadSavedData(server);
        };

        private void loadSavedData(MinecraftServer server) {
            if (savedData != null) return;
            savedData = server.overworld()
                .getDataStorage()
                .computeIfAbsent(new SavedData.Factory<>(() -> new BogglePatternSavedData(server.overworld()), (tag, registries) -> load(server.overworld(), tag)), "petrolpark_boggle_patterns");
        };

        public class BogglePatternSavedData extends SavedData {

            protected Level level;

            public BogglePatternSavedData(Level level) {
                this.level = level;  
            };

            @Override
            public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
                registries.lookupOrThrow(PetrolparkRegistries.Keys.BOGGLE_PATTERN).listElements().forEach(holder -> {
                    tag.putInt(holder.key().location().toString(), holder.value().getPattern(level));
                });
                return tag;
            };

        };

        protected BogglePatternSavedData load(Level overworld, CompoundTag tag) {
            BogglePatternSavedData savedData = new BogglePatternSavedData(overworld);
            RegistryLookup<BogglePattern> lookup = overworld.registryAccess().lookupOrThrow(PetrolparkRegistries.Keys.BOGGLE_PATTERN);

            for (String key : tag.getAllKeys()) {
                lookup.get(ResourceKey.create(PetrolparkRegistries.Keys.BOGGLE_PATTERN, ResourceLocation.parse(key))).ifPresent(holder ->
                    holder.value().pattern = tag.getInt(key)
                );
            };
            
            lookup.listElements().map(Holder::value).forEach(pattern -> pattern.getPattern(overworld)); // Generate any new patterns

            lookup.listElements().forEach(holder -> Petrolpark.LOGGER.info("pattern " + holder.key().location().toString() + " is " + holder.value().getPattern(overworld))); // Temp

            return savedData;
        };
    };
};
