package com.petrolpark.core.data.reward.entity;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkRewardTypes;
import com.petrolpark.core.data.reward.team.ITeamReward;
import com.petrolpark.core.team.GatherTeamProvidersEvent;
import com.petrolpark.core.team.ITeam;
import com.petrolpark.util.CodecHelper;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Issue a {@link ITeamReward} to all {@link ITeam}s of which the Player is a part.
 */
public record AllTeamsPlayerReward(ITeamReward reward) implements IPlayerReward {

    public static final MapCodec<AllTeamsPlayerReward> CODEC = CodecHelper.singleFieldMap(ITeamReward.CODEC, "reward", AllTeamsPlayerReward::reward, AllTeamsPlayerReward::new);

    @Override
    public void rewardPlayer(Player player, LootContext context, float multiplier) {
        GatherTeamProvidersEvent event = new GatherTeamProvidersEvent(player);
        NeoForge.EVENT_BUS.post(event);
        event.getTeamsUnmodifiable(context.getLevel()).forEach(team -> reward.reward(team, context, multiplier));
    };

    @Override
    public void render(GuiGraphics graphics) {
        reward.render(graphics);
    };

    @Override
    public Component getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    };

    @Override
    public EntityRewardType getType() {
        return PetrolparkRewardTypes.ALL_TEAMS.get();
    };
    
};
