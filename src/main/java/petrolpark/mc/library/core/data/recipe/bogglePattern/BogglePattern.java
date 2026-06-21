package petrolpark.mc.library.core.data.recipe.bogglePattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.ApiStatus;

import com.mojang.serialization.Codec;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.core.data.recipe.bogglePattern.generator.IBogglePatternGenerator;
import petrolpark.mc.library.registry.PetrolparkRegistries;
import petrolpark.mc.library.util.codec.CodecHelper;

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
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

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

    /**
     * Use {@link BogglePattern#getOrGeneratePattern(Level)} wherever possible.
     * @return Possible {@code null} pattern
     */
    @Nullable
    public Integer getPattern() {
        return pattern;
    };

    public int getOrGeneratePattern(Level level) {
        if (pattern == null) {
            pattern = generator.generate(level.random);
            Petrolpark.BOGGLE_PATTERNS.markDirty();
        };
        return pattern;
    };

    public void forgetPattern() {
        if (pattern != null) Petrolpark.BOGGLE_PATTERNS.markDirty();
        pattern = null;
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

        @SubscribeEvent
        public void onLevelLoaded(LevelEvent.Load event) {
            LevelAccessor level = event.getLevel();
            MinecraftServer server = level.getServer();
            if (server == null || server.overworld() != level) return;
            savedData = null;
            loadSavedData(server);
        };

        private void loadSavedData(MinecraftServer server) {
            if (savedData != null) return;
            savedData = server.overworld()
                .getDataStorage()
                .computeIfAbsent(new SavedData.Factory<>(() -> new BogglePatternSavedData(server.overworld()), (tag, registries) -> load(server.overworld(), tag)), Petrolpark.MOD_ID + "_boggle_patterns");
        };

        public class BogglePatternSavedData extends SavedData {

            protected Level level;

            public BogglePatternSavedData(Level level) {
                this.level = level;  
            };

            @Override
            public CompoundTag save(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
                registries.lookupOrThrow(PetrolparkRegistries.Keys.BOGGLE_PATTERN).listElements().forEach(holder -> {
                    tag.putInt(holder.key().location().toString(), holder.value().getOrGeneratePattern(level));
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
            
            lookup.listElements().map(Holder::value).forEach(pattern -> pattern.getOrGeneratePattern(overworld)); // Generate any new patterns

            return savedData;
        };

        public void markDirty() {
            if (savedData != null) savedData.setDirty();
        };
    };
};
