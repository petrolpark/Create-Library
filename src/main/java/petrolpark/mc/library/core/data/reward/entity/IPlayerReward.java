package petrolpark.mc.library.core.data.reward.entity;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;

@ParametersAreNonnullByDefault
public interface IPlayerReward extends IEntityReward {

    public boolean rewardPlayer(ServerPlayer player, LootContext context, float multiplier, boolean simulate);
    
    /**
     * @deprecated Override {@link IPlayerReward#rewardPlayer(Player, LootContext, float)} instead.
     */
    @Override
    @Deprecated
    @ApiStatus.NonExtendable
    public default boolean reward(Entity entity, LootContext context, float multiplier, boolean simulate) {
        return entity instanceof ServerPlayer player ? rewardPlayer(player, context, multiplier, simulate) : false;
    };
};
