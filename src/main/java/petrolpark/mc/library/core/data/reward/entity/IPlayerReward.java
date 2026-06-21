package petrolpark.mc.library.core.data.reward.entity;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;

public interface IPlayerReward extends IEntityReward {

    public void rewardPlayer(Player player, LootContext context, float multiplier);
    
    /**
     * @deprecated Override {@link IPlayerReward#rewardPlayer(Player, LootContext, float)} instead.
     */
    @Override
    @Deprecated
    @ApiStatus.NonExtendable
    public default void reward(Entity entity, LootContext context, float multiplier) {
        if (entity instanceof Player player) rewardPlayer(player, context, multiplier);
    };
};
