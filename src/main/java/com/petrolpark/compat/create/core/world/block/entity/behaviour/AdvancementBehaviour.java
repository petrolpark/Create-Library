package com.petrolpark.compat.create.core.world.block.entity.behaviour;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.petrolpark.registry.PetrolparkDataMapTypes;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;

import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class AdvancementBehaviour extends AbstractRememberPlacerBehaviour {

    public static final void award(Level level, CompoundTag blockEntityData, Consumer<ServerPlayer> trigger) {
        if (level.isClientSide()) return;
        if (getPlayer(level, blockEntityData) instanceof ServerPlayer player && player != null) trigger.accept(player);
    };

    public static final BehaviourType<AdvancementBehaviour> TYPE = new BehaviourType<>();

    public AdvancementBehaviour(SmartBlockEntity be) {
        super(be);
    };

    @Override
    public boolean shouldRememberPlacer(Player player) {
        if (!(player.level() instanceof ServerLevel level) || !(player instanceof ServerPlayer serverPlayer)) return false;
        final Holder<BlockEntityType<?>> holder = blockEntity.getType().builtInRegistryHolder();
        if (holder == null) return false;
        final List<ResourceLocation> advancementLocations = holder.getData(PetrolparkDataMapTypes.BLOCK_ENTITY_ADVANCEMENTS);
        if (advancementLocations == null) return false;
        return advancementLocations.stream()
            .map(level.getServer().getAdvancements()::get)
            .map(serverPlayer.getAdvancements()::getOrStartProgress)
            .anyMatch(Predicate.not(AdvancementProgress::isDone));
    };

    public void award(Consumer<ServerPlayer> trigger) {
        if (getPlayer() instanceof ServerPlayer serverPlayer) trigger.accept(serverPlayer);
    };

    @Override
    public BehaviourType<AdvancementBehaviour> getType() {
        return TYPE;
    };
    
};
