package com.petrolpark.badge;

import java.util.Date;
import java.util.Optional;

import com.petrolpark.Petrolpark;
import com.simibubi.create.foundation.utility.Pair;

import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.commands.CommandFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class BadgeAdvancementRewards extends AdvancementRewards {

    private final Badge badge;

    public BadgeAdvancementRewards(Badge badge) {
        super(0, new ResourceLocation[0], new ResourceLocation[0], CommandFunction.CacheableFunction.NONE);
        this.badge = badge;
    };

    @Override
    public void grant(ServerPlayer player) {
        super.grant(player);
        Optional<Pair<Badge, Date>> optional = player.getCapability(BadgesCapability.Provider.PLAYER_BADGES).map(pb -> 
            pb.getBadges().stream().filter(pair -> pair.getFirst().equals(badge)).findFirst()
        ).get();
        if (optional.isPresent()) {
            player.getInventory().placeItemBackInInventory(BadgeItem.of(player, badge, optional.get().getSecond()));
        } else {
            Petrolpark.LOGGER.info("Couldn't find Badge date");
        };
    };
    
};
