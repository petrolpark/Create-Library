package com.petrolpark.core.data.reward.entity;

import com.mojang.serialization.MapCodec;
import com.petrolpark.PetrolparkRewardTypes;
import com.petrolpark.core.data.reward.team.ITeamReward;
import com.petrolpark.core.team.ITeam;
import com.petrolpark.util.CodecHelper;
import com.petrolpark.util.Lang.IndentedTooltipBuilder;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.LootContext;

/**
 * <p>{@code petrolpark:}</p>
 * Issue a {@link ITeamReward} to all {@link ITeam}s of which the Player is a part.
 * 
 * Arguments:
 * <ul>
 * <li> {@code reward} - {@link ITeamReward} to award to all {@link ITeams}
 * </ul>
 * 
 * @author petrolpark
 */
public record AllTeamsPlayerReward(ITeamReward reward) implements IPlayerReward {

    public static final MapCodec<AllTeamsPlayerReward> CODEC = CodecHelper.singleFieldMap(ITeamReward.CODEC, "reward", AllTeamsPlayerReward::reward, AllTeamsPlayerReward::new);

    @Override
    public void rewardPlayer(Player player, LootContext context, float multiplier) {
        ITeam.streamAll(player).forEach(team -> reward().reward(team, context, multiplier));
    };

    @Override
    public void render(GuiGraphics graphics) {
        reward().render(graphics);
    };

    @Override
    public void addToDescription(IndentedTooltipBuilder builder) {
        builder.add(translateSimple())
            .indent();
        reward().addToDescription(builder);
        builder.unindent();
    };

    @Override
    public EntityRewardType getType() {
        return PetrolparkRewardTypes.ALL_TEAMS.get();
    };
    
};
